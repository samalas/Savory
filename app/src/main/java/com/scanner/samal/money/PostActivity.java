package com.scanner.samal.money;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.R.attr.button;
import static android.R.attr.data;

public class PostActivity extends AppCompatActivity {

    private ImageButton nSelectImage;
    private EditText nPostTitle;
    private EditText nPostDesc;
    private Button nSubmitB;

    private Uri nImageUri = null;


    private static final int GALLERY_REQUEST = 1;

    private StorageReference nStorage;
    private DatabaseReference nDatabase;
    private ProgressDialog nProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

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

        nStorage = FirebaseStorage.getInstance().getReference();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");



        nSelectImage = (ImageButton) findViewById(R.id.imageSelect);

        nPostTitle = (EditText) findViewById(R.id.titleField);
        nPostDesc = (EditText) findViewById(R.id.descField);

        nSubmitB = (Button) findViewById(R.id.submitB);

        nProgress = new ProgressDialog(this);

        nSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });



        nSubmitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startPosting();
            }
        });

    }

    private void startPosting() {

        nProgress.setMessage("posting to Blog ...");
        nProgress.show();


        final String title_val = nPostTitle.getText().toString().trim();
        final String desc_val = nPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && nImageUri != null) {

            nProgress.show();


            StorageReference filepath = nStorage.child("Blog_Images").child(nImageUri.getLastPathSegment());

            filepath.putFile(nImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = nDatabase.push();

                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl.toString());




                    nProgress.dismiss();

                    startActivity(new Intent(PostActivity.this, MainActivity.class));



                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

             nImageUri = data.getData();
            nSelectImage.setImageURI(nImageUri);

        }

    }
}
