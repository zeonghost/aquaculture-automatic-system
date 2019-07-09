package com.example.aquaculture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Model.Partner;
import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.Model.User;
import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.example.aquaculture.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SearchUserActivity extends AppCompatActivity {

    private static final String TAG = "SearchUserActivity";
    private RecyclerView linkUserRecyclerView;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseRecyclerOptions<User> options;
    private FirebaseRecyclerAdapter<User, UserViewHolder> adapter;
    private String getUserNameText;
    private EditText getUserName;
    private Button searchUserButton;
    private SharedPreferences sp;
    private SharedPreferences getUser;
    private SharedPreferences getPond;
    private FirebaseRecyclerOptions<Pond> optionsPond;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapterPond;
    private View pondSelectView;
    private RecyclerView pondList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        //getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("user");
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        getUser = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        getPond = getSharedPreferences("PondInfo", Context.MODE_PRIVATE);
        getUserName = findViewById(R.id.editTxtUserSearch);
        searchUserButton = findViewById(R.id.btnSearchUser);
        linkUserRecyclerView = findViewById(R.id.userRecyclerView);
        linkUserRecyclerView.setHasFixedSize(true);
        linkUserRecyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserNameText = getUserName.getText().toString().trim();

                if(Objects.equals(getUserNameText, sp.getString("username", ""))){
                    Toast.makeText(SearchUserActivity.this, "Di man gud pwede i-add imong kaugalingon dong/dzai!", Toast.LENGTH_SHORT).show();
                    return;
                }

                queryUsers();
                getMyPond();
                adapter.startListening();
                adapterPond.startListening();
            }
        });
    }

    @Override
    protected void onStop() {
        //adapter.stopListening();
        //adapterPond.stopListening();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(pondSelectView != null){
            ViewGroup parentView = (ViewGroup) pondSelectView.getParent();
            parentView.removeView(pondSelectView);
            adapter.stopListening();
            adapterPond.stopListening();
        }
        getUser.edit().clear().apply();
        super.onBackPressed();
    }

    private void queryUsers(){
        Query query = myRef.orderByChild("username").equalTo(getUserNameText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "getUserNameText: " + getUserNameText);
                Log.d(TAG, "queryUsers: " + dataSnapshot.getValue());
                if (!dataSnapshot.exists()){
                    Toast.makeText(SearchUserActivity.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                holder.userName.setText("User ID: " + model.getUsername());
                holder.lastName.setText("Last name: " + model.getLname());
                holder.firstName.setText("First name: " + model.getFname());
                holder.role.setText("User Role: " + model.getRole());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUser.edit().putString("userID", model.getUsername()).apply();
                        getUser.edit().putString("firstName", model.getFname()).apply();
                        getUser.edit().putString("lastName", model.getLname()).apply();
                        getUser.edit().putString("userRole", model.getRole()).apply();
                        Log.d(TAG, "SP - getUser: " + getUser.getAll().toString());
                        pondSelectionDialog();
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(SearchUserActivity.this).inflate(R.layout.activity_link_partner_card, viewGroup, false);
                return new UserViewHolder(view);
            }
        };
        linkUserRecyclerView.setAdapter(adapter);
    }

    private void pondSelectionDialog(){
        ViewGroup parentView = (ViewGroup) pondSelectView.getParent();
        if(parentView != null){
            parentView.removeView(pondSelectView);
        }

        pondList.setAdapter(adapterPond);
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchUserActivity.this);
        builder.setView(pondSelectView);
        builder.show();
    }

    private void getMyPond(){
        String username = sp.getString("username", "");
        String role = sp.getString("role", "");
        DatabaseReference refPond = database.getReference("PondDetail");

        LayoutInflater dialog = LayoutInflater.from(SearchUserActivity.this);
        pondSelectView = dialog.inflate(R.layout.dialog_pond_selection, null);
        pondList = pondSelectView.findViewById(R.id.recyclerPondSelection);
        pondList.setHasFixedSize(true);
        pondList.setLayoutManager(new LinearLayoutManager(this));

        Query query = refPond.orderByChild(username).equalTo(role);
        optionsPond = new FirebaseRecyclerOptions.Builder<Pond>().setQuery(query, Pond.class).build();
        adapterPond = new FirebaseRecyclerAdapter<Pond, PondViewHolder>(optionsPond) {
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
                        confirmDialog();
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(SearchUserActivity.this).inflate(R.layout.dialog_pond_selection_list, viewGroup, false);
                PondViewHolder holder = new PondViewHolder(view);
                return holder;
            }
        };
    }

    private void confirmDialog(){
        LayoutInflater floatingDialog = LayoutInflater.from(this);
        View view = floatingDialog.inflate(R.layout.dialog_link_user_pond, null);
        TextView userInfoDetail = view.findViewById(R.id.txtViewUserInfo);
        //TextView pondInfoDetail = view.findViewById(R.id.txtViewPondInfo);

        String userInfo = getUser.getString("userID", "") + " - "
                + getUser.getString("firstName", "") + " "
                + getUser.getString("lastName", "") + " to " +
                getPond.getString("piID", "") + " - "
                + getPond.getString("pondName", "") + " at "
                + getPond.getString("location","");

        String pondInfo = getPond.getString("piID", "") + " - "
                + getPond.getString("pondName", "") + " at "
                + getPond.getString("location","");

        userInfoDetail.setText(userInfo);
        //pondInfoDetail.setText(pondInfo);

        AlertDialog.Builder link = new AlertDialog.Builder(SearchUserActivity.this);
        //link.setTitle("Link User to Pond:");
        link.setView(view);

        link.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePartnersNode();
                getUser.edit().clear().apply();
                getPond.edit().clear().apply();
                Intent intent = new Intent(SearchUserActivity.this, PartnerAdminActivity.class);
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

        myRefPondDetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.exists()){
                    Toast.makeText(SearchUserActivity.this, "This user is already linked.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String key = myRefPartner.push().getKey();
                    Partner newPartner = new Partner(username, fullname, device, key);
                    myRefPartner.child(key).setValue(newPartner);
                    myRefPondDetail.setValue(role);
                    Toast.makeText(SearchUserActivity.this, "User successfully linked!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
