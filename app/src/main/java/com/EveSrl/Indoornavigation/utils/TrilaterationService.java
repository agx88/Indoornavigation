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
import com.estimote.sdk.Utils;


public class TrilaterationService extends Service {
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

                trilateration = new Trilateration2D();
                trilateration.initialize();
                //Impostazione delle coordinate
                trilateration.setA(1,1);
                trilateration.setB(0,2);
                trilateration.setC(2,3);

                trilateration.setR1(Utils.computeAccuracy(adapter.getItem(0)));
                trilateration.setR2(Utils.computeAccuracy(adapter.getItem(1)));
                trilateration.setR3(Utils.computeAccuracy(adapter.getItem(2)));

                result = trilateration.getPoint();

                //activity.sendLocation((float) result.getX(), (float) result.getY()); //Update Activity (client) by the implemented callback
            }

            activity.sendLocation(110, 110);

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
