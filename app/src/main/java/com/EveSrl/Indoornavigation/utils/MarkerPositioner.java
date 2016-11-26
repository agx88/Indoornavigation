package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.EveSrl.Indoornavigation.R;

import java.util.HashMap;



// Questa classe verrà utilizzata per gestire tutti i Marker.
public class MarkerPositioner extends FrameLayout {

    private Context mContext;
    private HashMap<String, ImageView> listMarker;


    public MarkerPositioner(Context context) {
        super(context);
        mContext = context;
        listMarker = new HashMap<>();
    }

    public MarkerPositioner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        listMarker = new HashMap<>();
    }

    public MarkerPositioner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        listMarker = new HashMap<>();
    }


    public void setContext(Context context){
        mContext = context;
    }

    public void addMarker(int x, int y, String tag){
        LayoutParams lp = new LayoutParams(40, 40);
        ImageView marker = new ImageView(mContext);

        marker.setImageResource(R.drawable.map_marker_outside_azure);

        lp.setMargins(x, y, 0, 0);

        listMarker.put(tag, marker);
    }

    public void setMarkerPosition(int x, int y, String tag){
        LayoutParams lp = new LayoutParams(40, 40);
        ImageView marker = listMarker.get(tag);

        lp.setMargins(x, y, 0, 0);
        marker.setLayoutParams(lp);
    }
}
