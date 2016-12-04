package com.EveSrl.Indoornavigation.fragments;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.location.LocationListener;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.World;

import java.util.ArrayList;

import com.EveSrl.Indoornavigation.utils.CustomWorldHelper;
import com.EveSrl.Indoornavigation.R;
import com.EveSrl.Indoornavigation.utils.GeoObjectExt;

public class ARFragment
        extends Fragment
        implements OnClickBeyondarObjectListener, LocationListener{

    protected BeyondarFragment mBeyondarFragment;
    protected World mWorld;

    protected RadarView mRadarView;
    protected RadarWorldPlugin mRadarPlugin;

    // Inflate the layout for this fragment.
    protected View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null)
                parent.removeView(view);
        }

        try {
            view = loadViewFromXML(inflater, container, savedInstanceState);
        } catch (InflateException ie) {
            // Just return the view as it is.
        }

        // We create the world and fill it
        //mWorld = CustomWorldHelper.sampleWorld(this, gpsReader.getLatitude(), gpsReader.getLongitude());

        //TODO: Usare .sampleWorld per fare un esempio. Usare .newWorld quando si passerà all'app "ufficiale".
        //mWorld = CustomWorldHelper.sampleWorld(this.getContext());

        // ARWorld may be created in MarkerPositioner Class.
        mWorld = CustomWorldHelper.getARWorld();
        if (mWorld == null)
            mWorld = CustomWorldHelper.newWorld(this.getContext());

        // Location can be set once the AR World was created.
        //CustomWorldHelper.setLocation(gpsReader.getLatitude(), gpsReader.getLongitude());

        mBeyondarFragment.setWorld(mWorld);
        mBeyondarFragment.showFPS(false);

        // set listener for the geoObjects
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);

        // Get the radar from the view.
        mRadarView = (RadarView) view.findViewById(R.id.radarView);

        // Create the Radar plugin.
        mRadarPlugin = new RadarWorldPlugin(this.getContext());
        // Set the radar view into our radar plugin.
        mRadarPlugin.setRadarView(mRadarView);
        // Set how far (in meters) we want to display in the view.
        mRadarPlugin.setMaxDistance(1.5d);

        /* This part will may be useful.

            // We can customize the color of the items
            mRadarPlugin.setListColor(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, Color.RED);
            // and also the size.
            mRadarPlugin.setListDotRadius(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, 3);
         */

        mWorld.addPlugin(mRadarPlugin);

        return view;
    }

    private View loadViewFromXML(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar, container, false);
        mBeyondarFragment = (BeyondarFragment) this.getActivity().getFragmentManager().findFragmentById(R.id.myFragmentSample);

        /*
        showMapButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("showMapButton", "Prova");
            }
        });
        */


        return view;
    }


    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        if (beyondarObjects.size() > 0) {
            /*Toast.makeText(this, "Clicked on: " + beyondarObjects.get(0).getName(),
                    Toast.LENGTH_LONG).show();
            */
            // When an object is touched, app opens an image.
            try {
                GeoObjectExt geo = (GeoObjectExt) beyondarObjects.get(0);
                Bundle bndl = geo.getInfo();

                if (bndl.containsKey("Imgs")) {
                    Intent intent = new Intent();
                    int size = bndl.getStringArrayList("Imgs").size();

                    intent.setAction(Intent.ACTION_VIEW);

                    for (int i = 0; i < size; i++){
                        intent.setDataAndType(Uri.parse("file://" + bndl.getStringArrayList("Imgs").get(i)), "image/*");
                    }
                    startActivity(intent);
                }
            } catch(Exception e){
                // Just for debugging
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        CustomWorldHelper.setLocation(0, 0);

        // Toast di cui ho bisogno per vedere se questo metodo funziona correttamente.
        Toast.makeText(this.getContext(), "La posizione dell'utente è cambiata!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "GPS restarted!", Toast.LENGTH_LONG).show();

    }
}
