package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.example.aquaculture.ViewHolder.PondViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import under are for firebase
import com.example.aquaculture.Model.Pond;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "LOG DATA: ";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private RecyclerView pondInfo;
    private FirebaseRecyclerOptions<Pond> options;
    private FirebaseRecyclerAdapter<Pond, PondViewHolder> adapter;
    private FloatingActionButton addPond;
    private SharedPreferences sp;
    public static String transferData;
    public static String qrResult;

    private ViewPager viewPager;
    private MenuItem menuItem;

    ArrayList<View> viewContainter = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_home);
        addPondButton();

        //View view1 = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        //View view2 = LayoutInflater.from(this).inflate(R.layout.activity_task, null);
        //View view3 = LayoutInflater.from(this).inflate(R.layout.activity_profile, null);
        //viewContainter.add(view1);
        //viewContainter.add(view2);
        //viewContainter.add(view3);

        //ViewPager viewPager = findViewById(R.id.vp_home);
        //viewPager.setAdapter(new MyPagerAdapter());

        buttonNavigationSettings();

        pondInfo = findViewById(R.id.recyclerview);
        pondInfo.setHasFixedSize(true);
        pondInfo.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        String role = sp.getString("role", "");
        final String uid = sp.getString("uid", "");

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();
                DatabaseReference tokRef = database.getReference("/user");
                Map<String, Object> tokenUpt = new HashMap<>();
                tokenUpt.put("pushToken", token);
                tokRef.child(uid).updateChildren(tokenUpt);
            }
        });

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
                final String pondName = model.getPondName();
                final String location = model.getLocation();

                holder.piId.setText("Pi ID: " + piID);
                holder.pondName.setText("Pond: " + pondName);
                holder.location.setText("Location: " + location);

                transferData = piID;
                Log.d(TAG, "Result-2: "+ transferData);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferData = piID;
                        Log.d(TAG, "Result-2: "+ transferData);
                        if(Objects.equals(sp.getString("role", ""), "Partner")){
                            sp.edit().putString("device", piID).apply();
                            sp.edit().putString("location", location).apply();
                            if(TIME_IN_STATUS == 0){
                                checkInOutDialog();
                            } else {
                                Intent toPondInfoActivity = new Intent(HomeActivity.this, PondInfoActivity.class);
                                startActivity(toPondInfoActivity);
                            }
                        } else {
                            Intent toPondInfoActivity = new Intent(HomeActivity.this, PondInfoActivity.class);
                            startActivity(toPondInfoActivity);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public PondViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.activity_home_cardview, viewGroup, false);
                return new PondViewHolder(view);
            }
        };
        pondInfo.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        getTimeOutStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    public class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return viewContainter.size();
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewContainter.get(position));
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewContainter.get(position));
            return viewContainter.get(position);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private long firstPressedTime;//first time press back button

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            //super.onBackPressed();
            //System.exit(0);
            finish();
        } else {
            Toast.makeText(HomeActivity.this, "Press again to Exit", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();

        }
    }

    private void addPondButton(){
        addPond = findViewById(R.id.btnAddPond);
        addPond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
                intentIntegrator.setPrompt("QR Scanner");//set display context
                intentIntegrator.setTimeout(60000);//set time out
                intentIntegrator.setBeepEnabled(true);//set scan notice voice
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Abort Scanning", Toast.LENGTH_LONG).show();
            } else {
                qrResult = result.getContents();
                Toast.makeText(this, "Scan Result: "+ qrResult, Toast.LENGTH_LONG).show();
                Intent toQRScanner = new Intent(HomeActivity.this, AddPondActivity.class);
                startActivity(toQRScanner);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//get result of qr scanner

    private void buttonNavigationSettings(){
        //viewPager = (ViewPager) findViewById(R.id.vp_home);
/*
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item2://btn 2 -> task
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item3://btn 3 -> profile
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });//bottom navigation
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(menuItem !=null){
                    menuItem.setChecked(false);
                }else{
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
            }
                menuItem = bottomNavigationView.getMenu().getItem(0);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
*/


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        Intent intent2 = new Intent(HomeActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void checkInOutDialog(){
        AlertDialog.Builder checkInOut = new AlertDialog.Builder (HomeActivity.this);
        checkInOut.setMessage("Please clock in first to proceed.");
        checkInOut.setPositiveButton("Clock In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toPartnerLogActivity = new Intent(HomeActivity.this, PartnerLogActivity.class);
                startActivity(toPartnerLogActivity);
            }
        });
        checkInOut.show();
    }

    private void getTimeOutStatus(){
        String username = sp.getString("username", "");
        String role = sp.getString("role", "");
        if(role == "Partner"){
            String path = "/PartnerLog/" + username;
            DatabaseReference ref = database.getReference(path);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Check Time Out: " + dataSnapshot.child("timeOut").getValue());
                    long timeOutStatus = dataSnapshot.child("timeOut").getValue(Long.class);
                    if(timeOutStatus == 0){
                        TIME_IN_STATUS = 1;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //TESTING PURPOSES
    private void connectionState(){
        String userID = sp.getString("uid", "");
        Log.d(TAG, "connectionState: " + userID);
        final DatabaseReference testRef = database.getReference().child("user").child(userID);
        testRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    testRef.child("online").onDisconnect().setValue(false);
                    testRef.child("online").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
