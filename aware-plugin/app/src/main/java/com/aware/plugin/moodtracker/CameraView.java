package com.aware.plugin.moodtracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aware.Aware;

import java.io.IOException;

/**
 * Created by Mikko on 15.2.2016.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context c = null;

    public CameraView(Context context, Camera camera){
        super(context);
        c = context;

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            if (!Aware.getSetting(c, Settings.STATUS_PLUGIN_MOODTRACKER_ESM_PREVIEW).equals("true")) {
                mCamera.setPreviewTexture(new SurfaceTexture(0));
            } else {
                mCamera.setPreviewDisplay(surfaceHolder);
            }
            mCamera.startPreview();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent esmIntent = new Intent(c, PhotoHandler.class);
                    // Put AppName as extra to intent
                    esmIntent.putExtra("AppName", "ESM_FOLLOWUP");
                    mCamera.takePicture(null,
                            null,
                            new PhotoHandler(c, esmIntent));
                    ((CameraActivity) c).closeActivity();
                }
            }, 5000);
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if(mHolder.getSurface() == null)//check if the surface is ready to receive camera data
            return;

        try{
            mCamera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        /*if (mCamera != null) {
            mCamera.stopPreview();
            //mCamera.release();
            mCamera = null;
        }*/
    }
}