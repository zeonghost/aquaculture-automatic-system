package com.example.aquaculture;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaculture.Model.Pond;
import com.example.aquaculture.Model.pondAdd;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPondActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText pond;
    private EditText locat;
    private EditText ch1n;
    private EditText ch2n;
    private EditText ch3n;
    private EditText spec;
    private EditText width;
    private EditText length;
    private EditText depth;
    private Button btr;
    private String piId;
    private TextView dev;
    public static boolean exist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pond);
        piId = HomeActivity.qrResult; // the result get from qr scanner, use this in real
        //piId = "pi2";
        dev = (TextView)findViewById(R.id.piId);
        dev.setText(piId);

        pond = (EditText)findViewById(R.id.pond);
        locat = (EditText)findViewById(R.id.locat);
        ch1n = (EditText)findViewById(R.id.ch1n);
        ch2n = (EditText)findViewById(R.id.ch2n);
        ch3n = (EditText)findViewById(R.id.ch3n);
        spec = (EditText)findViewById(R.id.spec);
        width = (EditText)findViewById(R.id.width);
        length = (EditText)findViewById(R.id.length);
        depth = (EditText)findViewById(R.id.depth);
        btr =(Button) findViewById(R.id.btr);

        final FirebaseDatabase database =  FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();
        final DatabaseReference ref1 = database.getReference("/PondDetail");

        ref1.orderByChild("piId").equalTo(piId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                showDialog();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                exist = true;
                Log.d(TAG, "Result-exist2: "+ exist);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                exist = true;
                Log.d(TAG, "Result-exist3: "+ exist);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                exist = true;
                Log.d(TAG, "Result-exist4: "+ exist);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = pond.getText().toString();
                String l = locat.getText().toString();
                String c1 = ch1n.getText().toString();
                String c2 = ch2n.getText().toString();
                String c3 = ch3n.getText().toString();
                String s = spec.getText().toString();
                String w = width.getText().toString();
                String le = length.getText().toString();
                String d = depth.getText().toString();
                String pi = piId;

                int wid = Integer.parseInt(w);
                int len = Integer.parseInt(le);
                int dep = Integer.parseInt(d);



                if(p.isEmpty() || l.isEmpty() || c1.isEmpty() || c2.isEmpty()|| c3.isEmpty()|| s.isEmpty()|| w.isEmpty()|| le.isEmpty()|| d.isEmpty()){
                    Toast.makeText(AddPondActivity.this, "Please fill up all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    pondAdd newPond = new pondAdd(pi, p, l, c1, c2, c3, s, wid, len, dep);
                    //String key = ref.push().getKey();
                    String key = piId + "-detail";
                    ref.child(key).setValue(newPond);
                    Toast.makeText(AddPondActivity.this, "Registered Account Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPondActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(AddPondActivity.this);
        normalDialog.setTitle("Warning").setMessage("Pond exist");
        normalDialog.setPositiveButton("Back to Home page",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddPondActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
        normalDialog.show();
    }
}
