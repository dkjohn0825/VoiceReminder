package com.example.choi.voicereminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class HubRegisterActivity extends AppCompatActivity {

    private String global_id = "";
    public List<String> modules = new ArrayList<>();
    private ListView lvModuleControl;
    private TextView moduleadd;
    HttpClient client = new DefaultHttpClient();
    MainMenu prev_MainMenu = (MainMenu)MainMenu.myActivity;

    private static final String SERVER_ADDRESS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_register);


        lvModuleControl=(ListView)findViewById(R.id.lvModuleControl);
        moduleadd=(TextView)findViewById(R.id.moduleadd);

        Intent intent = getIntent();
        global_id = intent.getStringExtra("USERID");

        showmodulelist();





        ArrayAdapter<String> moduleList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modules){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);



                // Return the view
                return view;
            }
        };

        lvModuleControl.setAdapter(moduleList);



        lvModuleControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.module_select,null);
                AlertDialog.Builder alert = new AlertDialog.Builder(HubRegisterActivity.this);
                alert.setView(dialogView);
                alert.setTitle(modules.get(position).toString());

                final TextView module_change=(TextView)dialogView.findViewById(R.id.module_change);
                final TextView module_delete=(TextView)dialogView.findViewById(R.id.module_delete);

                module_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.module_change,null);
                        AlertDialog.Builder alert_change = new AlertDialog.Builder(HubRegisterActivity.this);
                        alert_change.setTitle("모듈 정보 수정");
                        alert_change.setView(dialogView);


                        final EditText mod_change_name=(EditText) dialogView.findViewById(R.id.mod_change_name_edit);
                        final EditText mod_change_SSID=(EditText) dialogView.findViewById(R.id.mod_change_SSID_edit);
                        final EditText mod_change_passwd=(EditText) dialogView.findViewById(R.id.mod_change_password_edit);

                        mod_change_name.setText(modules.get(position).toString());

                        alert_change.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String result="";

                                        if(mod_change_SSID.getText().toString()==""||mod_change_passwd.getText().toString()=="")
                                        {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(HubRegisterActivity.this);
                                            alert.setTitle("입력이 잘못되었습니다.");

                                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }

                                            });

                                            alert.show();
                                        }else{

                                        try {

                                            String name = modules.get(position).toString();
                                            String new_name= mod_change_name.getText().toString();
                                            String SSID=mod_change_SSID.getText().toString();
                                            String password=mod_change_passwd.getText().toString();
/*
                                            String getURL=(SERVER_ADDRESS + "/vr/moduleinfochange.php?" + "id=" + URLEncoder.encode(global_id, "UTF-8")+ "&name=" + URLEncoder.encode(name, "UTF-8")+ "&new_name=" + URLEncoder.encode(new_name, "UTF-8")+ "&SSID=" + URLEncoder.encode(SSID, "UTF-8")+ "&password=" + URLEncoder.encode(password, "UTF-8"));
                                            HttpGet get = new HttpGet(getURL);
                                            HttpResponse responseGet = client.execute(get);
                                            HttpEntity resEntityGet = responseGet.getEntity();
                                            result= EntityUtils.toString(resEntityGet);
                                            if (result != null) {

                                                finish();
                                                Toast.makeText(HubRegisterActivity.this,"수정 완료.",Toast.LENGTH_LONG).show();
                                                startActivity(getIntent());

                                            } else {
                                                Toast.makeText(HubRegisterActivity.this, "수정 실패", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(getIntent());
                                            }
*/
                                            Toast.makeText(HubRegisterActivity.this, "페어링 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                                            Intent intent2 = new Intent(HubRegisterActivity.this, HubRegisterActivity2.class);
                                            intent2.putExtra("module",name);
                                            intent2.putExtra("new_module",new_name);
                                            intent2.putExtra("mac_address","00");
                                            intent2.putExtra("SSID",SSID);
                                            intent2.putExtra("password",password);
                                            intent2.putExtra("global_id",global_id);


                                            finish();
                                            startActivity(intent2);

                                        } catch (Exception e) {

                                            Log.e("error", e.getMessage());
                                        }
                                    }}
                                });
                            }});




                        alert_change.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        return;

                                    }
                                });

                        alert_change.show();


                    }
                });//수정리스너

                module_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert_delete = new AlertDialog.Builder(HubRegisterActivity.this);
                        alert_delete.setTitle("이 모듈을 삭제하시겠습니까?");


                        alert_delete.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                            String result="";

                                        try {

                                            String getURL=(SERVER_ADDRESS + "/vr/moduledelete.php?" + "id=" + URLEncoder.encode(global_id, "UTF-8")+ "&name=" + URLEncoder.encode(modules.get(position).toString(), "UTF-8"));
                                            HttpGet get = new HttpGet(getURL);
                                            HttpResponse responseGet = client.execute(get);
                                            HttpEntity resEntityGet = responseGet.getEntity();
                                            result= EntityUtils.toString(resEntityGet);
                                            if (result != null) {
                                                Toast.makeText(HubRegisterActivity.this, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                                                finish();
                                                prev_MainMenu.finish();
                                                Intent intent = new Intent(HubRegisterActivity.this,MainMenu.class);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(HubRegisterActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(getIntent());
                                            }

                                        } catch (Exception e) {

                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                });
                            }});




                        alert_delete.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        return;

                                    }
                                });

                        alert_delete.show();

                    }
                });//삭제리스너






                alert.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                return;

                            }
                        });
            alert.show();
            }
        });



        moduleadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.moduleadd,null);
                AlertDialog.Builder alert = new AlertDialog.Builder(HubRegisterActivity.this);
                alert.setView(dialogView);
                alert.setTitle("모듈을 등록합니다.");

                final EditText edit_name= (EditText)dialogView.findViewById(R.id.add_name_edit);
                final EditText edit_mac= (EditText)dialogView.findViewById(R.id.add_mac_edit);
                final EditText edit_SSID= (EditText)dialogView.findViewById(R.id.add_SSID_edit);
                final EditText edit_password= (EditText)dialogView.findViewById(R.id.add_password_edit);




                alert.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (edit_name.getText().toString().equals("")|| edit_password.getText().toString().equals("")|| edit_mac.getText().toString().equals("")|| edit_SSID.getText().toString().equals("")) {
                            Toast.makeText(HubRegisterActivity.this, "입력 오류입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String module = edit_name.getText().toString();
                                String mac_address = edit_mac.getText().toString();
                                String SSID = edit_SSID.getText().toString();
                                String password = edit_password.getText().toString();
                                try {

                                        Toast.makeText(HubRegisterActivity.this, "페어링 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent2 = new Intent(HubRegisterActivity.this, HubRegisterActivity2.class);
                                        intent2.putExtra("module",module);
                                        intent2.putExtra("new_module","00");
                                        intent2.putExtra("mac_address",mac_address);
                                        intent2.putExtra("SSID",SSID);
                                        intent2.putExtra("password",password);
                                        intent2.putExtra("global_id",global_id);


                                        finish();
                                        startActivity(intent2);


                                } catch (Exception e) {

                            Log.e("error", e.getMessage());
                        }
                    }
                });
                    }});


                alert.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                             return;

                            }
                        });

                alert.show();



            }

        });










}




    public void showmodulelist()
    {   String modulestring = "";
        int modulecount =0;
        try{
        String getURL = (SERVER_ADDRESS+"/vr/modulecount.php?"+ "id=" + URLEncoder.encode(global_id, "UTF-8"));
        HttpGet get = new HttpGet(getURL);
        HttpResponse responseGet = client.execute(get);
        HttpEntity resEntityGet = responseGet.getEntity();
            modulestring = EntityUtils.toString(resEntityGet);

        modulecount  = Integer.parseInt(modulestring);
        if(modulecount!=0){
            modulecount  = Integer.parseInt(modulestring);

            for (int i = 0; i < modulecount; i++) {
                modules.add(getmodulename(global_id, i));

            }

        }
    }catch(Exception e){
        Log.e("error", e.getMessage());
    }

    }



    public String getmodulename(String s, int count)
    {
        String get_name=null;
        String modulecount = Integer.toString(count);
        try
        {

            String getURL = (SERVER_ADDRESS+"/vr/modulename.php?"+ "id=" + URLEncoder.encode(s, "UTF-8")+"&modulecount=" + URLEncoder.encode(modulecount, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            get_name = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }
        } catch (Exception e) { e.printStackTrace(); }
        return get_name;
    }




}