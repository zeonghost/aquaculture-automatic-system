package com.example.aquaculture;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;

public class pondInfo extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pond_info);
    }

    public void basicReadWrite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/pi1/location");
        final DatabaseReference myRef1 = database.getReference("/pi1/pond1/ch1");
        final DatabaseReference myRef2 = database.getReference("/pi1/pond1/ch2");
        final DatabaseReference myRef3 = database.getReference("/pi1/pond1/ch3");
        final DatabaseReference myRef4 = database.getReference("/pi1/pond1/temp");
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
                        }
                        if(val1 == 0)
                        {
                            myRef1.setValue(1);
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
                        }
                        if(val2 == 0)
                        {
                            myRef2.setValue(1);
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
                        }
                        if(val3 == 0)
                        {
                            myRef3.setValue(1);
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
