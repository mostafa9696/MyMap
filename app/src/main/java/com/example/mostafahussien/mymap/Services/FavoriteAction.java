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


public class FavoriteAction extends IntentService {
    FavoriteDAO favoriteDAO;
    MyPlace place;
    String action_type,current_place;
    public FavoriteAction() {
        super("FavAction");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        favoriteDAO= AppDB.getInstance(this).getFavoriteDAO();
        //favoriteDAO.deleteAll();
        action_type=intent.getExtras().getString("action");
        if(action_type.equals("check_fav")){
            current_place=intent.getExtras().getString("current_place");
            place=favoriteDAO.getPlace(current_place);
            sendResult();
        }else {
            place = intent.getExtras().getParcelable("place");
            if (action_type.equals("insert_fav")) {
                favoriteDAO.inserFav(place);
            } else if (action_type.equals("remove_fav")) {
                favoriteDAO.deleteFav(place.getLatitude(),place.getLongitude());
            }
        }
    }

    public void sendResult(){
        Intent intent=new Intent("fav_action");
        if(place==null){
            intent.putExtra("is_fav",false);
        } else {
            intent.putExtra("is_fav",true);
        }

        sendBroadcast(intent);
    }
}
