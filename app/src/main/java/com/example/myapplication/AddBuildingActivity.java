package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Utilitys.PermissionUtils;
import com.example.myapplication.Utilitys.Utility;
import com.example.myapplication.classes.Buildings;
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
import android.Manifest;



public class AddBuildingActivity extends AppCompatActivity {
    private LinearLayout imageContainer;
    private EditText Address, price, size;
    private ActivityResultLauncher<String> multipleImagePickerLauncher;
    private Spinner area;

    private List<Uri> selectedImagesUris;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);
        imageContainer = findViewById(R.id.imageContainer);
        TextView goBack = findViewById(R.id.go_back_browsing);
        Button add_post = findViewById(R.id.add_post);
        ShapeableImageView addPicturesButton = findViewById(R.id.addPicturesButton);
        Address = findViewById(R.id.address);
        price = findViewById(R.id.price);
        size = findViewById(R.id.size);
        area = findViewById(R.id.spinner_type_of_buildings);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.building_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        area.setAdapter(adapter);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        selectedImagesUris = new ArrayList<>();
        goBack.setOnClickListener(v -> startActivity(new Intent(AddBuildingActivity.this, MainActivity.class)));

        add_post.setOnClickListener(v -> addBuilding());

        multipleImagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), result -> {
            if (result != null) {
                selectedImagesUris.addAll(result);
                addPictures(result);
            }
        });


        addPicturesButton.setOnClickListener(v -> {
            if(PermissionUtils.hasCameraPermission(this)&&PermissionUtils.hasReadStoragePermission(this)) {
                multipleImagePickerLauncher.launch("image/*");
            } else {
                // If we don't have permissions, show an alert dialog with an explanation and an option to go to settings
                new AlertDialog.Builder(this)
                        .setTitle("Permissions Required")
                        .setMessage("You need to enable storage and camera permissions to use this feature. Do you want to go to settings and enable them?")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }



    private void addPictures(List<Uri> imagesUris) {
        for (Uri imageUri : imagesUris) {
            ShapeableImageView shapeableImageView = createShapeableImageView(imageUri);
            imageContainer.addView(shapeableImageView);
        }
    }
private ShapeableImageView createShapeableImageView(Uri imageUri) {// function for styling the images and how they appear
    ShapeableImageView shapeableImageView = new ShapeableImageView(this);
    int imageSize = getResources().getDimensionPixelSize(R.dimen.image_size);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
    layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.image_margin_right), 0);
    shapeableImageView.setLayoutParams(layoutParams);
    shapeableImageView.setAdjustViewBounds(true);
    shapeableImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    shapeableImageView.setImageURI(imageUri);

    // Create the animation
    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(shapeableImageView, "alpha", 0f, 1f);
    alphaAnimator.setDuration(1000);

    // Start the animation
    alphaAnimator.start();

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

        // Get the selected radio button ID
        int selectedId = radioGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioButton = (RadioButton) findViewById(selectedId);

        // If the radio button is not null, retrieve its text
        if (radioButton != null) {
            String selectedOption = radioButton.getText().toString().trim();

            if (isEmpty(Address.getText()) || isEmpty(price.getText()) || isEmpty(size.getText()) ||  selectedImagesUris.size() == 0) {
                Utility.showToast(this, "Please fill in all fields");
                return;
            }

            // Retrieve the user from the Firestore database
            firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        long buildingcount=document.getLong("buildingcount");
                        if (buildingcount==5){
                            Utility.showToast(this,"you cant add more buildings you have reached the maximum amount");
                            return;
                        }

                        String address = Address.getText().toString().toLowerCase().trim();
                        double buildingPrice = Double.parseDouble(price.getText().toString());
                        double buildingSize = Double.parseDouble(size.getText().toString());
                        String area1=area.getSelectedItem().toString().trim();
                        Log.d(TAG, "addBuilding: "+area1);
                        Address.setText("");
                        price.setText("");
                        size.setText("");

                        // Increment building count for the user
                        incrementUserBuildingCount(document);

                        // Save the images to Firebase Storage and get their URLs to store in the Building object
                        uploadImagesAndGetUrls(selectedImagesUris, imageUrls -> {
                            // Generate a new document reference with an auto-generated ID
                            DocumentReference newBuildingRef = firestore.collection("Buildings").document();
                            // Get the UID of the new document
                            String buildingUid = newBuildingRef.getId();
                            //we needed to add the uid so we only
                            Buildings building = new Buildings(address, buildingPrice, buildingSize, userId, imageUrls,buildingUid,selectedOption.toLowerCase(), area1.toLowerCase());

                            // Save the Building object to the Firestore database using the generated document reference
                            newBuildingRef.set(building)
                                    .addOnSuccessListener(aVoid -> {

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

        } else {
            Utility.showToast(this, "Please select a radio button option");
        }

    }



    private void incrementUserBuildingCount(DocumentSnapshot userDocument) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = userDocument.getId();
        DocumentReference userDocRef = firestore.collection("users").document(userId);
        Long count = userDocument.getLong("buildingcount");
        if (count == null) {
            count = 0L;
        }
        userDocRef.update("buildingcount", count + 1);
    }


//An AtomicInteger called imagesUploaded is initialized with a value of 0. This variable will keep track of the number of images that have been successfully uploaded.
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

    private boolean isEmpty(CharSequence text) {
        return text.toString().trim().length() == 0;
    }

}
