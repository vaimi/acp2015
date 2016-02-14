package com.aware.plugin.moodtracker;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Mikko on 13.2.2016.
 */
public class CommonMethods {
    /**
     * Get Rotation Angle
     *
     * @param mContext
     * @param cameraId
     *            probably front cam
     * @return angel to rotate
     */
    public static int getRotationAngle(Context mContext, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        WindowManager window = (WindowManager) mContext.getSystemService(mContext.getApplicationContext().WINDOW_SERVICE);
        int rotation = window.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 270;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 90;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //Log.d("AWARE", "Orientation: " + Integer.toString(rotation));
        //Log.d("AWARE", "Rotate: " + Integer.toString(result));
        return result;
    }
}
