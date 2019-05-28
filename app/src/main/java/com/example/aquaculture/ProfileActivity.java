package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

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
                showAddDialog();
            }
        });

        Button btr2 = (Button)findViewById(R.id.btr2);
        btr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit()
                        .clear()
                        .apply();
                Intent intent4 = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent4);
            }
        });

    }

    protected void showAddDialog() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog, null);
        final EditText top = (EditText) textEntryView.findViewById(R.id.editTextNum1);
        final EditText bottom = (EditText)textEntryView.findViewById(R.id.editTextNum2);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(ProfileActivity.this);
        ad1.setTitle("Update Critical level:");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                int topnum = Integer.parseInt(top.getText().toString());
                int botnum = Integer.parseInt(bottom.getText().toString());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("pi1-pond1");
                Map<String, Object> passUpdate = new HashMap<>();
                passUpdate.put("high", topnum);
                passUpdate.put("low", botnum);
                myRef.updateChildren(passUpdate);
            }
        });
        ad1.show();
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
