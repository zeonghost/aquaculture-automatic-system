package com.example.aquaculture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aquaculture.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private EditText fname;//first name input
    private EditText lname;//last name input
    private Spinner role;//role
    private Button btr;
    private String getRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //setTitle("Register");
        getSupportActionBar().hide();
        name = (EditText)findViewById(R.id.un);
        pass = (EditText)findViewById(R.id.pass);
        fname = (EditText)findViewById(R.id.Fname);
        lname = (EditText)findViewById(R.id.Lname);
        role =(Spinner) findViewById(R.id.roleSele);
        btr =(Button) findViewById(R.id.btr);

        final FirebaseDatabase database =  FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("user");

        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choice = getResources().getStringArray(R.array.Role);
                Log.d(TAG, "Role: "+ choice[position]);
                getRole=choice[position];
                Log.d(TAG, "Result: "+ getRole);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String password = pass.getText().toString();
                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();


                if(username.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill up all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    User newUser = new User(username, password, firstname, lastname, getRole);
                    String key = ref.push().getKey();
                    ref.child(key).setValue(newUser);
                    Toast.makeText(RegisterActivity.this, "Registered Account Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}

// old codes
/*
    public static class Post{

        public String username;
        public String password;
        public String fname;
        public String lname;
        public String role;

        public Post(String username, String password, String fname, String lname, String role){
            this.username = username;
            this.password = password;
            this.fname = fname;
            this.lname = lname;
            this.role = role;
        }
    }
    */

    /*
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(registerActivity.this, homeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        break;
                    case R.id.item3:
                        Intent intent2 = new Intent(registerActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
        */


