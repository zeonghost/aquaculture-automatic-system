package com.example.aquaculture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Model.Partner;
import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.rpc.Help;

import java.util.HashMap;
import java.util.Map;

public class LinkUserPondActivity extends AppCompatActivity {
    private static final String TAG = "LinkUserPondActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private RecyclerView linkToPond;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;
    private SharedPreferences getUser;
    private SharedPreferences getPond;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_user_pond);
        getSupportActionBar().hide();

        linkToPond = findViewById(R.id.linkUserPondRecyclerView);
        linkToPond.setHasFixedSize(true);
        linkToPond.setLayoutManager(new LinearLayoutManager(this));
        getUser = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        getPond = getSharedPreferences("PondInfo", Context.MODE_PRIVATE);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);

        String username = sp.getString("username", "");
        String role = sp.getString("role", "");

        Log.d(TAG, "SP: " + sp.getAll().toString());
        myRef = database.getReference("/PondDetail");
        Query query = myRef.orderByChild(username).equalTo(role);

        options = new FirebaseRecyclerOptions.Builder<Pond>()
                .setQuery(query, Pond.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Pond, PondViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PondViewHolder holder, int position, @NonNull Pond model) {
                final String piID = model.getPiId();
                final String pondName = model.getPondName();
                final String location = model.getLocation();

                holder.piId.setText("Pi ID: " + piID);
                holder.pondName.setText("Pond: " + pondName);
                holder.location.setText("Location: " + location);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPond.edit().putString("piID", piID).apply();
                        getPond.edit().putString("pondName", pondName).apply();
                        getPond.edit().putString("location", location).apply();
                        Log.d(TAG, "SharedP - getUser: " + getUser.getAll().toString());
                        Log.d(TAG, "SharedP - getPond: " + getPond.getAll().toString());
                        confirmDialog();
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(LinkUserPondActivity.this).inflate(R.layout.activity_home_cardview, viewGroup, false);
                PondViewHolder holder = new PondViewHolder(view);
                return holder;
            }
        };
        linkToPond.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        getUser.edit().clear().apply();
        finish();
    }

    private void confirmDialog(){
        LayoutInflater floatingDialog = LayoutInflater.from(this);
        View view = floatingDialog.inflate(R.layout.dialog_link_user_pond, null);
        TextView userInfoDetail = view.findViewById(R.id.txtViewUserInfo);
        TextView pondInfoDetail = view.findViewById(R.id.txtViewPondInfo);

        String userInfo = getUser.getString("userID", "") + ": "
                + getUser.getString("firstName", "") + " "
                + getUser.getString("lastName", "");

        String pondInfo = getPond.getString("piID", "") + ": "
                + getPond.getString("pondName", "") + " at "
                + getPond.getString("location","");

        userInfoDetail.setText(userInfo);
        pondInfoDetail.setText(pondInfo);

        AlertDialog.Builder link = new AlertDialog.Builder(LinkUserPondActivity.this);
        link.setTitle("Link User to Pond:");
        link.setView(view);

        link.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePartnersNode();
                getUser.edit().clear().apply();
                getPond.edit().clear().apply();
                Intent intent = new Intent(LinkUserPondActivity.this, PartnerAdminActivity.class);
                startActivity(intent);
            }
        });

        link.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        link.show();
    }

    private void updatePartnersNode(){
        final String username = getUser.getString("userID", "");
        final String fullname = getUser.getString("firstName","") + " " + getUser.getString("lastName", "");
        final String role = getUser.getString("userRole", "");
        final String device = getPond.getString("piID","");

        final String pathToPartnerNode = "Partners/" + sp.getString("username","");
        final String pathToPondDetailNode = "PondDetail/" + device + "/" + username;

        final DatabaseReference myRefPartner = database.getReference(pathToPartnerNode);
        final DatabaseReference myRefPondDetail = database.getReference(pathToPondDetailNode);

        Query q = myRefPartner.orderByChild("username").equalTo(username).limitToFirst(1);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.exists()){
                    Toast.makeText(LinkUserPondActivity.this, "This user is already linked.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //String key = myRefPartner.push().getKey();
                    Partner newPartner = new Partner(username, fullname, device);
                    myRefPartner.child(username).setValue(newPartner);
                    myRefPondDetail.setValue(role);
                    Toast.makeText(LinkUserPondActivity.this, "User successfully linked!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
