package com.binitshah.hb141;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * PreviousReportsFragment allows users to view their previous submitted reports.
 */
public class PreviousReportsFragment extends Fragment {

    Context context;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private ArrayList<Report> reports_list;
    private RecyclerView mReportsRecyclerView;

    public PreviousReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        context = getActivity().getApplicationContext();

        View rootView = inflater.inflate(R.layout.fragment_previous_reports, container, false);

        mReportsRecyclerView = (RecyclerView) rootView.findViewById(R.id.report_recyclerview);

        getReports(mDatabase);

        return rootView;
    }

    private void getReports(DatabaseReference ref) {
        reports_list = new ArrayList<Report>();
        ref.child("report").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot r : dataSnapshot.getChildren()) {
                    if (mAuth.getCurrentUser().getUid().equals(r.child("VID").getValue().toString())) {
                        Report report = new Report();
                        report.setComment(r.child("Comment").getValue().toString());
                        report.setNo_view((boolean) r.child("No View").getValue());
                        report.setPublic_view((boolean) r.child("Public View").getValue());
                        report.setRestroom_view((boolean) r.child("Restroom View").getValue());
                        report.setVolunteerId(r.child("VID").getValue().toString());
                        report.setPlaceId(r.child("EID").getValue().toString());
                        SimpleDateFormat sdf = new SimpleDateFormat();
                        Date date;
                        try {
                            date = sdf.parse(r.child("Datetime").getValue().toString());
                        } catch (ParseException e) {
                            date = null;
                        }
                        report.setDateVisited(date);
                        reports_list.add(report);
                    }
                }
                LinearLayoutManager llm = new LinearLayoutManager(context);
                mReportsRecyclerView.setLayoutManager(llm);
                ListRecyclerAdapter mAdapter = new ListRecyclerAdapter(context, reports_list);
                mReportsRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
