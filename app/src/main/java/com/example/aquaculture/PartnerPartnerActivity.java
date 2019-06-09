package com.example.aquaculture;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.aquaculture.Model.PartnerLocationLog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;

public class PartnerPartnerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "PartnerPartnerActivity";
    private MapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_partner);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("PartnerLog");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(PartnerPartnerActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkGPSServices();

        //DEBUG NOTES: PERMISSIONS MUST BE REQUESTED PRIOR CALLING GOOGLE MAPS SERVICES, OTHERWISE SOME FUNCTION CALLS WILL CRASH THE APP.
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng dgte = new LatLng(9.316, 123.299);
        googleMap.addMarker(new MarkerOptions().position(dgte).title("This is Dumaguete City"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dgte, 15.0f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationService();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        getLastKnownLocation();
    }


    private void checkGPSServices() {
        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (location.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            AlertDialog.Builder gpsAlert = new AlertDialog.Builder(this);
            gpsAlert.setMessage("GPS must be on in order for this feature to work.");
            gpsAlert.setPositiveButton("Turn On GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent locationServiceIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(locationServiceIntent, 1);
                }
            });
            gpsAlert.show();
        }
    }

    private void checkLocationService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Log.d(TAG, "Latitude: " + location.getLatitude());
                Log.d(TAG, "Longitude: " + location.getLongitude());

                PartnerLocationLog test = new PartnerLocationLog
                        ("usernameHere", "fullnameHere", "deviceHere",
                                location.getTime(), 0, location.getLatitude(), location.getLongitude());
                myRef.child("someID").setValue(test);
            }
        });
    }
}
