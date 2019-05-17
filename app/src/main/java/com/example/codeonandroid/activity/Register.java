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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    FirebaseFirestore db;

    public void addUser(String id, String name, int exp, String avatar, ArrayList<String> codes){
        Map<String, Object> user = new HashMap<>();
        user.put("avatar", avatar);
        user.put("exp", exp);
        user.put("name", name);
        user.put("uid",id);
        user.put("codes",codes);

        db.collection("users")
                .document(id).set(user);

    }
    public void check(final String docId, final String name,final int exp, final String avatar,final ArrayList<String> codes){
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
                            }
                        }
                    }else{
                        addUser(docId,name,exp,avatar,codes);
                    }
                }else{

                }
            }
        });
    }
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
        db = FirebaseFirestore.getInstance();
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

    private void createUserProfile(final FirebaseUser user){
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
                            ArrayList<String> codes = new ArrayList<>();
//                            codes.add("print(\"Hello World\"");
                            String avatar ="empty";
                            if(user.getPhotoUrl()!=null){
                                avatar=user.getPhotoUrl().toString();
                            }
                            addUser(user.getUid(),user.getDisplayName(),0,avatar,codes);
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
                            ArrayList<String> codes = new ArrayList<>();
                            //codes.add("print(\"Hello World\")");
                            String avatar ="empty";
                            if(user.getPhotoUrl()!=null){
                                avatar=user.getPhotoUrl().toString();
                            }
                            check(user.getUid(),user.getDisplayName(),0,avatar,codes);
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
