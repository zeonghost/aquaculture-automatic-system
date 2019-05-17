package com.example.aquaculture;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private Button login;//login button
    private Button sign;//login button
    private CheckBox rp;//remember password
    private CheckBox al;//auto login
    private String rem_pass;
    private String auto_log;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        FirebaseApp.initializeApp(this);
        name = (EditText)findViewById(R.id.nameInput);
        pass = (EditText)findViewById(R.id.passInput);
        login = (Button)findViewById(R.id.loginBtn);
        sign = (Button)findViewById(R.id.sinbtr);
        rp=(CheckBox)findViewById(R.id.cb_rp);
        al=(CheckBox)findViewById(R.id.cd_al);
        //basicReadWrite();
        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        //rem_pass = rp.isChecked();
        //auto_log = al.isChecked();
        //rp.setChecked(true);

        rp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rem_pass="Y";
                    sp.edit()
                            .putString("rem_pass1", rem_pass)
                            .apply();
                    Log.d(TAG, "Result sp-rp1-changed: "+ sp.getString("rem_pass1", null));
                }
                else{
                    rem_pass="N";
                    sp.edit()
                            .putString("rem_pass1", rem_pass)
                            .apply();
                    Log.d(TAG, "Result sp-rp1-changed: "+ sp.getString("rem_pass1", null));
                }
            }
        });//detech remember password and save in SharedPreferences
        al.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    auto_log="Y";
                    sp.edit()
                            .putString("auto_log1", auto_log)
                            .apply();
                    Log.d(TAG, "Result sp-al1-changed: "+ sp.getString("auto_log1", null));
                }
                else{
                    auto_log="N";
                    sp.edit()
                            .putString("auto_log1", auto_log)
                            .apply();
                    Log.d(TAG, "Result sp-al1-changed: "+ sp.getString("auto_log1", null));
                }
            }
        });//detech auto login and saved in SharedPreference

        //Log.d(TAG, "Result sp-rp1: "+ sp.getString("rem_pass1", null));
        //Log.d(TAG, "Result sp-al1: "+ sp.getString("auto_log1", null));

        if(Objects.equals(sp.getString("rem_pass1", null), "Y")){//if Y, then auto fill up username and password
            rp.setChecked(true);
            name.setText(sp.getString("username", null));
            pass.setText(sp.getString("password", null));
            Log.d(TAG, "Reading sp");
            if(Objects.equals(sp.getString("auto_log1", null), "Y")){//auto login, which is directly jump to home page.
                al.setChecked(true);
                Intent intent = new Intent(MainActivity.this, home.class);
                startActivity(intent);
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInFunction();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, regist.class);
                startActivity(intent2);
            }
        });
    }


    public void logInFunction(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/user");

        String username = name.getText().toString();
        final String password = pass.getText().toString();

        if(!username.isEmpty() && !password.isEmpty()){
            Query checkUserPass = myRef.child(username).child("password");
            checkUserPass.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String fetchedPass = dataSnapshot.getValue().toString();
                    if(Objects.equals(fetchedPass, password)){
                        Intent intent = new Intent(MainActivity.this, home.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect Username or Password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(rp.isChecked()){
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit()
                        .putString("username", name.getText().toString())
                        .putString("password", pass.getText().toString())
                        .apply();
            }
        } else {
            if(username.isEmpty()){
                Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            }

            if(password.isEmpty()){
                Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}