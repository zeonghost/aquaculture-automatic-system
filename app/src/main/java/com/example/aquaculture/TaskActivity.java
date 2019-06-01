package com.example.aquaculture;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aquaculture.Fragment.DatePickerFragment;
import com.example.aquaculture.Model.Task;
import com.example.aquaculture.ViewHolder.TaskViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.aquaculture.HomeActivity.transferData;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "LOG DATA: ";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private RecyclerView taskInfo;
    private FirebaseRecyclerOptions<Task> options;
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> adapter;
    private String status;
    private SharedPreferences sp;
    public static String transId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        //setTitle("Task");
        getSupportActionBar().hide();
        buttonNavigationSettings();
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        taskInfo = findViewById(R.id.recyclerview);
        taskInfo.setHasFixedSize(true);
        taskInfo.setLayoutManager(new LinearLayoutManager(TaskActivity.this));

        myRef = database.getReference("/task");
        //Query query = myRef.orderByKey().equalTo("pi1");

        options = new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(myRef, Task.class)   //mRef in this parameter can be changed into a more specific query like in LINE 50.
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

                holder.TaskId.setText("Task ID: " + taskId);
                holder.date.setText("Date: " + date);
                holder.receiver.setText("Receiver: " + receiver);
                holder.status.setText("Status: " + status);
                holder.task.setText("Task: " + task);
                holder.time.setText("Time: " + time);
                holder.uploader.setText("Uploader: " + uploader);

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

                /*

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferData = piID;
                        Log.d(TAG, "Result-2: "+ transferData);
                        //FOR NOW THIS ONLY GOES TO PI1. Have not figured out how to filter other Pi's
                        Intent toPondInfoActivity = new Intent(HomeActivity.this, PondInfoActivity.class);
                        startActivity(toPondInfoActivity);
                    }
                });*/
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

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.addBtn);
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
        final View textEntryView = factory.inflate(R.layout.dialog_task_edit, null);
        final EditText date = (EditText) textEntryView.findViewById(R.id.editTextDate);
        final EditText time = (EditText)textEntryView.findViewById(R.id.editTextTime);
        final EditText receiver = (EditText)textEntryView.findViewById(R.id.editTextReceiver);
        final EditText task = (EditText)textEntryView.findViewById(R.id.editTextTask);

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
        ad1.setTitle("Add New Task:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                myRef1 = database.getReference("/task");
                String un = sp.getString("username", null);
                //DatabaseReference hopperRef = myRef1.child(transId);
                Task newUser = new Task(date.getText().toString(), receiver.getText().toString(), status, task.getText().toString(), time.getText().toString(), un);
                String key = myRef1.push().getKey();
                myRef1.child(key).setValue(newUser);
                Toast.makeText(TaskActivity.this, "Add Success", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.show();
    }

    public void showEditDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_task_edit, null);
        final EditText date = (EditText) textEntryView.findViewById(R.id.editTextDate);
        final EditText time = (EditText)textEntryView.findViewById(R.id.editTextTime);
        final EditText receiver = (EditText)textEntryView.findViewById(R.id.editTextReceiver);
        final EditText task = (EditText)textEntryView.findViewById(R.id.editTextTask);
        final RadioGroup radioGroup = textEntryView.findViewById(R.id.radioGroup);
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
                receiver.setText(taskTemp.getReceiver());
                if (Objects.equals(taskTemp.getStatus(), "Pending")){
                    radioGroup.check(R.id.radioPending);
                    //status = getString(R.string.pending);
                } else {
                    radioGroup.check(R.id.radioDone);
                }
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
        ad1.setTitle("Edit Task:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("date", date.getText().toString());
                hopperUpdates.put("time", time.getText().toString());
                hopperUpdates.put("receiver", receiver.getText().toString());
                hopperUpdates.put("status", status);
                hopperUpdates.put("task", task.getText().toString());
                hopperRef.updateChildren(hopperUpdates);
                Toast.makeText(TaskActivity.this, "Edit Success", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.show();
    }


    private void showDeleteDialog(){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TaskActivity.this);
        normalDialog.setTitle("Warning").setMessage("You sure you want to delete this task?");
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
                        Intent intent2 = new Intent(TaskActivity.this, TaskActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.item3://btn 3 -> profile
                        Intent intent3 = new Intent(TaskActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });//bottom navigation
    }

    private void showDatePickerDialog(final EditText date){
        final Calendar cal = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText((month+1) + "-" + dayOfMonth + "-" + year);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText time){
        final Calendar cal = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String ss;
                if(hourOfDay >= 12){
                    hourOfDay %= 12;
                    if(hourOfDay == 0){
                        hourOfDay = 12;
                    }
                    ss = " PM";
                } else {
                    if(hourOfDay == 0){
                        hourOfDay = 12;
                    }
                    ss = " AM";
                }
                time.setText(hourOfDay + ":" + minute + ss);
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }
}
