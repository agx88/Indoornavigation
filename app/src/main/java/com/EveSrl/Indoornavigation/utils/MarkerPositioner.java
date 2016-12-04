package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.graphics.*;
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

    private float meterPixelRatioX;
    private float meterPixelRatioY;

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

        sx = 1.0f;
        sy = 1.0f;
        tx = 0.0f;
        ty = 0.0f;
        meterPixelRatioX = 0.0f;
        meterPixelRatioY = 0.0f;
    }


    public void setContext(Context context) {
        mContext = context;
    }

    public void addMarker(float x, float y, String tag) {
        LayoutParams lp = new LayoutParams(50, 50); // Layout parameters of the marker.
        ImageView marker = new ImageView(mContext); // ImageView representing the marker on the map.

        double meterX, meterY;  // Coordinate in meter.
        double lat, lon;        // Coordinate in latitude-longitude format.

        // It sets the drawable for the marker.
        marker.setImageResource(R.drawable.map_marker_outside_azure);
        // Store the coordinate (in pixel).
        listCoordinate.put(tag, new PointF(x, y));
        // Some information related to the marker.
        marker.setTag(tag);
        marker.setOnClickListener(this);
        marker.setLayoutParams(lp);
        // It adds the marker to the list.
        listMarker.put(tag, marker);
        // It places the marker on the map.
        setMarkerPosition(x, y, tag);
        // It calculates the meter according to the meter-pixel ratio.
        meterX = x / meterPixelRatioX;
        meterY = y / meterPixelRatioY;

        // It transforms the coordinate in lat-lon format.
        lat = meterX / (1850 * 60);
        lon = meterY * 0.54 / (60 * 1000);

        // If it doesn't already exist, it creates a new AR world.
        if(CustomWorldHelper.getARWorld() == null)
            CustomWorldHelper.newWorld(mContext);

        // It adds Marker to the AR world.
        CustomWorldHelper.addObject(R.drawable.map_marker_outside_azure, lat, lon, tag, null);
        Log.d("MarkerPositioner", "meterX: " + meterX + "  lat:" + lat + "  lon:" + lon);

        this.addView(marker);
    }

    public void setMarkerPosition(float x, float y, String tag) {
        ImageView marker = listMarker.get(tag);
        LayoutParams lp = (LayoutParams) marker.getLayoutParams();

        listCoordinate.get(tag).set(x, y);

        //lp.leftMargin = (int) (tx + x * sx);
        //lp.topMargin = (int) (ty + y * sy);

        marker.setX(x * sx + tx - lp.width/2);
        marker.setY(y * sy + ty - lp.height);

        //marker.setX((x + tx) * sx);
        //marker.setY((y + ty) * sy);

        //marker.setX(x + tx);
        //marker.setY(y + ty);

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


    public void updateRatio(float ratioX, float ratioY){
        meterPixelRatioX = ratioX;
        meterPixelRatioY = ratioY;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mContext, view.getTag().toString(), Toast.LENGTH_SHORT).show();
    }
}
