package com.example.aquaculture;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private Button login;//login button
    private Button sign;//login button
    private Button fgt;//forget password
    private String a;//uid get from username
    private String b;//uid get from password
    private CheckBox al;//auto login
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    //private String rem_pass;
    private String auto_log;
    public static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.nameInput);
        pass = (EditText)findViewById(R.id.passInput);
        login = (Button)findViewById(R.id.loginBtn);
        sign = (Button)findViewById(R.id.sinbtr);
        fgt = (Button)findViewById(R.id.fgt_pass);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/user");
        al=(CheckBox)findViewById(R.id.cd_al);

        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        al=(CheckBox)findViewById(R.id.cd_al);
        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        al.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    auto_log="Y";
                    sp.edit().putString("auto_log1", auto_log).apply();
                }
                else{
                    auto_log="N";
                    sp.edit()
                            .putString("auto_log1", auto_log)
                            .apply();
                }
            }
        });//detech auto login and saved in SharedPreference

        if(Objects.equals(sp.getString("auto_log1", null), "Y")){//auto login, which is directly jump to HomeActivity page.
            al.setChecked(true);
            name.setText(sp.getString("username", null));
            pass.setText(sp.getString("password", null));
            if(name.getText().toString().isEmpty() && pass.getText().toString().isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter a username or password.", Toast.LENGTH_SHORT).show();
            }
            else{
                login();
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //firebaseConnectionState();
                if(name.getText().toString().isEmpty() && pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter a username or password.", Toast.LENGTH_SHORT).show();
                } else if(name.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();
                } else if(pass.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
                } else {
                    checkUserNameThenLogin();
                }

            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent2);
            }
        });

        fgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, ForgetPasswordActivity.class);
                startActivity(intent3);
            }
        });

        checkGPSServices();
        checkLocationService();
    }


    public void login(){
        final ProgressDialog waitingDialog= new ProgressDialog(MainActivity.this);
        waitingDialog.setMessage("Connecting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(true);
        waitingDialog.show();
        myRef.orderByChild("username").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String prevChildKey) {
                a = dataSnapshot.getKey();
                b = dataSnapshot.child("password").getValue().toString();
                if(Objects.equals(b, pass.getText().toString()))
                {
                    sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        sp.edit()
                            .putString("username", name.getText().toString())
                            .putString("password", pass.getText().toString())
                            .putString("role", dataSnapshot.child("role").getValue(String.class))
                            .putString("firstname", dataSnapshot.child("fname").getValue(String.class))
                            .putString("lastname", dataSnapshot.child("lname").getValue(String.class))
                            .putString("uid", dataSnapshot.getKey())
                            .apply();
                    Log.d(TAG, "SharedPref: LOG IN: " + sp.getAll().toString());

                    //save password and auto login status
                    //jump to HomeActivity page
                    waitingDialog.dismiss();
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    waitingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserNameThenLogin(){
        myRef.orderByChild("username").equalTo(name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //FOR CHECKING LOCATION AND GPS SERVICES

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
   /*
    //TESTING PURPOSES
    private void firebaseConnectionState(){
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "connected");
                    Toast.makeText(MainActivity.this, "CONNECTED TO DATABASE", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "not connected");
                    Toast.makeText(MainActivity.this, "DISCONNECTED FROM DATABASE", Toast.LENGTH_SHORT).show();
                }
                if(!connected)
                    Toast.makeText(MainActivity.this, "Cannot connect to database. Please check your Internet connection.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });
    }*/
}
