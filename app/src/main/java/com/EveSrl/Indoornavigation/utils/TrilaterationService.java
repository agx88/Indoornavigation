package com.EveSrl.Indoornavigation.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.EveSrl.Indoornavigation.adapters.BeaconListAdapter;
import com.EveSrl.Indoornavigation.fragments.MapFragment;


public class TrilaterationService extends Service {
    float x = 0.0f;
    float y = 0.0f;

    NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;

    private BeaconListAdapter adapter;

    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    Handler handler = new Handler();

    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            x += 1f;
            y += 1f;
            activity.sendLocation(x, y); //Update Activity (client) by the implementd callback
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Do what you need in onStartCommand when service has been started
        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    try {
                        Thread.sleep(1000);
                        //REST OF CODE HERE//
                        if(activity != null)
                            handler.postDelayed(serviceRunnable, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        return START_NOT_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void prova(){
        handler.postDelayed(serviceRunnable, 0);
    }

    // Here Activity register to the service as Callbacks client
    public void registerClient(MapFragment activity){
        this.activity = (Callbacks)activity;
    }

    // Set beacon list adapter.
    public void setAdapter(BeaconListAdapter bla){
        adapter = bla;
    }

    //callbacks interface for communication with service clients!
    public interface Callbacks{
        void sendLocation(float x, float y);
    }

    public class LocalBinder extends Binder {
        public TrilaterationService getServiceInstance(){
            return TrilaterationService.this;
        }
    }
}
