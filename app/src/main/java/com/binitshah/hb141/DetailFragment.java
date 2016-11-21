package com.binitshah.hb141;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "eid";

    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mDatabase;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_detail, container, false);
        final TextView mName = (TextView) rootView.findViewById(R.id.detail_location_name_id);
        final TextView mAddress = (TextView)rootView.findViewById(R.id.detail_address_id);
        final TextView mPhone = (TextView)rootView.findViewById(R.id.detail_phone_id);
        final TextView mUrl = (TextView)rootView.findViewById(R.id.detail_url_id);
        final TextView mStatus = (TextView)rootView.findViewById(R.id.detail_status_id);
        final Button mReportButton = (Button)rootView.findViewById(R.id.detail_report_button_id);

        DatabaseReference ref = mDatabase.child("establishment").child(mParam1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("Name").getValue().toString());
                mAddress.setText(dataSnapshot.child("Address").getValue().toString());
                mPhone.setText(dataSnapshot.child("Phone Number").getValue().toString());
                mUrl.setText(dataSnapshot.child("Website").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = mDatabase.child("report").orderByChild("EID").equalTo(mParam1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = status(dataSnapshot);
                switch (s) {
                    case "COMPLIES": mStatus.setTextColor(darker(Color.GREEN)); mReportButton.setEnabled(false); break;
                    case "NEEDS VISIT": mStatus.setTextColor(darker(Color.YELLOW)); mReportButton.setEnabled(true); break;
                    case "1ST NEGATIVE REPORT":
                    case "2ND NEGATIVE REPORT":
                    case "3RD NEGATIVE REPORT":
                    case "4TH NEGATIVE REPORT": mStatus.setTextColor(darker(Color.RED)); mReportButton.setEnabled(false); break;
                    default: mStatus.setTextColor(Color.BLACK); mReportButton.setEnabled(false); break;
                }
                mStatus.setText(s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initReportActivity();
            }
        });

        return rootView;
    }

    private void initReportActivity() {
        Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra("EID", mParam1);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private String status(DataSnapshot dataSnapshot) {
        int neg_reports = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.ENGLISH);
        Calendar latest_report = null;
        String s = "";
        for (DataSnapshot snap : dataSnapshot.getChildren()) {
            boolean public_view = Boolean.parseBoolean(snap.child("Public View").getValue().toString());
            boolean restroom_view = Boolean.parseBoolean(snap.child("Restroom View").getValue().toString());
            boolean no_view = Boolean.parseBoolean(snap.child("No View").getValue().toString());

            if (public_view && restroom_view) {
                return "COMPLIES";
            }
            if (no_view && neg_reports < 4) {
                neg_reports++;
            }

            Long year = (Long) snap.child("Datetime").child("year").getValue();
            Long month = (Long) snap.child("Datetime").child("month").getValue();
            Long day = (Long) snap.child("Datetime").child("day").getValue();
            Calendar datetime = Calendar.getInstance();
            datetime.set(year.intValue(), month.intValue(), day.intValue());
            if (latest_report == null || datetime.after(latest_report)) {
                latest_report = datetime;
            }
        }

        Calendar thirty_days_ago = Calendar.getInstance();
        thirty_days_ago.add(Calendar.DAY_OF_MONTH, -30);
        if (latest_report == null || thirty_days_ago.after(latest_report)) {
            return "NEEDS VISIT";
        }

        String ordinal = "";
        switch (neg_reports) {
            case 1:
                ordinal = "1st";
                break;
            case 2:
                ordinal = "2nd";
                break;
            case 3:
                ordinal = "3rd";
                break;
            case 4:
                ordinal = "4th";
                break;
        }
        return ordinal + " NEGATIVE REPORT";
    }

    private int darker(int c) {
        int a = Color.alpha(c);
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);
        return Color.argb(a, (int) (r*0.5), (int) (g*0.5), (int) (b*0.5));
    }
}
