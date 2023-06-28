package com.example.myapplication.CHAT;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.Utilitys.Utility;
import com.example.myapplication.adapters.MessageAdapter;
import com.example.myapplication.classes.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private EditText messageEditText;
    private ImageButton sendButton;
    private SharedPreferences mypref;
    private FirebaseFirestore db;
    private String otherUserId ,name;
    private List<Message> messages = new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room);
        db = FirebaseFirestore.getInstance();
        currentUserId = getIntent().getStringExtra("ownerId");
        otherUserId=getIntent().getStringExtra("currentUserId");
        String chatId = otherUserId;
        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.sendbtn);
        messageAdapter = new MessageAdapter(messages);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            addMessageToFirestore(messageText);
            messageEditText.setText("");
        });

        listenForMessages(chatId);
    }

    private void addMessageToFirestore(String messageText) {
        Message message = new Message(currentUserId , messageText, new Timestamp(new Date()));
        String chatId = otherUserId;
        String chatid2=currentUserId;
        Log.w(TAG, otherUserId);
        Log.w(TAG, currentUserId);

        // Create chat document with "id" field
        Map<String, String> chatData = new HashMap<>();
        chatData.put("id", otherUserId);
        Map<String, String> chatData2 = new HashMap<>();
        chatData.put("id", currentUserId);
        // Update chat document for the current user
        db.collection("users").document(currentUserId).collection("chat").document(chatId).set(chatData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Add the message to the current user's "Messages" sub-collection
                    db.collection("users").document(currentUserId).collection("chat").document(chatId).collection("Messages").add(message)
                            .addOnSuccessListener(documentReference -> {
                                // If the message was added successfully to the current user's sub-collection, add it to the other user's sub-collection
                                // Update chat document for the other user
                                db.collection("users").document(otherUserId).collection("chat").document(chatid2).set(chatData2, SetOptions.merge())
                                        .addOnSuccessListener(aVoid1 -> db.collection("users").document(otherUserId).collection("chat").document(chatid2).collection("Messages").add(message)
                                                .addOnSuccessListener(documentReference1 -> {
                                                    // Message successfully written for both users
                                                    Log.d(TAG, "Message added for both users with ID: " + documentReference1.getId());
                                                })
                                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document for other user", e)))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error updating chat document for other user", e));
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error adding document for current user", e));
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error updating chat document for current user", e));
    }

    private void listenForMessages(String chatId) {
        db.collection("users").document(currentUserId).collection("chat").document(chatId).collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Message message = dc.getDocument().toObject(Message.class);
                            messages.add(message);
                            sendNotification(message);
                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                });
    }

    private void sendNotification(Message message) {

        if (!message.getSentBy().equals(currentUserId)) {
            if (!Utility.isActivityOpen(this, ChatRoomActivity.class)) {

                String channelId = "com.example.myapplication";
                String channelName = "NewMessageNotification";

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                resultIntent.putExtra("ownerId", currentUserId);
                resultIntent.putExtra("currentUserId", otherUserId);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(/*mypref.getString("name","")*/"hamad ")
                        .setContentText(message.getMessage())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent);

                int notificationId = message.getMessage().hashCode();
                notificationManager.notify(notificationId, builder.build());
            }
        }
    }

}
