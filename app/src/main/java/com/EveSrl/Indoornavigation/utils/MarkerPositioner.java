package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.graphics.*;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.EveSrl.Indoornavigation.R;

import java.util.HashMap;



// Questa classe verrà utilizzata per gestire tutti i Marker.
public class MarkerPositioner
        extends FrameLayout
        implements View.OnClickListener {

    private Context mContext;
    private HashMap<String, ImageView> listMarker;
    private HashMap<String, android.graphics.PointF> listCoordinate;


    private float tx;
    private float ty;
    private float sx;
    private float sy;
    private float mScale;

    public MarkerPositioner(Context context) {
        super(context);
        init(context);
    }


    public MarkerPositioner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarkerPositioner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        listMarker = new HashMap<>();
        listCoordinate = new HashMap<>();

        mScale = 1.0f;
        sx = 1.0f;
        sy = 1.0f;
        tx = 0.0f;
        ty = 0.0f;
    }


    public void setContext(Context context) {
        mContext = context;
    }

    public void addMarker(float x, float y, String tag) {
        LayoutParams lp = new LayoutParams(50, 50);
        ImageView marker = new ImageView(mContext);

        marker.setImageResource(R.drawable.map_marker_outside_azure);


        listCoordinate.put(tag, new PointF(x, y));

        marker.setX(x);
        marker.setY(y);


        marker.setTag(tag);
        marker.setOnClickListener(this);
        marker.setLayoutParams(lp);

        listMarker.put(tag, marker);

        this.addView(marker);
    }

    public void setMarkerPosition(float x, float y, String tag) {
        ImageView marker = listMarker.get(tag);
        //LayoutParams lp = (LayoutParams) marker.getLayoutParams();

        listCoordinate.get(tag).set(x, y);

        //lp.leftMargin = (int) (tx + x * sx);
        //lp.topMargin = (int) (ty + y * sy);

        marker.setX(x * sx + tx);
        marker.setY(y * sy + ty);

        //marker.setX((x + tx) * sx);
        //marker.setY((y + ty) * sy);

        //marker.setX(x + tx);
        //marker.setY(y + ty);

        Log.d("ZoomableImageView", "x:= " + Float.toString(x) + "  y:= " + Float.toString(y));

        //marker.setLayoutParams(lp);
    }


    public void updateAllMarkerPosition() {
        for (String key : listMarker.keySet()
                ) {
            setMarkerPosition(listCoordinate.get(key).x, listCoordinate.get(key).y, key);
        }
    }

    public void updateTranslation(float tx, float ty) {
        this.tx = tx;
        this.ty = ty;
        //this.tx = 0;
        //this.ty = 0;
    }


    public void updateScaleFactor(float sx, float sy){
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mContext, view.getTag().toString(), Toast.LENGTH_SHORT).show();
    }
}
