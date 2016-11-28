package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
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
        LayoutParams lp = new LayoutParams(80, 80);
        ImageView marker = new ImageView(mContext);

        marker.setImageResource(R.drawable.map_marker_outside_azure);

        lp.leftMargin = x;
        lp.topMargin = y;

        marker.setOnClickListener(this);
        marker.setLayoutParams(lp);

        listMarker.put(tag, marker);

        this.addView(marker);
    }

    public void setMarkerPosition(int x, int y, String tag){
        ImageView marker = listMarker.get(tag);
        LayoutParams lp = (LayoutParams) marker.getLayoutParams();

        lp.setMargins(x - lp.width, y - lp.height, 0, 0);
        marker.setLayoutParams(lp);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mContext, "BANANANANANANANA", Toast.LENGTH_SHORT).show();
    }
}
