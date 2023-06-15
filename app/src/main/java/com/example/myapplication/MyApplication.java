package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class MyApplication extends Application implements LifecycleObserver {

    private static boolean isInBackground = true;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onAppForegrounded() {
        isInBackground = false;
        Log.d("MyApp", "App in the foreground");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        isInBackground = true;
        Log.d("MyApp", "App in the background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onAppDestroyed() {
        isInBackground = true;
        Log.d("MyApp", "App destroyed");
    }

    public static boolean isInBackground() {
        return isInBackground;
    }
}
