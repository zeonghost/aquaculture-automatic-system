package com.example.aquaculture;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText npass;//password input
    private EditText locat;//location
    private EditText species;//species
    private Button btr_res;//reset password button
    public String path1;//the device in user datebase
    public String locatget;//the database path of species and location
    public String speciesget;//species get from database
    public String uid;//uid get from database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        name = (EditText)findViewById(R.id.un);
        npass = (EditText)findViewById(R.id.pass);
        locat = (EditText)findViewById(R.id.location);
        species = (EditText)findViewById(R.id.species);
        btr_res = (Button)findViewById(R.id.btr_res);

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference Ref = db.getReference("/user");

        btr_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search username and get device path and uid
                //device path used to compare later with result of search location
                //uid used to update password

                Ref.orderByChild("username").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        uid = dataSnapshot.getKey();
                        //search location and get the path and species
                        DatabaseReference Ref2 = db.getReference();
                        Ref2.orderByChild("location").equalTo(locat.getText().toString()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                speciesget = dataSnapshot.child("species").getValue().toString();
                                locatget = dataSnapshot.getKey();
                                if(Objects.equals(speciesget, species.getText().toString())){
                                    DatabaseReference Ref3 = Ref.child(uid);
                                    Map<String, Object> passUpdate = new HashMap<>();
                                    passUpdate.put("password", npass.getText().toString());
                                    Ref3.updateChildren(passUpdate);// update password by achieve uid
                                    Toast.makeText(ForgetPasswordActivity.this, "Password Reset Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(ForgetPasswordActivity.this, "Password Reset Fail. Please check if any of the fields is incorrect.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ForgetPasswordActivity.this, "Password Reset Fail. Please check if any of the fields is incorrect.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

                Query q = Ref.orderByChild("username").equalTo(name.getText().toString());
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!(dataSnapshot.exists())){
                            Toast.makeText(ForgetPasswordActivity.this, "Password Reset Fail. Please check if any of the fields is incorrect.", Toast.LENGTH_SHORT).show();
                            return;
                        }/*
                        DatabaseReference Ref2 = db.getReference();
                        Query q = Ref2.orderByChild("location").equalTo(locat.getText().toString());
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!(dataSnapshot.exists())){
                                    Toast.makeText(ForgetPasswordActivity.this, "Password Reset Fail. Please check if any of the fields is incorrect.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                speciesget = dataSnapshot.child("species").getValue(String.class);
                                locatget = dataSnapshot.getKey();
                                if(!Objects.equals(speciesget, species.getText().toString())){
                                    Toast.makeText(ForgetPasswordActivity.this, "Password Reset Fail. Please check if any of the fields is incorrect.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        */
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
