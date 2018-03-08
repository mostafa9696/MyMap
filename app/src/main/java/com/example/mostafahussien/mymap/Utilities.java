package com.example.mostafahussien.mymap;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utilities{
    private static Context context;
    private  GeoDataClient mGeoDataClient;
    private  List<PlacePhotoMetadata> photosDataList;
    private int size,currentIndex;
    ImageView imageView,next,prev,noImage;
    private AVLoadingIndicatorView avi;
    public Utilities(Context context, View dialog) {
        this.context = context;
        size=0;
        imageView=(ImageView)dialog.findViewById(R.id.place_image);
        next=(ImageView)dialog.findViewById(R.id.next_image);
        prev=(ImageView)dialog.findViewById(R.id.prev_image);
        noImage=(ImageView)dialog.findViewById(R.id.no_image);
        avi= (AVLoadingIndicatorView)dialog. findViewById(R.id.avi);
        avi.hide();
        currentIndex=0;
    }
    public  void getPlaceImage(String placeID){

        mGeoDataClient = Places.getGeoDataClient(context, null);
        try {
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeID);
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    photosDataList=new ArrayList<>();
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    for(PlacePhotoMetadata photoMetadata : photoMetadataBuffer){
                        photosDataList.add(photoMetadata);
                    }
                    size=photosDataList.size();
                    displayImage();
                }
            });
        } catch (IllegalStateException e){
        }
    }
    public void getImage(PlacePhotoMetadata photoMetadata){
        avi.show();
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                imageView.invalidate();                 // forces the view to be redrawn immediately and not during the next cycle
                imageView.setImageBitmap(photoBitmap);
                avi.hide();
            }
        });
    }
    public void displayImage(){
        if(photosDataList.isEmpty()) {
            imageView.setImageResource(R.drawable.noimage);
            return;
        }

        getImage(photosDataList.get(currentIndex));
    }
    public void nextImage(){
        if(currentIndex==photosDataList.size()-1){
         next.setClickable(false);
        }else {
            currentIndex++;
            displayImage();
        }
        if(currentIndex>0){
            prev.setClickable(true);
        }
    }
    public void prevImage(){
        if(currentIndex==0){
         prev.setClickable(false);
        }else {
            currentIndex--;
            displayImage();
        }
        if(currentIndex<photosDataList.size()-1){
            next.setClickable(true);
        }
    }
    ////////////////////////////
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public int getSize() {
        return size;
    }

    public static boolean isNetworkAvailable(Context con) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
