package com.example.myapplication.adapters;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
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
import com.example.myapplication.classes.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.MyViewHolder> {
    private ArrayList<User> dataset;



    public WorkerAdapter(ArrayList<User> myDataset) {
        dataset = myDataset;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.worker_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            if (!dataset.isEmpty()) {
                Log.w(TAG, "onBindViewHolder2: " + dataset.get(position).toString());
                holder.myTextView.setText(dataset.get(position).getName());
                holder.email.setText(dataset.get(position).getEmail());
                String photoURL= dataset.get(position).getPhotoURL();
                if (photoURL != null && !photoURL.isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(photoURL)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .into(holder.picture);
                } else {
                    holder.picture.setImageResource(R.drawable.baseline_person_24);
                }
                holder.itemView.setOnClickListener(v ->{
                    // Handle the click event, e.g., open the chat
                    Intent intent = new Intent(holder.itemView.getContext(), ChatRoomActivity.class);
                    intent.putExtra("ownerId", dataset.get(position).getUid());
                    intent.putExtra("currentUserId", FirebaseAuth.getInstance().getUid());
                    holder.itemView.getContext().startActivity(intent);

                });

            }else {
                holder.myTextView.setText("no users");
            }


    }

    @Override
    public int getItemCount() {
        return dataset != null ? dataset.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView myTextView,email;
        private ImageView picture;

        public MyViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.chat_text_view2);
            picture=itemView.findViewById(R.id.picture);
            email=itemView.findViewById(R.id.email);
        }

        public TextView getMyTextView() {
            return myTextView;
        }
    }
}
