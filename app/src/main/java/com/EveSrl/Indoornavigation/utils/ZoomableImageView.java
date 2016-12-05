package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.EveSrl.Indoornavigation.R;


// This class allow to zoom-in and -out the ImageView.
public class ZoomableImageView extends ImageView {
    Matrix matrix;
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    int mode = NONE;

    // Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 0.5f; //1f;
    float maxScale = 3f;
    float[] m;
    int viewWidth, viewHeight;

    static final int CLICK = 3;

    float saveScale = 1f;

    protected float origWidth, origHeight;

    int oldMeasuredWidth, oldMeasuredHeight;

    ScaleGestureDetector mScaleDetector;

    Context context;

    // MIO-------------------------------------------------
    private MarkerPositioner drawSpace;

    float paddingX = 30;
    float paddingY = 60;
    // #MIO------------------------------------------------

    public ZoomableImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);

        this.context = context;

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        matrix = new Matrix();

        m = new float[9];

        setImageMatrix(matrix);

        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mScaleDetector.onTouchEvent(event);

                PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        last.set(curr);

                        start.set(last);

                        mode = DRAG;

                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (mode == DRAG) {

                            float deltaX = curr.x - last.x;

                            float deltaY = curr.y - last.y;

                            float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale + paddingX*2);

                            float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale + paddingY*2);

                            matrix.postTranslate(fixTransX, fixTransY);

                            fixTrans();

                            last.set(curr.x, curr.y);

                        }

                        break;

                    case MotionEvent.ACTION_UP:

                        mode = NONE;

                        int xDiff = (int) Math.abs(curr.x - start.x);

                        int yDiff = (int) Math.abs(curr.y - start.y);

                        if (xDiff < CLICK && yDiff < CLICK)

                            performClick();

                        break;

                    case MotionEvent.ACTION_POINTER_UP:

                        mode = NONE;

                        break;

                }

                setImageMatrix(matrix);

                // MIO------------------------------
                if(drawSpace == null)
                    setMarkerPositioner();

                // Mettendo l'update qui, i marker non "traballano" più.
                updateDrawSpace();
                // #MIO---------------------------------------------

                invalidate();

                return true; // indicate event was handled

            }

        });
    }

    public void setMaxZoom(float x) {

        maxScale = x;

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            mode = ZOOM;

            return true;

        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float mScaleFactor = detector.getScaleFactor();

            float origScale = saveScale;

            saveScale *= mScaleFactor;

            if (saveScale > maxScale) {

                saveScale = maxScale;

                mScaleFactor = maxScale / origScale;

            } else if (saveScale < minScale) {

                saveScale = minScale;

                mScaleFactor = minScale / origScale;

            }

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight)

                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);

            else

                matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());

            fixTrans();

            return true;

        }

    }

    void fixTrans() {

        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];  // mode==DRAG -> deltaX

        float transY = m[Matrix.MTRANS_Y];  // mode==DRAG -> deltaY

        float pX = paddingX;
        float pY = paddingY;

        if(mode == ZOOM){
            pX = pY = 0;
        }

        float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale, pX);

        float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale, pY);

        if (fixTransX != 0 || fixTransY != 0)

            matrix.postTranslate(fixTransX, fixTransY);

    }



    float getFixTrans(float trans, float viewSize, float contentSize, float padding) {

        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            //minTrans = 0;

            // MIO-------------------------------
            minTrans = 0 - padding;
            // MIO-------------------------------

            //maxTrans = viewSize - contentSize;

            // MIO-------------------------------
            maxTrans = viewSize - contentSize + padding;
            // MIO-------------------------------

        } else {
            //minTrans = viewSize - contentSize;
            // MIO-------------------------------
            minTrans = viewSize - contentSize - padding;
            // MIO-------------------------------

            //maxTrans = 0;
            // MIO-------------------------------
            maxTrans = 0 + padding;
            // MIO-------------------------------

        }

        //Log.d("Trans", "maxTrans:" + maxTrans + "  minTrans:" + minTrans + "  trans:" + trans);

        if (trans < minTrans)

            return -trans + minTrans;

        if (trans > maxTrans)

            return -trans + maxTrans;

        return 0;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        // If content is bigger than the View, then no movement are allowed.
        if (contentSize <= viewSize) {

            return 0;

        }

        return delta;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);

        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        //
        // Rescales image on rotation
        //
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight

                || viewWidth == 0 || viewHeight == 0)

            return;

        oldMeasuredHeight = viewHeight;

        oldMeasuredWidth = viewWidth;

        if (saveScale == 1) {

            //Fit to screen.

            float scale;

            Drawable drawable = getDrawable();

            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)

                return;

            int bmWidth = drawable.getIntrinsicWidth();

            int bmHeight = drawable.getIntrinsicHeight();

            //Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

            float scaleX = (float) (viewWidth) / (float) (bmWidth);

            float scaleY = (float) (viewHeight) / (float) (bmHeight);

            scale = Math.min(scaleX, scaleY);

            matrix.setScale(scale, scale);

            // Center the image

            float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);

            float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);

            redundantYSpace /= (float) 2;

            redundantXSpace /= (float) 2;

            matrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;

            origHeight = viewHeight - 2 * redundantYSpace;

            setImageMatrix(matrix);

            float origScale = saveScale;
            saveScale = 0.85f;
            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
                matrix.postScale(saveScale/origScale, saveScale/origScale, viewWidth / 2, viewHeight / 2);
                setImageMatrix(matrix);
            }
            else
                saveScale = origScale;

            // MIO------------------------------
            if(drawSpace == null)
                setMarkerPositioner();

            updateDrawSpace();
            // #MIO-----------------------------
        }

        fixTrans();
    }

    // MIO------------------------------------------------
    public void setMarkerPositioner(){
        drawSpace = (MarkerPositioner) ((View) this.getParent()).findViewById(R.id.overlay);
        drawSpace.setContext(context);

        // Supponiamo che il rettangolo rappresenti una stanza larga 3m e lunga 10m;
        drawSpace.updateRatio(oldMeasuredWidth / (3 * saveScale), oldMeasuredHeight / (10 * saveScale));

        drawSpace.addMarker(0, 0, "Prova1");
        drawSpace.addMarker(110, 0, "Prova2");
        drawSpace.addMarker(0, 110, "Prova3");
        drawSpace.addMarker(110, 110, "Prova4");
        drawSpace.addMarker(200, 180, "Prova5");
    }

    public MarkerPositioner getMarkerPositioner(){
        return drawSpace;
    }
    public void setMarkerPositioner(MarkerPositioner markerPositioner){ drawSpace = markerPositioner; }

    private void updateDrawSpace() {
        float[] mm = new float[9];
        matrix.getValues(mm);

        if (drawSpace != null) {
            drawSpace.updateScaleFactor(mm[Matrix.MSCALE_X], mm[Matrix.MSCALE_Y]);

            drawSpace.updateTranslation(mm[Matrix.MTRANS_X], mm[Matrix.MTRANS_Y]);
            drawSpace.updateAllMarkerPosition();

            // TODO: Cancellare quando le prove saranno finite.
            drawSpace.updateUserLocation(400, 250);
        }
    }


    public void updateUserLocation(float x, float y){
        if (drawSpace != null){
            drawSpace.updateUserLocation(x, y);
        }

    }
    // #MIO-----------------------------------------------

}