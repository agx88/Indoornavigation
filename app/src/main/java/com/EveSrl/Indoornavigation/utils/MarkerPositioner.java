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
import com.EveSrl.Indoornavigation.fragments.MapFragment;
import com.beyondar.android.opengl.texture.Texture;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;

import java.util.HashMap;
import java.util.Locale;


// Questa classe verrà utilizzata per gestire tutti i Marker.
public class MarkerPositioner
        extends FrameLayout
        implements View.OnClickListener {

    private Context mContext;
    private HashMap<String, ImageView> listMarker;
    private HashMap<String, android.graphics.PointF> listCoordinate;

    private float trickFactor = 10;

    private float tx;
    private float ty;
    private float sx;
    private float sy;


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

    }


    public void setContext(Context context) {
        mContext = context;
    }

    public void addMarker(float x, float y, String tag) {
            LayoutParams lp = new LayoutParams(50, 50); // Layout parameters of the marker.
            ImageView marker = new ImageView(mContext); // ImageView representing the marker on the map.

            double lat, lon;        // Coordinate in latitude-longitude format.

            // It sets the drawable for the marker.
            marker.setImageResource(R.drawable.map_marker_outside_azure);

            // If tag not already exists then it creates a new one....
            if (listMarker.get(tag) == null) {
                // Store the coordinate (in pixel).
                listCoordinate.put(tag, new PointF(x, y));
                // It adds the marker to the list.
                listMarker.put(tag, marker);

                // Some information related to the marker.
                marker.setTag(tag);
                marker.setOnClickListener(this);

                // It calculates the meter according to the meter-pixel ratio.
                Point meter = Point.fromPixelToMeter(new Point(x, y));

                // It transforms the coordinate in lat-lon format.
                // "* 10" is a trick to reduce marker size. (see also ARFragment.java: mRadarPlugin.setMaxDistance(2d * 10);)
                // "/1000" transforms meter in kilometer;
                // "/1,85" transforms kilometer in nautical miles.
                // "/60" transforms latitude from sexagesimal form to decimal form.
                lat = (meter.getX() / (1850 * 60)) * trickFactor;
                // "* 0.54" is a conversion factor from kilometer to longitude.
                // "/60" transforms latitude from sexagesimal form to decimal form.
                lon = (meter.getY() * 0.54 / (60 * 1000)) * trickFactor;

                // If it doesn't already exist, it creates a new AR world.
                if (CustomWorldHelper.getARWorld() == null)
                    CustomWorldHelper.newWorld(mContext);

                if(!tag.equals("User")) {
                    if(tag.equals(KnownBeacons.beacon_lato_corto_alto) || tag.equals(KnownBeacons.beacon_lato_corto_basso)){
                        marker.setImageResource(R.drawable.beacon_lemon);
                        // It adds Marker to the AR world.
                        CustomWorldHelper.addObject(CustomWorldHelper.BEACONS_LEMON, R.drawable.beacon_lemon, lat, lon, tag, null);

                    } else if(tag.equals(KnownBeacons.beacon_lato_lungo_sinistro_alto) || tag.equals(KnownBeacons.beacon_lato_lungo_destro_basso)){
                        marker.setImageResource(R.drawable.beacon_beetrot);
                        // It adds Marker to the AR world.
                        CustomWorldHelper.addObject(CustomWorldHelper.BEACONS_BEETROT, R.drawable.beacon_beetrot, lat, lon, tag, null);

                    } else if(tag.equals(KnownBeacons.beacon_lato_lungo_sinistro_basso) || tag.equals(KnownBeacons.beacon_lato_lungo_destro_alto)){
                        marker.setImageResource(R.drawable.beacon_candy);
                        // It adds Marker to the AR world.
                        CustomWorldHelper.addObject(CustomWorldHelper.BEACONS_CANDY, R.drawable.beacon_candy, lat, lon, tag, null);
                    }
                }
                else if (tag.equals("User")){

                    lp = new LayoutParams(65, 65);
                    // It sets the drawable for the user's marker.
                    marker.setImageResource(R.drawable.user_icon);
                    // It updates user's location.
                    CustomWorldHelper.setLocation(lat, lon);
                }
            }
            // ....otherwise, update marker location.
            else
                listCoordinate.get(tag).set(x, y);

            marker.setLayoutParams(lp);

            // It places the marker on the map.
            setMarkerPosition(x, y, tag);

            this.addView(marker);
    }

    public void setMarkerPosition(float x, float y, String tag) {
        ImageView marker = listMarker.get(tag);
        LayoutParams lp = (LayoutParams) marker.getLayoutParams();
        // It saves marker position on a list.
        listCoordinate.get(tag).set(x, y);
        // It calculates marker position based on scale and translate factors.
        marker.setX(x * sx + tx - lp.width/2);
        marker.setY(y * sy + ty - lp.height);

        if (tag.equals("User")){
            // It calculates the meter according to the meter-pixel ratio.
            Point meter = Point.fromPixelToMeter(new Point(x, y));
            // It transforms the coordinate in lat-lon format.
            // "* 10" is a trick to reduce marker size. (see also ARFragment.java: mRadarPlugin.setMaxDistance(2d * 10);)
            // "/1000" transforms meter in kilometer;
            // "/1,85" transforms kilometer in nautical miles.
            // "/60" transforms latitude from sexagesimal form to decimal form.
            double lat = (meter.getX() / (1850 * 60)) * trickFactor;
            // "* 0.54" is a conversion factor from kilometer to longitude.
            // "/60" transforms latitude from sexagesimal form to decimal form.
            double lon = (meter.getY() * 0.54 / (60 * 1000)) * trickFactor;
            // It updates user's location.
            CustomWorldHelper.setLocation(lat, lon);
        }
    }

    // Update the position of all the markers when the map resizing. Marker X and Y didn't change. Translation and scale factors changed.
    public void updateAllMarkerPosition() {
        for (String key : listMarker.keySet()
                ) {
            setMarkerPosition(listCoordinate.get(key).x, listCoordinate.get(key).y, key);
        }
    }

    // Update translation factors when the map resizing. Necessary to mantain X and Y relative to the MAP.
    public void updateTranslation(float tx, float ty) {
        this.tx = tx;
        this.ty = ty;
    }

    // Update scale factors when map resizing. Necessary to mantain X and Y relative to the MAP.
    public void updateScaleFactor(float sx, float sy){
        this.sx = sx;
        this.sy = sy;
    }

    // Update user's location with beacons provided location.
    public void updateUserLocation(float ux, float uy){
        addMarkerMeter(ux, uy, "User");
    }


    public void updateUserOrientation(float degree){
        ImageView user = listMarker.get("User");

        user.setRotation(degree - 90);
    }

    // This method adds a marker based on coordinates in meters.
    public void addMarkerMeter(float mx, float my, String tag){
        Point p = Point.fromMeterToPixel(new Point(mx, my));

        try {
            setMarkerPosition((float) p.getX(), (float) p.getY(), tag);
        } catch(Exception ex){
            addMarker((float) p.getX(), (float) p.getY(), tag);
        }
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(mContext, view.getTag().toString(), Toast.LENGTH_SHORT).show();
        double d;
        ImageView selectedMarker = (ImageView) view;

        // It searches the BeyondAR object corresponding to the clicked beacon.
        for (BeyondarObjectList bL:  CustomWorldHelper.getARWorld().getBeyondarObjectLists()
             ) {
            for (BeyondarObject obL : bL
                    ) {
                if (selectedMarker.getTag().equals(obL.getName())) {
                    // It gets the distance between the user and the selected beacons.
                    d = obL.getDistanceFromUser() / trickFactor;
                    Toast.makeText(mContext, obL.getName() + "   d:" + String.format(Locale.ITALY, "%.2f", d) + "m", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
