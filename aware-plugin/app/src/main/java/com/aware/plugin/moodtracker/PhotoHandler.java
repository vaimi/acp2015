package com.aware.plugin.moodtracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;

import android.graphics.BitmapFactory;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Face;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoHandler implements Camera.PictureCallback {

    private final Context context;
    private Bitmap photoFrameAsBmb = null;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(Plugin.TAG, "Saving a bitmap to file");

        Bitmap bitmap = null;

        if (data != null) {
            Bitmap orignalImage = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = rotate(orignalImage, 270);

            if (bitmap != null) {

                File file = new File(Environment.getExternalStorageDirectory() + "/dirr");
                if (!file.isDirectory()) {
                    file.mkdir();
                }

                file = new File(Environment.getExternalStorageDirectory() + "/dirr", System.currentTimeMillis() + ".jpg");


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
        photoFrameAsBmb = BitmapFactory.decodeByteArray(
                data, 0, data.length);

        Frame frame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(true)
                .build();


        SparseArray<Face> faces = detector.detect(frame);

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
        //Log.w(Plugin.TAG, Float.toString(faces.get(0).getIsSmilingProbability()));
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            Log.w(Plugin.TAG, Float.toString(face.getIsSmilingProbability()));
        }
        detector.release();
    }
}


