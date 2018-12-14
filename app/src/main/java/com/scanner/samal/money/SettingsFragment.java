package com.scanner.samal.money;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import static java.security.AccessController.getContext;



public class SettingsFragment extends AppCompatActivity {


   //private TextView mChangePasswordSettingBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

   // View mSettingView;

    private Button mLogoutBtn;
    private TextView mEditAcc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        //custom bar

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);

        View view =getSupportActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton imageButton2= (ImageButton)view.findViewById(R.id.action_bar_forward);

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Forward Button is clicked",Toast.LENGTH_LONG).show();
            }
        });

        //custom bar code ends



        // Inflate the layout for this fragment
      //  mSettingView= inflater.inflate(R.layout.fragment_settings, container, false);

       mAuth= FirebaseAuth.getInstance();

       /* mAuthListner=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){
                    //Toast.makeText(getContext(),"loged in as '.......'",Toast.LENGTH_LONG).show();

                }else{
                   /* Intent loginIntent= new Intent(getContext(),LoginActivity.class);
                    startActivity(loginIntent);
                    getActivity().finish();*//*
                }

            }
        };

/*/
        mLogoutBtn=(Button) findViewById(R.id.logoutbtn);
        mEditAcc=(TextView) findViewById(R.id.editprofileBtn);

      //  mChangePasswordSettingBtn=(TextView)mSettingView.findViewById(R.id.changePasswordSettingBtn);

       // mChangePasswordSettingBtn.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View view) {
        //        startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
       //     }
       // });



        mEditAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AccountSetupIntent= new Intent(SettingsFragment.this,SetupActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(AccountSetupIntent);

            }
        });

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(getContext(),"cicked",Toast.LENGTH_LONG).show();
                LogOut();
            }
        });


    }

    private void LogOut() {
        mAuth.signOut();
        Intent loginIntent= new Intent(SettingsFragment.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(loginIntent);

    }

   // @Override
   // public void onStart() {
   //     super.onStart();
      // //  mAuth.addAuthStateListener(mAuthListner);
   // }

   // @Override
   // public void onStop() {
    //    super.onStop();

   //     if (mAuthListner != null) {
          // // mAuth.removeAuthStateListener(mAuthListner);
       // }
   // }



}
