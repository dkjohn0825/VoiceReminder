package com.example.choi.voicereminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Calendar;
import java.util.List;


public class VoiceMenu1 extends AppCompatActivity {
    public static Activity myFileActivity;
    private ListView lvFileControl,lvFileCheck;
    List <FileList> list = new ArrayList<FileList>();
    FileControlAdapter adapter;
    private int filecount = 0;
    private String global_id = "";
    private String global_name = "";
    private String global_id_code = "";
    public List<String> modules = new ArrayList<>();
    private static final String SERVER_ADDRESS = "";
    String currentTime="";
    HttpClient client = new DefaultHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_menu1);

        lvFileControl = (ListView) findViewById(R.id.lvFileControl);
        TextView mPath = (TextView) findViewById(R.id.tvPath);
        TextView mPath2 = (TextView) findViewById(R.id.FilecountText);
        Intent intent = getIntent();
        global_id = intent.getStringExtra("USERID");
        global_name = intent.getStringExtra("USERNAME");
        modules = intent.getStringArrayListExtra("module");
        global_id_code = intent.getStringExtra("IDCODE");
        currentTime=getcurrenttime();
        fileUpdate(global_id,currentTime);

        adapter = new FileControlAdapter(VoiceMenu1.this, R.layout.filelist_container,list);
        lvFileControl.setAdapter(adapter);


        getFiles();

        mPath.setText("파일 목록");
        mPath2.setText(String.valueOf(filecount)+"개의 파일이 있습니다");
        myFileActivity=VoiceMenu1.this;







/*
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lItem){
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

        ArrayAdapter<String> fileList2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lItemstate){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                tv.setTextColor(Color.parseColor("#a8a8a8"));

                // Return the view
                return view;
            }
        };


        lvFileControl.setAdapter(fileList);
        lvFileCheck.setAdapter(fileList2);
*/


        lvFileControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {



                Intent intent = new Intent(VoiceMenu1.this, OptionActivity.class);
                intent.putExtra("USERID",global_id);
                intent.putExtra("filename",adapter.getname(position).toString());
                intent.putExtra("filestate",adapter.getsubname(position).toString());
                intent.putExtra("USERNAME",global_name);
                intent.putStringArrayListExtra("module",(ArrayList<String>)modules);
                intent.putExtra("IDCODE",global_id_code);
                startActivity(intent);


            }
        });



    }



        private void getFiles() {
                filecount = Integer.parseInt(httpget(global_id, SERVER_ADDRESS + "/vr/filecount.php?"));

                for (int i = 0; i < filecount; i++) {
                    String filename = "";
                    filename = getfilename(global_id, i);
                    String filestate = "";
                    filestate = getfilestate(global_id, i);
                    list.add(new FileList(filename,filestate));

                }

            adapter.notifyDataSetChanged();

        }


            public String httpget(String s, String address) {
                String get_id = null;
                try {
                    String getURL = address + "id=" + URLEncoder.encode(s, "UTF-8");
                    HttpGet get = new HttpGet(getURL);
                    HttpResponse responseGet = client.execute(get);
                    HttpEntity resEntityGet = responseGet.getEntity();
                    get_id = EntityUtils.toString(resEntityGet);
                    if (resEntityGet != null) {
                        // 결과를 처리합니다.
                        Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return get_id;
            }

            public String getfilename(String s, int count) {
                String get_name = null;
                String filecount = Integer.toString(count);
                try {
                    String getURL = (SERVER_ADDRESS + "/vr/filename.php?" + "id=" + URLEncoder.encode(s, "UTF-8") + "&filecount=" + URLEncoder.encode(filecount, "UTF-8"));
                    HttpGet get = new HttpGet(getURL);
                    HttpResponse responseGet = client.execute(get);
                    HttpEntity resEntityGet = responseGet.getEntity();
                    get_name = EntityUtils.toString(resEntityGet);
                    if (resEntityGet != null) {
                        // 결과를 처리합니다.
                        Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return get_name;
            }



    public String getfilestate(String s, int count) {
        String get_state = null;
        String filecount = Integer.toString(count);
        try {
            String getURL = (SERVER_ADDRESS + "/vr/filestate.php?" + "id=" + URLEncoder.encode(s, "UTF-8") + "&filecount=" + URLEncoder.encode(filecount, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            get_state = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return get_state;
    }





    public void fileUpdate(String s,String s2)
    {       String result="";
        try
        {
            String getURL = (SERVER_ADDRESS + "/vr/duedate.php?" + "id=" + URLEncoder.encode(s, "UTF-8") + "&date=" + URLEncoder.encode(s2, "UTF-8"));

            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            result = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }
            Toast.makeText(VoiceMenu1.this,result,Toast.LENGTH_LONG).show();
        } catch (Exception e) { e.printStackTrace(); }


    }

    public String getcurrenttime()
    {
        final Calendar cal = Calendar.getInstance();
        int aYear = cal.get(Calendar.YEAR);
        int aMonth = cal.get(Calendar.MONTH);
        int aDay = cal.get(Calendar.DAY_OF_MONTH);
        int aTime = cal.get(Calendar.HOUR_OF_DAY);
        int aMin = cal.get(Calendar.MINUTE);


        String m_hour="";
        String m_minute="";
        String the_month="";
        String the_day="";
        if(aDay<10)
            the_day+="0";
        if(aMonth+1<10)
            the_month+="0";

        if(aTime<10)
            m_hour+="0";
        if(aMin<10)
            m_minute+="0";

        String final_time ="";
        final_time+=String.valueOf(aYear-2000)+the_month+String.valueOf(aMonth+1)+the_day+String.valueOf(aDay)+m_hour+String.valueOf(aTime)+m_minute+(aMin);


        return final_time;

    }




        }