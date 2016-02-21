package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aware.Aware;

import java.io.IOException;

public class CameraActivity extends Activity {
    private Camera mCamera = null;
    private CameraView mCameraView = null;

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void closeActivity() {
        finish();
        Toast.makeText(getApplicationContext(),
                "Thank you!",
                Toast.LENGTH_SHORT).show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Toast.makeText(getApplicationContext(),
                "Submitting your mood rate",
                Toast.LENGTH_SHORT).show();

        try{
            releaseCameraAndPreview();
            mCamera = Camera.open(CommonMethods.getFrontCameraId());
        } catch (Exception e){
            if (Plugin.DEBUG) Log.d(Plugin.TAG, "Failed to get camera: " + e.getMessage());
        }
        if (mCamera == null) {
            SystemClock.sleep(500);
            if (Plugin.DEBUG) Log.d(Plugin.TAG, "Retrying");
            try{
                releaseCameraAndPreview();
                mCamera = Camera.open(CommonMethods.getFrontCameraId());
            } catch (Exception e2) {
                if (Plugin.DEBUG) Log.d(Plugin.TAG, "Also 2nd try failed");
                closeActivity();
            }
        }

        if(mCamera != null) {
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            if (!Aware.getSetting(getApplicationContext(), Settings.STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW).equals("1")) {
                try {
                    mCamera.setPreviewTexture(new SurfaceTexture(0));
                    mCamera.startPreview();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent esmIntent = new Intent(getApplicationContext(), PhotoHandler.class);
                            // Put AppName as extra to intent
                            esmIntent.putExtra("AppName", "ESM_FOLLOWUP");
                            try {
                                mCamera.takePicture(null,
                                        null,
                                        new PhotoHandler(getApplicationContext(), esmIntent));
                            } catch (Exception e) {
                                releaseCameraAndPreview();
                            }
                            closeActivity();
                        }
                    }, 5000);
                } catch (IOException e) {
                    if (Plugin.DEBUG) Log.d(Plugin.TAG, "Cannot make preview, aborting");
                    releaseCameraAndPreview();
                    closeActivity();
                }
            } else {
                mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
                FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
                camera_view.addView(mCameraView);//add the SurfaceView to the layout
            }
        }

        //btn to close the application
        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });
    }
}
