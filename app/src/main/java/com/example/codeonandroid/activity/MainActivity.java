package com.example.codeonandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.codeonandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private Button logout_btn;
    private Button code_btn;
    private TextView username;
    private ImageView avatar;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initData();
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
