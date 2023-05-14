package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        // Assuming User class has a method getName()
        // Replace 'getName' with your actual method or field name
        User user=dataset.get(position);
        String text=user.getName()+"\n "+user.getEmail()+"\n "+user.getNumber()+" ";
        holder.myTextView.setText(text);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        public MyViewHolder(View itemView) {
            super(itemView);

            myTextView = itemView.findViewById(R.id.worker);
        }
    }
}
