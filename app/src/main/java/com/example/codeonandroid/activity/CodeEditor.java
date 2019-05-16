package com.example.codeonandroid.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.codeonandroid.R;
import com.example.codeonandroid.api.RequestQueueSingleton;
import com.example.codeonandroid.fragment.EditorFragment;
import com.example.codeonandroid.widget.ShaderEditor;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CodeEditor extends AppCompatActivity implements ShaderEditor.OnTextChangedListener {

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

    private Snackbar snackbar;
    private EditText input_text;
    private TextView output_text;
    private EditorFragment editorFragment;
    private Spinner qualitySpinner;
    private ImageButton back_btn;
    private ImageButton hide_keyboard_btn;
    private LinearLayout linearLayoutTool;

    private static final String SELECTED_SHADER = "selected_shader";
    private static final String CODE_VISIBLE = "code_visible";
    private static final int PREVIEW_SHADER = 1;
    private static final int ADD_UNIFORM = 2;
    private static final int LOAD_SAMPLE = 3;
    private static final int FIRST_SHADER = -1;
    private static final int NO_SHADER = 0;


    public void hide_keyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    public void back(View view){
        finish();
    }
    public void create_inputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custome input");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stdl = input.getText().toString();
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
                "catch", "char", "class", "const",
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
                "void"));

        for(int i = 0; i < KEYWORD.size(); i++){
            map.put(KEYWORD.get(i), Color.CYAN);
        }

        List<String> TYPE = new ArrayList<>(Arrays.asList("int", "long",
                "float", "String", "char", "double"));

        for(int i = 0; i < TYPE.size(); i++){
            map.put(TYPE.get(i), Color.RED);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_editor);
        initData();

        inputColor();

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
    public void onTextChanged(String text) {

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
                    language = item.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
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
        }else if(id == R.id.new_code){

        }else if(id == R.id.save_as){

        }else if(id == R.id.delete_file){

        }else if(id == R.id.run_code){
            runCode();
        }else if(id == R.id.input_stdi){
            create_inputDialog();
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


}
