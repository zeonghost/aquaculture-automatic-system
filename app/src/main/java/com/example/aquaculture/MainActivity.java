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
    private String a;//uid get from username
    private String b;//uid get from password
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
        /*
        sp.edit().
                .clear();
                .apply();
         */
        //code for delete save status

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("/user");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef2 = database.getReference("/user");
        //final DatabaseReference myRef3 = database.getReference("/allUser");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.orderByChild("username").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                         a = dataSnapshot.getKey();
                        //Log.d(TAG,"Result1:" + a);
                        myRef2.orderByChild("password").equalTo(pass.getText().toString()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                b = dataSnapshot.getKey();
                                Log.d(TAG,"Result2:" + b);
                                Log.d(TAG,"Result3:" + a);
                                if(Objects.equals(a, b))
                                {
                                    if(rp.isChecked())
                                    {
                                        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                                        sp.edit()
                                                .putString("username", name.getText().toString())
                                                .putString("password", pass.getText().toString())
                                                //.putString("auto_log1", auto_log)
                                                //.putString("rem_pass1", rem_pass)
                                                .apply();
                                        //Log.d(TAG, "saving sp");
                                        //Log.d(TAG, "Result sp-rp2: "+ sp.getString("rem_pass1", null));
                                        //Log.d(TAG, "Result sp-al2: "+ sp.getString("auto_log1", null));
                                    }//save password and auto login status
                                    //jump to home page
                                    Intent intent = new Intent(MainActivity.this, home.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    //TextView textElement = (TextView) findViewById(R.id.warnMessage);
                                    //textElement.setVisibility(TextView.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                                //TextView textElement = (TextView) findViewById(R.id.warnMessage);
                                //textElement.setVisibility(TextView.VISIBLE);
                            }
                        });
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
                    // i don't know why i can't delete those four(onChildChanged, Removed, Moved,Cancelled)
                    // it will cause error if delete
                    // the one actual work is onChildAdded
                });
                /*
                myRef2.orderByChild("password").equalTo(pass.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        b = dataSnapshot.getKey();
                        Log.d(TAG,"Result2:" + b);
                        Log.d(TAG,"Result3:" + a);
                        if(a == b)
                        {
                            //jump to home page
                            //Log.d(TAG, " Result4: "+ a);
                            //Log.d(TAG, " Result5: "+ b);
                            Intent intent = new Intent(MainActivity.this, home.class);
                            startActivity(intent);
                        }
                        else
                        {
                            TextView textElement = (TextView) findViewById(R.id.warnMessage);
                            textElement.setVisibility(TextView.VISIBLE);
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //System.out.println(dataSnapshot.getKey());
                        //Log.d(TAG,"Result2:" + dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        //System.out.println(dataSnapshot.getKey());
                        //Log.d(TAG,"Result3: " + dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //System.out.println(dataSnapshot.getKey());
                        //Log.d(TAG,"Result4: " + dataSnapshot.getKey());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //TextView textElement = (TextView) findViewById(R.id.warnMessage);
                        //textElement.setVisibility(TextView.VISIBLE);
                    }
                });
                */
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



}

//communicate with databasse
    /*
    public void basicReadWrite() {
        // [START write_message]
        // Write a message to the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/pi2/pond1/ch1");
        //set path
        //myRef.setValue("Hello, World!");
        //update val
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                //TextView textElement = (TextView) findViewById(R.id.this_is_id_name);
                //textElement.setText(value);
                //get data from db

                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        // [END read_message]
    }
    */