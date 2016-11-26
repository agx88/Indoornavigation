package com.EveSrl.Indoornavigation.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.EveSrl.Indoornavigation.R;


// Questa classe verrà utilizzata
public class MarkerPositioner extends FrameLayout {

    private Context mContext;

    public MarkerPositioner(Context context) {
        super(context);
        mContext = context;
    }

    public MarkerPositioner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkerPositioner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setContext(Context context){
        mContext = context;
    }

    public void setMarkerPosition(int x, int y){
        LayoutParams lp = new LayoutParams(40, 40);
        ImageView marker;

        //coordinate.leftMargin = x;
        //coordinate.topMargin = y;

        marker = new ImageView(mContext);
        marker.setImageResource(R.drawable.map_marker_outside_azure);


        lp.setMargins(x, y, 0, 0);

        this.addView(marker, lp);
    }
}
