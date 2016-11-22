package com.binitshah.hb141;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class OnboardingFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnboardingFragment.
     */

    private int position;

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mClickHere;
    private Button mSignUpButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static OnboardingFragment newInstance(int positionHolder) {
        OnboardingFragment fragment = new OnboardingFragment();
        fragment.setPosition(positionHolder);
        return fragment;
    }

    public void setPosition(int positionHolder){
        position = positionHolder;
    }

    public OnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_onboarding, container, false);
        switch (position) {
            case 0:
                RelativeLayout relativeLayout1 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page1);
                relativeLayout1.setVisibility(View.VISIBLE);
                break;
            case 1:
                RelativeLayout relativeLayout2 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page2);
                relativeLayout2.setVisibility(View.VISIBLE);
                break;
            case 2:
                RelativeLayout relativeLayout3 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page3);
                relativeLayout3.setVisibility(View.VISIBLE);
                break;
            case 3:
                RelativeLayout relativeLayout4 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page4);
                relativeLayout4.setVisibility(View.VISIBLE);
                break;
            case 4:
                RelativeLayout relativeLayout5 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page5);
                relativeLayout5.setVisibility(View.VISIBLE);
                break;
            case 5:
                RelativeLayout relativeLayout6 = (RelativeLayout) rootView.findViewById(R.id.onboarding_page6);
                relativeLayout6.setVisibility(View.VISIBLE);
                break;
        }

        mEmailField = (EditText) rootView.findViewById(R.id.email_field_id);
        mPasswordField = (EditText) rootView.findViewById(R.id.password_field_id);
        mClickHere = (TextView) rootView.findViewById(R.id.link_to_login_id);
        mSignUpButton = (Button) rootView.findViewById(R.id.sign_up_button_id);

        mClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("TEST");
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        };

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignUp();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignUp() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Fields are empty", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Sign Up Problem", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
