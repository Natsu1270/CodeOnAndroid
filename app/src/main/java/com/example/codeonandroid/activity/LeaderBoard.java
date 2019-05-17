package com.example.codeonandroid.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.codeonandroid.R;
import com.example.codeonandroid.User;
import com.example.codeonandroid.adapter.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.support.v7.widget.Toolbar;
import java.util.ArrayList;
import java.util.Map;

public class LeaderBoard extends AppCompatActivity {
    ListView leadView;
    ArrayList<User> users;
    UserAdapter adapter;
    Toolbar normal_toolbar;
    ImageButton btn_back;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);


        leadView = findViewById(R.id.leader_list);
        normal_toolbar = findViewById(R.id.normal_toolbar);
        db = FirebaseFirestore.getInstance();

        setSupportActionBar(normal_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_back = findViewById(R.id.lead_back_btn);
        users = new ArrayList<>();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereGreaterThan("exp",4).limit(15).orderBy("exp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        int exp =0;
                        String name ="";
                        String avatar = "";
                        User u;
                        Map<String, Object> map = doc.getData();

                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("exp")) {
                                exp =Integer.valueOf(entry.getValue().toString());
                            }else if(entry.getKey().equals("name")){
                                name = entry.getValue().toString();
                            }else if(entry.getKey().equals("avatar")){
                                avatar = entry.getValue().toString();
                            }
                        }
                        u = new User(name,exp,avatar);
                        users.add(u);

                    }
                    adapter = new UserAdapter(LeaderBoard.this,R.layout.leader_board_item,users);
                    leadView.setAdapter(adapter);
                }

            }
        });

    }
}
