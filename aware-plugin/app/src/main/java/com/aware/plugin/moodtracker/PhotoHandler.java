package com.aware.plugin.moodtracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Camera;

import android.graphics.BitmapFactory;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Face;

import android.util.Log;
import android.util.SparseArray;

public class PhotoHandler implements Camera.PictureCallback {

    private final Context context;
    private Bitmap photoFrameAsBmb = null;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        photoFrameAsBmb = BitmapFactory.decodeByteArray(
                data, 0, data.length);

        Frame frame = new Frame.Builder()
                .setBitmap(photoFrameAsBmb)
                .build();

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
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
        Log.w(Plugin.TAG, Float.toString(faces.get(0).getIsSmilingProbability()));

        detector.release();
    }
}
