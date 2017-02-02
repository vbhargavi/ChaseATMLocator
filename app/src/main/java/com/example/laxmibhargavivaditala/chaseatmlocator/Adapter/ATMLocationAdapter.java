package com.example.laxmibhargavivaditala.chaseatmlocator.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.laxmibhargavivaditala.chaseatmlocator.Modal.ChaseATMResponse;
import com.example.laxmibhargavivaditala.chaseatmlocator.R;

import java.util.ArrayList;

/**
 * Adapter used to display ATMs/Branches in RecyclerView.
 */

public class ATMLocationAdapter extends RecyclerView.Adapter<ATMLocationAdapter.ATMViewHolder> {
    private Context mContext;
    private ArrayList<ChaseATMResponse.Location> mLocations;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(ChaseATMResponse.Location location);
    }

    public ATMLocationAdapter(Context context, ArrayList<ChaseATMResponse.Location> locations, OnItemClickListener onItemClickListener) {
        mContext = context;
        mLocations = locations;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ATMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_atm_location, parent, false);
        ATMViewHolder viewHolder = new ATMViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ATMViewHolder holder, int position) {
        final ChaseATMResponse.Location location = mLocations.get(position);
        holder.update(location);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(location);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLocations != null ? mLocations.size() : 0;
    }

    /**
     * ViewHolder for each ATM/Branch row.
     */
    public static class ATMViewHolder extends RecyclerView.ViewHolder {
        private TextView mLocationTypeTxtView;
        private TextView mLocationDistanceTxtView;
        private TextView mLocationAddressTxtView;
        private TextView mLocationCityZipTxtView;

        public ATMViewHolder(View itemView) {
            super(itemView);
            mLocationTypeTxtView = (TextView) itemView.findViewById(R.id.location_type_txt);
            mLocationDistanceTxtView = (TextView) itemView.findViewById(R.id.location_distance_txt);
            mLocationAddressTxtView = (TextView) itemView.findViewById(R.id.location_address);
            mLocationCityZipTxtView = (TextView) itemView.findViewById(R.id.location_city_zip);
        }

        public void update(ChaseATMResponse.Location location) {
            mLocationTypeTxtView.setText(location.getLocType());
            mLocationAddressTxtView.setText(location.getAddress());
            mLocationDistanceTxtView.setText(String.format(itemView.getContext().getString(R.string.miles), location.getDistance()));
            mLocationCityZipTxtView.setText(location.getCity() + ", " + location.getZip());
        }
    }
}
