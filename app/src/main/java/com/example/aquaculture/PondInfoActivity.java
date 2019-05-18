package com.example.aquaculture;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;

public class PondInfoActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        basicReadWrite();
        setContentView(R.layout.activity_pond_info);
        setTitle("Pond Details");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(PondInfoActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        break;
                    case R.id.item3:
                        Intent intent2 = new Intent(PondInfoActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
        final Switch sw = (Switch) findViewById(R.id.autosw);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference swRef = db.getReference("/pi1-pond1/auto");
        swRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Integer val4 = dataSnapshot.getValue(Integer.class);
                if(val4 == 1){
                    sw.setChecked(true);
                }
                else{
                    sw.setChecked(false);
                }//check auto model status and change switch display
                sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            swRef.setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Automatic Model ON", Toast.LENGTH_SHORT).show();//show message
                        }
                        else{
                            swRef.setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Automatic Model OFF", Toast.LENGTH_SHORT).show();
                        }
                    }//upload database when switch work
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public void basicReadWrite() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/pi1-detail/location");
        final DatabaseReference myRef1 = database.getReference("/pi1-pond1/ch1");
        final DatabaseReference myRef2 = database.getReference("/pi1-pond1/ch2");
        final DatabaseReference myRef3 = database.getReference("/pi1-pond1/ch3");
        DatabaseReference myRef4 = database.getReference("/pi1-pond1/temp");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                TextView textElement = (TextView) findViewById(R.id.locate);
                textElement.setText(value);
                //get data from db
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            } 
        });

        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer val4 = dataSnapshot.getValue(Integer.class);
                TextView textElement = (TextView) findViewById(R.id.tempRead);
                textElement.setText(val4.toString());
                //get current tempeature data from database and display
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myRef1.addValueEventListener(new ValueEventListener() {//button for ch1
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer val1 = dataSnapshot.getValue(Integer.class);
                final Button btr1 = (Button) findViewById(R.id.ch1);
                if(val1 == 1)//1 means on
                {
                    btr1.setBackgroundColor(Color.RED);//change color
                }
                if(val1 == 0)//0 means off
                {
                    btr1.setBackgroundColor(Color.GREEN);
                }
                btr1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val1 == 1)
                        {
                            myRef1.setValue(0);//if click then change status
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 1", Toast.LENGTH_SHORT).show();
                        }
                        if(val1 == 0)
                        {
                            myRef1.setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 1", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //get data from db
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

       myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer val2 = dataSnapshot.getValue(Integer.class);
                final Button btr2 = (Button) findViewById(R.id.ch2);
                if(val2 == 1)
                {
                    btr2.setBackgroundColor(Color.RED);
                }
                if(val2 == 0)
                {
                    btr2.setBackgroundColor(Color.GREEN);
                }
                btr2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val2 == 1)
                        {
                            myRef2.setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 2", Toast.LENGTH_SHORT).show();
                        }
                        if(val2 == 0)
                        {
                            myRef2.setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 2", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //get data from db
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer val3 = dataSnapshot.getValue(Integer.class);
                final Button btr3 = (Button) findViewById(R.id.ch3);
                if(val3 == 1)
                {
                    btr3.setBackgroundColor(Color.RED);
                }
                if(val3 == 0)
                {
                    btr3.setBackgroundColor(Color.GREEN);
                }
                btr3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val3 == 1)
                        {
                            myRef3.setValue(0);
                            Toast.makeText(PondInfoActivity.this, "Turned off ch 3", Toast.LENGTH_SHORT).show();
                        }
                        if(val3 == 0)
                        {
                            myRef3.setValue(1);
                            Toast.makeText(PondInfoActivity.this, "Turned on ch 3", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //get data from db
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        // [END read_message]
    }

}
