package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private TextView welcomeText;
        private MessageAdapter messageAdapter;
        private EditText messageEditText;
        private ImageButton sendButton;

        private DatabaseReference chatReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_room);
            recyclerView=findViewById(R.id.recyclerView);
            welcomeText=findViewById(R.id.welcometext);
            messageEditText=findViewById(R.id.message_edit_text);
            sendButton=findViewById(R.id.sendbtn);
            messageAdapter=new MessageAdapter(chatReference);
            recyclerView.setAdapter(messageAdapter);
            LinearLayoutManager llm=new LinearLayoutManager(this);
            llm.setStackFromEnd(true);
            recyclerView.setLayoutManager(llm);
            sendButton.setOnClickListener(v->{
                String question=messageEditText.getText().toString().trim();
            });



    }
    private void  addtochat(String message,String sentby){


    }
}