package com.aware.plugin.moodtracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;

import android.graphics.BitmapFactory;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Aware_Sensor;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Face;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoHandler implements Camera.PictureCallback {

    private final Context context;
    private final Intent intent;
    private Bitmap photoFrameAsBmb = null;

    public PhotoHandler(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    /**
     * Function to rotate bitmap images
     * @param bitmap image to be rotated
     * @param degree amount of degrees the image will be rotated
     * @return Bitmap rotated image
     */
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * Run after picture is taken. Function rotates the image and analyses it using Google Mobile
     * Vision API smile analysis. Happiness is saved to the database along with the app that
     * triggered the process.
     * @param data image data
     * @param camera camera used
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // Release the camera
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }

        Bitmap bitmap = null;

        if (data == null) return;

        Bitmap originalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        int angleToRotate = CommonMethods.getRotationAngle(context);
        // Solve image inverting problem
        angleToRotate = angleToRotate + 180;
        bitmap = rotate(originalBitmap, angleToRotate);

        //saveImage(bitmap);

        // Set Mobile vision API frame
        Frame frame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        // Set up detector
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(true)
                .build();

        // Get faces
        SparseArray<Face> faces = detector.detect(frame);
        detector.release();
        bitmap.recycle();
        originalBitmap.recycle();

        if (!detector.isOperational()) {
            Log.w(Plugin.TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Log.w(Plugin.TAG, "Low storage, cannot download library");
            }
        }
        saveHappinessData(faces);
    }

    /**
     * Function for saving happiness data to db. Although there should be only one face, function
     * loops through the whole array.
     * @param faces detected faces
     */
    private void saveHappinessData(SparseArray<Face> faces) {
        // Loop through faces (there should be only one)
        if (faces.size() != 0) {
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);
                newHappinessValue(face.getIsSmilingProbability());
                // For debug
                /*Cursor cursor = context.getContentResolver()
                        .query(Provider.Moodtracker_Data.CONTENT_URI,
                                new String[] { Provider.Moodtracker_Data.TIMESTAMP,
                                        Provider.Moodtracker_Data.HAPPINESS_VALUE },
                                null,
                                null,
                                null);
                if(cursor.moveToFirst()) { Toast.makeText(context,
                        cursor.getString(0) + " " + cursor.getString(1),
                        Toast.LENGTH_SHORT).show(); }
                cursor.close();*/
            }
        } else {
            newHappinessValue(Face.UNCOMPUTED_PROBABILITY);
        }
    }

    /**
     * Function to save new happiness line to db
     * @param happiness
     */
    private void newHappinessValue(float happiness) {
        ContentValues new_data = new ContentValues();
        new_data.put(Provider.Moodtracker_Data.DEVICE_ID,
                Aware.getSetting(context.getApplicationContext(), Aware_Preferences.DEVICE_ID));
        new_data.put(Provider.Moodtracker_Data.TIMESTAMP,
                System.currentTimeMillis());
        new_data.put(Provider.Moodtracker_Data.HAPPINESS_VALUE,
                happiness);
        new_data.put(Provider.Moodtracker_Data.TRIGGER,
                intent.getExtras().getString("AppName"));

        //Insert the data to the ContentProvider
        context.getContentResolver().insert(Provider.Moodtracker_Data.CONTENT_URI, new_data);
    }

    /**
     * Function to save bitmap to disk. Should not be used in production.
     * @param bitmap
     */
    private void saveImage(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory() + "/dir");
        if (!file.isDirectory()) {
            file.mkdir();
        }

        file = new File(Environment.getExternalStorageDirectory() + "/dir", System.currentTimeMillis() + ".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}




