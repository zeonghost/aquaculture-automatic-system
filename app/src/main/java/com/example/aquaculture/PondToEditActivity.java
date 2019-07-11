package com.example.aquaculture;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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

    private void editPondDialog(String piID){
        String path = piID + "-detail";
        DatabaseReference myPondRef = FirebaseDatabase.getInstance().getReference(path);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_edit_pond, null);
        EditText pondInput = view.findViewById(R.id.editTextPond);
        EditText locationInput = view.findViewById(R.id.editTextLocation);
        EditText ch1Input = view.findViewById(R.id.editTextChannel1);
        EditText ch2Input = view.findViewById(R.id.editTextChannel2);
        EditText ch3Input = view.findViewById(R.id.editTextChannel3);
        EditText speciesInput = view.findViewById(R.id.editTextSpecies);
        EditText widthInput = view.findViewById(R.id.editTextWidth);
        EditText lengthInput = view.findViewById(R.id.editTextLength);
        EditText depthInput = view.findViewById(R.id.editTextDepth);
        Button updatePondButton = view.findViewById(R.id.btnEditPond);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(view);
        dialog.show();
    }
}
