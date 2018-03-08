package com.example.mostafahussien.mymap.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.example.mostafahussien.mymap.Adapter.FavoriteAdapter;
import com.example.mostafahussien.mymap.Interface.OnClickPlaceListener;
import com.example.mostafahussien.mymap.R;
import com.example.mostafahussien.mymap.Services.RetriveFavoritsService;
import com.example.mostafahussien.mymap.model.MyPlace;

import java.util.ArrayList;
import java.util.List;

public class FavoritePlaces extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView toolbarText;
    Typeface typeface;
    FavoriteAdapter favoriteAdapter;
    List<MyPlace>placeList;
    BroadcastReceiver favList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            placeList=intent.getParcelableArrayListExtra("fav_list");
            favoriteAdapter=new FavoriteAdapter( placeList,FavoritePlaces.this, new OnClickPlaceListener() {
                @Override
                public void onPlaceClick(int position) {
                    Intent mapIntent=new Intent(FavoritePlaces.this,MapActivity.class);
                    mapIntent.putExtra("from_fav_activity",true);
                    mapIntent.putExtra("fav_place",placeList.get(position));
                    mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mapIntent);
                }
            });
            recyclerView.setAdapter(favoriteAdapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_places);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        typeface=Typeface.createFromAsset(getAssets(),"HAMMOCK-Black.otf");
        toolbarText=(TextView)findViewById(R.id.toolbar_text);
        toolbarText.setTypeface(typeface);
        setAnim();
        getFavList();
    }

    public void setAnim(){
        int resId = R.anim.layout_animation;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);
    }
    public void getFavList(){
        Intent intent=new Intent(this, RetriveFavoritsService.class);
        startService(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter("favorite_result");
        registerReceiver(favList,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(favList);
    }

}
