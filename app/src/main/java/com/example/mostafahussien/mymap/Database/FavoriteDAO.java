package com.example.mostafahussien.mymap.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;

import com.example.mostafahussien.mymap.model.MyPlace;

import java.util.List;

@Dao
public interface FavoriteDAO {
    @Query("select * from myPlaces")
    List<MyPlace> getAllFav();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void inserFav(MyPlace... myPlaces);           // ... means that it can take more than one topic
    @Update
    void updateFav(MyPlace... myPlaces);
    @Query("DELETE FROM myPlaces where latitude=:lat and longitude=:lon")
    void deleteFav(double lat,double lon);
    @Query("DELETE FROM myPlaces")
    public void deleteAll();
    @Query("select * from myPlaces where address=:name")
    MyPlace getPlace(String name);
}
