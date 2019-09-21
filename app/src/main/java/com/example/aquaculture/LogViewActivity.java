package com.example.aquaculture;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aquaculture.Fragment.DatePickerFragment;
import com.example.aquaculture.Model.Log;
import com.example.aquaculture.ViewHolder.LogViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class LogViewActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private DatabaseReference myPartner;
    private RecyclerView logInfo;
    private FirebaseRecyclerOptions<Log> options;
    private FirebaseRecyclerAdapter<Log, LogViewHolder> adapter;
    private EditText startDate;
    private EditText endDate;
    private Button plot;
    private Long fromDate;
    private Long toDate;
    private String user;
    private String dateTemp;
    private Spinner partnerSpinner;
    private ArrayList<String> partnerList;
    private SharedPreferences sp;
    private static final String TAG = "MainActivity";
    public static long tr1;
    public static long tr2;
    public static String userLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logview);
        buttomNavigation();
        startDate = findViewById(R.id.et_start);
        endDate = findViewById(R.id.et_end);
        plot = findViewById(R.id.btr_go);
        logInfo = findViewById(R.id.rm);
        sp = getSharedPreferences("login", MODE_PRIVATE);
        partnerSpinner = findViewById(R.id.partnersSpinner);
        partnerList = new ArrayList<>();
        logInfo.setHasFixedSize(true);
        startDate.setFocusable(false);
        startDate.setClickable(true);
        endDate.setFocusable(false);
        endDate.setClickable(true);
        LinearLayoutManager layout = new LinearLayoutManager(LogViewActivity.this);
        layout.setStackFromEnd(true);
        layout.setReverseLayout(true);

        logInfo.setLayoutManager(layout);

        String getData = HomeActivity.transferData;
        String path = getData + "-log";

        start();
        plot();
        showSpinner();
        getPartners();
        android.util.Log.d(TAG, "onCreate: " + sp.getAll());
    }

    /***********************************************
     * REQUIRED TO RETURN A DATE FOR DATE LISTENER *
     ***********************************************/
    //@Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        dateTemp = DateFormat.getDateInstance().format(calendar.getTime()); // String to reflect the chosen date

        if(startDate.getText().toString().isEmpty()){
            if(!endDate.getText().toString().isEmpty() && calendar.getTimeInMillis() > toDate){
                Toast.makeText(LogViewActivity.this, "Invalid Date: Please choose before your end date.", Toast.LENGTH_SHORT).show();
                startDate.setText(null);
                endDate.setText(null);
                return;
            }
            startDate.setText(dateTemp);
            fromDate = calendar.getTimeInMillis(); // Timestamp format
        } else {
            if(fromDate > calendar.getTimeInMillis()){
                Toast.makeText(LogViewActivity.this, "Invalid Date: Please choose after your start date.", Toast.LENGTH_SHORT).show();
                startDate.setText(null);
                endDate.setText(null);
                return;
            }
            endDate.setText(dateTemp);
            toDate = calendar.getTimeInMillis() + 86399000; // Timestamp format
        }
    }
    /***********************************************
     * REQUIRED TO RETURN A DATE FOR DATE LISTENER *
     ***********************************************/

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

    public void plot(){
        final String role = sp.getString("role", "");

        partnerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(partnerSpinner.getSelectedItemPosition() == 0){
                    user = "";
                } else {
                    user = partnerSpinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /***********************************
         * START OF THE DATE PICKING LOGIC *
         ***********************************/
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate.setText(null);
                DialogFragment pickStartDate = new DatePickerFragment();
                pickStartDate.show(getSupportFragmentManager(), "From Date");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.getText().toString().isEmpty()){
                    Toast.makeText(LogViewActivity.this, "Set a starting date first.", Toast.LENGTH_SHORT).show();
                } else {
                    DialogFragment pickEndDate = new DatePickerFragment();
                    pickEndDate.show(getSupportFragmentManager(), "To Date");
                }
            }
        });
        /*********************************
         * END OF THE DATE PICKING LOGIC *
         *********************************/
        plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d(TAG, "USER " + user);

                if(user == null){
                    user = sp.getString("username", "");
                    android.util.Log.d(TAG, "GET USER " + user);
                }

                if (startDate.getText().toString().isEmpty() || endDate.getText().toString().isEmpty() || user.isEmpty()){
                    Toast.makeText(LogViewActivity.this, "Please choose the dates and select the user to proceed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                android.util.Log.d(TAG, "toDate: " + toDate);
                android.util.Log.d(TAG, "fromDate: " + fromDate);
                tr1 = toDate;
                tr2 = fromDate;
                userLog = user.split(" ")[0];
                android.util.Log.d(TAG, "USERLOG " + userLog + " SPLIT " + user.split(" ")[0]);

                Query q2 = myRef.orderByChild("logTime").startAt(fromDate).endAt(toDate).limitToFirst(1);
                q2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Intent intent4 = new Intent(LogViewActivity.this, LogSearchActivity.class);
                            startActivity(intent4);
                        } else {
                            noDataDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });
    }

    public void start(){
        String getData = HomeActivity.transferData;
        String path = getData + "-log";
        final String ownAccount = sp.getString("username", "");

        myRef = database.getReference(path);
        Query query = myRef.orderByChild("logTime").limitToLast(20);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    noDataDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<Log>()
                .setQuery(query, Log.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Log, LogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LogViewHolder holder, int position, @NonNull Log model) {
                String username = model.getUsername();
                if(Objects.equals(ownAccount, username)){
                    long logTime = model.getTime();
                    String logDetail = model.getdetail();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    Timestamp timestamp = new Timestamp(logTime);
                    Date date = new Date(timestamp.getTime());
                    String formattedDateTime = dateFormat.format(date);

                    holder.logTime.setText("• " + formattedDateTime);
                    holder.logDetail.setText(logDetail);
                } else {
                    holder.logHolder.setVisibility(View.GONE);
                    holder.logTime.setVisibility(View.GONE);
                    holder.logDetail.setVisibility(View.GONE);
                    holder.itemView.setVisibility(View.GONE);
                }

//                long logTime = model.getTime();
//                String logDetail = model.getdetail();
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
//                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//                Timestamp timestamp = new Timestamp(logTime);
//                Date date = new Date(timestamp.getTime());
//                String formattedDateTime = dateFormat.format(date);
//
//                holder.logTime.setText("• " + formattedDateTime);
//                holder.logDetail.setText(logDetail);
            }

            @NonNull
            @Override
            public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(LogViewActivity.this).inflate(R.layout.log_info, viewGroup, false);
                LogViewHolder holder = new LogViewHolder(view);
                return holder;
            }
        };
        logInfo.setAdapter(adapter);
    }

    public void buttomNavigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        Intent intent1 = new Intent(LogViewActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2:
                        Intent intent2 = new Intent(LogViewActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3:
                        Intent intent3 = new Intent(LogViewActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void getPartners(){
        myPartner = database.getReference("Partners");
        myPartner.child(sp.getString("username", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    partnerList.add("Select a user to search log by name");
                    partnerList.add("All users");
                    String ownAccount = sp.getString("username","") + " - " + sp.getString("firstname", "") + " " + sp.getString("lastname", "");
                    partnerList.add(ownAccount);
                    for(DataSnapshot snaps : dataSnapshot.getChildren()){
                        partnerList.add(snaps.child("username").getValue().toString() + " - " + snaps.child("fullname").getValue().toString());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LogViewActivity.this, android.R.layout.simple_spinner_item, partnerList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    partnerSpinner.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showSpinner(){
        String role = sp.getString("role","");
        if(Objects.equals(role, "Partner")){
            partnerSpinner.setVisibility(View.GONE);
        } else {
            partnerSpinner.setVisibility(View.VISIBLE);
        }
    }

    private void noDataDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(LogViewActivity.this);
        dialog.setTitle("Oops!").setMessage("There are no log reports on the selected dates.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Understood", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

}
