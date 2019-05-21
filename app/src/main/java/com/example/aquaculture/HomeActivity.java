package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

//import under are for firebase
import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.ViewHolder.PondViewHolder;
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
    private Button addPond;
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
                String piID = model.getPiId();
                String pondName = model.getPondName();
                String location = model.getLocation();

                holder.piId.setText("Pi ID: " + piID);
                holder.pondName.setText("Pond: " + pondName);
                holder.location.setText("Location: " + location);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }//ban back button

    private void addPondButton(){
        addPond = findViewById(R.id.btnAddPond);
        addPond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toQRScanner = new Intent(HomeActivity.this, QRScannerActivity.class);
                startActivity(toQRScanner);
            }
        });
    }

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
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent2 = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });//bottom navigation

        btr1 = (Button)findViewById(R.id.exit_log);
        btr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit()
                        .clear()
                        .apply();
                Log.d(TAG,"Result: delete");
            }
        });
    }
}
