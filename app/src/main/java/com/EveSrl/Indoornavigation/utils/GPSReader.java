package com.EveSrl.Indoornavigation.utils;

/**
 * Created by Cloud on 11/10/2016.
 */

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

//import com.google.android.gms.location.LocationListener;

public class GPSReader extends Service implements LocationListener {

    private Context mContext;

    private Location mLocation;
    private LocationManager mLocationManager;

    protected double latitude;
    protected double longitude;

    // Flag for GPS status
    protected boolean isGPSEnalbed = false;
    protected boolean isNetworkEnabled = false;
    protected boolean canGetLocation = false;

    public GPSReader(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnalbed = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting Network status
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // check Network and GPS status
        if (!isGPSEnalbed && !isNetworkEnabled) {
            // if no GPS system available, advice the user
            Toast.makeText(mContext, "Nessun sistema GPS è disponibile!", Toast.LENGTH_SHORT).show();
            showSettingsAlert();
        } else {
            // otherwise, "abilita" GPS manager..
            canGetLocation = true;
            // ..obtain the location..
            getLocation();
            // ..and record latitude and longitude.
            if(mLocation != null){
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
            }
        }
    }

    public Location getLocation() {
        try {
            // To get location, Network or GPS service is required.
            if(isNetworkEnabled) {
                mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else if(isGPSEnalbed){
                mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            else{
                //..otherwise, request the user to activate the GPS.
                showSettingsAlert();
            }
        }catch(SecurityException se){
            // In case of error, print the stack.
            se.printStackTrace();
        }

        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {    }

    public Double getLatitude(){
        return latitude;
    }

    public Double getLongitude(){
        return longitude;
    }


    /* If the GPS service is offline, it requires the user to turn on GPS service. */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    // Check if the GPS or the Network service is online.
    public boolean isCanGetLocation(){ return canGetLocation; }
    // It removes the last known position.
    public void stopUsingGPS(){
        try {
            if (mLocationManager != null) {
                mLocationManager.removeUpdates(this);
            }
        }catch (SecurityException se){
            se.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
