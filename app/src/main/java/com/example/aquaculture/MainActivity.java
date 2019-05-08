package com.example.aquaculture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private Button login;//login button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);
        name = (EditText)findViewById(R.id.nameInput);
        pass = (EditText)findViewById(R.id.passInput);
        login = (Button)findViewById(R.id.loginBtn);
        //basicReadWrite();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("user1")){
                    if(pass.getText().toString().equals("123456")){
                        //jump to home page
                        Intent intent = new Intent(MainActivity.this, home.class);
                        startActivity(intent);
                    }
                    else{
                        //show wrong password
                        TextView textElement = (TextView) findViewById(R.id.warnMessage);
                        textElement.setVisibility(TextView.VISIBLE);
                    }
                }
                else{
                    //show wrong username
                    TextView textElement2 = (TextView) findViewById(R.id.warnMessage2);
                    textElement2.setVisibility(TextView.VISIBLE);
                }
            }
        });

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
}