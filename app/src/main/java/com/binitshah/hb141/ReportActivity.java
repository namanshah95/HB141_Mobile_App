package com.binitshah.hb141;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
* ReportActivity can be started two ways. In either read mode or write mode.
 * In write mode, ReportActivity allows users to fill out a survey on the location and file the report.
 * In read mode, ReportActivity reads the raw report to provide thier previous submitted report in a view only form.
 * In both use cases, ReportActivity relies on CardView to provide cards that act as components of the report.
 */
public class ReportActivity extends AppCompatActivity {

    String mode = "write";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private String EID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView mLocationName = (TextView)findViewById(R.id.location_name);
        final TextView mLocationAddress = (TextView)findViewById(R.id.location_address);
        Button mSendButton = (Button)findViewById(R.id.send_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        EID = extras.getString("EID");

        DatabaseReference ref = mDatabase.child("establishment").child(EID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLocationName.setText(dataSnapshot.child("Name").getValue().toString());
                mLocationAddress.setText(dataSnapshot.child("Address").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReport();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void sendReport() {
        String VID = mAuth.getCurrentUser().getUid();
        boolean publicView = ((CheckBox)findViewById(R.id.publicview)).isChecked();
        boolean restroomView = ((CheckBox)findViewById(R.id.restroomview)).isChecked();
        boolean noView = ((CheckBox)findViewById(R.id.noview)).isChecked();
        String comment = ((EditText)findViewById(R.id.additional_comments)).getText().toString();

        Calendar calendar = Calendar.getInstance();

        String key = mDatabase.child("report").push().getKey();
        mDatabase.child("report").child(key).child("EID").setValue(EID);
        mDatabase.child("report").child(key).child("VID").setValue(VID);
        mDatabase.child("report").child(key).child("Public View").setValue(publicView);
        mDatabase.child("report").child(key).child("Restroom View").setValue(restroomView);
        mDatabase.child("report").child(key).child("No View").setValue(noView);
        mDatabase.child("report").child(key).child("Comment").setValue(comment);
        mDatabase.child("report").child(key).child("Datetime").setValue(calendar.getTime());


        startActivity(new Intent(ReportActivity.this, MainActivity.class));
    }

}
