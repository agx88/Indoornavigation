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

import java.util.HashMap;


public class TrilaterationService extends Service {
    private HashMap<String, Point> coordinateBeacons;
    private BeaconListAdapter adapter;


    NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;
    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    Handler handler = new Handler();
    // TODO: Da testare!
    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            final int BEACON_NEAREST = 0;
            final int BEACON_NEAR = 1;
            final int BEACON_FAR = 2;
            Trilateration2D trilateration;

            Point result = new Point();

            String tag_beacon_nearest;
            String tag_beacon_near;
            String tag_beacon_far;

            Point beacon_nearest;
            Point beacon_near;
            Point beacon_far;

            if(activity != null){
                if (adapter.isReady()) {
                    tag_beacon_nearest = adapter.getItem(BEACON_NEAREST).getMajor() + ":" + adapter.getItem(BEACON_NEAREST).getMinor();
                    tag_beacon_near = adapter.getItem(BEACON_NEAR).getMajor() + ":" + adapter.getItem(BEACON_NEAR).getMinor();
                    tag_beacon_far = adapter.getItem(BEACON_FAR).getMajor() + ":" + adapter.getItem(BEACON_FAR).getMinor();

                    beacon_nearest = coordinateBeacons.get(tag_beacon_nearest);
                    beacon_near = coordinateBeacons.get(tag_beacon_near);
                    beacon_far = coordinateBeacons.get(tag_beacon_far);

                    trilateration = new Trilateration2D();
                    trilateration.initialize();
                    //Impostazione delle coordinate
                    trilateration.setA(beacon_nearest.getX(), beacon_nearest.getY());
                    trilateration.setB(beacon_near.getX(), beacon_near.getY());
                    trilateration.setC(beacon_far.getX(), beacon_far.getY());

                    trilateration.setR1(Utils.computeAccuracy(adapter.getItem(BEACON_NEAREST)));
                    trilateration.setR2(Utils.computeAccuracy(adapter.getItem(BEACON_NEAR)));
                    trilateration.setR3(Utils.computeAccuracy(adapter.getItem(BEACON_FAR)));

                    result = trilateration.getPoint();

                    activity.updateLocation(result);
                }


                // Per fare delle prove, ma senza beacons.
                //result.setX(Math.random() * 8.0f - 2.0f);
                //result.setY(Math.random() * 15.0f - 3.0f);

                //activity.updateLocation(result);
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Do what you need in onStartCommand when service has been started

        //handler.postDelayed(serviceRunnable, 0);
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

    public void setAdapter(BeaconListAdapter adapter) {
        this.adapter = adapter;
    }

    public void setCoordinateBeacons(HashMap<String, Point> coordinateBeacons) {
        this.coordinateBeacons = coordinateBeacons;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // Here Activity register to the service as Callbacks client
    public void registerClient(MapFragment activity){
        this.activity = (Callbacks)activity;
    }

    //callbacks interface for communication with service clients!
    public interface Callbacks{
        void updateLocation(Point result);
    }

    public class LocalBinder extends Binder {
        public TrilaterationService getServiceInstance(){
            return TrilaterationService.this;
        }
    }
}
