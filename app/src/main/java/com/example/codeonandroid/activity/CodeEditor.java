package com.example.codeonandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.codeonandroid.R;
import com.example.codeonandroid.api.RequestQueueSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CodeEditor extends AppCompatActivity  {

    private Toolbar toolbar;
    private RelativeLayout layout;
    RequestQueue requestQueue;
    HashMap<String, Integer> map;
    static final String REQ_TAG = "CodeEditor";

    String compile_url = "https://api.jdoodle.com/v1/execute";
    String clientId = "af64cb00f3669b312e163a779bf5a9ff";
    String clientSecret = "80ce273edd18483c47d3b687d53019e2565e39079fa1e847b1adb6395a6d8e85";
    String script = "";
    String language = "";
    String versionIndex = "0";
    String output="";
    String stdl ="";
    JSONObject json_param;
    String filename ="filename";

    private Snackbar snackbar;
    private EditText input_text;
    private TextView output_text;

    private static final int READ_REQUEST_CODE = 42;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public void hide_keyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    public void back(View view){
        finish();
    }
    public void create_inputDialog(final String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(title.equals("Custom input")){
                    stdl = input.getText().toString();
                }else{
                    filename = input.getText().toString();
                    save_file(filename,input_text.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#FF0B8B42"));
    }
    public void do_tab(View view){
        input_text.append("\t");
    }
    public void clear_code(View view){
        input_text.setText("");
    }
    public void initData(){
        input_text = findViewById(R.id.txt_code);
        toolbar = findViewById(R.id.toolbar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        map = new HashMap<>();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        layout = findViewById(R.id.code_editor_layout);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
    }
    public void inputColor(){
        List<String> KEYWORD = new ArrayList<>(Arrays.asList("abstract", "and",
                "arguments", "assert", "break", "case",
                "catch", "char", "const",
                "continue", "default", "def", "in",
                "init", "delete", "do", "dynamic",
                "type", "if", "else", "elif",
                "enum", "extend", "false", "final",
                "for", "from", "function", "get",
                "go", "goto", "interface", "local",
                "map", "namespace", "new", "null",
                "or", "override", "package", "prefix",
                "print", "private", "protected", "public",
                "return", "sizeof", "static", "struct",
                "switch", "this", "true", "try",
                "void","echo"));

        for(int i = 0; i < KEYWORD.size(); i++){
            map.put(KEYWORD.get(i), Color.CYAN);
        }

        List<String> TYPE = new ArrayList<>(Arrays.asList("int", "long","bool","string",
                "float", "String", "char", "double","endl"));

        for(int i = 0; i < TYPE.size(); i++){
            map.put(TYPE.get(i), Color.GREEN);


        }
        List<String> CLASS = new ArrayList<>(Arrays.asList("class", "using","#include","string",
                "cout","cin","throw"));

        for(int i = 0; i < CLASS.size(); i++) {
            map.put(CLASS.get(i),Color.parseColor("#FFCF5AE2"));
        }
    }
    public void show_output(String res){
        snackbar = Snackbar
                .make(layout,res, Snackbar.LENGTH_INDEFINITE)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                })
                .setActionTextColor(Color.RED);


        View snackbarView = snackbar.getView();
        TextView sbTextView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        sbTextView.setTextSize(15f);
        sbTextView.setGravity(Gravity.TOP);
        sbTextView.setMaxLines(100);
        snackbar.show();
    }
    public void save_file(String filename,String content){
        String extension="";
        if(language.equals("cpp")){
            extension=".cpp";
        }else if(language.equals("python3")){
            extension=".py";
        }else if(language.equals("java")){
            extension=".java";
        }else if(language.equals("csharp")){
            extension=".cs";
        }else if(language.equals("php")){
            extension=".php";
        }
        String fileName = filename + extension;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),fileName);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this,"Code's saved as file!",Toast.LENGTH_SHORT).show();
        }catch(FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this,"Error occured while saving file!",Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this,"Error occured while saving file!",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);
        initData();
        inputColor();
        text_watcher();

    }

    public void text_watcher(){
        input_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                String[] split = string.split("\\s");

                int i = 0;
                int startIndex = 0;
//                for(int i = 0 ; i < split.length ; i++){
                while(i < split.length){
                    String s = split[i];
                    if(map.containsKey(s)){

                        int index = string.indexOf(s, startIndex);
                        int color = map.get(s);
                        editable.setSpan(new ForegroundColorSpan(color),
                                index,
                                index + s.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        startIndex = index + s.length();
                    }
                    else if(!s.isEmpty()){
                        if(s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"'){
                            int index = string.indexOf(s, startIndex);
                            editable.setSpan(new ForegroundColorSpan(Color.GREEN),
                                    index,
                                    index + s.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            startIndex = index + s.length();
                        }else{
                            int index = string.indexOf(s, startIndex);
                            editable.setSpan(new ForegroundColorSpan(Color.WHITE),
                                    index,
                                    index + s.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            startIndex = index + s.length();
                        }
                    }
                    else if(!map.containsKey(s)){
                        int index = string.indexOf(s, startIndex);
                        editable.setSpan(new ForegroundColorSpan(Color.WHITE),
                                index,
                                index + s.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        startIndex = index + s.length();
                    }
                    Log.w("number", string);
                    i++;
                }
                //
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_actionbar,menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.cube_face_text), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    text_watcher();
                    language = item.toString();
                    String lang = item.toString();
                    if(lang.equals("cpp")){
                        input_text.setText("#include <iostream>\nusing namespace std;\nint main(){\ncout <<\"Hello World!\"<< endl;\nreturn 0;\n}");
                    }else if(lang.equals("java")){
                        input_text.setText("public class Helloword {\n\tpublic static void main(String[] args){\n\t\tSystem.out.println(\"Hello World!\");\n\t\t}\n\t}");
                    }else if(lang.equals("csharp")){
                        input_text.setText("using System;\nnamespace HelloWorld {\n\tclass Hello {\n\t\tstatic void Main(){\n\t\t\tConsole.WriteLine(\"Hello World!\");\n\t\t\t}\n\t\t}\n\t}");
                    }else if(lang.equals("php")){
                        input_text.setText("<?php echo (\"Hello Word\") ?>");
                    }else if(lang.equals("python3")){
                        input_text.setText("print (\"Hello World!\")");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(this,"Permission not granted!",Toast.LENGTH_SHORT).show();
                }
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.read_only){
            if(input_text.isEnabled() == true){
                input_text.setEnabled(false);
            }else{
                input_text.setEnabled(true);
            }
        }else if(id == R.id.exit){
            finish();
            moveTaskToBack(true);
        }else if(id == R.id.paste_code){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = clipboard.getPrimaryClip();
            input_text.append(clip.getItemAt(0).getText().toString());

        }else if(id == R.id.copy_code){
            String res="";
            if(input_text.getText().toString().isEmpty()){
                res="Nothing to copy!";
            }else{
                String text = input_text.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("code",text);
                clipboard.setPrimaryClip(clip);
                res="Code copied to clipboard!";
            }
            Toast.makeText(this,res,Toast.LENGTH_SHORT).show();

        }else if(id == R.id.back){
            finish();
        } else if(id == R.id.save_as){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ){
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
            }
            create_inputDialog("Save file","Input file name: ");


        }else if(id == R.id.share_code){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, input_text.getText().toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }else if(id == R.id.run_code){
            runCode();
        }else if(id == R.id.input_stdi){
            create_inputDialog("Custom input","Input parameter: ");
        }else if(id == R.id.save_online){
            if(user !=null){
                final DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                final ArrayList<String> list = (ArrayList<String>) document.get("codes");
                                list.add(input_text.getText().toString());
                                docRef.update("codes",list);
                                Toast.makeText(CodeEditor.this,"Code has been saved online!",Toast.LENGTH_SHORT).show();
                            }else{
                            }
                        }else{
                        }
                    }
                });
            }else{
                Toast.makeText(CodeEditor.this,"You must login to use this feature!",Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void runCode() {
        script = input_text.getText().toString();
        Log.d("language", "runCode: "+language);
        if(script.isEmpty()){
            Toast.makeText(this,"You are not coding anything!",Toast.LENGTH_SHORT).show();
            return;
        }
        json_param = new JSONObject();
        try{
            json_param.put("clientId",clientId);
            json_param.put("clientSecret",clientSecret);
            json_param.put("script",script);
            json_param.put("stdin",stdl);
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
                        output = "Output: \n"+output+"\nRun time:\n"+run_time;
                        show_output(output);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                output = "Error getting response!";
                show_output(output);
            }
        });
        jsonObjectRequest.setTag(REQ_TAG);
        requestQueue.add(jsonObjectRequest);
    }

    // Open file
}
