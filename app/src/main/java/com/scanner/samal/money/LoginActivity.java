package com.scanner.samal.money;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends FragmentActivity {

    private EditText nLoginEmailField;
    private EditText nLoginPasswordField;
    private Button nLoginBtn;
    private SignInButton mGoogleBtn;
    private Button nNewAccountBtn;

    private FirebaseAuth nAuth;

    private ProgressDialog nProgress;

    private DatabaseReference nDatabaseUsers;

    private TextView mForgotpswdLoginTv;

    ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    private static final String TAG="LoginActivity";

    private static final int RC_SIGN_IN=1;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
       // overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


        nAuth = FirebaseAuth.getInstance();

        nDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        nDatabaseUsers.keepSynced(true);

        nProgress = new ProgressDialog(this);

        mGoogleBtn=(SignInButton)findViewById(R.id.googleBtn);



        nLoginPasswordField = (EditText) findViewById(R.id.loginPasswordField);
        nLoginEmailField = (EditText) findViewById(R.id.loginEmailField);
        //forgot password
        mForgotpswdLoginTv=(TextView)findViewById(R.id.forgotpswdLoginTv);

        nLoginBtn = (Button) findViewById(R.id.loginBtn);

        nNewAccountBtn = (Button) findViewById(R.id.NewAccountbtn);

        mForgotpswdLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivityForResult(intent,0);
                overridePendingTransition( R.anim.trans_left_in, R.anim.trans_left_out );


               // startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

        nNewAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAccount();
            }
        });


        nLoginBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });






        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(),"google signIn failed",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        //////////////////hide soft keys (navigation drawer )/////////////////////////////////
        final View decorView = (LoginActivity.this).getWindow().getDecorView();
        LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });








    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("Strating SignIn...");
            mProgress.show();
            if (result.isSuccess()) {
                mProgress.dismiss();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(),"Error SignIn...",Toast.LENGTH_LONG).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            mProgress.dismiss();

                            checkUserExist();
                        }
                        // ...
                    }
                });
    }

    //end of google login shit


    private void newAccount()  {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
       // startActivity(intent);
        startActivityForResult(intent,0);
        overridePendingTransition( R.anim.trans_left_in, R.anim.trans_left_out );


    }




    private void checkLogin() {

        String email = nLoginEmailField.getText().toString().trim();
        String password = nLoginPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            nProgress.setMessage("Checking Login...");
            nProgress.show();

            nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {

                        nProgress.dismiss();

                        checkUserExist();
                    } else {

                        nProgress.dismiss();

                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

    private void checkUserExist() {

            final String user_id = nAuth.getCurrentUser().getUid();
            nDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(user_id)) {

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //startActivity(mainIntent);

                        startActivityForResult(mainIntent,0);
                        overridePendingTransition( R.anim.trans_left_in, R.anim.trans_left_out );


                    }else {

                       // Toast.makeText(LoginActivity.this, "You need to setup your account", Toast.LENGTH_LONG).show();

                        Intent setupIntent= new Intent(LoginActivity.this,SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //startActivity(setupIntent);
                        startActivityForResult(setupIntent,0);
                        overridePendingTransition( R.anim.trans_left_in, R.anim.trans_left_out );
                        finish();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }
}
