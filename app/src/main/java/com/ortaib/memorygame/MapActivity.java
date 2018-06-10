package com.ortaib.memorygame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean locationPermissionGranted = false;
    private LocationManager locationManager;
    DatabaseHelper myDatabaseHelper;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final String TAG = "MapActivity";
    private final float DEFUALT_ZOOM = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myDatabaseHelper = new DatabaseHelper(this);
        getLocationPermission();
        getDeviceLocation();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        getDeviceLocation();
        mMap.setMyLocationEnabled(true);
        putMarkers();
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "initMap: initializing map");

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: requesting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");

        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted ");
                    locationPermissionGranted = true;
                    initMap();
                }
        }
    }
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationPermissionGranted){
            try{
                @SuppressLint("MissingPermission") Location currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFUALT_ZOOM);
            }catch(SecurityException e){
                Log.e(TAG, "getDeviceLocation: SecurityException: "+e.getMessage());
            }
        }
    }
    private void moveCamera(LatLng lat,float zoom){
        Log.d(TAG, "moveCamera: moving the camere to : "+lat.latitude +", "+lat.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat,zoom));
    }
    private void putMarkers(){
        Cursor data = myDatabaseHelper.getData();
        double lat,lon;
        String name;
        int num;
        while(data.moveToNext()){
            name = data.getString(1);
            lat = data.getDouble(2);
            lon = data.getDouble(3);
            num = data.getInt(0);
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title("#"+num).snippet("name: " +
                    ", Score : "+name));
        }
    }

}
