package com.example.codeonandroid.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.codeonandroid.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    private EditText log_userName;
    private EditText log_password;
    private Button log_btn;
    private Button gmail_log_btn;
    private ImageButton btn_back;
    private FirebaseAuth mAuth;

    private String username;
    private String password;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSingInClient;
    private String clientID;

    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initData();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("login", "Google sign in failed", e);
                // ...
            }
        }
    }
    public void initData(){
        log_userName = findViewById(R.id.txt_user);
        log_password = findViewById(R.id.txt_pass);
        log_btn = findViewById(R.id.log_in);
        gmail_log_btn = findViewById(R.id.sign_gmail);
        mAuth = FirebaseAuth.getInstance();
        btn_back = findViewById(R.id.btn_back);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient = GoogleSignIn.getClient(this,gso);
    }

    public void back_click(View view){
        finish();
    }
    private void logIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent main_intent = new Intent(Login.this,AppMain.class);
                            startActivity(main_intent);

                        }else{
                            Toast.makeText(Login.this,"Login failed "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void gmail_login(View view){
        Intent signInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    public void login_btn_click(View view){
        username = log_userName.getText().toString();
        password = log_password.getText().toString();
        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please fill in your email/password to login!",Toast.LENGTH_SHORT).show();
            return;
        }
        logIn(username,password);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("login", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent main_intent = new Intent(Login.this,AppMain.class);
                            startActivity(main_intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.bgImg), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
