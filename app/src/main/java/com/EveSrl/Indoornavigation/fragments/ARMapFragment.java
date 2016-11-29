package com.EveSrl.Indoornavigation.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import com.EveSrl.Indoornavigation.utils.CustomWorldHelper;
import com.EveSrl.Indoornavigation.R;

public class ARMapFragment extends Fragment implements OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMapWorldPlugin mGoogleMapPlugin;

    private World mWorld;
    private GeoObject user;

    // Inflate the layout fot this fragment.
    private View view;

    public ARMapFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            view = inflater.inflate(R.layout.map_google, container, false);
        } catch (InflateException ie) {
            // Just return the view as it is.
        }



        //((SupportMapFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        ((MapFragment) this.getActivity().getFragmentManager().findFragmentByTag("map")).getMapAsync(this);

        // We create the world and fill the world
        //mWorld = CustomWorldHelper.generateObjects(this);
        //mWorld = CustomWorldHelper.sampleWorld(this);
        mWorld = CustomWorldHelper.getARWorld();

        // Lets add the user position
        user = new GeoObject(CustomWorldHelper.getIndex());
        CustomWorldHelper.incIndex();
        user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
        user.setImageResource(R.drawable.flag);
        user.setName("User position");

        // Flag to check the presence of the user's object.
        boolean containUser = false;
        // This cycle is needed to check if the user's geoobject is already present.
        // This cycle is optimized!
        for (int i = 0; i < mWorld.getBeyondarObjectList(World.LIST_TYPE_DEFAULT).size() && !containUser; i++) {
            if (mWorld.getBeyondarObjectList(World.LIST_TYPE_DEFAULT).get(i).getName().equals("User position"))
                containUser = true;
        }

        // If it isn't then add it.
        if (!containUser)
            mWorld.addBeyondarObject(user);


        return view;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // To get the GeoObject that owns the marker we use the following
        // method:
        GeoObject geoObject = mGoogleMapPlugin.getGeoObjectOwner(marker);
        if (geoObject != null) {
            Toast.makeText(this.getContext(),
                    "Click on a marker owned by a GeoOject with the name: " + geoObject.getName(),
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mMap != null) {

            // Enabling MyLocatoin Layer of Google Map
            if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            googleMap.setMyLocationEnabled(true);

            // As we want to use GoogleMaps, we are going to create the plugin and
            // attach it to the World
            mGoogleMapPlugin = new GoogleMapWorldPlugin(this.getContext());
            // Then we need to set the map in to the GoogleMapPlugin
            mGoogleMapPlugin.setGoogleMap(mMap);
            // Now that we have the plugin created let's add it to our world.
            // NOTE: It is better to load the plugins before start adding object in to the world.
            mWorld.addPlugin(mGoogleMapPlugin);

            mMap.setOnMarkerClickListener(this);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGoogleMapPlugin.getLatLng(), 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

            Toast.makeText(this.getContext(), "Object in the World: " + mWorld.getBeyondarObjectList(World.LIST_TYPE_DEFAULT).size(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Once the user exits ARMapFragment, it has to remove user GeoObject.
        mWorld.remove(user);

        android.app.FragmentTransaction ft = this.getActivity().getFragmentManager().beginTransaction();

        ft.remove(this.getActivity().getFragmentManager().findFragmentByTag("map"));
        ft.commit();
    }
}
