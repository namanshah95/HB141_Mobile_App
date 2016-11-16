package com.binitshah.hb141;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;


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
        final TextView address = (TextView)rootView.findViewById(R.id.detail_address_id);
        final TextView phone = (TextView)rootView.findViewById(R.id.detail_phone_id);
        final TextView url = (TextView)rootView.findViewById(R.id.detail_url_id);
        final TextView status = (TextView)rootView.findViewById(R.id.detail_status_id);

        DatabaseReference ref = mDatabase.child("establishment").child(mParam1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                address.setText(dataSnapshot.child("Address").getValue().toString());
                phone.setText(dataSnapshot.child("Phone Number").getValue().toString());
                url.setText(dataSnapshot.child("Website").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        Query query = mDatabase.child("report").orderByChild("EID").equalTo("1", "EID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int neg_reports = 0;
                Calendar latest_report;
                String s = "";
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    for (DataSnapshot node : key.getChildren()) {
                        s += node.getKey().toString() + node.getValue().toString();
                    }
                }
                status.setText(s);
                Calendar thirty_days_ago = Calendar.getInstance();
                thirty_days_ago.add(Calendar.DAY_OF_MONTH, -30);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
}
