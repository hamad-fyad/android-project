package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AddBuildingActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    private LinearLayout imageContainer;
    private EditText Address, price, size;
    private Button add_post;
    private ShapeableImageView addPicturesButton;
    private TextView GoBack;
    private ActivityResultLauncher<String> multipleImagePickerLauncher;
    private List<Uri> selectedImagesUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);
        imageContainer = findViewById(R.id.imageContainer);
        GoBack = findViewById(R.id.go_back_browsing);
        add_post = findViewById(R.id.add_post);
        addPicturesButton = findViewById(R.id.addPicturesButton);
        Address = findViewById(R.id.address);
        price = findViewById(R.id.price);
        size = findViewById(R.id.size);
        requestStoragePermission();
        selectedImagesUris = new ArrayList<>();
        GoBack.setOnClickListener(v -> startActivity(new Intent(AddBuildingActivity.this, MainActivity.class)));

        add_post.setOnClickListener(v -> addBuilding());

        multipleImagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), result -> {
            if (result != null) {
                selectedImagesUris.addAll(result);
                addPictures(result);
            }
        });


        addPicturesButton.setOnClickListener(v -> multipleImagePickerLauncher.launch("image/*"));
    }

    private void addPictures(List<Uri> imagesUris) {
        for (Uri imageUri : imagesUris) {
            ShapeableImageView shapeableImageView = createShapeableImageView(imageUri);
            imageContainer.addView(shapeableImageView);
        }
    }

    private ShapeableImageView createShapeableImageView(Uri imageUri) {
        ShapeableImageView shapeableImageView = new ShapeableImageView(this);
        int imageSize = getResources().getDimensionPixelSize(R.dimen.image_size);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.image_margin_right), 0);
        shapeableImageView.setLayoutParams(layoutParams);
        shapeableImageView.setAdjustViewBounds(true);
        shapeableImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        shapeableImageView.setImageURI(imageUri);

        float cornerRadius = getResources().getDimension(R.dimen.image_corner_radius);
        shapeableImageView.setShapeAppearanceModel(
                shapeableImageView.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
                        .build()
        );

        return shapeableImageView;
    }

    private void addBuilding() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Retrieve the user from the Firestore database
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String address = Address.getText().toString();
                    double buildingPrice = Double.parseDouble(price.getText().toString());
                    double buildingSize = Double.parseDouble(size.getText().toString());

                    // Increment building count for the user
                    incrementUserBuildingCount(document);

                    // Save the images to Firebase Storage and get their URLs to store in the Building object
                    uploadImagesAndGetUrls(selectedImagesUris, imageUrls -> {
                        Buildings building = new Buildings(address, buildingPrice, buildingSize, userId, imageUrls);

                        // Save the Building object to the Firestore database
                        firestore.collection("Buildings")
                                .add(building)
                                .addOnSuccessListener(documentReference -> {
                                    Utility.showToast(this, "Building added successfully");
                                    startActivity(new Intent(AddBuildingActivity.this, MainActivity.class));
                                })
                                .addOnFailureListener(e -> Utility.showToast(this, "Error adding building: " + e.getMessage()));
                    });
                } else {
                    Utility.showToast(this, "User not found");
                }
            } else {
                Utility.showToast(this, "Error getting user: " + task.getException().getMessage());
            }
        });
    }

    private void incrementUserBuildingCount(DocumentSnapshot userDocument) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = userDocument.getId();
        DocumentReference userDocRef = firestore.collection("users").document(userId);
        Long count = userDocument.getLong("buildingcount");
        if (count == null) {
            count = 1L;
        }
        userDocRef.update("buildingcount", count + 1);
    }



    private void uploadImagesAndGetUrls(List<Uri> imageUris, OnImagesUploadedListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ArrayList<String> imageUrls = new ArrayList<>();
        AtomicInteger imagesUploaded = new AtomicInteger(0);

        for (Uri imageUri : imageUris) {
            String imageName = UUID.randomUUID().toString() + ".jpg";
            StorageReference storageRef = storage.getReference().child("images/" + imageName);

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                imageUrls.add(downloadUrl.toString());
                if (imagesUploaded.incrementAndGet() == imageUris.size()) {
                    listener.onImagesUploaded(imageUrls);
                }
            })).addOnFailureListener(e -> Utility.showToast(this, "Error uploading image: " + e.getMessage()));
        }
    }

    private interface OnImagesUploadedListener {
        void onImagesUploaded(ArrayList<String> imageUrls);
    }



    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access the gallery
            } else {
                Utility.showToast(this, "Permission denied. Unable to access the gallery.");
            }
        }
    }
}