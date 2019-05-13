package com.example.codeonandroid.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.codeonandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private Button logout_btn;
    private TextView username;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    public void initData(){
        logout_btn = findViewById(R.id.logout_btn);
        username = findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        username.setText(user.getDisplayName());
    }


    public void logout(View view){
        mAuth.signOut();
        finish();
    }

}
