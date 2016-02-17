package com.aware.plugin.moodtracker;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;

import com.aware.Aware;

import java.io.IOException;

public class FacePhoto extends Service {
    private Camera camera;
    private Camera.Parameters cameraParameters;

    // Warmup time for camera. Short warmup may cause e.g. dark photos
    private final int previewTime = 1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Plugin.DEBUG) Log.d(Plugin.TAG, "Created camera service");
    }

    /**
     * Prepares camera to take picture. NOTE that it doesn't steal the camera if it's reserved!
     *
     * @return Boolean success (true/fail)
     */
    private Boolean prepareCamera() {
        // Get front camera
        int cameraId = CommonMethods.getFrontCameraId();
        if (cameraId != -1) {
            try {
                camera = Camera.open(cameraId);
            } catch (Exception e) {
                if (Plugin.DEBUG) Log.d(Plugin.TAG, "Camera reserved, aborting");
                return false;
            }
            try {
                // Hack to make preview-less camera
                camera.setPreviewTexture(new SurfaceTexture(0));
                cameraParameters = camera.getParameters();
                camera.setParameters(cameraParameters);
                camera.startPreview();
            } catch (IOException e) {
                if (Plugin.DEBUG) Log.d(Plugin.TAG, "Cannot make preview, aborting");
                return false;
            }
        } else {
            Log.e(Plugin.TAG, "No front facing camera");
        }
        return true;
    }

    /**
     * Called on start. Basically prepares the camera, takes photo and launches pictureHandler.
     * @param intent
     * @param startID
     */
    @Override
    public void onStart(Intent intent, int startID) {
        super.onStart(intent, startID);
        if (Plugin.DEBUG) Log.d(Plugin.TAG, "Connected camera service");

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Wait before taking the photo.
        int waitTime = Integer.valueOf(Aware.getSetting(getApplicationContext(),
                Settings.PLUGIN_MOODTRACKER_WAIT));
        SystemClock.sleep(waitTime);

        // Get last app
        String lastApp = CommonMethods.getLastApp(getApplicationContext());

        if (lastApp != null) {
            // Check that app on front haven't changed
            if (intent == null) return;
            if (lastApp.equals(intent.getExtras().getString("AppName"))) {
                // Prepare camera
                if (prepareCamera()) {
                    final Intent lastIntent = intent;
                    // Wait a bit to warm up the camera
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                camera.takePicture(null,
                                        null,
                                        new PhotoHandler(getApplicationContext(), lastIntent));
                                } catch (Exception e) {
                                    if (camera != null) {
                                        camera.stopPreview();
                                        camera.release();
                                    }
                                }
                            }
                    }, previewTime);

                }
            } else {
                if (Plugin.DEBUG) Log.d(Plugin.TAG, "App on front changed. Aborting");
            }
        } else {
            Log.e(Plugin.TAG, "Unable to fetch last app");
        }
    }
}
