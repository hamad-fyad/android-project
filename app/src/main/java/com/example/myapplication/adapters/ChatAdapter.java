package com.example.myapplication.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.CHAT.ChatRoomActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utilitys.Utility;
import com.example.myapplication.classes.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    List<String> chatIds;

    public ChatAdapter(List<String> chatIds) {
        this.chatIds = chatIds;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item2, null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (chatIds != null && !chatIds.isEmpty()) {
            String chatId = chatIds.get(position);
            Utility.getUser(new Utility.UserCallback() {
                @Override
                public void onUserReceived(User user) {
                   holder.chatTextView.setText(user.getName());
                   String photoURL= user.getPhotoURL();
                    if (photoURL != null && !photoURL.isEmpty()) {
                        Glide.with(holder.itemView.getContext())
                                .load(photoURL)
                                .placeholder(R.drawable.baseline_person_24)
                                .error(R.drawable.baseline_person_24)
                                .into(holder.Picture);
                    } else {
                        holder.Picture.setImageResource(R.drawable.baseline_person_24);
                    }
                }
                @Override
                public void onError(Exception e) {
                    Utility.showToast(holder.itemView.getContext(),"could not get the user ");
                }
            },chatId);

            holder.itemView.setOnClickListener(view -> {
                // Handle the click event, open the chat
                Intent intent = new Intent(holder.itemView.getContext(), ChatRoomActivity.class);
                intent.putExtra("ownerId", chatId);
                intent.putExtra("currentUserId", FirebaseAuth.getInstance().getUid());
                holder.itemView.getContext().startActivity(intent);
            });
        }  if (chatIds.size() == 0) {
            holder.chatTextView.setText("no chats you haven't talked with anyone ");
        }
    }


    @Override
    public int getItemCount() {
        return chatIds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatTextView;
        ImageView Picture;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           Picture = itemView.findViewById(R.id.picture);
            chatTextView = itemView.findViewById(R.id.chat_text_view1);
        }
    }
}
