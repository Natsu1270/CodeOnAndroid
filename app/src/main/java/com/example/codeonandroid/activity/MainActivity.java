package com.example.codeonandroid.activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codeonandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private Button logout_btn;
    private Button code_btn;
    private TextView username;
    private ImageView avatar;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initData();
        drawerLayout = findViewById(R.id.nav_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.about){
            Toast.makeText(this,"fuck",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }else{
            switch (item.getItemId()){
                case R.id.leader_board:
                    Toast.makeText(this,"fuck",Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                case R.id.about:
                    return true;
                case R.id.exit:
                    finish();
                    System.exit(0);
                    return true;
                case R.id.feedback:
                    return true;
                case R.id.help:
                    return true;
                case R.id.rate:
                    return true;
                case R.id.setting:
                    return true;
            }
        }

        return true;
    }

    public void initData(){
//        logout_btn = findViewById(R.id.logout_btn);
//        code_btn = findViewById(R.id.code);
//        username = findViewById(R.id.username);
//        avatar = findViewById(R.id.avatar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            username.setText(user.getDisplayName());
        }else{
            username.setText("The Coder");
        }
        avatar.setImageResource(R.drawable.avatar);
    }


    public void logout(View view){
        mAuth.signOut();
        finish();
    }

    public void code(View view){
        Intent code_intent = new Intent(MainActivity.this,CodeEditor.class);
        startActivity(code_intent);
    }

}
