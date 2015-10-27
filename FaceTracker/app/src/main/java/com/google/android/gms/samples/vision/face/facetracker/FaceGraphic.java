/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private Paint mFaceCirclePaint;
    private Paint mLeftEyePaint;
    private Paint mRightEyePaint;
    private Paint mMouthPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        mFaceCirclePaint = new Paint();
        mFaceCirclePaint.setColor(selectedColor);

        mLeftEyePaint = new Paint();
        mLeftEyePaint.setColor(Color.WHITE);

        mRightEyePaint = new Paint();
        mRightEyePaint.setColor(Color.WHITE);

        mMouthPaint = new Paint();
        mMouthPaint.setColor(Color.WHITE);

    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        /*canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);
        */
        // Draws a bounding box around the face.
        /*float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);*/
        float yOffset = scaleY(face.getHeight() / 2.0f);
        canvas.drawCircle(x, y, yOffset, mFaceCirclePaint);

        float leftPosX = translateX(face.getPosition().x + face.getWidth() / 2);
        float leftPosY = translateY(face.getPosition().y + face.getHeight() / 2);
        RectF leftOval = new RectF(leftPosX - face.getWidth() / 4, leftPosY-50, leftPosX-20, leftPosY + (face.getIsLeftEyeOpenProbability()) * 50 - 50);
        canvas.drawOval(leftOval, mLeftEyePaint);
        RectF rightOval = new RectF(leftPosX+20, leftPosY-50, leftPosX + face.getWidth() / 4, leftPosY + (face.getIsRightEyeOpenProbability()) * 50 - 50);
        canvas.drawOval(rightOval, mRightEyePaint);

        RectF rectF = new RectF(leftPosX-80, leftPosY-50, leftPosX+80, leftPosY-100);
        canvas.drawOval(rectF, mMouthPaint);
        float radius = face.getHeight() / 3;
        final RectF oval = new RectF();
        oval.set(leftPosX - radius, leftPosY - radius + 50, leftPosX + radius, leftPosY + radius + 30);
        Path myPath = new Path();
        float startPoint= 80-face.getIsSmilingProbability()*80;
        float endPoint = 20+face.getIsSmilingProbability()*160;
        myPath.arcTo(oval, startPoint, endPoint, true);
        canvas.drawPath(myPath, mMouthPaint);
        //canvas.drawArc(rectF, 0, 45, true, mMouthPaint);
    }
}
