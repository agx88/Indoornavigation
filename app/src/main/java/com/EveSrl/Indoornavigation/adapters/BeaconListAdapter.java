package com.EveSrl.Indoornavigation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.EveSrl.Indoornavigation.R;
import com.EveSrl.Indoornavigation.fragments.MapFragment;
import com.EveSrl.Indoornavigation.utils.CustomWorldHelper;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.ViewHolder> {

    private ArrayList<Beacon> beacons;
    private LayoutInflater inflater;

    public BeaconListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.beacons = new ArrayList<>();
    }

    public void replaceWith(Collection<Beacon> newBeacons) {
        this.beacons.clear();
        this.beacons.addAll(newBeacons);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public Beacon getItem(int position) {
        return beacons.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.macTextView.setText(String.format("MAC: %s (%.2fm)", beacons.get(position).getMacAddress().toStandardString(), Utils.computeAccuracy(beacons.get(position))));
        holder.majorTextView.setText("Major: " + beacons.get(position).getMajor());
        holder.minorTextView.setText("Minor: " + beacons.get(position).getMinor());
        holder.measuredPowerTextView.setText("MPower: " + beacons.get(position).getMeasuredPower());
        holder.rssiTextView.setText("RSSI: " + beacons.get(position).getRssi());

        String tag = beacons.get(position).getMajor() + ":" + beacons.get(position).getMinor();
        if(tag.equals(MapFragment.beacon_lato_corto_alto) || tag.equals(MapFragment.beacon_lato_corto_basso)){
            holder.beaconImageView.setImageResource(R.drawable.beacon_lemon);

        } else if(tag.equals(MapFragment.beacon_lato_lungo_sinistro_alto) || tag.equals(MapFragment.beacon_lato_lungo_destro_basso)){
            holder.beaconImageView.setImageResource(R.drawable.beacon_beetrot);

        } else if(tag.equals(MapFragment.beacon_lato_lungo_sinistro_basso) || tag.equals(MapFragment.beacon_lato_lungo_destro_alto)){
            holder.beaconImageView.setImageResource(R.drawable.beacon_candy);
        }



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isReady(){
      if(getItemCount() >= 3) return true;
        return false;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView macTextView;
        final TextView majorTextView;
        final TextView minorTextView;
        final TextView measuredPowerTextView;
        final TextView rssiTextView;
        final ImageView beaconImageView;

        ViewHolder(View view) {
            super(view);
            macTextView = (TextView) view.findViewWithTag("mac");
            majorTextView = (TextView) view.findViewWithTag("major");
            minorTextView = (TextView) view.findViewWithTag("minor");
            measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
            rssiTextView = (TextView) view.findViewWithTag("rssi");
            beaconImageView = (ImageView) view.findViewWithTag("beacon_image");
        }
    }
}
