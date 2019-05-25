package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.aquaculture.Fragment.DatePickerFragment;
import com.example.aquaculture.Model.Task;
import com.example.aquaculture.ViewHolder.TaskViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.aquaculture.HomeActivity.transferData;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "LOG DATA: ";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private RecyclerView taskInfo;
    private FirebaseRecyclerOptions<Task> options;
    private FirebaseRecyclerAdapter<Task, TaskViewHolder> adapter;
    public static String transId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setTitle("Task");
        buttonNavigationSettings();

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
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    protected void showAddDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_task_edit, null);
        final EditText date = (EditText) textEntryView.findViewById(R.id.editTextDate);
        final EditText time = (EditText)textEntryView.findViewById(R.id.editTextTime);
        final EditText receiver = (EditText)textEntryView.findViewById(R.id.editTextReceiver);
        final EditText status = (EditText)textEntryView.findViewById(R.id.editTextStatus);
        final EditText task = (EditText)textEntryView.findViewById(R.id.editTextTask);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(TaskActivity.this);
        ad1.setTitle("Add New Task:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

                myRef1 = database.getReference("/task");
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                String un = sp.getString("username", null);
                //DatabaseReference hopperRef = myRef1.child(transId);
                Task newUser = new Task(date.getText().toString(), receiver.getText().toString(), status.getText().toString(), task.getText().toString(), time.getText().toString(), un);
                String key = myRef1.push().getKey();
                myRef1.child(key).setValue(newUser);
                Toast.makeText(TaskActivity.this, "Add Success", Toast.LENGTH_SHORT).show();
            }
        });
        ad1.show();
    }

    protected void showEditDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_task_edit, null);
        final EditText date = (EditText) textEntryView.findViewById(R.id.editTextDate);
        final EditText time = (EditText)textEntryView.findViewById(R.id.editTextTime);
        final EditText receiver = (EditText)textEntryView.findViewById(R.id.editTextReceiver);
        final EditText status = (EditText)textEntryView.findViewById(R.id.editTextStatus);
        final EditText task = (EditText)textEntryView.findViewById(R.id.editTextTask);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(TaskActivity.this);
        ad1.setTitle("Edit Task:");
        ad1.setView(textEntryView);
        ad1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                myRef1 = database.getReference("/task");
                DatabaseReference hopperRef = myRef1.child(transId);
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("date", date.getText().toString());
                hopperUpdates.put("time", time.getText().toString());
                hopperUpdates.put("receiver", receiver.getText().toString());
                hopperUpdates.put("status", status.getText().toString());
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
}
