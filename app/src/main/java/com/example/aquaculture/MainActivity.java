package com.example.aquaculture;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText name;//name input
    private EditText pass;//password input
    private Button login;//login button
    private Button sign;//login button
    private Button fgt;//forget password
    private String a;//uid get from username
    private String b;//uid get from password
    //private CheckBox rp;//remember password
    private CheckBox al;//auto login

    //private String rem_pass;
    private String auto_log;
    public static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        //Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        name = (EditText)findViewById(R.id.nameInput);
        pass = (EditText)findViewById(R.id.passInput);
        login = (Button)findViewById(R.id.loginBtn);
        sign = (Button)findViewById(R.id.sinbtr);
        fgt = (Button)findViewById(R.id.fgt_pass);
        //rp=(CheckBox)findViewById(R.id.cb_rp);
        al=(CheckBox)findViewById(R.id.cd_al);
        //basicReadWrite();
        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        al=(CheckBox)findViewById(R.id.cd_al);
        //basicReadWrite();
        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        al.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    auto_log="Y";
                    sp.edit()
                            .putString("auto_log1", auto_log)
                            .apply();
                    //Log.d(TAG, "Result sp-al1-changed: "+ sp.getString("auto_log1", null));
                }
                else{
                    auto_log="N";
                    sp.edit()
                            .putString("auto_log1", auto_log)
                            .apply();
                    //Log.d(TAG, "Result sp-al1-changed: "+ sp.getString("auto_log1", null));
                }
            }
        });//detech auto login and saved in SharedPreference

            if(Objects.equals(sp.getString("auto_log1", null), "Y")){//auto login, which is directly jump to HomeActivity page.
                al.setChecked(true);
                name.setText(sp.getString("username", null));
                pass.setText(sp.getString("password", null));
                login();
            }


        if(Objects.equals(sp.getString("auto_log1", null), "Y")){//if Y, then auto fill up username and password
            name.setText(sp.getString("username", null));
            pass.setText(sp.getString("password", null));
            Log.d(TAG, "SharedPref: AUTO LOG IN: " + sp.getAll().toString());
            al.setChecked(true);
            login();
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent2);
            }
        });

        fgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, ForgetPasswordActivity.class);
                startActivity(intent3);
            }
        });

    }
    public void login(){
        //showWaitingDialog();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("/user");
        myRef.orderByChild("username").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String prevChildKey) {
                a = dataSnapshot.getKey();
                b = dataSnapshot.child("password").getValue().toString();
                if(Objects.equals(b, pass.getText().toString())){
                    showWaitingDialog();
                    sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        sp.edit()
                            .putString("username", name.getText().toString())
                            .putString("password", pass.getText().toString())
                            .putString("role", dataSnapshot.child("role").getValue(String.class))
                            .putString("firstname", dataSnapshot.child("fname").getValue(String.class))
                            .putString("lastname", dataSnapshot.child("lname").getValue(String.class))
                            .apply();
                    Log.d(TAG, "SharedPref: LOG IN: " + sp.getAll().toString());
                    //save password and auto login status
                    //jump to HomeActivity page
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    return;
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

    private void showWaitingDialog() {
        ProgressDialog waitingDialog= new ProgressDialog(MainActivity.this);
        //waitingDialog.setTitle("");
        waitingDialog.setMessage("Connecting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
}
