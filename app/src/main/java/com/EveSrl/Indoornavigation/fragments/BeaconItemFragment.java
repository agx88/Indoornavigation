package com.EveSrl.Indoornavigation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.EveSrl.Indoornavigation.R;
import com.EveSrl.Indoornavigation.adapters.BeaconListAdapter;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BeaconItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private BeaconManager beaconManager;
    private BeaconListAdapter beaconListAdapter;

    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BeaconItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BeaconItemFragment newInstance(int columnCount) {
        BeaconItemFragment fragment = new BeaconItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        findBeacons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacon_list, container, false);

        //Mantiene il fragment "vivo" durante il cambio di orientamento
        setRetainInstance(true);

        // Set the beaconListAdapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            //TODO: check
            recyclerView.setAdapter(beaconListAdapter);
        }
        return view;
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

    @Override public void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override public void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(getActivity())){
            startScanning();
        }
    }

    @Override public void onStop() {
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);

        super.onStop();
    }

    private void findBeacons(){
        beaconListAdapter = new BeaconListAdapter(getActivity());
        beaconManager = new BeaconManager(getActivity());
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        //toolbar.setSubtitle("Found beacons: " + beacons.size());
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Found beacons: " + beacons.size());
                        beaconListAdapter.replaceWith(beacons);
                    }
                });
            }
        });
    }

    private void startScanning() {
        //toolbar.setSubtitle("Scanning...");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Scanning...");
        beaconListAdapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Beacon item);
    }

}
