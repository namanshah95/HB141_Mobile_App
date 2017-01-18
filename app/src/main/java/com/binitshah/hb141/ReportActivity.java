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
import java.util.Date;

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

    private TextView mLocationName;
    private TextView mLocationAddress;
    private CheckBox mPublicView;
    private CheckBox mRestroomView;
    private CheckBox mNoView;
    private EditText mComment;
    private Button mSendButton;

    private Establishment establishment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mLocationName = (TextView)findViewById(R.id.location_name);
        mLocationAddress = (TextView)findViewById(R.id.location_address);
        mPublicView = (CheckBox)findViewById(R.id.publicview);
        mRestroomView = (CheckBox)findViewById(R.id.restroomview);
        mNoView = (CheckBox)findViewById(R.id.noview);
        mComment = (EditText)findViewById(R.id.additional_comments);
        mSendButton = (Button)findViewById(R.id.send_button);

        establishment = (Establishment) getIntent().getExtras().get("establishment");

        mDatabase.child("establishment").child(establishment.getId()).child("Name").setValue(establishment.getName());
        mDatabase.child("establishment").child(establishment.getId()).child("Address").setValue(establishment.getAddress());
        mDatabase.child("establishment").child(establishment.getId()).child("Phone Number").setValue(establishment.getPhoneNumber());
        mDatabase.child("establishment").child(establishment.getId()).child("Website").setValue(establishment.getWebsiteUri());
        for (int i = 0; i < establishment.getPlaceTypes().size(); i++) {
            mDatabase.child("establishment").child(establishment.getId()).child("Place Type").child(Integer.valueOf(i).toString())
                    .setValue(establishment.getPlaceTypes().get(i));
        }

        mLocationName.setText(establishment.getName());
        mLocationAddress.setText(establishment.getAddress());

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Report report = new Report();
                report.setPlaceId(establishment.getId());
                report.setDateVisited(new Date());
                report.setVolunteerId(mAuth.getCurrentUser().getUid());
                report.setPublic_view(mPublicView.isChecked());
                report.setRestroom_view(mRestroomView.isChecked());
                report.setNo_view(mNoView.isChecked());
                report.setComment(mComment.getText().toString());

                sendReport(report);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void sendReport(Report report) {
        String key = mDatabase.child("report").push().getKey();
        mDatabase.child("report").child(key).child("EID").setValue(report.getPlaceId());
        mDatabase.child("report").child(key).child("VID").setValue(report.getVolunteerId());
        mDatabase.child("report").child(key).child("Public View").setValue(report.isPublic_view());
        mDatabase.child("report").child(key).child("Restroom View").setValue(report.isRestroom_view());
        mDatabase.child("report").child(key).child("No View").setValue(report.isNo_view());
        mDatabase.child("report").child(key).child("Comment").setValue(report.getComment());
        SimpleDateFormat sdf = new SimpleDateFormat();
        mDatabase.child("report").child(key).child("Datetime").setValue(sdf.format(report.getDateVisited()));


        startActivity(new Intent(ReportActivity.this, MainActivity.class));
    }

}
