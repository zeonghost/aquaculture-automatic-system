package com.example.aquaculture;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class PartnerLogActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myLog;
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
    private Boolean clockDetails;
    private TextView txtDevice;
    private TextView txtLocation;
    private TextView txtTimeIn;
    private TextView txtTimeOut;
    private String logLocation;
    private ProgressDialog connectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_log);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("PartnerLog");
        myLog = database.getReference("pi1-log");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(PartnerLogActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        timeIn = findViewById(R.id.btnTimeIn);
        timeOut = findViewById(R.id.btnTimeOut);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        txtName = findViewById(R.id.txtViewName);
        txtTimeIn = findViewById(R.id.txtViewTimeIn);
        txtTimeOut = findViewById(R.id.txtViewTimeOut);
        connectDialog = new ProgressDialog(PartnerLogActivity.this);
        connectDialog.setMessage("Google maps is loading...");
        connectDialog.setIndeterminate(true);
        connectDialog.setCancelable(false);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkGPSServices();
            checkLocationService();
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

                fullname = sp.getString("firstname", "") + " " + sp.getString("lastname", "");
                username = sp.getString("username", "");
                device = sp.getString("device", "");

                partnerLog = new PartnerLocationLog (username, fullname, device, myLocation.getTime(), 0, myLocation.getLatitude(), myLocation.getLongitude());

                Geocoder gcd = new Geocoder(PartnerLogActivity.this, Locale.getDefault());
                try {
                    connectDialog.show();
                    List<Address> addresses = gcd.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    if(addresses.size() > 0){
                        logLocation = addresses.get(0).getAddressLine(0);
                    }
                    connectDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PartnerLogActivity.this, "Internet Connection timeout. Google Maps has failed to load. Try again.", Toast.LENGTH_SHORT).show();
                    Intent toHomeActivity = new Intent (PartnerLogActivity.this, HomeActivity.class);
                    startActivity(toHomeActivity);

                }

            }
        });
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        clockDetails=sp.getBoolean("clockInDetails",false);
        if(clockDetails)
        {
            timeOut.setEnabled(true);
            timeOut.setBackgroundColor(Color.parseColor("#0F7D63"));
            timeIn.setEnabled(false);
            timeIn.setBackgroundColor(Color.GRAY);
        }
        else
        {
            timeOut.setEnabled(false);
            timeOut.setBackgroundColor(Color.GRAY);
            timeIn.setEnabled(true);
            timeIn.setBackgroundColor(Color.parseColor("#0F7D63"));
        }

        txtName.setText("Name: " + sp.getString("firstname", "") + " " + sp.getString("lastname", ""));

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
                String key = myLog.push().getKey();
                long currentTimestamp = System.currentTimeMillis();

                myLog.child(key).child("logTime").setValue(currentTimestamp);
                myLog.child(key).child("logDetail").setValue(sp.getString("firstname", "") + " " + sp.getString("lastname", "") + " has clocked in nearby " + logLocation);
                myLog.child(key).child("username").setValue(sp.getString("username", ""));

                txtTimeOut.setText("--- --, ---- --:-- AM/PM");
                sp.edit().putBoolean("clockInDetails", true).apply();
                Intent toHomeActivity = new Intent (PartnerLogActivity.this, HomeActivity.class);
                startActivity(toHomeActivity);
            }
        });

        timeOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentTimestamp = System.currentTimeMillis();
                    myRef.child(username).child("timeOut").setValue(currentTimestamp);
                    String key = myLog.push().getKey();
                    myLog.child(key).child("logTime").setValue(currentTimestamp);
                    myLog.child(key).child("logDetail").setValue(sp.getString("firstname", "") + " " + sp.getString("lastname", "") + " has clocked out nearby " + logLocation);
                    myLog.child(key).child("username").setValue(sp.getString("username", ""));

                    sp.edit().putBoolean("clockInDetails", false).apply();
                    sp.edit().clear().apply();
                    TIME_IN_STATUS = 0;
                    Intent toLogInPage = new Intent (PartnerLogActivity.this, MainActivity.class);
                    startActivity(toLogInPage);
            }
        });
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
}
