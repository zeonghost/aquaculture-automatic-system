package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
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
    private Button btr1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        basicReadWrite();//communicate to database
        setContentView(R.layout.activity_home);
        setTitle("Home");//set action bar name

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> home
                        Intent intent1 = new Intent(home.this, home.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent2 = new Intent(home.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });//bottom navigation

        btr = (Button)findViewById(R.id.click1);
        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to pond info page
                Intent intent = new Intent(home.this, pondInfo.class);
                startActivity(intent);
            }
        });

        btr1 = (Button)findViewById(R.id.exit_log);
        btr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp.edit()
                        .clear()
                        .apply();
                Log.d(TAG,"Result: delete");
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }//ban back button




    public void basicReadWrite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();//connect to database
        DatabaseReference myRef = database.getReference("/pi1-detail/location");//declear path to location
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
        // [END read_message]
    }

}
