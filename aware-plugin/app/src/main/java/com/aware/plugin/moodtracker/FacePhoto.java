package com.aware.plugin.moodtracker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class FacePhoto extends Service {
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Parameters cameraParameters;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Plugin.TAG, "Created camera service");
    }

    @Override
    public void onStart(Intent intent, int startID) {
        super.onStart(intent, startID);
        Log.d(Plugin.TAG, "Connected camera service");

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int cameraId = getFrontCameraId();
        if (cameraId != -1) {
            try {
                releaseCameraAndPreview();
                camera = Camera.open(cameraId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SurfaceView surfaceView = new SurfaceView(getApplicationContext());
            try {
                camera.setPreviewDisplay(surfaceView.getHolder());
                cameraParameters = camera.getParameters();
                camera.setParameters(cameraParameters);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(Plugin.TAG, "No front facing camera");
        }
        camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
        //camera.release();
    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /*@Override
    protected void onPause()
    {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }*/

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {
            Log.d(Plugin.TAG, "Picture taken");
        }
    };

    int getFrontCameraId() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i=0; i<Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
        }
        return -1;
    }
}
