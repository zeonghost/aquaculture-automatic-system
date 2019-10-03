package com.example.aquaculture;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aquaculture.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;//name input
    private EditText pass;//password input
    private EditText fname;//first name input
    private EditText lname;//last name input
    private Spinner role;//role
    private Button btr;
    private String getRole;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private boolean duplicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText)findViewById(R.id.un);
        pass = (EditText)findViewById(R.id.pass);
        fname = (EditText)findViewById(R.id.Fname);
        lname = (EditText)findViewById(R.id.Lname);
        role =(Spinner) findViewById(R.id.roleSele);
        btr =(Button) findViewById(R.id.btr);
        database =  FirebaseDatabase.getInstance();
        ref = database.getReference("user");

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String username = name.getText().toString();
                    ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                Toast.makeText(RegisterActivity.this, "Username: " + name.getText().toString() + " is already taken.", Toast.LENGTH_SHORT).show();
                                name.getText().clear();
                                name.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choice = getResources().getStringArray(R.array.Role);
                getRole=choice[position];
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

