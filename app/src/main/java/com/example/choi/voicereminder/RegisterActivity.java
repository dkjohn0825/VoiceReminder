package com.example.choi.voicereminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;


public class RegisterActivity extends AppCompatActivity {
    Button joinOK;
    EditText edtname, edtid, edtpass,edtphone;
    ProgressDialog dialog1;
    private static final String SERVER_ADDRESS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        joinOK = (Button) findViewById(R.id.SignOkButton);


        edtid = (EditText) findViewById(R.id.idInput);
        edtpass = (EditText) findViewById(R.id.passwordInput);
        edtname = (EditText) findViewById(R.id.nameInput);
        edtphone = (EditText)findViewById(R.id.phoneInput);

        joinOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtname.getText().toString().equals("") || edtid.getText().toString().equals("") || edtpass.getText().toString().equals("")||edtphone.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "입력 오류입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        String name = edtname.getText().toString();
                        String id = edtid.getText().toString();
                        String password = edtpass.getText().toString();
                        String phone = edtphone.getText().toString();


                        try {
                            dialog1 = ProgressDialog.show(RegisterActivity.this, "", "처리중... ", true);
                            URL url = new URL(SERVER_ADDRESS + "/vr/list.php?" + "name=" + URLEncoder.encode(name, "UTF-8") + "&id=" + URLEncoder.encode(id, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&phone=" + URLEncoder.encode(phone, "UTF-8"));
                            url.openStream();
                            String result = getXmlData("insertresult.xml", "result");
                            if (result!=null) {
                                Toast.makeText(RegisterActivity.this, "가입완료! 로그인하세요", Toast.LENGTH_SHORT).show();

                                edtid.setText("");
                                edtpass.setText("");
                                edtname.setText("");
                                edtphone.setText("");

                            } else {
                                Toast.makeText(RegisterActivity.this, "회원 가입 실패", Toast.LENGTH_SHORT).show();
                            }
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();

                        } catch (Exception e) {

                            Log.e("error", e.getMessage());
                        }
                    }
                });


            }

        });
    }


    private String getXmlData(String filename, String str) {
        String rss = SERVER_ADDRESS + "/";
        String ret = "";


        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            URL server = new URL(rss + filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals(str)) {
                        ret = xpp.nextText();

                    }
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        return ret;
    }
}