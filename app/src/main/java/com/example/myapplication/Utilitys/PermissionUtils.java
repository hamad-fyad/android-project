package com.example.myapplication.Utilitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    // Permission request codes
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int REQUEST_FINE_LOCATION = 3;
    public static final int REQUEST_COARSE_LOCATION = 4;

    public static boolean hasReadStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasWriteStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasCoarseLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadStoragePermission(Activity activity) {
        requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE);
    }

    public static void requestWriteStoragePermission(Activity activity) {
        requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    public static void requestCameraPermission(Activity activity) {
        requestPermission(activity, Manifest.permission.CAMERA, REQUEST_CAMERA);
    }

    public static void requestFineLocationPermission(Activity activity) {
        requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
    }

    public static void requestCoarseLocationPermission(Activity activity) {
        requestPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_COARSE_LOCATION);
    }

    private static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }
}
