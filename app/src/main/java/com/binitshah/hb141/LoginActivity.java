package com.binitshah.hb141;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    //todo: replace all toasts with snackbars
    //todo: replace all hardcoded strings in errors
    //todo: use your string resource file for xmls too

    private Context context;
    private SharedPreferences sharedPref;
    private FirebaseStorage storage;
    private ProgressDialog pDialog;
    private final String TAG = "HB141Log";
    private final int FILE_SYSTEM_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int GOOGLE_SIGN_IN_REQUEST_CODE = 3;

    //switchers
    private RelativeLayout signInView;
    private RelativeLayout signUpView;
    private RelativeLayout socialsView;
    private RelativeLayout resetView;
    private Button signUpSwitcherButton;
    private Button forgotPassSwitcherButton;

    //Sign up: email/pass
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private EditText nameSignUpField;
    private EditText emailSignUpField;
    private EditText passwordSignUpField;
    private EditText confirmPassSignUpField;
    private Button signUpButton;
    private CircleImageView mSignUpPropic;
    private Bitmap photo;
    private boolean photoTaken = false;

    //sign in: email/pass
    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;

    //password reset
    private EditText emailResetField;
    private Button passResetButton;

    //socials login
    private GoogleApiClient mGoogleApiClient; //google
    private CallbackManager mCallbackManager; //fb
    private LoginButton fbloginButton; //fb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplication();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //handles switching to sign up view
        signInView = (RelativeLayout) findViewById(R.id.sign_in_view);
        signUpView = (RelativeLayout) findViewById(R.id.sign_up_view);
        socialsView = (RelativeLayout) findViewById(R.id.social_media_view);
        signUpSwitcherButton = (Button) findViewById(R.id.sign_up_switcher_button);
        signUpSwitcherButton.setOnClickListener(new View.OnClickListener() {
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
                    Log.d(TAG, "User has signed in");
                    if(photoTaken) {//we know that if photo was set, that the user when through the flow of signing up
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://hb141-2fc0d.appspot.com");
                        StorageReference userpropic = storageRef.child("propics/" + user.getUid() + "-propic.jpg");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask mUploadTask = userpropic.putBytes(data);
                        mUploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                try {
                                    File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "hb141");
                                    file.mkdir();
                                    File image = new File(file.getAbsoluteFile(), "propic.jpg");
                                    FileOutputStream out = new FileOutputStream(image);
                                    photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();
                                    Log.d(TAG, "Bitmap was saved locally.");
                                    finishCreateUser();
                                }
                                catch (Exception ex) {
                                    Toast.makeText(LoginActivity.this, "Listen brother, it's really not your day. I've tried everything and something keeps breaking. You're not even supposed to see this message. If you do, just do us both a solid and try again with a different email address.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri propicUri = taskSnapshot.getDownloadUrl();
                                Log.d(TAG, "Bitmap was saved online at " + propicUri.toString());
                                finishCreateUser(propicUri);
                            }
                        });
                    }
                    else { //the user did not just register.
                        pDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
        };

        //handles sign up
        mSignUpPropic = (CircleImageView) findViewById(R.id.propic_signup_field_id);
        mSignUpPropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[] {"File System", "Camera"};

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Get Image From");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if(which == 0) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent, FILE_SYSTEM_REQUEST_CODE);
                        }
                        else if(which == 1) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        }
                        else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        }
                    }
                });
                builder.show();
            }
        });
        nameSignUpField = (EditText) findViewById(R.id.name_signup_field_id);
        emailSignUpField = (EditText) findViewById(R.id.email_signup_field_id);
        passwordSignUpField = (EditText) findViewById(R.id.password_signup_field_id);
        confirmPassSignUpField = (EditText) findViewById(R.id.password_confirm_signup_field_id);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyAll(nameSignUpField.getText().toString(), emailSignUpField.getText().toString(), passwordSignUpField.getText().toString(), confirmPassSignUpField.getText().toString())) {
                    pDialog.show();
                    createUser(emailSignUpField.getText().toString(), passwordSignUpField.getText().toString());
                }
            }
        });

        //handles sign in process
        emailField = (EditText) findViewById(R.id.email_field_id);
        passwordField = (EditText) findViewById(R.id.password_field_id);
        signInButton = (Button) findViewById(R.id.login_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyLogin(emailField.getText().toString(), passwordField.getText().toString())) {
                    pDialog.show();
                    loginUser(emailField.getText().toString(), passwordField.getText().toString());
                }
            }
        });

        //handles reset switch
        resetView = (RelativeLayout) findViewById(R.id.pass_reset_view);
        forgotPassSwitcherButton = (Button) findViewById(R.id.forgot_password_button);
        forgotPassSwitcherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetView.setVisibility(View.VISIBLE);
                signInView.setVisibility(View.GONE);
                socialsView.setVisibility(View.GONE);
            }
        });

        //handles forgot password
        emailResetField = (EditText) findViewById(R.id.email_reset_field_id);
        passResetButton = (Button) findViewById(R.id.reset_button);
        passResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyReset(emailResetField.getText().toString())) {
                    pDialog.show();
                    resetPass(emailResetField.getText().toString());
                }
            }
        });

        //handles facebook login
        fbloginButton = (LoginButton) findViewById(R.id.facebooksignin_button);
        fbloginButton.setVisibility(View.GONE);
        fbloginButton.setReadPermissions("email", "public_profile");
        mCallbackManager = CallbackManager.Factory.create();
        fbloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(LoginActivity.this, "Facebook login cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LoginActivity.this, "Error: unable to login with facebook.", Toast.LENGTH_SHORT).show();
            }
        });
        Button fbButton = (Button) findViewById(R.id.fbbutton);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbloginButton.performClick();
            }
        });

        //handles google login
        Button gButton = (Button) findViewById(R.id.gbutton);
        gButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_idd))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void postSocialsUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        try {
            final DatabaseReference ref = mDatabase.child("users").child(user.getUid()).child("reputation");
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object rep = dataSnapshot.getValue();
                    if(rep == null) {
                        ref.setValue(0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Getting data from firebase database failed.", databaseError.toException());
                }
            };
            ref.addListenerForSingleValueEvent(postListener);
        }
        catch (NullPointerException e) {
            Log.e(TAG, "Nullpointer where we check the the social user rep.");
        }
    }

    //FACEBOOK
    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException()); //todo remove at release
                            String errorUnableToSignIn = "Unable to login with Facebook"; //todo change to getString
                            Toast.makeText(LoginActivity.this, errorUnableToSignIn, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            postSocialsUser();
                        }
                    }
                });
    }

    //GOOGLE
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException()); //todo remove at release
                            String errorUnableToSignIn = "Unable to login with Google"; //todo change to getString
                            Toast.makeText(LoginActivity.this, errorUnableToSignIn, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            postSocialsUser();
                        }
                    }
                });
    }

    public void resetPass(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content), "Reset email sent", Snackbar.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            p.addRule(RelativeLayout.BELOW, R.id.sign_in_view);
                            p.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            p.setMargins(0, 10, 0, 0);

                            socialsView.setLayoutParams(p);
                            resetView.setVisibility(View.GONE);
                            signInView.setVisibility(View.VISIBLE);
                            socialsView.setVisibility(View.VISIBLE);
                        }
                        else {
                            Snackbar.make(findViewById(android.R.id.content), "No such account exists", Snackbar.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                });
    }

    public void loginUser(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content), "Email or password incorrect", Snackbar.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                });
    }

    public void postCreateUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        try {
            mDatabase.child("users").child(user.getUid()).child("reputation").setValue(0);

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        }
        catch (NullPointerException e) {
            Log.e(TAG, "A nullpointer here really shouldn't have happened unless the user's email was never set... but then how was the user created?!? That would be a scary mystery. Let's hope this never happens");
        }

        //pDialog.dismiss();
        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
        //finish(); See, this is where you would think that we would want to switch over to the mainactivity. except guess what bitch, firebase hates you. so fuck you, instead we're going to be super counterintiutive. we're gonna sign out and then back in. that'll show you bitch. for real tho, i think that if we do this, then the FirebaseAuth will be forced to refresh or something and actually load the data.
        FirebaseAuth.getInstance().signOut();
        photoTaken = false;
        loginUser(emailSignUpField.getText().toString(), passwordSignUpField.getText().toString());
    }

    public void finishCreateUser() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameSignUpField.getText().toString())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.displayname_sharedpref_key), nameSignUpField.getText().toString());
                                editor.apply();
                                Log.d(TAG, "User's incomplete data saved locally");
                            } else {
                                Log.d(TAG, "User's incomplete data saved online");
                            }
                            postCreateUser();
                        }
                    });
        }
        catch (Exception e) {
            Toast.makeText(context, "Error: unable to finish user registration.", Toast.LENGTH_SHORT).show();
        }
    }

    public void finishCreateUser(final Uri propicUri) {
        Log.d(TAG, "In finishCreateUser with uri:" + propicUri.toString());
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameSignUpField.getText().toString())
                    .setPhotoUri(propicUri)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.displayname_sharedpref_key), nameSignUpField.getText().toString());
                                editor.putString(getString(R.string.imageUrl_sharedpref_key), propicUri.toString());
                                editor.apply();
                                Log.d(TAG, "User's data saved locally");
                            } else {
                                Log.d(TAG, "User's data saved online");
                            }
                            postCreateUser();
                        }
                    });
        }
        catch (Exception e) {
            Toast.makeText(context, "Error: unable to finish user registration.", Toast.LENGTH_SHORT).show();
        }
    }

    public void createUser(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content), "Email address taken already", Snackbar.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                });
    }

    public boolean verifyReset(String email) {
        if(email.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Email is empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(!email.contains("@") || !email.contains(".")) {
            Snackbar.make(findViewById(android.R.id.content), "Email address is not valid", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean verifyLogin(String email, String pass) {
        if(email.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Email is empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(pass.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Password is empty", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(!email.contains("@") || !email.contains(".")) {
            Snackbar.make(findViewById(android.R.id.content), "Email address is not valid", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(pass.length() <= 5) {
            Snackbar.make(findViewById(android.R.id.content), "Password incorrect.", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean verifyAll(String name, String email, String pass, String confirmPass) {
        if(name.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Name not completed", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(email.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Email not completed", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(pass.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Password not completed", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(confirmPass.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Password confirm not completed", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(!email.contains("@") || !email.contains(".")) {
            Snackbar.make(findViewById(android.R.id.content), "Email address is not valid", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(pass.length() <= 5) {
            Snackbar.make(findViewById(android.R.id.content), "Password must be longer than 5 characters", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(!pass.equals(confirmPass)) {
            Snackbar.make(findViewById(android.R.id.content), "The passwords do not match", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(!photoTaken) {
            Snackbar.make(findViewById(android.R.id.content), "Profile picture not set", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(signUpView.getVisibility() == View.VISIBLE) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, R.id.sign_in_view);
            p.addRule(RelativeLayout.CENTER_HORIZONTAL);
            p.setMargins(0, 10, 0, 0);

            socialsView.setLayoutParams(p);
            signInView.setVisibility(View.VISIBLE);
            signUpView.setVisibility(View.GONE);
        }
        else if(resetView.getVisibility() == View.VISIBLE) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, R.id.sign_in_view);
            p.addRule(RelativeLayout.CENTER_HORIZONTAL);
            p.setMargins(0, 10, 0, 0);

            socialsView.setLayoutParams(p);
            resetView.setVisibility(View.GONE);
            signInView.setVisibility(View.VISIBLE);
            socialsView.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        String googleServiceErrorMessage = "Error: unable to login with Google."; //todo: change to getString
        Toast.makeText(this, googleServiceErrorMessage, Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            Snackbar.make(findViewById(android.R.id.content), "Did not receive expected result", Snackbar.LENGTH_SHORT).show();
            return;
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data); //facebook part
        if(requestCode == FILE_SYSTEM_REQUEST_CODE) {
            try {
                Uri selectedImageUri = data.getData();
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                mSignUpPropic.setImageBitmap(photo);
                photoTaken = true;
            }
            catch (Exception e) {
                Toast.makeText(context, "Error: file system returned null.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == CAMERA_REQUEST_CODE) {
            Bundle extras = data.getExtras();
            if(extras != null) {
                photo = (Bitmap) extras.get("data");
                mSignUpPropic.setImageBitmap(photo);
                photoTaken = true;
            }
            else {
                Toast.makeText(context, "Error: camera returned null.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Successful Google Sign In
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Failed Google Sign In
                String errorUnableToSignIn = "Unable to sign in with Google."; //todo: change to getString
                Toast.makeText(LoginActivity.this, errorUnableToSignIn, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
