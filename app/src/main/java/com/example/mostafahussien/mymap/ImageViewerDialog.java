package com.example.mostafahussien.mymap;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Mostafa Hussien on 06/03/2018.
 */

public class ImageViewerDialog extends DialogFragment implements View.OnClickListener{
    ImageView imageView, next, prev,noImage;
    Context context;
    View dialogView;
    Dialog dialog;
    String placeID;
    Utilities utilities;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    // todo put imageView and next,prev image in Utilities class and control them in Utilities class
    // todo ask "Can i communicate fragment with non-activity class with interface ?"

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater=getActivity().getLayoutInflater();
        dialogView=inflater.inflate(R.layout.image_viewer_dialog,null);
        imageView=(ImageView)dialogView.findViewById(R.id.place_image);
        next=(ImageView)dialogView.findViewById(R.id.next_image);
        prev=(ImageView)dialogView.findViewById(R.id.prev_image);
        placeID=getArguments().getString("place_id");
        intializeDialog();
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        return dialog;
    }

    public void intializeDialog(){
        dialog=new Dialog(getActivity());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        utilities=new Utilities(context,dialogView);
        utilities.getPlaceImage(placeID);

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.next_image){
            utilities.nextImage();
        } else if(id==R.id.prev_image){
            utilities.prevImage();
        }
    }

}