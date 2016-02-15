package com.aware.plugin.moodtracker;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import com.aware.providers.Applications_Provider;

/**
 * Created by Mikko on 13.2.2016.
 */
public class CommonMethods {
    /**
     * Get camera rotation
     *
     * @param context
     * @return Rotation angle
     */
    public static int getRotationAngle(Context context) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        WindowManager window = (WindowManager) context
                .getSystemService(context.getApplicationContext().WINDOW_SERVICE);
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
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360; // compensate the mirror
        return result;
    }

    /**
     * Gets last foreground app from AWARE application provider
     * @return String last app package name
     */
    public static String getLastApp(Context context) {
        // Get the app on front from application sensor
        Cursor cursor = context
                .getContentResolver()
                .query(Applications_Provider.Applications_Foreground.CONTENT_URI,
                        new String[] { Applications_Provider.Applications_Foreground.PACKAGE_NAME},
                        null,
                        null,
                        Applications_Provider.Applications_Foreground.TIMESTAMP + " DESC LIMIT 1");
        String response = null;
        if (cursor != null && cursor.moveToFirst()) {
            response = cursor.getString(0);
        }
        // close the cursor
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return response;
    }
}
