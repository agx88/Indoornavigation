package com.EveSrl.Indoornavigation.fragments;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.EveSrl.Indoornavigation.MainActivity;
import com.EveSrl.Indoornavigation.R;
import com.EveSrl.Indoornavigation.adapters.BeaconListAdapter;
import com.EveSrl.Indoornavigation.utils.MarkerPositioner;
import com.EveSrl.Indoornavigation.utils.Point;
import com.EveSrl.Indoornavigation.utils.Trilateration2D;
import com.EveSrl.Indoornavigation.utils.TrilaterationService;
import com.EveSrl.Indoornavigation.utils.ZoomableImageView;
import com.estimote.sdk.Utils;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment
        extends Fragment
        implements TrilaterationService.Callbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ZoomableImageView zIView;
   //Punto calcolato tramite trilaterazione
    private Point target;
    private BeaconListAdapter adapter;

    private Intent serviceIntent;
    private TrilaterationService myService;
    // Inflate the layout for this fragment
    private View view;



    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Mantiene il fragment "vivo" durante il cambio di orientamento
        setRetainInstance(true);

        serviceIntent = new Intent(getContext(), TrilaterationService.class);
        adapter = ((MainActivity) getActivity()).getBeaconListAdapter();
        startTrilaterationService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
            zIView = (ZoomableImageView) view.findViewById(R.id.piantina);
        } catch (InflateException ie) {
            // Just return the view as it is.
        }

        return view;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //Toast.makeText(getActivity(), "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            TrilaterationService.LocalBinder binder = (TrilaterationService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(MapFragment.this); //Activity register in the service as client for callabcks!
            myService.setAdapter(adapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //Toast.makeText(MapFragment.this.getActivity(), "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void sendLocation(float x, float y) {
        zIView.updateUserLocation(x, y);
    }


    public void startTrilaterationService(){
        getContext().startService(serviceIntent); // Starting the service
        getContext().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); // Binding to the service!
    }

    public void stopTrilaterationService(){
        getContext().unbindService(mConnection);
        getContext().stopService(serviceIntent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTrilaterationService();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTrilaterationService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTrilaterationService();
    }
}
