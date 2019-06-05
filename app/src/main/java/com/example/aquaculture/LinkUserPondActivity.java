package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LinkUserPondActivity extends AppCompatActivity {
    private static final String TAG = "LinkUserPondActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private RecyclerView linkToPond;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_user_pond);
        getSupportActionBar().hide();

        linkToPond = findViewById(R.id.linkUserPondRecyclerView);
        linkToPond.setHasFixedSize(true);
        linkToPond.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
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
                String pondName = model.getPondName();
                String location = model.getLocation();

                holder.piId.setText("Pi ID: " + piID);
                holder.pondName.setText("Pond: " + pondName);
                holder.location.setText("Location: " + location);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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
}
