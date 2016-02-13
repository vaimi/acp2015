package com.aware.plugin.moodtracker;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.aware.Aware;
import com.aware.providers.Applications_Provider;

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


    private Boolean prepareCamera() {
        int cameraId = getFrontCameraId();
        if (cameraId != -1) {
            try {
                camera = Camera.open(cameraId);
            } catch (Exception e) {
                Log.d(Plugin.TAG, "Camera reserved, aborting");
                return false;
            }
            SurfaceView surfaceView = new SurfaceView(getApplicationContext());
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
                cameraParameters = camera.getParameters();
                //setCameraRotation(cameraId);
                camera.setParameters(cameraParameters);
                camera.startPreview();
            } catch (IOException e) {
                Log.d(Plugin.TAG, "Cannot make preview, aborting");
                return false;
            }
        } else {
            Log.d(Plugin.TAG, "No front facing camera");
        }
        return true;
    }

    public void setCameraRotation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        WindowManager window = (WindowManager) getSystemService(getApplicationContext().WINDOW_SERVICE);
        int rotation = window.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result = (info.orientation + degrees) % 360;
        camera.setDisplayOrientation(result);
        cameraParameters.setRotation(90);
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

        // Wait before taking the photo
        SystemClock.sleep(Integer.valueOf(Aware.getSetting(getApplicationContext(), Settings.PLUGIN_MOODTRACKER_WAIT)));

        // Get the last app
        Cursor lastApp = getApplicationContext().getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, new String[]{"package_name"}, null, null, "timestamp DESC LIMIT 1");
        if ((lastApp != null) && (lastApp.moveToFirst())) {
            // Check that app on front haven't changed
            if (lastApp.getString(0).equals(intent.getExtras().getString("AppName"))) {
                if (prepareCamera()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
                        }
                    }, 1000);

                }
            } else {
                Log.d(Plugin.TAG, "App on front changed. Aborting");
            }
        }
        // Close cursor
        if ((lastApp != null) && !lastApp.isClosed()) {
            lastApp.close();
        }
    }

    int getFrontCameraId() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i=0; i<Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
        }
        return -1;
    }
}
