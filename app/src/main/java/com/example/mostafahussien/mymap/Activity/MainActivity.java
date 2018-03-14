package com.example.mostafahussien.mymap.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafahussien.mymap.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final int ERROR_DIALOG_REQUEST=9001;
    TextView textView;
    Thread splashThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView) findViewById(R.id.map_text);
        startAnimation();
        if(isOK()) {
            startThread();
        }
    }
    public void startAnimation(){
        Animation fadeIn = new AlphaAnimation(0, 1);        // from alpha 0 to alpha 1
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(2500);
        textView.setAnimation(fadeIn);
    }
    public void startThread(){
        splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 200;
                    }
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    MainActivity.this.finish();
                } catch (InterruptedException e) {

                } finally {
                    MainActivity.this.finish();
                }
            }
        };
        splashThread.start();
    }

    public boolean isOK(){
        int avail= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(avail== ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), "fine", Toast.LENGTH_SHORT).show();
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(avail)){
            Toast.makeText(getApplicationContext(), "error but can fix it", Toast.LENGTH_SHORT).show();
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,avail,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "cann;t make  map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
