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
import android.widget.Toast;

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
    private SharedPreferences partnerTemp;
    private FloatingActionButton linkUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_admin);
        getSupportActionBar().hide();
        linkUser = findViewById(R.id.btnLinkUser);
        recyclerPartner = findViewById(R.id.partnerRecyclerView);
        recyclerPartner.setHasFixedSize(true);
        recyclerPartner.setLayoutManager(new LinearLayoutManager(PartnerAdminActivity.this));

        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        partnerTemp = getSharedPreferences("partnerTemp", Context.MODE_PRIVATE);

        path = "Partners/" + sp.getString("username", "");
        ref = database.getReference(path);
        options = new FirebaseRecyclerOptions.Builder<Partner>()
                .setQuery(ref, Partner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Partner, PartnerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PartnerViewHolder holder, int position, @NonNull Partner model) {
                final String username = model.getUsername();
                final String fullname = model.getFullname();
                final String deviceId = model.getDevice();

                holder.username.setText(username);
                holder.fullNamePartner.setText(fullname);
                holder.deviceId.setText(deviceId);

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        partnerTemp.edit().putString("deviceId", deviceId).apply();
                        partnerTemp.edit().putString("username", username).apply();
                        unLinkPartnerDialog();
                        return true;
                    }
                });
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
        linkUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerAdminActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PartnerAdminActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void unLinkPartnerDialog(){
        final String username = partnerTemp.getString("username", "");
        final String device = partnerTemp.getString("deviceId","");
        Log.d(TAG, "unLinkPartnerDialog: " + partnerTemp.getAll().toString());
        final String pathToPondDetailNode = "PondDetail/" + device + "/" + username;
        final DatabaseReference removeLinkFromPondDetail = database.getReference(pathToPondDetailNode);
        final DatabaseReference removeLinkFromPartner = database.getReference(path);


        AlertDialog.Builder unLinkPartner= new AlertDialog.Builder(this);
        unLinkPartner.setTitle("Unlink Partner: ");
        unLinkPartner.setMessage("This will remove your partner from the pond where he/she is assigned into.\n\n" +
                "Do you want to un-link this user?");
        unLinkPartner.setPositiveButton("Unlink", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeLinkFromPartner.child(username).removeValue();
                removeLinkFromPondDetail.removeValue();
                Toast.makeText(PartnerAdminActivity.this, "User has been removed access to the pond.", Toast.LENGTH_SHORT).show();
            }
        });
        unLinkPartner.show();
    }
}
