package com.example.aquaculture;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aquaculture.Model.User;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        //getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("user");
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        getUser = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

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
                    Toast.makeText(SearchUserActivity.this, "Oh yeah, so you want to add your doppleganger?", Toast.LENGTH_SHORT).show();
                    return;
                }

                queryUsers();
                adapter.startListening();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
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
                        Intent intent = new Intent(SearchUserActivity.this, LinkUserPondActivity.class);
                        startActivity(intent);
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
}
