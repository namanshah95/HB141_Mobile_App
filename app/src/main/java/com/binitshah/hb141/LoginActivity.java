package com.binitshah.hb141;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {

    //switchers
    private RelativeLayout signInView;
    private RelativeLayout signUpView;
    private RelativeLayout socialsView;
    private Button signUpButton;

    //Sign up: email/pass
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText nameSignUpField;
    private EditText emailSignUpField;
    private EditText passwordSignUpField;
    private EditText confirmPassSignUpField;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //handles switching to sign up view
        signInView = (RelativeLayout) findViewById(R.id.sign_in_view);
        signUpView = (RelativeLayout) findViewById(R.id.sign_up_view);
        socialsView = (RelativeLayout) findViewById(R.id.social_media_view);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, R.id.sign_up_view);
                p.addRule(RelativeLayout.CENTER_HORIZONTAL);
                p.setMargins(0, 10, 0, 0);

                socialsView.setLayoutParams(p);
                signInView.setVisibility(View.GONE);
                signUpView.setVisibility(View.VISIBLE);
            }
        });

        //mAuth listener
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mFirebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user != null) {
                    //user has signed in
                }
                else {
                    //user has signed out
                }
            }
        };

        nameSignUpField = (EditText) findViewById(R.id.name_signup_field_id);
        emailSignUpField = (EditText) findViewById(R.id.email_signup_field_id);
        passwordSignUpField = (EditText) findViewById(R.id.password_signup_field_id);
        confirmPassSignUpField = (EditText) findViewById(R.id.password_confirm_signup_field_id);
        signUpButton = (Button) findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
