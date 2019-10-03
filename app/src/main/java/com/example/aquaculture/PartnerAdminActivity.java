package com.example.aquaculture;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.aquaculture.Model.Partner;
import com.example.aquaculture.ViewHolder.PartnerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class PartnerAdminActivity extends AppCompatActivity implements OnMapReadyCallback{
    private RecyclerView recyclerPartner;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseRecyclerOptions<Partner> options;
    private FirebaseRecyclerAdapter<Partner, PartnerViewHolder> adapter;
    private String path;
    private SharedPreferences sp;
    private SharedPreferences partnerTemp;
    private FloatingActionButton linkUser;
    private MapFragment mapFragment;
    private LatLng location;
    private String logInfo;
    public ProgressDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_admin);
        buttonNavigationSettings();
        waitingDialog= new ProgressDialog(PartnerAdminActivity.this);
        waitingDialog.setMessage("Connecting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(true);
        linkUser = findViewById(R.id.btnLinkUser);
        recyclerPartner = findViewById(R.id.partnerRecyclerView);
        recyclerPartner.setHasFixedSize(true);
        recyclerPartner.setLayoutManager(new LinearLayoutManager(PartnerAdminActivity.this));

        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        partnerTemp = getSharedPreferences("partnerTemp", Context.MODE_PRIVATE);

        path = "Partners/" + sp.getString("username", "");
        ref = database.getReference(path);
        options = new FirebaseRecyclerOptions.Builder<Partner>()
                .setQuery(ref, Partner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Partner, PartnerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PartnerViewHolder holder, int position, @NonNull Partner model) {
                final String username = model.getUsername();
                final String fullname = model.getFullname();
                final String deviceId = model.getDevice();
                final String key = model.getId();

                holder.username.setText(username);
                holder.fullNamePartner.setText(fullname);
                holder.deviceId.setText(deviceId);

            }

            @NonNull
            @Override
            public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(PartnerAdminActivity.this).inflate(R.layout.activity_partner_card, viewGroup, false);
                return new PartnerViewHolder(v);
            }
        };
        recyclerPartner.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        linkUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerAdminActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(location).title(logInfo)).showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.5f));
    }

    private void getLocationPartner(String username){
        DatabaseReference myRef = database.getReference("PartnerLog");
        myRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    waitingDialog.dismiss();
                    location = new LatLng(dataSnapshot.child("latitude").getValue(Double.class), dataSnapshot.child("longitude").getValue(Double.class));
                    long timestampTimeIn = dataSnapshot.child("timeIn").getValue(Long.class);
                    long timestampTimeOut = dataSnapshot.child("timeOut").getValue(Long.class);
                    String dateTodayIn, dateTodayOut;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestampTimeIn);
                    dateTodayIn = DateFormat.format("MMM dd, yyyy h:mm a", calendar).toString();

                    if(timestampTimeOut != 0){
                        calendar.setTimeInMillis(timestampTimeOut);
                        dateTodayOut = DateFormat.format("MMM dd, yyyy h:mm a", calendar).toString();
                    } else {
                        dateTodayOut = "";
                    }

                    logInfo = "In: " + dateTodayIn + " | Out: " + dateTodayOut;
                    partnerInfoDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void partnerInfoDialog(){
        final LayoutInflater floatingDialog = LayoutInflater.from(this);
        final View view = floatingDialog.inflate(R.layout.dialog_partner_log, null);
        final AlertDialog.Builder lastLogInfo = new AlertDialog.Builder(PartnerAdminActivity.this);
        lastLogInfo.setView(view);
        lastLogInfo.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mapFragment != null)
                    getFragmentManager().beginTransaction().remove(mapFragment).commit();
            }
        });
        lastLogInfo.show().setCanceledOnTouchOutside(false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMap);
        mapFragment.getMapAsync(PartnerAdminActivity.this);
    }

    private void buttonNavigationSettings(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(PartnerAdminActivity.this, HomeActivity.class);
                        startActivity(intent1);

                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(PartnerAdminActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(PartnerAdminActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }
}
