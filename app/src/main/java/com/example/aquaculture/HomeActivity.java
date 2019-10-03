package com.example.aquaculture;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aquaculture.Model.Forecast;
import com.example.aquaculture.Model.ForecastResult;
import com.example.aquaculture.Model.SimpleExponentialSmoothing;
import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import under are for firebase
import com.example.aquaculture.Model.Pond;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class HomeActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private RecyclerView pondInfo;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;
    private FloatingActionButton addPond;
    private SharedPreferences sp;
    public static String transferData;
    public static String qrResult;
    private boolean clockDetails;
    public ProgressDialog waitingDialog;
    private Forecast forecast;
    private float highCriticalLevel;
    private float lowCriticalLevel;
    private SimpleExponentialSmoothing sme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_home);
        addPondButton();

        buttonNavigationSettings();

        pondInfo = findViewById(R.id.recyclerview);
        pondInfo.setHasFixedSize(true);
        pondInfo.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        String role = sp.getString("role", "");
        final String uid = sp.getString("uid", "");

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();
                DatabaseReference tokRef = database.getReference("/user");
                Map<String, Object> tokenUpt = new HashMap<>();
                tokenUpt.put("pushToken", token);
                tokRef.child(uid).updateChildren(tokenUpt);
            }
        });
        waitingDialog= new ProgressDialog(HomeActivity.this);
        waitingDialog.setMessage("Connecting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(true);
        waitingDialog.dismiss();

        forecast = new Forecast();
        sme = new SimpleExponentialSmoothing();

        myRef = database.getReference("/PondDetail");
        Query query = myRef.orderByChild(username).equalTo(role);

        options = new FirebaseRecyclerOptions.Builder<Pond>()
                .setQuery(query, Pond.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Pond, PondViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PondViewHolder holder, int position, @NonNull Pond model) {
                final String piID = model.getPiId();
                final String pondName = model.getPondName();
                final String location = model.getLocation();

                holder.piId.setText("Pi ID: " + piID);
                holder.pondName.setText("Pond: " + pondName);
                holder.location.setText("Location: " + location);

                transferData = piID;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferData = piID;
                        if(Objects.equals(sp.getString("role", ""), "Partner")){
                            sp.edit().putString("device", piID).apply();
                            sp.edit().putString("location", location).apply();
                            clockDetails=sp.getBoolean("clockInDetails",false);
                            if(!clockDetails){
                                checkInOutDialog();
                            } else {
                                waitingDialog.show();
                                Intent toPondInfoActivity = new Intent(HomeActivity.this, PondInfoActivity.class);
                                startActivity(toPondInfoActivity);
                            }
                        } else {
                            waitingDialog.show();
                            Intent toPondInfoActivity = new Intent(HomeActivity.this, PondInfoActivity.class);
                            startActivity(toPondInfoActivity);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.activity_home_cardview, viewGroup, false);
                return new PondViewHolder(view);
            }
        };
        pondInfo.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        getTimeOutStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        waitingDialog.dismiss();
    }

    private long firstPressedTime;//first time press back button

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            finish();
        } else {
            Toast.makeText(HomeActivity.this, "Press again to Exit", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();

        }
    }

    private void addPondButton(){
        addPond = findViewById(R.id.btnAddPond);
        addPond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
                intentIntegrator.setPrompt("QR Scanner");//set display context
                intentIntegrator.setTimeout(60000);//set time out
                intentIntegrator.setBeepEnabled(true);//set scan notice voice
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Abort Scanning", Toast.LENGTH_LONG).show();
            } else {
                qrResult = result.getContents();
                Toast.makeText(this, "Scan Result: "+ qrResult, Toast.LENGTH_LONG).show();
                Intent toQRScanner = new Intent(HomeActivity.this, AddPondActivity.class);
                startActivity(toQRScanner);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//get result of qr scanner

    private void buttonNavigationSettings(){

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(HomeActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void checkInOutDialog(){
        AlertDialog.Builder checkInOut = new AlertDialog.Builder (HomeActivity.this);
        checkInOut.setMessage("Please clock in first to proceed.");
        checkInOut.setPositiveButton("Clock In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toPartnerLogActivity = new Intent(HomeActivity.this, PartnerLogActivity.class);
                startActivity(toPartnerLogActivity);
            }
        });
        checkInOut.show();
    }

    private void getTimeOutStatus(){
        String username = sp.getString("username", "");
        String role = sp.getString("role", "");
        if(role == "Partner"){
            String path = "/PartnerLog/" + username;
            DatabaseReference ref = database.getReference(path);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long timeOutStatus = dataSnapshot.child("timeOut").getValue(Long.class);
                    if(timeOutStatus == 0){
                        TIME_IN_STATUS = 1;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void initializeForecast(){
        myRef = database.getReference("pi1-forecast");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateForecast();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateForecast(){
        myRef = database.getReference("pi1-forecast-test");
        Query query = myRef.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snaps : dataSnapshot.getChildren()){
                    if(Objects.equals(snaps.child("time").getValue(String.class), "12:00 AM")){
                        forecast.addClock0(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "1:00 AM")){
                        forecast.addClock1(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "2:00 AM")){
                        forecast.addClock2(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "3:00 AM")){
                        forecast.addClock3(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "4:00 AM")){
                        forecast.addClock4(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "5:00 AM")){
                        forecast.addClock5(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "6:00 AM")){
                        forecast.addClock6(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "7:00 AM")){
                        forecast.addClock7(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "8:00 AM")){
                        forecast.addClock8(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "9:00 AM")){
                        forecast.addClock9(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "10:00 AM")){
                        forecast.addClock10(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "11:00 AM")){
                        forecast.addClock11(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "12:00 PM")){
                        forecast.addClock12(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "1:00 PM")){
                        forecast.addClock13(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "2:00 PM")){
                        forecast.addClock14(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "3:00 PM")){
                        forecast.addClock15(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "4:00 PM")){
                        forecast.addClock16(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "5:00 PM")){
                        forecast.addClock17(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "6:00 PM")){
                        forecast.addClock18(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "7:00 PM")){
                        forecast.addClock19(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "8:00 PM")){
                        forecast.addClock20(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "9:00 PM")){
                        forecast.addClock21(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "10:00 PM")){
                        forecast.addClock22(snaps.child("val").getValue(Float.class)/10);
                    }

                    if(Objects.equals(snaps.child("time").getValue(String.class), "11:00 PM")){
                        forecast.addClock23(snaps.child("val").getValue(Float.class)/10);
                    }
                }
                forecast.collectClockLists();
                init_forecastNode();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init_forecastNode(){
        DatabaseReference refForecastNode = database.getReference("pi1-forecast");
        ForecastResult forecastResult = new ForecastResult();
        ArrayList<Float> result = new ArrayList<>();
        int size = forecast.getAllClock().size();
        for(int i = 0 ; i < size ; i++){
            sme.setValueList(forecast.getClockIndex(i));
            result.add(sme.getBestSME());
        }
        forecastResult.setHighCritical(highCriticalLevel);
        forecastResult.setLowCritical(lowCriticalLevel);
        forecastResult.update(result);
        refForecastNode.setValue(forecastResult);
    }


    private void getCriticalLevels(){
        DatabaseReference refPiPondNode = database.getReference("pi1-pond1");
        refPiPondNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Integer lowCritValue = dataSnapshot.child("low").getValue(Integer.class);
                    lowCriticalLevel = (float) lowCritValue/10.0f;
                    Integer highCritValue = dataSnapshot.child("high").getValue(Integer.class);
                    highCriticalLevel = (float) highCritValue/10.0f;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
