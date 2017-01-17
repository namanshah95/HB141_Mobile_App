package com.binitshah.hb141;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naman on 1/16/2017.
 */

class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.RecyclerViewHolder> {

    DatabaseReference mDatabase;

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Report> reports;
    private static final String TAG = "HB141Log";

    public ListRecyclerAdapter(Context context, ArrayList<Report> reports) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.reports = reports;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.report_card, parent, false);
        return new RecyclerViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        Report report = reports.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat();
        holder.mDatetime.setText(sdf.format(report.getDateVisited()));

        mDatabase.child("establishment").child(report.getPlaceId()).child("Name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.mEstablishment.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mComment.setText(report.getComment());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView mDatetime;
        private TextView mEstablishment;
        private TextView mComment;

        private RecyclerViewHolder(final View v) {
            super(v);

            mDatetime = (TextView) v.findViewById(R.id.report_datetime);
            mEstablishment = (TextView) v.findViewById(R.id.report_establishment);
            mComment = (TextView) v.findViewById(R.id.report_comment);
        }
    }
}