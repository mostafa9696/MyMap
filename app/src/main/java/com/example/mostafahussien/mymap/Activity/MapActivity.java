package com.example.mostafahussien.mymap.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mostafahussien.mymap.Adapter.CustomInfoWindowAdapter;
import com.example.mostafahussien.mymap.Adapter.PlaceAutoCompleteAdapter;
import com.example.mostafahussien.mymap.ImageViewerDialog;
import com.example.mostafahussien.mymap.R;
import com.example.mostafahussien.mymap.Services.FavoriteAction;
import com.example.mostafahussien.mymap.Utilities;
import com.example.mostafahussien.mymap.model.MyPlace;
import com.example.mostafahussien.mymap.model.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import devlight.io.library.ntb.NavigationTabBar;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CROSS_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));    // specify location coordinate (length line & width line)
    String currentPlace = "empty",placeID;
    private boolean locationPermissionGranted = true;
    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView editText;
    private ImageView search, favImage;
    private PlaceAutoCompleteAdapter adapter;
    private GoogleApiClient googleApiClient;
    private PlaceInfo placeInfo;
    private Marker marker;
    private boolean isFave = false, from_fav_activity = false,placeHasImages=false;
    private MyPlace myPlace,favPlace;
    DecimalFormat df;
    ArrayList<NavigationTabBar.Model> icons;
    NavigationTabBar navigationTabBar;
    private BroadcastReceiver favReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isFave = intent.getBooleanExtra("is_fav", false);
            if (isFave) {
                favImage.setImageResource(com.example.mostafahussien.mymap.R.drawable.ic_unfav_icon);
                favImage.setTag("fav");
            } else {
                favImage.setImageResource(R.drawable.ic_fav_icon);
                favImage.setTag("un_fav");
            }
        }
    };
    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            Place place = places.get(0);
            try {                       // because some of paramters may be null
                placeInfo = new PlaceInfo();
                placeInfo.setName(place.getName().toString());
                placeInfo.setAddress(place.getAddress().toString());
                placeInfo.setLatlng(place.getLatLng());
                placeInfo.setId(place.getId());
                placeID=place.getId();
                placeInfo.setPhoneNumber(place.getPhoneNumber().toString());
                placeInfo.setWebsiteUri(place.getWebsiteUri());
                placeInfo.setRating(place.getRating());
            } catch (NullPointerException e) {}
            double lat=0;
            double lon=0;
            try {
                currentPlace = place.getAddress().toString();
                 lat=place.getViewport().getCenter().latitude;
                 lon=place.getViewport().getCenter().longitude;
                lat= Double.parseDouble(df.format(lat));
                lon= Double.parseDouble(df.format(lon));
                myPlace = new MyPlace(currentPlace, lat,lon);  // to insert it in favorite DB if press on fav btn
            }
            catch (NullPointerException e) {
            }
            moveCamera(new LatLng(lat, lon), 15f, placeInfo);
            places.release();
        }
    };
    private AdapterView.OnItemClickListener autoCompleteClickLis = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard();
            AutocompletePrediction item = adapter.getItem(i);
            String placeID = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeID);
            placeResult.setResultCallback(updatePlaceDetailsCallback);
        }
    };
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;                          // control the map when it ready
        myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (from_fav_activity) {
            favImage.setImageResource(R.drawable.ic_unfav_icon);
            favImage.setTag("fav");
            favPlace=getIntent().getExtras().getParcelable("fav_place");
            myPlace=favPlace;
            init();
            moveToFavoritePlace();
        } else {
            if (locationPermissionGranted) {            // if user accept to get his location then move map to this location
                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                myMap.setMyLocationEnabled(true);                               // user can move to his location from another location
                myMap.getUiSettings().setMyLocationButtonEnabled(false);        // hide button which navigate to my location and make custom one
                init();
            }                             // ask user for permission to access his location
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if(getIntent().getExtras()!=null){
            from_fav_activity = getIntent().getExtras().getBoolean("from_fav_activity", false);
        }
        editText = (AutoCompleteTextView) findViewById(R.id.input_serach);
        search = (ImageView) findViewById(R.id.ic_search);
        favImage = (ImageView) findViewById(R.id.ic_fav);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.nav_bar);
        icons = new ArrayList<>();
        myPlace = new MyPlace();
        df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.FLOOR);
        getLocationPermission();                                // ask user for permission to access his location
    }
    public void moveToFavoritePlace(){
        if(!Utilities.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(),"Check your netwerok connection !",Toast.LENGTH_LONG).show();
            return;
        }
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        Address address=null;
        double lat=favPlace.getLatitude();
        double lon=favPlace.getLongitude();
        lat= Double.parseDouble(df.format(lat));
        lon= Double.parseDouble(df.format(lon));
        try {
            address = geocoder.getFromLocation(lat, lon, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address != null) {
            moveCamera(new LatLng(lat,lon),15f,address);
        } else {
            Toast.makeText(getApplicationContext(), "Unable to get this location try again!", Toast.LENGTH_SHORT).show();
        }
    }
    private void init() {
        googleApiClient = new GoogleApiClient.Builder(this)           // to use it in autoComplete place adapter
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        editText.setOnItemClickListener(autoCompleteClickLis);
        adapter = new PlaceAutoCompleteAdapter(this, googleApiClient, LAT_LNG_BOUNDS, null);
        editText.setAdapter(adapter);
        search.setOnClickListener(new View.OnClickListener() {          // when click on search imageButton
            @Override
            public void onClick(View view) {
                locateInputText();              // if press on search ImageView
            }
        });
        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {        // start service of insert fav or remove it from room SB
                if (favImage.getTag().toString().equals("un_fav")) {
                    favImage.setImageResource(R.drawable.ic_unfav_icon);
                    favImage.setTag("fav");
                    favAction("insert_fav");
                } else {
                    favImage.setImageResource(R.drawable.ic_fav_icon);
                    favImage.setTag("un_fav");
                    favAction("remove_fav");
                }
            }
        });
        icons.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_place_picker), Color.WHITE).build()
        );
        icons.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_fav_list), Color.WHITE).build()
        );
        icons.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_gps), Color.WHITE).build()
        );
        icons.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_info), Color.WHITE).build()
        );
        icons.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_place_images), Color.WHITE).build()
        );
        navigationTabBar.setModels(icons);
        navigationTabBar.setModelIndex(2, true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }
            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                if (index == 0) {
                    if(!Utilities.isNetworkAvailable(MapActivity.this)) {
                        Toast.makeText(getApplicationContext(),"Check your netwerok connection !",Toast.LENGTH_LONG).show();
                        return;
                    }
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(MapActivity.this), 1);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else if (index == 1) {
                    final Intent intent = new Intent(MapActivity.this, FavoritePlaces.class);
                    startActivity(intent);
                } else if (index == 2) {
                    editText.getText().clear();
                    getDeviceLocation();
                } else if (index == 3) {
                    try {
                        if (marker.isInfoWindowShown()) {
                            marker.hideInfoWindow();
                        } else {
                            marker.showInfoWindow();
                        }
                    } catch (NullPointerException e) {
                    }
                } else if (index == 4) {
                    if(!Utilities.isNetworkAvailable(MapActivity.this)) {
                        Toast.makeText(getApplicationContext(),"Check your netwerok connection !",Toast.LENGTH_LONG).show();
                        return;
                    }
                        if(!placeID.equals("none")) {
                            DialogFragment dialogFragment = new ImageViewerDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("place_id", placeInfo.getId());
                            dialogFragment.setArguments(bundle);
                            if(dialogFragment!=null)
                            dialogFragment.show(getSupportFragmentManager(), "ImageViewerDialog");
                        }
                }
            }
        });
        hideKeyboard();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {     // when pick a place and set marker
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi     // to call updatePlaceDetailsCallback to moveCamer
                    .getPlaceById(googleApiClient, place.getId());
            placeResult.setResultCallback(updatePlaceDetailsCallback);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void locateInputText() {
        placeID="none";
        String searchText= editText.getText().toString();
        //Geocoder is transforming a street address or other description of a location into a (latitude, longitude) coordinat
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchText, 1); // 1 for get max result from input text address
        } catch (IOException e) {
        }
        if (list.size() > 0) {
            Address address = list.get(0);        // get user input location
            currentPlace = address.getAddressLine(0);
            double lat=address.getLatitude();
            double lon=address.getLongitude();
            lat= Double.parseDouble(df.format(lat));
            lon= Double.parseDouble(df.format(lon));
            moveCamera(new LatLng(lat, lon), 15f, address);      // move camera to user input location
        }
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted) {
                Task task = fusedLocationProviderClient.getLastLocation();        // task is used to move camera from location to another in the same activity
                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            placeID="none";
                            Location location = (Location) task.getResult();
                            Address address = null;
                            Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                            double lat=location.getLatitude();
                            double lon=location.getLongitude();
                            try {
                                lat= Double.parseDouble(df.format(lat));
                                lon= Double.parseDouble(df.format(lon));
                                address = geocoder.getFromLocation(lat, lon, 1).get(0);
                                currentPlace = address.getAddressLine(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (address != null) {
                                moveCamera(new LatLng(lat, lon), 15f, address);

                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to get current location, Check your Internet connection!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to get current location, Check your Internet connection then try again !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }
    private void moveCamera(LatLng lating, float zoom, PlaceInfo placeInfo) {
        placeHasImages=true;
        checkPlaceFav();
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lating, zoom));       // move camera position of the map
        myMap.clear();       // clear all the marker of the map
        myMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));      // set adapter to show place info dialog
        if (placeInfo != null) {
            try {
                String dialogData = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone number: " + placeInfo.getPhoneNumber() + "\n" +
                        "website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";
                MarkerOptions options = new MarkerOptions();        // put marker on selected location
                options.position(lating)
                        .title(placeInfo.getName())
                        .snippet(dialogData);
                marker = myMap.addMarker(options);    // global marker to deal with
            } catch (NullPointerException e) {
            }
        } else {
            myMap.addMarker(new MarkerOptions().position(lating));
        }
        hideKeyboard();
    }

    private void moveCamera(LatLng lating, float zoom, Address address) {
        placeHasImages=false;
        checkPlaceFav();
        myPlace = new MyPlace(address.getAddressLine(0), lating.latitude, lating.longitude);                // to insert it in favorite DB if press on fav btn
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lating, zoom));       // move camera position of the map
        try {
            String dialogData = "Address: " + address.getAddressLine(0) + "\n" +
                    "Country Name: " + address.getCountryName() + "\n" +
                    "phone: " + address.getPhone() + "\n";
            MarkerOptions options = new MarkerOptions();        // put marker on selected location
            options.position(lating)
                    .title(address.getAddressLine(0))
                    .snippet(dialogData);
            myMap.addMarker(options).remove();
            marker = myMap.addMarker(options);    // global marker to deal with

        } catch (NullPointerException e) {
        }
        hideKeyboard();
    }

    public void checkPlaceFav() {
        if (!currentPlace.equals("empty")) {
            Intent favAction = new Intent(this, FavoriteAction.class);
            favAction.putExtra("action", "check_fav");
            favAction.putExtra("current_place", currentPlace);
            startService(favAction);
        }
    }
    public void favAction(String action) {
        //Toast.makeText(getApplicationContext(),myPlace.getAddress(),Toast.LENGTH_LONG).show();
        Intent favAction = new Intent(this, FavoriteAction.class);
        favAction.putExtra("action", action);
        favAction.putExtra("place", myPlace);
        startService(favAction);
    }
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION};   // internet permission not need to check
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), CROSS_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    initMap();
                } else {
                    ActivityCompat.requestPermissions(this, permissions, 15);
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, 15);
            }
        } else {
            locationPermissionGranted = true;
            initMap();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case 15: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){      // if one permission is not accept then not intialize th map
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    protected void onResume() {
        super.onResume();
        editText.getText().clear();
        registerReceiver(favReceiver, new IntentFilter("fav_action"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(favReceiver);
    }

}

// todo make internet connection and no location permission
// todo insert new column in DB "String placeID" which is be "none" if place is deviceLocation or search button location
// todo reDesign dialog shape
// todo make splash screen at mainActivity
// TODO reDesign mapActivity.java (make utils, .. etc)
// TOdo make final test
