package com.example.codeonandroid.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.codeonandroid.R;
import com.example.codeonandroid.api.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CodeEditor extends AppCompatActivity {
    RequestQueue requestQueue;
    static final String REQ_TAG = "CodeEditor";

    String compile_url = "https://api.jdoodle.com/v1/execute";
    String clientId = "af64cb00f3669b312e163a779bf5a9ff";
    String clientSecret = "80ce273edd18483c47d3b687d53019e2565e39079fa1e847b1adb6395a6d8e85";
    String script = "";
    String language = "python3";
    String versionIndex = "0";
    JSONObject json_param;

    private EditText input_text;
    private TextView output_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);

        input_text = findViewById(R.id.txt_code);
        output_text = findViewById(R.id.txt_output);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
    }
    public void runCode(View view) {
        script = input_text.getText().toString();
        if(script.isEmpty()){
            Toast.makeText(this,"You are not coding anything!",Toast.LENGTH_SHORT).show();
            return;
        }
        json_param = new JSONObject();
        try{
            json_param.put("clientId",clientId);
            json_param.put("clientSecret",clientSecret);
            json_param.put("script",script);
            json_param.put("language",language);
            json_param.put("versionIndex",versionIndex);

        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, compile_url, json_param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String output="";
                        String run_time="";
                        try{
                            output = response.get("output").toString();
                            run_time = response.get("cpuTime").toString();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        output_text.setText("Result: \n"+output+"\nRun time:\n"+run_time);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                output_text.setText("Error getting response");
            }
        });
        jsonObjectRequest.setTag(REQ_TAG);
        requestQueue.add(jsonObjectRequest);
    }
}
