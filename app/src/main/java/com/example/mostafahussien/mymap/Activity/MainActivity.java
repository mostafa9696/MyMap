package com.example.mostafahussien.mymap.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mostafahussien.mymap.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final int ERROR_DIALOG_REQUEST=9001;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.btn);
        if(isOK()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    init();
                }
            });
        }
    }
    private void init(){
        Intent intent=new Intent(this,MapActivity.class);
        startActivity(intent);
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
