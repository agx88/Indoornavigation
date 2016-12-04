package com.EveSrl.Indoornavigation.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.EveSrl.Indoornavigation.MainActivity;
import com.EveSrl.Indoornavigation.R;
import com.EveSrl.Indoornavigation.adapters.BeaconListAdapter;
import com.EveSrl.Indoornavigation.utils.MarkerPositioner;
import com.EveSrl.Indoornavigation.utils.Point;
import com.EveSrl.Indoornavigation.utils.Trilateration2D;
import com.EveSrl.Indoornavigation.utils.ZoomableImageView;
import com.estimote.sdk.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MarkerPositioner drawSpace;
    private ZoomableImageView zIView;
   //Punto calcolato tramite trilaterazione
    private Point target;

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
        } catch (InflateException ie) {
            // Just return the view as it is.
        }

        zIView = (ZoomableImageView) view.findViewById(R.id.piantina);
        //Mantiene il fragment "vivo" durante il cambio di orientamento
        setRetainInstance(true);

        // TODO: We have to test the TrilaterationTask.
        //TrilaterationTask triTask = new TrilaterationTask();
        //triTask.execute(((MainActivity) getActivity()).getBeaconListAdapter());

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public class TrilaterationTask extends AsyncTask {

        private Trilateration2D trilateration;
        private BeaconListAdapter adapter;

        @Override
        protected Point doInBackground(Object[] params) {
            adapter = (BeaconListAdapter) params[0];
            trilateration = new Trilateration2D();
            trilateration.initialize();
            //Impostazione delle coordinate
            trilateration.setA(1,1);
            trilateration.setB(0,2);
            trilateration.setC(2,3);
            trilateration.setR1(Utils.computeAccuracy(adapter.getItem(0)));
            trilateration.setR2(Utils.computeAccuracy(adapter.getItem(1)));
            trilateration.setR3(Utils.computeAccuracy(adapter.getItem(2)));
            return trilateration.getPoint();
        }

        protected void onPostExecute(Point result) {
           //TODO cambiare l'esecuzione della post execute in modo che imposti diretamente il marker
            target = result;

            zIView.updateUserLocation((float) result.getX(), (float) result.getY());
        }
    }
}
