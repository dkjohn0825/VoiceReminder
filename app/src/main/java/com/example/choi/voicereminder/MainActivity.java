package com.example.choi.voicereminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    EditText idInput, passwordInput;
    Button LoginBtn, SignUpBtn;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    CheckBox autoLogin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Boolean saveLogin;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        LoginBtn = (Button) findViewById(R.id.loginButton);
        SignUpBtn = (Button) findViewById(R.id.signupButton);
        autoLogin = (CheckBox) findViewById(R.id.checkBox);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        saveLogin=pref.getBoolean("saveLogin",false);
        if(saveLogin==true){
            idInput.setText(pref.getString("ID",""));
            passwordInput.setText(pref.getString("PW",""));
            autoLogin.setChecked(true);

        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SignUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(MainActivity.this, "", "USER 확인중... ", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Login();
                        Looper.loop();
                    }
                }).start();




            }
        });



    }


    void Login() {
        try {

            httpclient = getThreadSafeClient();
            httppost = new HttpPost("/vr/logincheck.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", idInput.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", passwordInput.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();



            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });
            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                String pre_id = idInput.getText().toString();
                String pre_pw = passwordInput.getText().toString();

                if (autoLogin.isChecked()) {
                    putPreferences(pre_id,pre_pw,true);


                }else{putPreferences(pre_id,pre_pw,false);}

                startActivity(new Intent(MainActivity.this, MainMenu.class));
                finish();
            }

            else {
                Toast.makeText(MainActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }


    public static DefaultHttpClient getThreadSafeClient() {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();

        client = new DefaultHttpClient(
                new ThreadSafeClientConnManager(params,
                        mgr.getSchemeRegistry()), params);

        return client;
    }

    private void putPreferences(String name,String name2,Boolean x) {
        editor.putString("ID", name);
        editor.putString("PW",name2);
        editor.putBoolean("saveLogin",x);
        editor.commit();

    }




}




