package com.example.mostafahussien.mymap.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafahussien.mymap.Interface.OnClickPlaceListener;
import com.example.mostafahussien.mymap.R;
import com.example.mostafahussien.mymap.Utilities;
import com.example.mostafahussien.mymap.model.MyPlace;

import java.util.List;



public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>{
    List<MyPlace> placeList;
    Context context;
    OnClickPlaceListener listener;
    public FavoriteAdapter(List<MyPlace> placeList, Context context,OnClickPlaceListener listener) {
        this.placeList = placeList;
        this.context = context;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavoriteAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_favorite_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.address.setText(placeList.get(position).getAddress());
        holder.latitude.setText("Latitude : "+String.valueOf(placeList.get(position).getLatitude()));
        holder.longitude.setText("Longitude : "+String.valueOf(placeList.get(position).getLongitude()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPlaceClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address,latitude,longitude;
        ImageView placeImage;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            address=(TextView)itemView.findViewById(R.id.address_text);
            latitude=(TextView)itemView.findViewById(R.id.latitude_text);
            longitude=(TextView)itemView.findViewById(R.id.longitude_text);
            placeImage=(ImageView) itemView.findViewById(R.id.place_image);
            view=itemView;
        }
    }
}
