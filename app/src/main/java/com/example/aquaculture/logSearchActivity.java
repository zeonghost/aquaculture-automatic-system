package com.example.aquaculture;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.aquaculture.Model.Log;
import com.example.aquaculture.ViewHolder.LogViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class logSearchActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseRecyclerOptions<Log> options;
    private FirebaseRecyclerAdapter<Log, LogViewHolder> adapter;
    private DatabaseReference myRef;
    private Long fromDate;
    private Long toDate;
    private RecyclerView logInfo;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_search);
        buttomNavigation();

        logInfo = findViewById(R.id.recycle_view);
        logInfo.setHasFixedSize(true);

        LinearLayoutManager layout = new LinearLayoutManager(logSearchActivity.this);
        layout.setStackFromEnd(true);
        layout.setReverseLayout(true);

        logInfo.setLayoutManager(layout);

        fromDate = LogViewActivity.tr2;
        toDate = LogViewActivity.tr1;
        android.util.Log.d(TAG, "fromDate: " + fromDate);
        android.util.Log.d(TAG, "fromDate: " + toDate);

        String getData = HomeActivity.transferData;
        String path = getData + "-log";

        myRef = database.getReference(path);

        Query q2 = myRef.orderByChild("logTime").startAt(fromDate).endAt(toDate);

        options = new FirebaseRecyclerOptions.Builder<Log>()
                .setQuery(q2, Log.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Log, LogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LogViewHolder holder, int position, @NonNull Log model) {
                long logTime = model.getTime();
                String logDetail = model.getdetail();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Timestamp timestamp = new Timestamp(logTime);
                Date date = new Date(timestamp.getTime());
                String formattedDateTime = dateFormat.format(date);

                holder.logTime.setText(formattedDateTime);
                holder.logDetail.setText(logDetail);
            }

            @NonNull
            @Override
            public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(logSearchActivity.this).inflate(R.layout.log_info, viewGroup, false);
                LogViewHolder holder = new LogViewHolder(view);
                return holder;
            }
        };
        logInfo.setAdapter(adapter);

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

    public void buttomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(logSearchActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        Intent intent2 = new Intent(logSearchActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3:
                        Intent intent3 = new Intent(logSearchActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

}
