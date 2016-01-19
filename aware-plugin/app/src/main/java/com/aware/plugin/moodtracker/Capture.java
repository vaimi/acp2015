package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class Capture extends Service {
    private Camera camera;
    private int cameraId = 0;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        cameraId = findFrontFacingCamera();
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(Plugin.TAG, "No camera on this device");
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Log.d(Plugin.TAG, "No front facing camera on this device");
            } else {
                camera = Camera.open(cameraId);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void launchCamera() {
        camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
    };

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(Plugin.TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    /*@Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }*/

}
