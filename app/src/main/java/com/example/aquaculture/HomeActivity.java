package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;

import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import under are for firebase
import com.example.aquaculture.Model.Pond;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "LOG DATA: ";
    private Button btr1;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private RecyclerView pondInfo;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;
    private FloatingActionButton addPond;
    public static String transferData;
    public static String qrResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_home);
        setTitle("Home");

        addPondButton();
        buttonNavigationSettings();

        pondInfo = findViewById(R.id.recyclerview);
        pondInfo.setHasFixedSize(true);
        pondInfo.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        myRef = database.getReference("/PondDetail");
        //Query query = myRef.orderByKey().equalTo("pi1");

        options = new FirebaseRecyclerOptions.Builder<Pond>()
                .setQuery(myRef, Pond.class)   //mRef in this parameter can be changed into a more specific query like in LINE 50.
                .build();

        adapter = new FirebaseRecyclerAdapter<Pond, PondViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PondViewHolder holder, int position, @NonNull Pond model) {
                final String piID = model.getPiId();
                String pondName = model.getPondName();
                String location = model.getLocation();

                holder.piId.setText("Pi ID: " + piID);
                holder.pondName.setText("Pond: " + pondName);
                holder.location.setText("Location: " + location);

                transferData = piID;
                Log.d(TAG, "Result-2: "+ transferData);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferData = piID;
                        Log.d(TAG, "Result-2: "+ transferData);
                        //FOR NOW THIS ONLY GOES TO PI1. Have not figured out how to filter other Pi's
                        Intent toPondInfoActivity = new Intent(HomeActivity.this, PondInfoActivity.class);
                        startActivity(toPondInfoActivity);
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.activity_home_cardview, viewGroup, false);
                PondViewHolder holder = new PondViewHolder(view);
                return holder;
            }
        };
        pondInfo.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private long firstPressedTime;//first time press back buttom

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            //super.onBackPressed();
            //System.exit(0);
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
                //Intent toQRScanner = new Intent(HomeActivity.this, AddPondActivity.class);
                //startActivity(toQRScanner);//this jump is only for testing

                IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
                intentIntegrator.setPrompt("QR Scanner");//set display context
                intentIntegrator.setTimeout(60000);//set time out
                intentIntegrator.setBeepEnabled(true);//set scan notice voice
                intentIntegrator.initiateScan();

                //use this code for real scan
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Error", Toast.LENGTH_LONG).show();
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
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
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
    }
}
