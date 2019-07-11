package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.Model.pondAdd;
import com.example.aquaculture.ViewHolder.PondViewHolder;
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

public class PondToEditActivity extends AppCompatActivity {
    private static final String TAG = "PondToEditActivity";
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private RecyclerView pondInfoRecycler;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;
    private SharedPreferences cookie;
    private String username;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pond_to_edit);
        buttonNavigationSettings();
        cookie = getSharedPreferences("login", Context.MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/PondDetail");
        pondInfoRecycler = findViewById(R.id.recyclerViewPond);
        pondInfoRecycler.setHasFixedSize(true);
        pondInfoRecycler.setLayoutManager(new LinearLayoutManager(PondToEditActivity.this));

        username = cookie.getString("username", "");
        role = cookie.getString("role", "");

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
                        Log.d(TAG, "onClick: " + piID);
                        editPondDialog(piID);
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(PondToEditActivity.this).inflate(R.layout.activity_home_cardview, viewGroup, false);
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

    private void editPondDialog(final String piID){
        String path = piID + "-detail";
        final DatabaseReference myPondDetailRef = FirebaseDatabase.getInstance().getReference(path);
        final DatabaseReference myPondDetailRef2 = FirebaseDatabase.getInstance().getReference("PondDetail");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_edit_pond, null);
        final EditText pondInput = view.findViewById(R.id.editTextPond);
        final EditText locationInput = view.findViewById(R.id.editTextLocation);
        final EditText ch1Input = view.findViewById(R.id.editTextChannel1);
        final EditText ch2Input = view.findViewById(R.id.editTextChannel2);
        final EditText ch3Input = view.findViewById(R.id.editTextChannel3);
        final EditText speciesInput = view.findViewById(R.id.editTextSpecies);
        final EditText widthInput = view.findViewById(R.id.editTextWidth);
        final EditText lengthInput = view.findViewById(R.id.editTextLength);
        final EditText depthInput = view.findViewById(R.id.editTextDepth);
        Button updatePondButton = view.findViewById(R.id.btnEditPond);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(view);
        final AlertDialog updateDialog = dialog.show();

        myPondDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pondInput.setText(dataSnapshot.child("pondName").getValue(String.class));
                locationInput.setText(dataSnapshot.child("location").getValue(String.class));
                ch1Input.setText(dataSnapshot.child("ch1n").getValue(String.class));
                ch2Input.setText(dataSnapshot.child("ch2n").getValue(String.class));
                ch3Input.setText(dataSnapshot.child("ch3n").getValue(String.class));
                speciesInput.setText(dataSnapshot.child("species").getValue(String.class));
                widthInput.setText(String.valueOf(dataSnapshot.child("width").getValue(Float.class)));
                lengthInput.setText(String.valueOf(dataSnapshot.child("length").getValue(Float.class)));
                depthInput.setText(String.valueOf(dataSnapshot.child("depth").getValue(Float.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updatePondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pond = pondInput.getText().toString();
                String location = locationInput.getText().toString().trim();
                String ch1 = ch1Input.getText().toString().trim();
                String ch2 = ch2Input.getText().toString().trim();
                String ch3 = ch3Input.getText().toString().trim();
                String species = speciesInput.getText().toString().trim();
                String width = widthInput.getText().toString().trim();
                String length = lengthInput.getText().toString().trim();
                String depth = depthInput.getText().toString().trim();

                if(pond.isEmpty() || location.isEmpty() || ch1.isEmpty() || ch2.isEmpty() || ch3.isEmpty() || species.isEmpty() || width.isEmpty() || length.isEmpty() || depth.isEmpty()){
                    Toast.makeText(PondToEditActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    Float wid = Float.parseFloat(width);
                    Float len = Float.parseFloat(length);
                    Float dep = Float.parseFloat(depth);
                    pondAdd updatePondDetail = new pondAdd(piID, pond, location, ch1, ch2, ch3, species, wid, len, dep);
                    myPondDetailRef.setValue(updatePondDetail);
                    myPondDetailRef2.child(piID).child("location").setValue(location);
                    myPondDetailRef2.child(piID).child("pondName").setValue(pond);
                    updateDialog.cancel();
                }
            }
        });
        updateDialog.show();
    }

    private void buttonNavigationSettings(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(PondToEditActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(PondToEditActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(PondToEditActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
    }
}
