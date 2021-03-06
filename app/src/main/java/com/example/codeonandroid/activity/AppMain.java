package com.example.codeonandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codeonandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class AppMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView nav_username;
    private TextView nav_email;
    private ImageView iavatar;
    private ImageView profile_avatar;
    private TextView profile_name;
    private TextView profile_email;
    private TextView exp;
    private TextView codes_save;
    LinearLayout list_code_view;
    FirebaseFirestore db;


    NavigationView navigationView;
    DrawerLayout drawer;
    Uri avatar;


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public void code_now(View view){
        Intent code_intent = new Intent(AppMain.this,CodeEditor.class);
        startActivity(code_intent);
    }


    public void update_exp(String docId){
        final DocumentReference docRef = db.collection("users").document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("exp")) {
                                int update_exp=Integer.valueOf(entry.getValue().toString())+1;
                                docRef.update("exp",update_exp);
                                exp.setText(Integer.toString(update_exp));
                            }
                        }
                    }else{
                    }
                }else{
                }
            }
        });
    }
    public void query_count(final String docId){
        final DocumentReference docRef = db.collection("users").document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        final ArrayList<String> list = (ArrayList<String>) document.get("codes");

                        codes_save.setText(Integer.toString(list.size()));
                        list_code_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(Integer.valueOf(codes_save.getText().toString())<1){
                                    Toast.makeText(AppMain.this,"You have not saved any code yet!",Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(AppMain.this,ListCode.class);
                                intent.putExtra("list",list);
                                startActivity(intent);
                            }
                        });

                    }else{
                    }
                }else{
                }
            }
        });
    }
    public void initData(){

        list_code_view = findViewById(R.id.list_code_view);
        exp = findViewById(R.id.exp);
        codes_save = findViewById(R.id.code_save);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        nav_username = headerView.findViewById(R.id.nav_user_name);
        nav_email = headerView.findViewById(R.id.nav_email);
        iavatar = headerView.findViewById(R.id.avatar);
        profile_avatar = findViewById(R.id.profile_avatar);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        if(user!=null && user.getPhotoUrl()==null){
            profile_avatar.setImageResource(R.drawable.people);
        }
        if(user != null){
            update_exp(user.getUid());
            try{
                profile_email.setText(user.getEmail());
                profile_name.setText(user.getDisplayName());
                nav_username.setText(user.getDisplayName());
                nav_email.setText(user.getEmail());
                avatar = user.getPhotoUrl();
                new DownloadImageTask(iavatar).execute(avatar.toString());
                new DownloadImageTask(profile_avatar).execute(avatar.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            profile_avatar.setImageResource(R.drawable.people);
            profile_name.setText("The coder");
            nav_username.setText("The coder");
            nav_email.setText("Anonymous@coa.com");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
        if(user != null){
            update_exp(user.getUid());
            query_count(user.getUid());
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(user !=null){
            query_count(user.getUid());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exit(){
        finish();
        moveTaskToBack(true);
    }
    private void make_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Did you code enough?")
                .setTitle("Exit the app")
                .setIcon(R.drawable.exit_ico)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
                    }
                })
                .setNegativeButton("No",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.RED);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FF0B8B42"));
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.leader_board) {
            Intent intent = new Intent(this,LeaderBoard.class);
            startActivity(intent);

        }  else if (id == R.id.help) {

        } else if (id == R.id.about) {

        } else if (id == R.id.exit) {
            make_dialog();
        } else if (id == R.id.feedback) {

        }else if (id == R.id.rate){

        }else if (id == R.id.logout){
            mAuth.signOut();
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
