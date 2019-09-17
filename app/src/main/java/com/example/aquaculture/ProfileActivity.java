package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private SharedPreferences sp;
    private TextView fullname;
    private TextView username;
    private CardView partnerCard;
    private CardView pondCard;
    private CardView timeCard;
    private CardView signOutCard;
    private LinearLayout pondOptions;
    private LinearLayout partnerOptions;
    private boolean isPondVisible;
    private boolean isPartnerVisible;
    private TextView addAssignPartner;
    private TextView unlinkPartner;
    private TextView updatePond;
    private TextView deletePond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        buttonNavigationSettings();
        fullname = findViewById(R.id.txtUserFullName);
        username = findViewById(R.id.username);
        partnerCard = findViewById(R.id.cardViewPartner);
        partnerOptions = findViewById(R.id.layoutPartnerOptions);
        pondCard = findViewById(R.id.cardViewPond);
        pondOptions = findViewById(R.id.layoutPondOptions);
        timeCard = findViewById(R.id.cardViewTimeClock);
        signOutCard = findViewById(R.id.cardViewSignOut);
        addAssignPartner = findViewById(R.id.textViewAddAssignPartner);
        unlinkPartner = findViewById(R.id.textViewUnlinkPartner);
        updatePond = findViewById(R.id.textViewEditPond);
        deletePond = findViewById(R.id.textViewDeletePond);

        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        String role = sp.getString("role", null);

        if(Objects.equals(role, "Partner")){
            partnerCard.setVisibility(View.GONE);
            pondCard.setVisibility(View.GONE);
        } else {
            timeCard.setVisibility(View.GONE);
        }

        fullname.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        username.setText(sp.getString("username", null));

        pondOptions.setVisibility(View.GONE);
        partnerOptions.setVisibility(View.GONE);
        isPondVisible = false;
        isPartnerVisible = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PartnerLogActivity.class);
                startActivity(intent);
            }
        });

        pondCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPondVisible){
                    pondOptions.setVisibility(View.GONE);
                    isPondVisible = false;
                } else {
                    pondOptions.setVisibility(View.VISIBLE);
                    isPondVisible = true;
                }
            }
        });

        partnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPartnerVisible){
                    partnerOptions.setVisibility(View.GONE);
                    isPartnerVisible = false;
                } else {
                    partnerOptions.setVisibility(View.VISIBLE);
                    isPartnerVisible = true;
                }
            }
        });

        signOutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean clockedIn = sp.getBoolean("clockInDetails", false);
                Log.d(TAG, "onClick: SP CLOCKED IN " + clockedIn);

                if(clockedIn){
                    AlertDialog.Builder timeOutDialog = new AlertDialog.Builder(ProfileActivity.this);
                    timeOutDialog.setTitle("Reminder:");
                    timeOutDialog.setMessage("Please clock out prior signing out.");
                    timeOutDialog.setPositiveButton("Clock Out", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            Intent intent = new Intent(ProfileActivity.this, PartnerLogActivity.class);
                            startActivity(intent);
                        }
                    });
                    timeOutDialog.show();

//                    DatabaseReference myRef = database.getReference("PartnerLog");
//                    long currentTimestamp = System.currentTimeMillis();
//                    myRef.child(sp.getString("username", null)).child("timeOut").setValue(currentTimestamp);
//
//                    DatabaseReference myLog = database.getReference("pi1-log");
//                    String key = myLog.push().getKey();
//                    myLog.child(key).child("logTime").setValue(currentTimestamp);
//                    myLog.child(key).child("logDetail").setValue(sp.getString("firstname", "") + " " + sp.getString("lastname", "") + " has clocked out.");
//                    myLog.child(key).child("username").setValue(sp.getString("username", ""));
//
//
//                    sp.edit().putBoolean("clockInDetails", false).apply();
//                    Toast.makeText(ProfileActivity.this, "You have successfully clocked out, now may now can log out safely.", Toast.LENGTH_SHORT).show();
                } else {
                    sp.edit().clear().apply();
                    TIME_IN_STATUS = 0;
                    Intent intent4 = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent4);
                }
            }
        });

        addAssignPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PartnerAdminActivity.class);
                startActivity(intent);
            }
        });

        unlinkPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UnlinkPartnerActivity.class);
                startActivity(intent);
            }
        });

        updatePond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPondToEditActivity = new Intent(ProfileActivity.this, PondToEditActivity.class);
                startActivity(toPondToEditActivity);
            }
        });

        deletePond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPondDeleteActivity = new Intent(ProfileActivity.this, PondToDeleteActivity.class);
                startActivity(toPondDeleteActivity);
            }
        });

        Button btr1 = (Button)findViewById(R.id.btr1);
        btr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PartnerAdminActivity.class);
                startActivity(intent);
            }
        });

        Button btrEdit = findViewById(R.id.btrEdit);
        btrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog();
            }
        });

        Button btr3 = findViewById(R.id.btr3);
        btr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ForecastActivityTest.class);
                startActivity(intent);
            }
        });

        Button btr4 = findViewById(R.id.btr4);
        btr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, WeatherActivityTest.class);
                startActivity(intent);
            }
        });

        hideTestingButtons(btr1, btr3, btr4);
    }

    public void editDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_edit_profile, null);
        final DatabaseReference myRef = database.getReference("user");

        final EditText fname = (EditText) textEntryView.findViewById(R.id.et_fname);
        final EditText lname = (EditText)textEntryView.findViewById(R.id.et_lname);
        final Button del = (Button) textEntryView.findViewById(R.id.btr_del);

        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        final String id = sp.getString("uid", null);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(ProfileActivity.this);
                normalDialog.setTitle("Warning");
                normalDialog.setMessage("Are you sure you want to delete this account?");
                normalDialog.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myRef.child(id).removeValue();
                                sp.edit().clear().apply();
                                TIME_IN_STATUS = 0;
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                normalDialog.show();
            }
        });

        myRef.orderByKey().equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fname.setText(dataSnapshot.child("fname").getValue().toString());
                lname.setText(dataSnapshot.child("lname").getValue().toString());
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

        AlertDialog.Builder ad1 = new AlertDialog.Builder(ProfileActivity.this);

        ad1.setTitle("Edit Profile:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String fn = fname.getText().toString();
                String ln = lname.getText().toString();
                Map<String, Object> profileupdate = new HashMap<>();
                profileupdate.put("fname", fn);
                profileupdate.put("lname", ln);
                myRef.child(id).updateChildren(profileupdate);
                sp.edit()
                        .putString("firstname", fn)
                        .putString("lastname", ln)
                        .apply();
                fullname.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
                username.setText(sp.getString("username", null));
                Toast.makeText(ProfileActivity.this, "Edit Success", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.show();
    }

    private void buttonNavigationSettings() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(ProfileActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        break;
                }
                return false;
            }
        });
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void hideTestingButtons(Button a, Button b, Button c){
        a.setVisibility(View.GONE);
        b.setVisibility(View.GONE);
        c.setVisibility(View.GONE);
    }
}
