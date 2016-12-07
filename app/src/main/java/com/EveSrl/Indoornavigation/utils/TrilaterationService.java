package com.EveSrl.Indoornavigation.utils;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.EveSrl.Indoornavigation.adapters.BeaconListAdapter;
import com.EveSrl.Indoornavigation.fragments.MapFragment;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;


public class TrilaterationService extends Service {
    private final int BEACON_NEAREST = 0;
    private final int BEACON_NEAR = 1;
    private final int BEACON_FAR = 2;

    float x = 0.0f;
    float y = 0.0f;

    private Trilateration2D trilateration;
    private BeaconListAdapter adapter;

    NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;
    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    Handler handler = new Handler();

    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            x += 1f;
            y += 1f;

            if(adapter.isReady()) {
                Point result = null;


                Beacon beacon_nearest = adapter.getItem(BEACON_NEAREST);
                Beacon beacon_near = adapter.getItem(BEACON_NEAR);
                Beacon beacon_far = adapter.getItem(BEACON_FAR);



                trilateration = new Trilateration2D();
                trilateration.initialize();
                //Impostazione delle coordinate
                trilateration.setA(beacon_nearest.getMajor(), beacon_nearest.getMinor());
                trilateration.setB(beacon_near.getMajor(), beacon_near.getMinor());
                trilateration.setC(beacon_far.getMajor(), beacon_far.getMinor());

                trilateration.setR1(Utils.computeAccuracy(beacon_nearest));
                trilateration.setR2(Utils.computeAccuracy(beacon_near));
                trilateration.setR3(Utils.computeAccuracy(beacon_far));

                result = trilateration.getPoint();

                activity.sendLocation((float) result.getX(), (float) result.getY()); //Update Activity (client) by the implemented callback
            }

            //activity.sendLocation(0, 3.3f);

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Do what you need in onStartCommand when service has been started
        new Thread(new Runnable(){
            public void run() {
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
