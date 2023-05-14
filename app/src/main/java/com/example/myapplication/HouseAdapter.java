package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.ViewHolder> {

    private List<Buildings> houses;

    public HouseAdapter(List<Buildings> houses) {
        this.houses = houses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Buildings building = houses.get(position);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(building.getUseruid()).get().addOnSuccessListener(documentSnapshot->{
            if (documentSnapshot.exists()) {
                String email = documentSnapshot.getString("email");
                String number = documentSnapshot.getString("number");
                String buildingDetails = "Address: " + building.getAddress() + "\n" +
                        "Price: " + building.getPrice() + " $\n" +
                        "Size: " + building.getSize()+ " m\n"+email+
                        "\n"+number;
                holder.textViewDetails.setText(buildingDetails);
            }else{
                Log.d(TAG, "No such document");
            }
        });
        // Set building details


        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.imageRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        holder.imageRecyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(holder.imageRecyclerView);

        // Set up the ImageAdapter for the nested RecyclerView
        ArrayList<String> buildingImages = building.getPicture();
        ImageAdapter imageAdapter = new ImageAdapter(buildingImages);
        holder.imageRecyclerView.setAdapter(imageAdapter);

    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView imageRecyclerView;
        TextView textViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRecyclerView = itemView.findViewById(R.id.imageRecyclerView);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
        }
    }
}
