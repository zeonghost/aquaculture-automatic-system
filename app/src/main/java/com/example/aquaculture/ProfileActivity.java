package com.example.aquaculture;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");
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
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent2 = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }
}
