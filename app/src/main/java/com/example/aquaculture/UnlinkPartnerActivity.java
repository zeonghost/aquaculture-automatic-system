package com.example.aquaculture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aquaculture.Model.Partner;
import com.example.aquaculture.ViewHolder.PartnerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UnlinkPartnerActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_unlink_partner);
        buttomNavigation();
        linkUser = findViewById(R.id.btnLinkUser);
        recyclerPartner = findViewById(R.id.partnerRecyclerView);
        recyclerPartner.setHasFixedSize(true);
        recyclerPartner.setLayoutManager(new LinearLayoutManager(UnlinkPartnerActivity.this));

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
                final String key = model.getId();

                holder.username.setText(username);
                holder.fullNamePartner.setText(fullname);
                holder.deviceId.setText(deviceId);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        partnerTemp.edit().putString("deviceId", deviceId).apply();
                        partnerTemp.edit().putString("username", username).apply();
                        partnerTemp.edit().putString("key", key).apply();
                        unLinkPartnerDialog();
                    }
                });
            }

            @NonNull
            @Override
            public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(UnlinkPartnerActivity.this).inflate(R.layout.activity_partner_card, viewGroup, false);
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

    private void unLinkPartnerDialog(){
        final String username = partnerTemp.getString("username", "");
        final String key = partnerTemp.getString("key", "");
        final String device = partnerTemp.getString("deviceId","");
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
                removeLinkFromPartner.child(key).removeValue();
                removeLinkFromPondDetail.removeValue();
                Toast.makeText(UnlinkPartnerActivity.this, "User has been removed access to the pond.", Toast.LENGTH_SHORT).show();
            }
        });
        unLinkPartner.show();
    }

    public void buttomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(UnlinkPartnerActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        Intent intent2 = new Intent(UnlinkPartnerActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3:
                        Intent intent3 = new Intent(UnlinkPartnerActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }
}
