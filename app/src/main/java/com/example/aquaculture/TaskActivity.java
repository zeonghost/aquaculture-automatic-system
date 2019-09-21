package com.example.aquaculture;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aquaculture.Fragment.DatePickerFragment;
import com.example.aquaculture.Model.Task;
import com.example.aquaculture.ViewHolder.TaskViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.aquaculture.Model.Constant.TIME_IN_STATUS;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "LOG DATA: ";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private DatabaseReference myPartner;
    private RecyclerView taskInfo;
    private FirebaseRecyclerOptions<Task> options;
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> adapter;
    private String status;
    private FloatingActionButton add;
    private SharedPreferences sp;
    public SharedPreferences sp1;
    public static SharedPreferences sp2;
    public static String transId;
    public String pushToken;
    private ArrayList<String> partnerList;
    private Calendar today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        today = Calendar.getInstance();
        partnerList = new ArrayList<>();
        partnerList.add("");
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        sp1 = getSharedPreferences("temp", Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("login", Context.MODE_PRIVATE);
        buttonNavigationSettings();
        getPartners();
        taskInfo = findViewById(R.id.recyclerview);
        taskInfo.setHasFixedSize(true);
        taskInfo.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
        add = (FloatingActionButton) findViewById(R.id.addBtn);
        Query query;

        myRef = database.getReference("/task");
        final String role = sp.getString("role", null);
        final String un = sp.getString("username", null);

        if(Objects.equals(role, "Admin"))
        {
            query = myRef.orderByChild("uploader").equalTo(un);
        }
        else
        {
            query = myRef.orderByChild("receiver").equalTo(un);
            add.hide();
        }

        options = new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Task model) {
                final String taskId = adapter.getRef(position).getKey();
                String date = model.getDate();
                String receiver = model.getReceiver();
                String status = model.getStatus();
                String task = model.getTask();
                String time = model.getTime();
                String uploader = model.getUploader();
                String uploaderName = model.getUploaderName();
                String receiverName = model.getReceiverName();

                //holder.TaskId.setText(taskId);
                holder.date.setText(date);
                holder.receiver.setText(receiver);
                holder.status.setText(status);
                holder.task.setText(task);
                holder.time.setText(time);
                holder.uploader.setText(uploader);
                holder.uploaderName.setText(uploaderName);
                holder.receiverName.setText(receiverName);

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transId = taskId;
                        showEditDialog();
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transId = taskId;
                        showDeleteDialog();
                    }
                });

                if(Objects.equals(role, "Partner")){
                    boolean clockedIn = sp.getBoolean("clockInDetails", false);
                    if(clockedIn){
                        if(Objects.equals(status, "Pending")){
                            holder.edit.setVisibility(View.INVISIBLE);
                            holder.delete.setVisibility(View.INVISIBLE);
                            holder.done.setVisibility(View.VISIBLE);
                            holder.done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    transId = taskId;
                                    showDoneDialog();
                                }
                            });
                        } else {
                            holder.done.setVisibility(View.GONE);
                            holder.edit.setVisibility(View.GONE);
                            holder.delete.setVisibility(View.GONE);
                        }
                    }
                    else{
                        holder.done.setVisibility(View.GONE);
                        holder.edit.setVisibility(View.GONE);
                        holder.delete.setVisibility(View.GONE);
                    }
                } else {
                    holder.done.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.VISIBLE);
                }
            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(TaskActivity.this).inflate(R.layout.activity_task_cardview, viewGroup, false);
                TaskViewHolder holder = new TaskViewHolder(view);
                return holder;
            }
        };
        taskInfo.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String role = sp.getString("role", null);
        boolean clockedIn = sp.getBoolean("clockInDetails", false);
        if(!clockedIn){
            if(Objects.equals(role, "Partner")){
                reminderToClockIn();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void radioCheck(View view){
        if(view.getId() == R.id.radioPending){
            status = getString(R.string.pending);
            Toast.makeText(this, getString(R.string.pending), Toast.LENGTH_SHORT).show();
        } else {
            status = getString(R.string.done);
            Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
        }
    }

    protected void showAddDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_task_add, null);
        final EditText date = (EditText) textEntryView.findViewById(R.id.editTextDate);
        final EditText time = (EditText)textEntryView.findViewById(R.id.editTextTime);
        final EditText task = (EditText)textEntryView.findViewById(R.id.editTextTask);
        final Spinner receiver = textEntryView.findViewById(R.id.spinnerReceiver);
        status = "Pending";

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TaskActivity.this, android.R.layout.simple_spinner_dropdown_item, partnerList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiver.setAdapter(arrayAdapter);
        receiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference tokRef = database.getReference("/user");
                tokRef.orderByChild("username").equalTo(receiver.getSelectedItem().toString().split(" ")[0]).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        pushToken = dataSnapshot.child("pushToken").getValue().toString();
                        sp1.edit()
                                .putString("pushToken", pushToken)
                                .apply();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        DatabaseReference uptokRef = database.getReference("/user");
        uptokRef.orderByChild("username").equalTo(sp.getString("username", null)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                pushToken = dataSnapshot.child("pushToken").getValue().toString();
                sp1.edit()
                        .putString("uppushToken", pushToken)
                        .apply();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        date.setFocusable(false);
        date.setClickable(true);
        time.setFocusable(false);
        time.setClickable(true);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(date);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(time);
            }
        });

        AlertDialog.Builder ad1 = new AlertDialog.Builder(TaskActivity.this);

        ad1.setView(textEntryView);
        ad1.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                myRef1 = database.getReference("/task");
                String rt = receiver.getSelectedItem().toString().split(" ")[0];

                if(date.getText().toString().isEmpty() || rt.isEmpty() || task.getText().toString().isEmpty() || time.getText().toString().isEmpty()){
                    Toast.makeText(TaskActivity.this, "Please fill up all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    String un = sp.getString("username", null);
                    String token = sp1.getString("pushToken", null);
                    String uptoken = sp1.getString("uppushToken", null);
                    String receiverName = receiver.getSelectedItem().toString().split(" ")[2] + " " + receiver.getSelectedItem().toString().split(" ")[3];
                    String uploaderName = sp.getString("firstname",  "") + " " + sp.getString("lastname","");
                    String key = myRef1.push().getKey();
                    Task newUser = new Task(date.getText().toString(), rt, status, task.getText().toString(), time.getText().toString(), un, receiverName, uploaderName);
                    myRef1.child(key).setValue(newUser);
                    Map<String, Object> pushT = new HashMap<>();
                    pushT.put("receiveToken", token);
                    pushT.put("uploaderToken", uptoken);
                    myRef1.child(key).updateChildren(pushT);
                    Toast.makeText(TaskActivity.this, "Add Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad1.show();
    }

    public void showEditDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_task_edit, null);
        final EditText date = (EditText) textEntryView.findViewById(R.id.editTextDate);
        final EditText time = (EditText)textEntryView.findViewById(R.id.editTextTime);
        final EditText task = (EditText)textEntryView.findViewById(R.id.editTextTask);
        final RadioGroup radioGroup = textEntryView.findViewById(R.id.radioGroup);
        final Spinner receiver = textEntryView.findViewById(R.id.spinnerReceiver);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TaskActivity.this, android.R.layout.simple_spinner_dropdown_item, partnerList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiver.setAdapter(arrayAdapter);

        date.setFocusable(false);
        date.setClickable(true);
        time.setFocusable(false);
        time.setClickable(true);
        myRef1 = database.getReference("/task");
        final DatabaseReference hopperRef = myRef1.child(transId);
        hopperRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Task taskTemp = dataSnapshot.getValue(Task.class);
                Log.d(TAG, "Datasnapshot: " + dataSnapshot.getValue().toString());
                date.setText(taskTemp.getDate());
                time.setText(taskTemp.getTime());
                if (Objects.equals(taskTemp.getStatus(), "Pending")){
                    radioGroup.check(R.id.radioPending);
                } else {
                    radioGroup.check(R.id.radioDone);
                }

                String completeReceiverInfo = taskTemp.getReceiver() + " - " + taskTemp.getReceiverName();
                int position = getPositionOfSpinner(receiver, completeReceiverInfo);
                receiver.setSelection(position);
                receiver.setEnabled(false);
                task.setText(taskTemp.getTask());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(date);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(time);
            }
        });

        AlertDialog.Builder ad1 = new AlertDialog.Builder(TaskActivity.this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioPending : status = "Pending"; break;
                    case R.id.radioDone : status = "Done"; break;
                }
            }
        });

        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                if(!task.getText().toString().isEmpty()){
                    Map<String, Object> hopperUpdates = new HashMap<>();
                    hopperUpdates.put("date", date.getText().toString());
                    hopperUpdates.put("time", time.getText().toString());
                    hopperUpdates.put("status", status);
                    hopperUpdates.put("task", task.getText().toString());
                    hopperRef.updateChildren(hopperUpdates);
                    Toast.makeText(TaskActivity.this, "Edit Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TaskActivity.this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad1.show();
    }

    private void showDeleteDialog(){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(TaskActivity.this);
        normalDialog.setMessage("You sure you want to delete this task?");
        normalDialog.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myRef1 = database.getReference("/task");
                        myRef1.child(transId).removeValue();
                        Toast.makeText(TaskActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.show();
    }

    private void showDoneDialog(){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TaskActivity.this);
        normalDialog.setMessage("You sure you want to set this task to Done?");
        normalDialog.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myRef1 = database.getReference("/task");
                        Map<String, Object> upStatus = new HashMap<>();
                        upStatus.put("status", "Done");
                        myRef1.child(transId).updateChildren(upStatus);
                        Toast.makeText(TaskActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.show();
    }

    private void buttonNavigationSettings(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1://btn 1 -> HomeActivity
                        Intent intent1 = new Intent(TaskActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item2://btn 2 -> task
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(TaskActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void showDatePickerDialog(final EditText date){
        final Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Log.d(TAG, "today get month " + today.get(Calendar.MONTH));
                Log.d(TAG, "cal get month " + month);

                if(today.get(Calendar.MONTH) > month || today.get(Calendar.YEAR) > year){
                    Toast.makeText(TaskActivity.this, "You cannot set a task in the past.", Toast.LENGTH_SHORT).show();
                } else if (today.get(Calendar.DAY_OF_MONTH) > dayOfMonth){
                    Toast.makeText(TaskActivity.this, "You cannot set a task in the past.", Toast.LENGTH_SHORT).show();
                } else {
                    date.setText((month+1) + "-" + dayOfMonth + "-" + year);
                }
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText time){
        final Calendar cal = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                time.setText(new SimpleDateFormat("h:mm a").format(calendar.getTime()));
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void getPartners(){
        myPartner = database.getReference("Partners");
        myPartner.child(sp.getString("username", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snaps : dataSnapshot.getChildren()){
                        partnerList.add(snaps.child("username").getValue().toString() + " - " + snaps.child("fullname").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getPositionOfSpinner(Spinner s, String str){
        int i = 0, found = 0;
        while(i < s.getCount()){
            if(Objects.equals(s.getItemAtPosition(i), str)){
                found = i;
                break;
            }
            i++;
        }
        return found;
    }

    private void reminderToClockIn(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(TaskActivity.this);
        dialog.setTitle("Caution").setMessage("Don't forget to clock in!");
        dialog.setPositiveButton("Understood", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
