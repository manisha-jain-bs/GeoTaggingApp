package com.geotaggingapp.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import com.geotaggingapp.R;
import com.geotaggingapp.interfaces.OnItemClickListener;
import com.geotaggingapp.model.GeoTagInfo;
import com.google.android.gms.maps.model.LatLng;

public class RecyclerViewAdapter extends RecyclerView.Adapter {


    private ArrayList<GeoTagInfo> mTaggedLocationsInfo = new ArrayList<>();
    private Context mContext;
    private LatLng latlng;
    private OnItemClickListener mOnItemClickListener;


    public RecyclerViewAdapter(Context context, ArrayList<GeoTagInfo> listItem, LatLng latlng, OnItemClickListener onItemClick) {
        this.mContext = context;
        this.latlng = latlng;
        mTaggedLocationsInfo = listItem;
        this.mOnItemClickListener = onItemClick;
    }


    @Override
    public int getItemCount() {
        return mTaggedLocationsInfo.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.tagged_locations_list, parent, false);
        viewHolder = new ItemViewHolder(itemView, mContext);;
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        GeoTagInfo geoTagInfo = mTaggedLocationsInfo.get(position);

        // Set Tagged image take from camera in imageview
        byte[] byteArray = geoTagInfo.getGeoTagImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        itemViewHolder.taggedImageView.setImageBitmap(bmp);

        // Show LatLng of tagged location in textview
        itemViewHolder.mLatLng.setText(geoTagInfo.getLatitude() + ", " + geoTagInfo.getLongitude());

        // Show Address of Tagged Location in textview.
        itemViewHolder.mAddress.setText(geoTagInfo.getAddress());

        // Show the list Item Highlighted if this is being clicked from a marker position on Map.
        if(latlng != null && latlng.latitude == geoTagInfo.getLatitude()) {
            itemViewHolder.container.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_light));
        } else {
            itemViewHolder.container.setBackgroundColor(0);
        }

        itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoTagInfo geoTagInfo = mTaggedLocationsInfo.get(itemViewHolder.getAdapterPosition());
                mOnItemClickListener.onItemClick(geoTagInfo.getLatitude(), geoTagInfo.getLongitude(), geoTagInfo.getGeoTagImage());
            }
        });
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        // Fields for Recent Chats and Contacts List
        public ImageView taggedImageView;
        public TextView mLatLng, mAddress;
        public View container;

        public ItemViewHolder(View view, Context context) {
            super(view);

            container = view;
            taggedImageView = (ImageView) view.findViewById(R.id.tagged_location_image);
            mLatLng = (TextView) view.findViewById(R.id.latLng);
            mAddress = (TextView) view.findViewById(R.id.address);
        }
    }

}

