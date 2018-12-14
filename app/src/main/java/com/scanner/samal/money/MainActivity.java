package com.scanner.samal.money;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView nBlogList;

    private DatabaseReference nDatabase;

    private DatabaseReference nDatabaseUsers;

    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener nAuthListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        nAuth = FirebaseAuth.getInstance();
        nAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);


                }
            }
        };

        nDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        nDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        nDatabaseUsers.keepSynced(true);
        nDatabase.keepSynced(true);

        nBlogList = (RecyclerView) findViewById(R.id.blog_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        nBlogList.setHasFixedSize(true);
        nBlogList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();



        nAuth.addAuthStateListener(nAuthListener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                nDatabase

        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {



                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.nView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getContext(),"image clicked",Toast.LENGTH_SHORT).show();

                        Intent singleBlogIntent=new Intent(MainActivity.this,BlogSingleActivity.class);
                        startActivity(singleBlogIntent);

                    }
                });








            }


        };

        nBlogList.setAdapter(firebaseRecyclerAdapter);


    }


    private void checkUserExist() {

        final String user_id = nAuth.getCurrentUser().getUid();
        nDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)) {

                    Intent setupIntent = new Intent(MainActivity.this, MainActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View nView;

        public BlogViewHolder(View itemView) {
            super(itemView);

            nView = itemView;
        }

        public void setTitle(String title) {

            TextView post_title = (TextView) nView.findViewById(R.id.post_title);
            post_title.setText(title);

        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) nView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image) {

            ImageView post_image = (ImageView) nView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

        }


    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        if(item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileFragment.class));

        }
        if(item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsFragment.class));
        }

        if(item.getItemId() ==R.id.action_logout) {
            
            logout();


        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        nAuth.signOut();
    }
}
