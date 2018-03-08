package com.example.mostafahussien.mymap.Services;


import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.example.mostafahussien.mymap.Database.AppDB;
import com.example.mostafahussien.mymap.Database.FavoriteDAO;
import com.example.mostafahussien.mymap.model.MyPlace;

import java.util.ArrayList;
import java.util.List;

public class RetriveFavoritsService extends IntentService{
    FavoriteDAO favoriteDAO;
    List<MyPlace> placeList;
    public RetriveFavoritsService() {
        super("retrieve_service");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        favoriteDAO= AppDB.getInstance(this).getFavoriteDAO();
        placeList=favoriteDAO.getAllFav();
        sendResult();
    }
    public void sendResult(){
        Intent intent=new Intent("favorite_result");
        intent.putParcelableArrayListExtra("fav_list", (ArrayList<? extends Parcelable>) placeList);
        sendBroadcast(intent);
    }
}
