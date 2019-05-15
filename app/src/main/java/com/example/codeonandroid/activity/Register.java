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
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText txt_rusername;
    private EditText txt_rpassword;
    private EditText txt_name;
    private Button reg_btn;
    private Button gmail_log_btn;
    private ImageButton back_btn;

    private String rusername;
    private String rpassword;
    private String name;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSingInClient;
    private static int RC_SIGN_IN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        txt_name = findViewById(R.id.txt_name);
        txt_rusername = findViewById(R.id.reguser);
        txt_rpassword = findViewById(R.id.regpass);
        gmail_log_btn = findViewById(R.id.reg_gmail);
        back_btn = findViewById(R.id.btn_back2);

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient = GoogleSignIn.getClient(this,gso);
    }

    public void back_click(View view){
        finish();
    }
    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Register.this,"Create new user successfully!",Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    createUserProfile(user);
                }else{
                    Toast.makeText(Register.this,"Register error: "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUserProfile(FirebaseUser user){
        name = txt_name.getText().toString();
        if(name.isEmpty()){
            name = "The Coder";
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent main_intent = new Intent(Register.this,AppMain.class);
                            startActivity(main_intent);
                        }
                    }
                });
    }

    public void register_btnClick(View view){
        rusername = txt_rusername.getText().toString();
        rpassword = txt_rpassword.getText().toString();
        if(rusername.isEmpty() || rpassword.isEmpty()){
            Toast.makeText(this,"Please fill in email/password to register!",Toast.LENGTH_SHORT).show();
            return;
        }
        createUser(rusername,rpassword);
    }

    public void continue_gmail(View view){
        Intent signInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
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
                            Intent main_intent = new Intent(Register.this,AppMain.class);
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
