package com.example.aquaculture;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private Button login;//login button
    private Button sign;//login button
    private String a;
    private String b;
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
        //basicReadWrite();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("/user");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef2 = database.getReference("/user");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.orderByChild("username").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                         a = dataSnapshot.getKey();
                        //System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result1:" + a);
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result2:" + dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result3: " + dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result4: " + dataSnapshot.getKey());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        TextView textElement = (TextView) findViewById(R.id.warnMessage2);
                        textElement.setVisibility(TextView.VISIBLE);
                    }
                    // i don't know why i can't delete those four(onChildChanged, Removed, Moved,Cancelled)
                    // it will cause error if delete
                    // the one actual work is onChildAdded
                });
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
                        System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result2:" + dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result3: " + dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        System.out.println(dataSnapshot.getKey());
                        Log.d(TAG,"Result4: " + dataSnapshot.getKey());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        TextView textElement = (TextView) findViewById(R.id.warnMessage);
                        textElement.setVisibility(TextView.VISIBLE);
                    }
                });

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