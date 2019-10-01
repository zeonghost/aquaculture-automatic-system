package com.example.aquaculture;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.Model.pondAdd;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPondActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText pond;
    private EditText locat;
    private EditText ch1n;
    private EditText ch2n;
    private EditText ch3n;
    private EditText spec;
    private EditText width;
    private EditText length;
    private EditText depth;
    private Button btr;
    private String piId;
    private TextView dev;
    public static boolean exist = false;
    private FirebaseDatabase database;
    private DatabaseReference addPondRef;
    private DatabaseReference linkPondRef;
    private String username;
    private SharedPreferences sp;
    private CheckBox activeChannel1;
    private CheckBox activeChannel2;
    private CheckBox activeChannel3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pond);
        piId = HomeActivity.qrResult; // the result get from qr scanner, use this in real
        dev = (TextView)findViewById(R.id.piId);
        dev.setText(piId);
        pond = (EditText)findViewById(R.id.pond);
        locat = (EditText)findViewById(R.id.locat);
        ch1n = (EditText)findViewById(R.id.ch1n);
        ch2n = (EditText)findViewById(R.id.ch2n);
        ch3n = (EditText)findViewById(R.id.ch3n);
        spec = (EditText)findViewById(R.id.spec);
        width = (EditText)findViewById(R.id.width);
        length = (EditText)findViewById(R.id.length);
        depth = (EditText)findViewById(R.id.depth);
        btr =(Button) findViewById(R.id.btr);
        activeChannel1 = findViewById(R.id.activeChannel1);
        activeChannel2 = findViewById(R.id.activeChannel2);
        activeChannel3 = findViewById(R.id.activeChannel3);

        ch1n.setVisibility(View.GONE);
        ch1n.setText("non-active");
        ch2n.setVisibility(View.GONE);
        ch2n.setText("non-active");
        ch3n.setVisibility(View.GONE);
        ch3n.setText("non-active");

        activeChannel1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ch1n.setVisibility(View.VISIBLE);
                    ch1n.setText("");
                } else {
                    ch1n.setVisibility(View.GONE);
                    ch1n.setText("non-active");
                }
            }
        });

        activeChannel2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ch2n.setVisibility(View.VISIBLE);
                    ch2n.setText("");
                } else {
                    ch2n.setVisibility(View.GONE);
                    ch2n.setText("non-active");
                }
            }
        });

        activeChannel3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ch3n.setVisibility(View.VISIBLE);
                    ch3n.setText("");
                } else {
                    ch3n.setVisibility(View.GONE);
                    ch3n.setText("non-active");
                }
            }
        });

        database =  FirebaseDatabase.getInstance();
        addPondRef = database.getReference();
        linkPondRef = database.getReference("/PondDetail");

        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        linkPondRef.orderByChild("piId").equalTo(piId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "linkPondRef DataSnap: " + dataSnapshot.child(username).getValue());
                if(dataSnapshot.child(username).getValue() == null){
                    showDialog();
                } else {
                    showDialogAlreadyLinked();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                exist = true;
                Log.d(TAG, "Result-exist2: "+ exist);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                exist = true;
                Log.d(TAG, "Result-exist3: "+ exist);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                exist = true;
                Log.d(TAG, "Result-exist4: "+ exist);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = pond.getText().toString();
                String l = locat.getText().toString();
                String c1 = ch1n.getText().toString().trim();
                String c2 = ch2n.getText().toString().trim();
                String c3 = ch3n.getText().toString().trim();
                String s = spec.getText().toString();
                String w = width.getText().toString();
                String le = length.getText().toString();
                String d = depth.getText().toString();
                String pi = piId;


                if(p.isEmpty() || l.isEmpty() || s.isEmpty()|| w.isEmpty()|| le.isEmpty()|| d.isEmpty() || c1.isEmpty() || c2.isEmpty() || c3.isEmpty()){
                    Toast.makeText(AddPondActivity.this, "Please fill up all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!isNumericzidai(w)||!isNumericzidai(le)||!isNumericzidai(d)){
                    if(!isNumericzidai(w)){
                        Toast.makeText(AddPondActivity.this, "The width should be number!", Toast.LENGTH_SHORT).show();
                        width.getText().clear();
                        width.requestFocus();
                    }
                    if(!isNumericzidai(le)){
                        Toast.makeText(AddPondActivity.this, "The length should be number!", Toast.LENGTH_SHORT).show();
                        length.getText().clear();
                        length.requestFocus();
                    }
                    if(!isNumericzidai(d)){
                        Toast.makeText(AddPondActivity.this, "The depth should be number!", Toast.LENGTH_SHORT).show();
                        depth.getText().clear();
                        depth.requestFocus();
                    }
                } else{
                    Float wid = Float.parseFloat(w);
                    Float len = Float.parseFloat(le);
                    Float dep = Float.parseFloat(d);
                    pondAdd newPond = new pondAdd(pi, p, l, c1, c2, c3, s, wid, len, dep);
                    String key = piId + "-detail";
                    addPondRef.child(key).setValue(newPond);

                    String username = sp.getString("username", "");
                    String role = sp.getString("role", "");
                    linkPondRef.child(piId).child(username).setValue(role);
                    linkPondRef.child(piId).child("location").setValue(l);
                    linkPondRef.child(piId).child("pondName").setValue(p);
                    linkPondRef.child(piId).child("piId").setValue(piId);

                    Toast.makeText(AddPondActivity.this, "Added Pond Successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddPondActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(AddPondActivity.this);
        normalDialog.setCancelable(false);
        normalDialog.setTitle("Good News,").setMessage("Pond exists!");
        normalDialog.setPositiveButton("Back to Home page",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddPondActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
        normalDialog.setNegativeButton("Link Account",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = sp.getString("username", "");
                        String role = sp.getString("role", "");
                        linkPondRef.child(piId).child(username).setValue(role);
                        Intent intent = new Intent(AddPondActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
        normalDialog.show().setCanceledOnTouchOutside(false);
    }

    public static boolean isNumericzidai(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void showDialogAlreadyLinked(){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(AddPondActivity.this);
        normalDialog.setCancelable(false);
        normalDialog.setTitle("Oops!").setMessage("This account is already linked to this hardware.");
        normalDialog.setPositiveButton("Back to Home page",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddPondActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
        normalDialog.show().setCanceledOnTouchOutside(false);
    }
}
