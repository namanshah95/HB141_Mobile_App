package com.binitshah.hb141;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
/*
* ReportActivity can be started two ways. In either read mode or write mode.
 * In write mode, ReportActivity allows users to fill out a survey on the location and file the report.
 * In read mode, ReportActivity reads the raw report to provide thier previous submitted report in a view only form.
 * In both use cases, ReportActivity relies on CardView to provide cards that act as components of the report.
 */
public class ReportActivity extends AppCompatActivity {

    String mode = "write";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
