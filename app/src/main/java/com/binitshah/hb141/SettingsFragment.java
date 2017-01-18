package com.binitshah.hb141;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * SettingsFragment is a place for users to set and change settings. Furthermore, there will be a section to describe who we are and to share the app with friends.
 */
public class SettingsFragment extends Fragment {

    FirebaseAuth mAuth;
    Button mSignOutButton;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();

        mSignOutButton = (Button) v.findViewById(R.id.signout_button);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return v;
    }

}
