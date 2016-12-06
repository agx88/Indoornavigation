package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.EveSrl.Indoornavigation.R;
import com.beyondar.android.world.BeyondarObject;

import java.util.HashMap;


// Questa classe verrà utilizzata per gestire tutti i Marker.
public class MarkerPositioner
        extends FrameLayout
        implements View.OnClickListener {

    private Context mContext;
    private HashMap<String, ImageView> listMarker;
    private HashMap<String, android.graphics.PointF> listCoordinate;
    private HashMap<String, Integer> listImageId;

    private float trickFactor = 10;

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
        listImageId = new HashMap<>();

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

        marker.setLayoutParams(lp);

        // It sets the drawable for the marker.
        marker.setImageResource(R.drawable.map_marker_outside_azure);

        // If tag not already exists then it creates a new one....
        if (listMarker.get(tag) == null) {
            // Store the coordinate (in pixel).
            listCoordinate.put(tag, new PointF(x, y));
            // It adds the marker to the list.
            listMarker.put(tag, marker);

            listImageId.put(tag, 0);

            // Some information related to the marker.
            marker.setTag(tag);
            marker.setOnClickListener(this);

            // It calculates the meter according to the meter-pixel ratio.
            meterX = x / meterPixelRatioX;
            meterY = y / meterPixelRatioY;

            // It transforms the coordinate in lat-lon format.
            // "* 10" is a trick to reduce marker size. (see also ARFragment.java: mRadarPlugin.setMaxDistance(2d * 10);)
            // "/1000" transforms meter in kilometer;
            // "/1,85" transforms kilometer in nautical miles.
            // "/60" transforms latitude from sexagesimal form to decimal form.
            lat = (meterX / (1850 * 60)) * trickFactor;
            // "* 0.54" is a conversion factor from kilometer to longitude.
            // "/60" transforms latitude from sexagesimal form to decimal form.
            lon = (meterY * 0.54 / (60 * 1000)) * trickFactor;

            if(!tag.equals("User")) {
                // If it doesn't already exist, it creates a new AR world.
                if (CustomWorldHelper.getARWorld() == null)
                    CustomWorldHelper.newWorld(mContext);

                // It adds Marker to the AR world.
                CustomWorldHelper.addObject(R.drawable.map_marker_outside_azure, lat, lon, tag, null);
            }
            else if (tag.equals("User")){
                // It sets the drawable for the user's marker.
                marker.setImageResource(R.drawable.flag);
                // It updates user's location.
                CustomWorldHelper.setLocation(lat, lon);
            }
        }
        // ....otherwise, update marker location.
        else
            listCoordinate.get(tag).set(x, y);

        // It places the marker on the map.
        setMarkerPosition(x, y, tag);

        this.addView(marker);
    }

    public void setMarkerPosition(float x, float y, String tag) {
        ImageView marker = listMarker.get(tag);
        LayoutParams lp = (LayoutParams) marker.getLayoutParams();

        listCoordinate.get(tag).set(x, y);

        marker.setX(x * sx + tx - lp.width/2);
        marker.setY(y * sy + ty - lp.height);

        if (tag.equals("User")){
            // It calculates the meter according to the meter-pixel ratio.
            float meterX = x / meterPixelRatioX;
            float meterY = y / meterPixelRatioY;

            // It transforms the coordinate in lat-lon format.
            // "* 10" is a trick to reduce marker size. (see also ARFragment.java: mRadarPlugin.setMaxDistance(2d * 10);)
            // "/1000" transforms meter in kilometer;
            // "/1,85" transforms kilometer in nautical miles.
            // "/60" transforms latitude from sexagesimal form to decimal form.
            double lat = (meterX / (1850 * 60)) * trickFactor;
            // "* 0.54" is a conversion factor from kilometer to longitude.
            // "/60" transforms latitude from sexagesimal form to decimal form.
            double lon = (meterY * 0.54 / (60 * 1000)) * trickFactor;
            // It updates user's location.
            CustomWorldHelper.setLocation(lat, lon);
        }
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
    }


    public void updateScaleFactor(float sx, float sy){
        this.sx = sx;
        this.sy = sy;
    }


    public void updateRatio(float ratioX, float ratioY){
        meterPixelRatioX = ratioX;
        meterPixelRatioY = ratioY;
    }

    public void updateUserLocation(float ux, float uy){
        try {
            setMarkerPosition(ux, uy, "User");
        } catch(Exception ex){
            addMarker(ux, uy, "User");
        }
    }

    public Point fromMeterToPixel(Point m){
        Point p = new Point();

        p.setX(m.getX() * meterPixelRatioX);
        p.setY(m.getY() * meterPixelRatioY);

        return p;
    }

    public Point fromPixelToMeter(Point p){
        Point m = new Point();

        m.setX(p.getX() / meterPixelRatioX);
        m.setY(p.getY() / meterPixelRatioY);

        return m;
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(mContext, view.getTag().toString(), Toast.LENGTH_SHORT).show();
        double d = 0.0d;
        ImageView selectedMarker = (ImageView) view;

        for (BeyondarObject obL: CustomWorldHelper.getARWorld().getBeyondarObjectLists().get(0)
             ) {
            d = obL.getDistanceFromUser() / trickFactor;
            if(view.getTag().equals(obL.getName())) {
                Toast.makeText(mContext, obL.getName() + "   d:" + String.format("%.2f", d) + "m", Toast.LENGTH_SHORT).show();


                // Clinking on a Marker, it changes its image cycles through beacons drawable resources.
                if(listImageId.get(view.getTag().toString()) == 0){
                    listImageId.put(view.getTag().toString(), 1);
                    selectedMarker.setImageResource(R.drawable.beacon_candy);
                } else if(listImageId.get(view.getTag().toString()) == 1){
                    listImageId.put(view.getTag().toString(), 2);
                    selectedMarker.setImageResource(R.drawable.beacon_beetrot);
                } else if(listImageId.get(view.getTag().toString()) == 2){
                    listImageId.put(view.getTag().toString(), 3);
                    selectedMarker.setImageResource(R.drawable.beacon_lemon);
                } else if(listImageId.get(view.getTag().toString()) == 3){
                    listImageId.put(view.getTag().toString(), 0);
                    selectedMarker.setImageResource(R.drawable.map_marker_outside_azure);
                }

            }

        }
    }
}
