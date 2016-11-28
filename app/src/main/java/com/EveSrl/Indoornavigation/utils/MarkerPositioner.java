package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.graphics.*;
import android.graphics.Point;
import android.util.AttributeSet;
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
    private HashMap<String, android.graphics.Point> listCoordinate;


    private int tx;
    private int ty;

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

    private void init(Context context){
        mContext = context;
        listMarker = new HashMap<>();
        listCoordinate = new HashMap<>();

        tx = 0;
        ty = 0;
    }


    public void setContext(Context context){
        mContext = context;
    }

    public void addMarker(int x, int y, String tag){
        LayoutParams lp = new LayoutParams(50, 50);
        ImageView marker = new ImageView(mContext);

        marker.setImageResource(R.drawable.map_marker_outside_azure);


        listCoordinate.put(tag, new Point(x, y));
        lp.leftMargin = tx + x - (lp.width / 2);
        lp.topMargin = ty + y - lp.height;

        marker.setTag(tag);
        marker.setOnClickListener(this);
        marker.setLayoutParams(lp);

        listMarker.put(tag, marker);

        this.addView(marker);
    }

    public void setMarkerPosition(int x, int y, String tag){
        ImageView marker = listMarker.get(tag);

        LayoutParams lp = (LayoutParams) marker.getLayoutParams();

        listCoordinate.get(tag).set(x, y);

        lp.leftMargin = tx + x - (lp.width / 2);
        lp.topMargin = ty + y - lp.height;

        marker.setLayoutParams(lp);
    }


    public void updateAllMarkerPosition(int x, int y){
        tx = x;
        ty = y;

        for (String key: listMarker.keySet()
             ) {
            setMarkerPosition(listCoordinate.get(key).x, listCoordinate.get(key).y, key);
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mContext, view.getTag().toString(), Toast.LENGTH_SHORT).show();
    }
}
