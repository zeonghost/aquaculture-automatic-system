package com.example.aquaculture;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class PartnerLogActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "PartnerLogActivity";
    private MapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private LatLng currentLocation;
    private Button timeIn;
    private Button timeOut;
    private Location myLocation;
    private PartnerLocationLog partnerLog;
    private SharedPreferences sp;
    private String fullname;
    private String username;
    private String device;
    private TextView txtName;
    private TextView txtDevice;
    private TextView txtLocation;
    private TextView txtTimeIn;
    private TextView txtTimeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_log);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("PartnerLog");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(PartnerLogActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        timeIn = findViewById(R.id.btnTimeIn);
        timeOut = findViewById(R.id.btnTimeOut);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        txtName = findViewById(R.id.txtViewName);
        txtDevice = findViewById(R.id.txtViewDeviceName);
        txtLocation = findViewById(R.id.txtViewLocationName);
        txtTimeIn = findViewById(R.id.txtViewTimeIn);
        txtTimeOut = findViewById(R.id.txtViewTimeOut);

        //NOTES: PERMISSIONS MUST BE REQUESTED PRIOR CALLING GOOGLE MAPS SERVICES, OTHERWISE SOME FUNCTION CALLS WILL CRASH THE APP.
        checkGPSServices();
        checkLocationService();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                myLocation = task.getResult();
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("YOU!"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.5f));
                Log.d(TAG, "Latitude: " + location.getLatitude());
                Log.d(TAG, "Longitude: " + location.getLongitude());

                fullname = sp.getString("firstname", "") + " " + sp.getString("lastname", "");
                username = sp.getString("username", "");
                device = sp.getString("device", "");
                partnerLog = new PartnerLocationLog (username, fullname, device, myLocation.getTime(), 0, myLocation.getLatitude(), myLocation.getLongitude());
            }
        });
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(TIME_IN_STATUS == 1){
            timeOut.setEnabled(true);
            timeIn.setEnabled(false);
        } else {
            timeOut.setEnabled(false);
            timeIn.setEnabled(true);
        }

        txtName.setText("Name: " + sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        txtDevice.setText("Device: " + sp.getString("device", ""));
        txtLocation.setText("Location: " + sp.getString("location", ""));

        Query query = myRef.child(sp.getString("username",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long timestampTimeIn = dataSnapshot.child("timeIn").getValue(Long.class);
                    long timestampTimeOut = dataSnapshot.child("timeOut").getValue(Long.class);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestampTimeIn);
                    String dateTodayIn = DateFormat.format("MMM dd, yyyy h:mm a", calendar).toString();
                    txtTimeIn.setText(dateTodayIn);

                    if(timestampTimeOut != 0){
                        calendar.setTimeInMillis(timestampTimeOut);
                        String dateTodayOut = DateFormat.format("MMM dd, yyyy h:mm a", calendar).toString();
                        txtTimeOut.setText(dateTodayOut);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        timeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(username).setValue(partnerLog);
                txtTimeOut.setText("--- --, ---- --:-- AM/PM");
                //sp.edit().putBoolean("timeInStatus", true).apply();
                TIME_IN_STATUS = 1;
                finish();
                //Intent toPondInfoActivity = new Intent (PartnerLogActivity.this, PondInfoActivity.class);
                //startActivity(toPondInfoActivity);
            }
        });
        timeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimestamp = System.currentTimeMillis();
                myRef.child(username).child("timeOut").setValue(currentTimestamp);
                //sp.edit().putBoolean("timeInStatus", false).apply();
                TIME_IN_STATUS = 0;
                Intent toHomeActivity = new Intent (PartnerLogActivity.this, HomeActivity.class);
                startActivity(toHomeActivity);
            }
        });
    }

    /*
    @Override
    public void onBackPressed() {
        if(TIME_IN_STATUS == 1){
            Toast.makeText(PartnerLogActivity.this,"You need to time out before you can exit this page or transfer to another pond.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
    */

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
}
