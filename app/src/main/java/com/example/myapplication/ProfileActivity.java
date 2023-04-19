package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private Button logout;

    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private TextView Name, Address, Email, Number;
    private EditText Name1, Address1, Number1;
    private MaterialButton EditProfile, save;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ShapeableImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = findViewById(R.id.Progress_bar);
        imageView = findViewById(R.id.picture);
        Name = findViewById(R.id.text_name);
        Address = findViewById(R.id.address);
        Email = findViewById(R.id.email);
        Number = findViewById(R.id.number);
        Name1 = findViewById(R.id.text_name1);
        Address1 = findViewById(R.id.address1);
        Number1 = findViewById(R.id.number1);

        loadUserData();
        EditProfile = findViewById(R.id.edit_profile);
        EditProfile.setOnClickListener(v -> ChangeDetails(true));
        save = findViewById(R.id.save);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(v->Logout());



        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    // Start ProfileActivity
                    Utility.showToast(ProfileActivity.this, "you are already in profile page");
                    return true;
                case R.id.browsing:
                    // Start BrowsingActivity
                    Intent browsingIntent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(browsingIntent);
                    return true;
                case R.id.service:
                    // Start ServiceActivity
                    Intent serviceIntent = new Intent(ProfileActivity.this, serviceActivity2.class);
                    startActivity(serviceIntent);
                    return true;
            }
            return false;
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        // Set an OnClickListener on the profile image view
        imageView.setOnClickListener(v -> openFileChooser());
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void loadUserData() {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            // Retrieve the user data from Firestore using the UID
            firestore.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get user data from the document and update the UI
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String address = documentSnapshot.getString("address");
                            String number = documentSnapshot.getString("number");
                            String photoURL = documentSnapshot.getString("photoURL");
                            Name.setText("name : " + name);
                            Email.setText("email : " + email);
                            Address.setText("address : " + address);
                            Number.setText("number : " + number);

                            // Load the user's profile image using Glide
                            if (photoURL != null && !photoURL.isEmpty()) {
                                Glide.with(this)
                                        .load(photoURL)
                                        .placeholder(R.drawable.addpicture)
                                        .error(R.drawable.addpicture)
                                        .into(imageView);
                            } else {
                                imageView.setImageResource(R.drawable.addpicture);
                            }

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error getting user data", e));
        }
    }


    private void SaveDetails() {
        String name = Name1.getText().toString();
        String address = Address1.getText().toString();
        String number = Number1.getText().toString();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DocumentReference userDocRef = firestore.collection("users").document(uid);

            Map<String, Object> updatedData = new HashMap<>();

            if (name != null && !name.isEmpty()) {
                updatedData.put("name", name);
            }
            if (address != null && !address.isEmpty()) {
                updatedData.put("address", address);
            }
            if (number != null && !number.isEmpty()) {
                updatedData.put("number", number);
            }

            if (!updatedData.isEmpty()) {
                userDocRef.update(updatedData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "User data successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating user data", e);
                            }
                        });
            }
}
        ChangeDetails(false);
    }
    private void ChangeDetails(boolean isEditMode) {
        if (isEditMode) {
            Name.setVisibility(View.INVISIBLE);
            Email.setVisibility(View.INVISIBLE);
            Address.setVisibility(View.INVISIBLE);
            Number.setVisibility(View.INVISIBLE);
            Name1.setVisibility(View.VISIBLE);
            Address1.setVisibility(View.VISIBLE);
            Number1.setVisibility(View.VISIBLE);
            EditProfile.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);
            save.setOnClickListener(v -> SaveDetails());
        } else {

            Name.setVisibility(View.VISIBLE);
            Email.setVisibility(View.VISIBLE);
            Address.setVisibility(View.VISIBLE);
            Number.setVisibility(View.VISIBLE);
            Name1.setVisibility(View.INVISIBLE);
            Address1.setVisibility(View.INVISIBLE);
            Number1.setVisibility(View.INVISIBLE);
            EditProfile.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);
            loadUserData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Use Glide to load the selected image into the ImageView
            Glide.with(this).load(imageUri).into(imageView);
            // Call the uploadImage method to save the image to Firebase Storage
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                StorageReference fileReference = storageReference.child(uid + ".jpg");

                // Check if the image already exists
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Image already exists, skipping upload.");
                }).addOnFailureListener(e -> {
                    if (e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        Log.d(TAG, "Image not found, uploading a new one...");

                        // Upload the image
                        fileReference.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String downloadUrl = uri.toString();
                                        Log.d(TAG, "File uploaded successfully: " + downloadUrl);
                                        // Save the download URL to the Firestore database
                                        saveProfileImageUrl(downloadUrl);
                                    });
                                })
                                .addOnFailureListener(uploadError -> {

                                    Log.w(TAG, "Failed to upload file", uploadError);
                                });
                    } else {
                        Log.w(TAG, "Error checking for image existence", e);
                    }
                });
            }
        } else {
            Log.w(TAG, "No file was selected");
        }
    }



    private void saveProfileImageUrl(String imageUrl) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DocumentReference userDocRef = firestore.collection("users").document(uid);

            userDocRef.update("photoURL", imageUrl)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile image URL saved successfully"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error saving profile image URL", e));
        }
    }

}