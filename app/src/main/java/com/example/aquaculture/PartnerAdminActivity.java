package com.example.aquaculture;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aquaculture.Model.Partner;
import com.example.aquaculture.ViewHolder.PartnerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PartnerAdminActivity extends AppCompatActivity {
    private static final String TAG = "PartnerAdminActivity";
    private RecyclerView recyclerPartner;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseRecyclerOptions<Partner> options;
    private FirebaseRecyclerAdapter<Partner, PartnerViewHolder> adapter;
    private String path;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_admin);

        recyclerPartner = findViewById(R.id.partnerRecyclerView);
        recyclerPartner.setHasFixedSize(true);
        recyclerPartner.setLayoutManager(new LinearLayoutManager(PartnerAdminActivity.this));
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        path = "Partners/" + sp.getString("username", "");
        ref = database.getReference(path);
        options = new FirebaseRecyclerOptions.Builder<Partner>()
                .setQuery(ref, Partner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Partner, PartnerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PartnerViewHolder holder, int position, @NonNull Partner model) {
                holder.username.setText(model.getUsername());
                holder.fullNamePartner.setText(model.getFullname());
                holder.deviceId.setText(model.getDevice());
            }

            @NonNull
            @Override
            public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(PartnerAdminActivity.this).inflate(R.layout.activity_partner_card, viewGroup, false);
                return new PartnerViewHolder(v);
            }
        };
        recyclerPartner.setAdapter(adapter);

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
}

/*
        ref = database.getReference("Partners/");
        ref.child("user0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = ref.orderByKey().equalTo("user0");

        options = new FirebaseRecyclerOptions.Builder<Partner>()
                .setQuery(query, Partner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Partner, PartnerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PartnerViewHolder holder, int position, @NonNull Partner model) {
                Log.d(TAG, "onBindViewHolder: " + model.getPartner().get("user1"));

            }

            @NonNull
            @Override
            public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(PartnerAdminActivity.this).inflate(R.layout.activity_partner_card, viewGroup, false);
                return new PartnerViewHolder(v);
            }
        };
        recyclerPartner.setAdapter(adapter);
        */
        /*
        Map<String, Map<String, String>> link = new HashMap<>();
        Map<String, String> partner = new HashMap<>();
        partner.put("user1", "Michael Lazaro");

        link.put("pi1", partner);


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Partners");
        //myRef.child("user0").setValue(link);
        myRef.child("user0").child("pi1").child("user2").setValue("Arvin Sandalo");
        */
