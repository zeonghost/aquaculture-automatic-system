package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SharedPreferences sp;
    private TextView fullname;
    private TextView username;
    private ImageView signOut;
    private CardView partnerCard;
    private CardView pondCard;
    private CardView timeCard;
    private LinearLayout pondOptions;
    private LinearLayout partnerOptions;
    private boolean isPondVisible;
    private boolean isPartnerVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        buttonNavigationSettings();
        fullname = findViewById(R.id.txtUserFullName);
        username = findViewById(R.id.username);
        signOut = findViewById(R.id.imgViewSignOut);
        partnerCard = findViewById(R.id.cardViewPartner);
        partnerOptions = findViewById(R.id.layoutPartnerOptions);
        pondCard = findViewById(R.id.cardViewPond);
        pondOptions = findViewById(R.id.layoutPondOptions);
        timeCard = findViewById(R.id.cardViewTimeClock);

        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        fullname.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        username.setText(sp.getString("username", null));

        pondOptions.setVisibility(View.GONE);
        partnerOptions.setVisibility(View.GONE);
        isPondVisible = false;
        isPartnerVisible = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        pondCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPondVisible){
                    pondOptions.setVisibility(View.GONE);
                    isPondVisible = false;
                } else {
                    pondOptions.setVisibility(View.VISIBLE);
                    isPondVisible = true;
                }
            }
        });

        partnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPartnerVisible){
                    partnerOptions.setVisibility(View.GONE);
                    isPartnerVisible = false;
                } else {
                    partnerOptions.setVisibility(View.VISIBLE);
                    isPartnerVisible = true;
                }
            }
        });

        timeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PartnerLogActivity.class);
                startActivity(intent);
            }
        });


        Button btr1 = (Button)findViewById(R.id.btr1);
        btr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PartnerAdminActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit()
                        .clear()
                        .apply();
                TIME_IN_STATUS = 0;
                Intent intent4 = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent4);
            }
        });

        Button btr3 = findViewById(R.id.btr3);
        btr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ForecastActivityTest.class);
                startActivity(intent);
            }
        });

        Button btr4 = findViewById(R.id.btr4);
        btr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, WeatherActivityTest.class);
                startActivity(intent);
            }
        });
    }

    private void buttonNavigationSettings() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(ProfileActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        break;
                }
                return false;
            }
        });
    }
}
