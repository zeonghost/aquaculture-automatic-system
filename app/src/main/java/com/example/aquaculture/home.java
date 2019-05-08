package com.example.aquaculture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import under are for firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;

public class home extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btr;//butten to jump to pond info
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        basicReadWrite();//communicate to database

        setContentView(R.layout.activity_home);
        btr = (Button)findViewById(R.id.click1);
        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to pond info page
                Intent intent = new Intent(home.this, pondInfo.class);
                startActivity(intent);
            }
        });
    }

    public void basicReadWrite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();//connect to database
        DatabaseReference myRef = database.getReference("/pi1/location");//declear path to location
        //final DatabaseReference myRef1 = database.getReference("/pi1/pond1/ch1");
        //final DatabaseReference myRef2 = database.getReference("/pi1/pond1/ch2");
        //final DatabaseReference myRef3 = database.getReference("/pi1/pond1/ch3");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);//get data from database
                TextView textElement = (TextView) findViewById(R.id.locate);//connect with text id is locate
                textElement.setText(value);//change text to value
                //get data from db
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());//log is for error message, check in logcat
            }
        });
        /*myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer val1 = dataSnapshot.getValue(Integer.class);
                final Button btr1 = (Button) findViewById(R.id.ch1);
                if(val1 == 1)
                {
                    btr1.setBackgroundColor(Color.RED);
                }
                if(val1 == 0)
                {
                    btr1.setBackgroundColor(Color.GREEN);
                }
                btr1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(val1 == 1)
                        {
                            myRef1.setValue(0);
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
        });*/
        // [END read_message]
    }

}
