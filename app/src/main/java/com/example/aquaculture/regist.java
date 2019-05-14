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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;

public class regist extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private EditText fname;//first name input
    private EditText lname;//last name input
    //private Button login;//login button
    private Spinner role;//role
    private Button btr;
    private String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        name = (EditText)findViewById(R.id.un);
        pass = (EditText)findViewById(R.id.pass);
        fname = (EditText)findViewById(R.id.Fname);
        lname = (EditText)findViewById(R.id.Lname);
        //login = (Button)findViewById(R.id.btr);
        role =(Spinner) findViewById(R.id.roleSele);
        btr =(Button) findViewById(R.id.btr);

        final FirebaseDatabase database =  FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();

        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choice = getResources().getStringArray(R.array.Role);
                Log.d(TAG, "Role: "+ choice[position]);
                a=choice[position];
                Log.d(TAG, "Result: "+ a);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference postRef = ref.child("user");
                DatabaseReference newPostRef = postRef.push();
                Log.d(TAG, "Result2: "+ a);
                newPostRef.setValue(new Post(name.getText().toString(), pass.getText().toString(), fname.getText().toString(), lname.getText().toString(), a));

                Intent intent2 = new Intent(regist.this, MainActivity.class);
                startActivity(intent2);
            }
        });

    }
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

}



