package com.example.aquaculture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PondToDeleteActivity extends AppCompatActivity {
    private static final String TAG = "PondToDeleteActivity";
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private RecyclerView pondInfoRecycler;
    private SharedPreferences sp;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;
    private String username;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pond_to_delete);
        buttomNavigation();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/PondDetail");
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        pondInfoRecycler = findViewById(R.id.recyclerViewDeletePond);
        pondInfoRecycler.setHasFixedSize(true);
        pondInfoRecycler.setLayoutManager(new LinearLayoutManager(PondToDeleteActivity.this));
        username = sp.getString("username", "");
        role = sp.getString("role", "");

        Query query = reference.orderByChild(username).equalTo(role);

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
                        if(!Objects.equals(piID, "pi1")){
                            deletePondDialog(piID, username);
                        } else {
                            Toast.makeText(PondToDeleteActivity.this, "Do not delete this pond!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(PondToDeleteActivity.this).inflate(R.layout.activity_home_cardview, viewGroup, false);
                return new PondViewHolder(view);
            }
        };
        pondInfoRecycler.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.stopListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void deletePondDialog(String str, String str1){
        final String piID = str;
        final String username = str1;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Warning");
        dialog.setMessage("All the information about this pond will be removed. \n\n Are you sure wanted this pond deleted?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String detail = piID + "-detail";
                DatabaseReference refDetail = database.getReference(detail);
                String forecast = piID + "-forecast";
                DatabaseReference refForecast = database.getReference(forecast);
                String forecastSrc = piID + "-forecast-test";
                DatabaseReference refForecastSrc = database.getReference(forecastSrc);
                String log = piID + "-log";
                DatabaseReference refLog = database.getReference(log);
                String temp = piID + "-temp";
                DatabaseReference refTemp = database.getReference(temp);

                refDetail.removeValue();
                refForecast.removeValue();
                refForecastSrc.removeValue();
                refLog.removeValue();
                refTemp.removeValue();

                final DatabaseReference refPartners = database.getReference("Partners");
                refPartners.child(username).orderByChild("device").equalTo(piID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot snaps : dataSnapshot.getChildren()){
                                String id = snaps.getKey();
                                refPartners.child(username).child(id).removeValue();
                                Log.d(TAG, "onDataChange: key " + id);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference refPondDetail = database.getReference("PondDetail");
                refPondDetail.child(piID).removeValue();
            }
        });
        dialog.show();
    }

    public void buttomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(PondToDeleteActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        Intent intent2 = new Intent(PondToDeleteActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3:
                        Intent intent3 = new Intent(PondToDeleteActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }
}
