package com.example.mostafahussien.mymap.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.mostafahussien.mymap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Mostafa Hussien on 28/02/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        window= LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }
    public void setWindowText(Marker marker,View view){
        String title=marker.getTitle();
        String data=marker.getSnippet();
        TextView tvTitle,tvData;
        tvTitle=(TextView)view.findViewById(R.id.title);
        tvData=(TextView)view.findViewById(R.id.data);
        if(!title.equals(""))
            tvTitle.setText(title);
        if(!data.equals(""))
            tvData.setText(data);
    }
    @Override
    public View getInfoWindow(Marker marker) {
        setWindowText(marker,window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        setWindowText(marker,window);
        return window;
    }
}
