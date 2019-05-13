package com.example.codeonandroid.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codeonandroid.R;
import com.google.firebase.auth.FirebaseAuth;

public class IntroScreen extends AppCompatActivity {
    private ImageView arrow_left;
    private ImageView arrow_right;
    private ImageView arrow_end;
    private TextView  app_name;
    private Button  btn_register;
    private Button btn_login;

    Display display;
    Point size;
    private int device_width;
    private int device_height;
    Animation anim;

    private static int duration = 1000;
    private static int btn_timeout = 1000;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        splashScreenAni();
        Log.e("Width", "" + device_width);
        Log.e("height", "" + device_height);
    }

    public void initData(){

        arrow_left = findViewById(R.id.arrow_left);
        arrow_right = findViewById(R.id.arrow_right);
        arrow_end = findViewById(R.id.arrow_end);
        app_name = findViewById(R.id.app_name);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        device_height = size.y;
        device_width = size.x;

        mAuth = FirebaseAuth.getInstance();

        arrow_left.setX(-1000);
        arrow_right.setX(1000);
        anim = AnimationUtils.loadAnimation(this,R.anim.splashanim);
        anim.setInterpolator((new AccelerateDecelerateInterpolator()));
        anim.setFillAfter(true);
    }

    public void button_animate(){
        btn_login.animate().alpha(1).setDuration(duration);
        btn_register.animate().alpha(1).setDuration(duration);
    }
    public void splashScreenAni(){

        arrow_left.animate().translationX(90f).setDuration(duration);
        arrow_right.animate().translationX(-90f).setDuration(duration);
        arrow_end.setAnimation(anim);
        app_name.animate().alpha(1).scaleX(2f).scaleY(2f).translationY(200f).setDuration(duration);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser() == null) {
                    button_animate();
                }else{
                    Intent main_intent = new Intent(IntroScreen.this,MainActivity.class);
                    startActivity(main_intent);
                }
            }
        }, btn_timeout);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        button_animate();
    }

    public void buttonClick(View view){
        Intent login_intent = new Intent(this,Login.class);
        Intent register_intent = new Intent( this,Register.class);
        if(view.getId() == R.id.btn_login){
            startActivity(login_intent);
        }else if(view.getId() == R.id.btn_register){
            startActivity(register_intent);
        }
    }
}
