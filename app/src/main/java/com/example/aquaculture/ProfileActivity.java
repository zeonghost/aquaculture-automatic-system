package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button partnerPage;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //setTitle("Profile");
        getSupportActionBar().hide();
        buttonNavigationSettings();
        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        TextView un = (TextView) findViewById(R.id.username);
        un.setText(sp.getString("username", null));
        Button btr1 = (Button)findViewById(R.id.btr1);
        btr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PartnerAdminActivity.class);
                startActivity(intent);
            }
        });

        Button btr2 = (Button)findViewById(R.id.btr2);
        btr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit()
                        .clear()
                        .apply();
                //MainActivity.sp.edit().clear().apply();
                Intent intent4 = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent4);
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
                        Intent intent3 = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }
}
