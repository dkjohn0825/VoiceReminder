package com.example.choi.voicereminder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;
import static com.example.choi.voicereminder.R.layout.repeat;


public class OptionActivity extends AppCompatActivity {

    private ListView lvFileOption,lvFileOption2;
    public String global_id,global_name;
    public String file_name,file_state;
    private String global_id_code = "";
    public List<String> modules = new ArrayList<>();
    HttpClient client = new DefaultHttpClient();
    private static final String SERVER_ADDRESS = "http://168.131.151.95:80";
    ArrayAdapter<String> OptionList,OptionList2;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    String CurrentTime="";
    String ParticularTime="";
    String vr_date,vr_time ="";
    String repeatcode="00000000";
    String modulecode="";
    VoiceMenu1 prev_Voicemenu1 = (VoiceMenu1)VoiceMenu1.myFileActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        Intent intent = getIntent();
        global_id = intent.getStringExtra("USERID");
        global_name = intent.getStringExtra("USERNAME");
        modules = intent.getStringArrayListExtra("module");
        global_id_code = intent.getStringExtra("IDCODE");
        file_name=intent.getStringExtra("filename");
        file_state=intent.getStringExtra("filestate");
        lvFileOption = (ListView) findViewById(R.id.lvFileOption);
        lvFileOption2 = (ListView) findViewById(R.id.lvFileOption2);

        final TextView tv_filename = (TextView)findViewById(R.id.filename);
        final TextView tv_filestate = (TextView)findViewById(R.id.file_state_view);
        final TextView tv_filedelete = (TextView)findViewById(R.id.filedelete);
        final TextView tv_filechange = (TextView)findViewById(R.id.changename);
        final TextView tv_time_from = (TextView)findViewById(R.id.time_from);
        final TextView tv_time_to = (TextView)findViewById(R.id.time_to);
        final CheckBox allday = (CheckBox)findViewById(R.id.alldaylong);
        final CheckBox justalarm = (CheckBox)findViewById(R.id.just_alarm);
        final TextView Option_save = (TextView)findViewById(R.id.option_save);
        CurrentTime=getcurrenttime();
        getparticulartime(CurrentTime);
        tv_filename.setText(file_name+".wav");
        tv_filestate.setText(file_state);


            tv_time_from.setText("20" + CurrentTime.substring(0, 2) + "." + CurrentTime.substring(2, 4) + "." + CurrentTime.substring(4, 6) + "(" + getcurrentday() + ")" + "\n" + CurrentTime.substring(6, 8) + ":" + CurrentTime.substring(8, 10));
            tv_time_to.setText("20" + ParticularTime.substring(0, 2) + "." + ParticularTime.substring(2, 4) + "." + ParticularTime.substring(4, 6) + "(" + getparticularday(2000+Integer.parseInt(ParticularTime.substring(0,2)),Integer.parseInt(ParticularTime.substring(2,4)),Integer.parseInt(ParticularTime.substring(4,6))) + ")" + "\n" + ParticularTime.substring(6,8) + ":" + CurrentTime.substring(8, 10));


        final String[] Item = new String[]{"모듈","반복"};
        final String[] Item2 = new String[]{"모듈 선택","반복 없음"};
        final List<String>  OptionItem = new ArrayList<String>(Arrays.asList(Item));
        final List<String>  OptionItem2 = new ArrayList<String>(Arrays.asList(Item2));





        OptionList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OptionItem){
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
        lvFileOption.setAdapter(OptionList);




        OptionList2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OptionItem2){
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
        lvFileOption2.setAdapter(OptionList2);




        lvFileOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {



            }


            });


        lvFileOption2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if(position==0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(OptionActivity.this);
                    alert.setTitle("모듈 선택");
                    final boolean[] checkeditems = new boolean[modules.size()];


                    alert.setMultiChoiceItems(modules.toArray(new String[modules.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked == true) {
                                checkeditems[which] = true;
                            } else {
                                checkeditems[which] = false;
                            }
                        }
                    });


                    alert.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                Item2[0]="";

                                for(int i = 0;i<modules.size();i++)
                                {
                                    if(checkeditems[i]==true)
                                    {
                                        Item2[0]+=modules.get(i).toString()+" ";
                                        modulecode+="1";

                                    }else{modulecode+="0";}
                                    onData2Changed(Item2);


                                }
                            } catch (Exception ex) {
                                Log.e("optionactivity", "Exception : ", ex);
                            }
                        }
                    });


                    alert.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    return;// Canceled.
                                }
                            });

                    alert.show();

                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(repeat, null);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(OptionActivity.this);
                    alert.setView(dialogView);
                    alert.setTitle("반복 설정");

                    final TextView repeat0=(TextView)dialogView.findViewById(R.id.repeat0);
                    final TextView repeat1=(TextView)dialogView.findViewById(R.id.repeat1);
                    final TextView repeat2=(TextView)dialogView.findViewById(R.id.repeat2);
                    final TextView repeat3=(TextView)dialogView.findViewById(R.id.repeat3);
                    final TextView repeat4=(TextView)dialogView.findViewById(R.id.repeat4);
                    final TextView repeat5=(TextView)dialogView.findViewById(R.id.repeat5);
                    final TextView tv_text=(TextView)dialogView.findViewById(R.id.repeat_text);

                    repeat0.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            repeat0.setTextColor(Color.parseColor("#3a5fcd"));
                            repeat1.setTextColor(Color.parseColor("#000000"));
                            repeat2.setTextColor(Color.parseColor("#000000"));
                            repeat3.setTextColor(Color.parseColor("#000000"));
                            repeat4.setTextColor(Color.parseColor("#000000"));
                            repeat5.setTextColor(Color.parseColor("#000000"));

                            tv_text.setText("반복 없음");
                            repeatcode="00000000";
                        }
                    });

                    repeat1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            repeat0.setTextColor(Color.parseColor("#000000"));
                            repeat1.setTextColor(Color.parseColor("#3a5fcd"));
                            repeat2.setTextColor(Color.parseColor("#000000"));
                            repeat3.setTextColor(Color.parseColor("#000000"));
                            repeat4.setTextColor(Color.parseColor("#000000"));
                            repeat5.setTextColor(Color.parseColor("#000000"));

                            tv_text.setText("매일 이 시간에만 진행");
                            repeatcode="10000000";

                        }
                    });

                    repeat2.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            repeat0.setTextColor(Color.parseColor("#000000"));
                            repeat1.setTextColor(Color.parseColor("#000000"));
                            repeat2.setTextColor(Color.parseColor("#3a5fcd"));
                            repeat3.setTextColor(Color.parseColor("#000000"));
                            repeat4.setTextColor(Color.parseColor("#000000"));
                            repeat5.setTextColor(Color.parseColor("#000000"));

                            AlertDialog.Builder alert2 = new AlertDialog.Builder(OptionActivity.this);
                            alert2.setTitle("요일 선택");
                            final CharSequence[] days = {"월", "화", "수","목","금","토","일"};
                            final boolean[] daycheck = new boolean[7];


                            alert2.setMultiChoiceItems(days, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if (isChecked == true) {
                                        daycheck[which] = true;
                                    } else {
                                        daycheck[which] = false;
                                    }
                                }
                            });

                            alert2.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        Item2[1]="";
                                        repeatcode="2";

                                        for(int i = 0;i<7;i++)
                                        {
                                            if(daycheck[i]==true)
                                            {
                                                Item2[1]+=days[i].toString()+" ";
                                                repeatcode+="1";
                                            }else{repeatcode+="0";}

                                        }
                                        tv_text.setText("매주 <"+Item2[1].toString() +"> 요일에만 진행");

                                    } catch (Exception ex) {
                                        Log.e("optionactivity", "Exception : ", ex);
                                    }
                                }
                            });


                            alert2.setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            return;// Canceled.
                                        }
                                    });

                            alert2.show();

                        }
                    });

                    repeat3.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            repeat0.setTextColor(Color.parseColor("#000000"));
                            repeat1.setTextColor(Color.parseColor("#000000"));
                            repeat2.setTextColor(Color.parseColor("#000000"));
                            repeat3.setTextColor(Color.parseColor("#3a5fcd"));
                            repeat4.setTextColor(Color.parseColor("#000000"));
                            repeat5.setTextColor(Color.parseColor("#000000"));



                            tv_text.setText("매주 설정한 시간대에만 진행");

                            if(tv_time_to.getText().toString().equalsIgnoreCase("이 날 하루종일")||tv_time_to.getText().toString().equalsIgnoreCase("이 시간 한번")){
                                int startday=getparticularday_int(Integer.parseInt("20"+tv_time_from.getText().toString().substring(2,4)),Integer.parseInt(tv_time_from.getText().toString().substring(5,7)),Integer.parseInt(tv_time_from.getText().toString().substring(8,10)));
                                repeatcode="3";

                                for(int i =1;i<7;i++)
                                {
                                    if(i==startday)
                                        repeatcode+="1";
                                    else
                                        repeatcode+="0";
                                }
                                if(startday==0)//일요일일때
                                    repeatcode+="1";
                            }
                            else{
                            int startday=getparticularday_int(Integer.parseInt("20"+tv_time_from.getText().toString().substring(2,4)),Integer.parseInt(tv_time_from.getText().toString().substring(5,7)),Integer.parseInt(tv_time_from.getText().toString().substring(8,10)));
                            int endday=getparticularday_int(Integer.parseInt("20"+tv_time_to.getText().toString().substring(2,4)),Integer.parseInt(tv_time_to.getText().toString().substring(5,7)),Integer.parseInt(tv_time_to.getText().toString().substring(8,10)));



                            if(startday==endday)
                                repeatcode="30000000";
                            else if(startday<endday){
                                repeatcode="3";
                                for(int i =1;i<7;i++) {
                                    if (i >= startday && i <= endday)
                                    {
                                        repeatcode+="1";
                                    }else{
                                        repeatcode+="0";
                                    }

                                }
                                if(startday==0)
                                    repeatcode+="1";
                                else
                                    repeatcode+="0";

                            }else{
                                repeatcode="3";

                                for(int i=1;i<8;i++)
                                {
                                    if(i<=endday||i>=startday)
                                        repeatcode+="1";
                                    else
                                        repeatcode+="0";
                                }

                            }}
                        }
                    });

                    repeat4.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            repeat0.setTextColor(Color.parseColor("#000000"));
                            repeat1.setTextColor(Color.parseColor("#000000"));
                            repeat2.setTextColor(Color.parseColor("#000000"));
                            repeat3.setTextColor(Color.parseColor("#000000"));
                            repeat4.setTextColor(Color.parseColor("#3a5fcd"));
                            repeat5.setTextColor(Color.parseColor("#000000"));

                            tv_text.setText("매월 설정한 기간동안 진행");
                            repeatcode="40000000";
                        }
                    });
                    repeat5.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            repeat0.setTextColor(Color.parseColor("#000000"));
                            repeat1.setTextColor(Color.parseColor("#000000"));
                            repeat2.setTextColor(Color.parseColor("#000000"));
                            repeat3.setTextColor(Color.parseColor("#000000"));
                            repeat4.setTextColor(Color.parseColor("#000000"));
                            repeat5.setTextColor(Color.parseColor("#3a5fcd"));

                            tv_text.setText("매년 설정한 기간동안 진행");
                            repeatcode="50000000";
                        }
                    });

                    alert.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                Item2[1]=tv_text.getText().toString();
                                onData2Changed(Item2);

                            } catch (Exception ex) {
                                Log.e("optionactivity", "Exception : ", ex);
                            }
                        }
                    });


                    alert.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    return;// Canceled.
                                }
                            });

                    alert.show();



                }




        }});


        allday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!allday.isChecked())
                {
                    tv_time_from.setText("20"+CurrentTime.substring(0,2)+"."+CurrentTime.substring(2,4)+"."+CurrentTime.substring(4,6)+"("+getcurrentday()+")"+"\n"+CurrentTime.substring(6,8)+":"+CurrentTime.substring(8,10));
                    tv_time_to.setText("20"+ParticularTime.substring(0,2)+"."+ParticularTime.substring(2,4)+"."+ParticularTime.substring(4,6)+"("+getcurrentday()+")"+"\n"+ParticularTime.substring(6,8)+":"+ParticularTime.substring(8,10));}
                else
                {   justalarm.setChecked(false);
                    tv_time_from.setText("20"+CurrentTime.substring(0,2)+"."+CurrentTime.substring(2,4)+"."+CurrentTime.substring(4,6)+"("+getcurrentday()+")");
                    tv_time_to.setText("이 날 하루종일");
                }
            }
        });


        justalarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!justalarm.isChecked())
                { tv_time_from.setText("20"+CurrentTime.substring(0,2)+"."+CurrentTime.substring(2,4)+"."+CurrentTime.substring(4,6)+"("+getcurrentday()+")"+"\n"+CurrentTime.substring(6,8)+":"+CurrentTime.substring(8,10));
                    tv_time_to.setText("20"+ParticularTime.substring(0,2)+"."+ParticularTime.substring(2,4)+"."+ParticularTime.substring(4,6)+"("+getcurrentday()+")"+"\n"+ParticularTime.substring(6,8)+":"+CurrentTime.substring(8,10));}
                else
                {   allday.setChecked(false);
                    tv_time_from.setText("20"+CurrentTime.substring(0,2)+"."+CurrentTime.substring(2,4)+"."+CurrentTime.substring(4,6)+"("+getcurrentday()+")"+"\n"+Integer.toString(Integer.parseInt(CurrentTime.substring(6,8))+1)+":"+CurrentTime.substring(8,10));
                    tv_time_to.setText("이 시간 한번");
                }
            }
        });


        tv_filechange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(tv_filestate.getText().toString().substring(1,4).equalsIgnoreCase("미등록")) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.recordfile,null);
                    AlertDialog.Builder alert = new AlertDialog.Builder(OptionActivity.this);
                    alert.setView(dialogView);


                    final EditText edit_file= (EditText)dialogView.findViewById(R.id.file_edit);
                    edit_file.setTextColor(Color.DKGRAY);

                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            final String change_name = edit_file.getText().toString();
                            change_name.toString();

                            String my_result= filenamechange(global_id,file_name,change_name);

                            if (my_result != null) {
                                Toast.makeText(OptionActivity.this,"변경되었습니다", Toast.LENGTH_LONG).show();

                                tv_filename.setText(change_name+".wav");
                                file_name=change_name;
                            } else {
                                Toast.makeText(OptionActivity.this, "파일명 변경 실패. 다시 확인해주세요", Toast.LENGTH_LONG).show();
                            }





                        }
                    });


                    alert.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    return;// Canceled.
                                }
                            });

                    alert.show();
                }else
                {
                    Toast.makeText(OptionActivity.this,"이미 전송된 파일입니다.",Toast.LENGTH_LONG).show();
                }


            }
        });



        tv_filedelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(OptionActivity.this);
                alert.setTitle("파일을 삭제하시겠습니까?");


                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        final String delete_name = file_name;
                        final String my_result = filedelete(global_id,delete_name);


                        if(my_result!=null) {
                            Toast.makeText(OptionActivity.this, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(OptionActivity.this, "파일 삭제 실패. 다시 확인해주세요", Toast.LENGTH_LONG).show();
                        }

                        finish();
                        prev_Voicemenu1.finish();
                        Intent intent = new Intent(OptionActivity.this, VoiceMenu1.class);
                        intent.putExtra("USERID",global_id);
                        intent.putExtra("USERNAME",global_name);
                        intent.putStringArrayListExtra("module",(ArrayList<String>)modules);
                        intent.putExtra("IDCODE",global_id_code);
                        startActivity(intent);

                    }
                });


                alert.setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                return;// Canceled.
                            }
                        });

                alert.show();



            }
        });

        Option_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Item2[0]=="모듈 선택")
                {
                    Toast.makeText(OptionActivity.this,"모듈을 선택해주세요",Toast.LENGTH_LONG).show();
                }
                else{
                    ProgressDialog progress = new ProgressDialog(OptionActivity.this);
                    progress.setMessage("설정 중...");
                    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progress.setIndeterminate(true);

                String result="";
                String time_from_code="";
                String time_to_code="";
                String time_from = tv_time_from.getText().toString();
                String time_to = tv_time_to.getText().toString();
                if(time_to=="이 시간 한번")
                {time_from_code=time_from.substring(2,4)+time_from.substring(5,7)+time_from.substring(8,10)+time_from.substring(14,16)+time_from.substring(17,19);
                time_to_code = time_from_code;
                }
                else if(time_to=="이 날 하루종일")
                {
                    time_from_code=time_from.substring(2,4)+time_from.substring(5,7)+time_from.substring(8,10)+"0000";
                    time_to_code=time_from.substring(2,4)+time_from.substring(5,7)+time_from.substring(8,10)+"2400";

                }else
                {
                    time_from_code=time_from.substring(2,4)+time_from.substring(5,7)+time_from.substring(8,10)+time_from.substring(14,16)+time_from.substring(17,19);
                    time_to_code=time_to.substring(2,4)+time_to.substring(5,7)+time_to.substring(8,10)+time_to.substring(14,16)+time_to.substring(17,19);
                    if(Integer.parseInt(time_from_code)>=Integer.parseInt(time_to_code))
                    {Toast.makeText(OptionActivity.this,"시간 범위가 잘못되었습니다",Toast.LENGTH_SHORT).show();
                        return;}
                }


               try {
                    String getURL = (SERVER_ADDRESS + "/vr/moduleset.php?" + "id=" + URLEncoder.encode(global_id, "UTF-8") + "&nickname=" + URLEncoder.encode(file_name, "UTF-8")+"&time_from=" + URLEncoder.encode(time_from_code, "UTF-8")+"&time_to=" + URLEncoder.encode(time_to_code, "UTF-8")+"&repeat_code=" + URLEncoder.encode(repeatcode, "UTF-8")+"&module_code=" + URLEncoder.encode(modulecode, "UTF-8"));

                    HttpGet get = new HttpGet(getURL);
                    HttpResponse responseGet = client.execute(get);
                    HttpEntity resEntityGet = responseGet.getEntity();
                   result = EntityUtils.toString(resEntityGet);
                        finish();
                       Intent intent = new Intent(OptionActivity.this, VoiceMenu1.class);
                       intent.putExtra("USERID",global_id);
                       intent.putExtra("USERNAME",global_name);
                       intent.putStringArrayListExtra("module",(ArrayList<String>)modules);
                       intent.putExtra("IDCODE",global_id_code);
                        prev_Voicemenu1.finish();
                       Toast.makeText(OptionActivity.this,"파일 전송을 요청하였습니다. 상태를 확인하세요",Toast.LENGTH_LONG).show();
                       startActivity(intent);


               }
               catch (Exception e) { e.printStackTrace(); }




            }}
        });


        tv_time_from.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(tv_time_to.getText().toString()=="이 날 하루종일")
                        {
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current month
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                            // date picker dialog
                            datePickerDialog = new DatePickerDialog(OptionActivity.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            String the_month = "";
                                            String the_day = "";
                                            if (dayOfMonth < 10)
                                                the_day += "0";
                                            if (monthOfYear + 1 < 10)
                                                the_month += "0";
                                            String date = getparticularday(year,monthOfYear,dayOfMonth);


                                            tv_time_from.setText(year+"." + the_month + (monthOfYear + 1) +"."+ (the_day) + dayOfMonth+"("+date+")");

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();


                        }
                        else{
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.my_time, null);
                            AlertDialog.Builder alert = new AlertDialog.Builder(OptionActivity.this);
                            alert.setView(dialogView);
                            alert.setTitle("날짜 선택");

                            final String te=tv_time_from.getText().toString();

                            final EditText edit_date = (EditText) dialogView.findViewById(R.id.date_edit);
                            edit_date.setText(te.substring(2,4)+te.substring(5,7)+te.substring(8,10));
                            final EditText edit_time = (EditText) dialogView.findViewById(R.id.time_edit);
                            edit_time.setText(te.substring(14,16)+te.substring(17,19));

                            edit_date.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    final int DRAWABLE_LEFT = 0;
                                    final int DRAWABLE_TOP = 1;
                                    final int DRAWABLE_RIGHT = 2;
                                    final int DRAWABLE_BOTTOM = 3;

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        if (event.getRawX() >= (edit_date.getRight() - edit_date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                            final Calendar c = Calendar.getInstance();
                                            int mYear = c.get(Calendar.YEAR); // current year
                                            int mMonth = c.get(Calendar.MONTH); // current month
                                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                                            // date picker dialog
                                            datePickerDialog = new DatePickerDialog(OptionActivity.this,
                                                    new DatePickerDialog.OnDateSetListener() {

                                                        @Override
                                                        public void onDateSet(DatePicker view, int year,
                                                                              int monthOfYear, int dayOfMonth) {
                                                            // set day of month , month and year value in the edit text
                                                            String the_month = "";
                                                            String the_day = "";
                                                            if (dayOfMonth < 10)
                                                                the_day += "0";
                                                            if (monthOfYear + 1 < 10)
                                                                the_month += "0";


                                                            edit_date.setText((year - 2000) + the_month + (monthOfYear + 1) + (the_day) + dayOfMonth);

                                                        }
                                                    }, mYear, mMonth, mDay);
                                            datePickerDialog.show();

                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            });


                            edit_time.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    final int DRAWABLE_LEFT = 0;
                                    final int DRAWABLE_TOP = 1;
                                    final int DRAWABLE_RIGHT = 2;
                                    final int DRAWABLE_BOTTOM = 3;

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        if (event.getRawX() >= (edit_date.getRight() - edit_date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                            final Calendar mCurrentTime = Calendar.getInstance();
                                            int mTime = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                                            int mMin = mCurrentTime.get(Calendar.MINUTE);
                                            timePickerDialog = new TimePickerDialog(OptionActivity.this, THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                                    String m_hour = "";
                                                    String m_minute = "";
                                                    if (selectedHour < 10)
                                                        m_hour += "0";
                                                    if (selectedMinute < 10)
                                                        m_minute += "0";
                                                    edit_time.setText(m_hour + selectedHour + m_minute + selectedMinute);
                                                }
                                            }, mTime, mMin, true);

                                            timePickerDialog.show();
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            });


                            alert.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        vr_date = edit_date.getText().toString();
                                        vr_time = edit_time.getText().toString();
                                        getparticulartime(vr_date+vr_time);
                                        if (tv_time_to.getText().toString() == "이 시간 한번")
                                        {
                                            tv_time_from.setText("20"+vr_date.substring(0,2)+"."+vr_date.substring(2,4)+"."+vr_date.substring(4,6)+"("+getparticularday(Integer.parseInt("20"+vr_date.substring(0,2)),Integer.parseInt(vr_date.substring(2,4)),Integer.parseInt(vr_date.substring(4,6)))+")"+"\n"+vr_time.substring(0,2)+":"+vr_time.substring(2,4));
                                        }else {
                                                getparticulartime(vr_date+vr_time);

                                                tv_time_from.setText("20" + vr_date.substring(0, 2) + "." + vr_date.substring(2, 4) + "." + vr_date.substring(4, 6) + "(" + getparticularday(Integer.parseInt("20" + vr_date.substring(0, 2)), Integer.parseInt(vr_date.substring(2, 4)), Integer.parseInt(vr_date.substring(4, 6))) + ")" + "\n" + vr_time.substring(0, 2) + ":" + vr_time.substring(2, 4));
                                                tv_time_to.setText("20" + ParticularTime.substring(0, 2) + "." + ParticularTime.substring(2, 4) + "." + ParticularTime.substring(4, 6) + "(" + getparticularday(Integer.parseInt("20" + ParticularTime.substring(0, 2)), Integer.parseInt(ParticularTime.substring(2, 4)), Integer.parseInt(ParticularTime.substring(4, 6))) + ")" + "\n" + ParticularTime.substring(6,8) + ":" + ParticularTime.substring(8, 10));
                                            }
                                    } catch (Exception ex) {
                                        Log.e("OptionActivity", "Exception : ", ex);
                                    }
                                }
                            });


                            alert.setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            return;// Canceled.
                                        }
                                    });

                            alert.show();


                        }

                    }
                });


        tv_time_to.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(tv_time_to.getText().toString()=="이 날 하루종일")
                    {
                        Toast.makeText(OptionActivity.this,"체크 박스를 풀어주세요",Toast.LENGTH_SHORT).show();
                    }
                    else{

                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.my_time, null);
                            AlertDialog.Builder alert = new AlertDialog.Builder(OptionActivity.this);
                            alert.setView(dialogView);
                            alert.setTitle("날짜 선택");

                            final String te=tv_time_to.getText().toString();

                            final EditText edit_date = (EditText) dialogView.findViewById(R.id.date_edit);
                            edit_date.setText(te.substring(2,4)+te.substring(5,7)+te.substring(8,10));
                            final EditText edit_time = (EditText) dialogView.findViewById(R.id.time_edit);
                            edit_time.setText(te.substring(14,16)+te.substring(17,19));

                            edit_date.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    final int DRAWABLE_LEFT = 0;
                                    final int DRAWABLE_TOP = 1;
                                    final int DRAWABLE_RIGHT = 2;
                                    final int DRAWABLE_BOTTOM = 3;

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        if (event.getRawX() >= (edit_date.getRight() - edit_date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                            final Calendar c = Calendar.getInstance();
                                            int mYear = c.get(Calendar.YEAR); // current year
                                            int mMonth = c.get(Calendar.MONTH); // current month
                                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                                            // date picker dialog
                                            datePickerDialog = new DatePickerDialog(OptionActivity.this,
                                                    new DatePickerDialog.OnDateSetListener() {

                                                        @Override
                                                        public void onDateSet(DatePicker view, int year,
                                                                              int monthOfYear, int dayOfMonth) {
                                                            // set day of month , month and year value in the edit text
                                                            String the_month = "";
                                                            String the_day = "";
                                                            if (dayOfMonth < 10)
                                                                the_day += "0";
                                                            if (monthOfYear + 1 < 10)
                                                                the_month += "0";


                                                            edit_date.setText((year - 2000) + the_month + (monthOfYear + 1) + (the_day) + dayOfMonth);

                                                        }
                                                    }, mYear, mMonth, mDay);
                                            datePickerDialog.show();

                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            });


                            edit_time.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    final int DRAWABLE_LEFT = 0;
                                    final int DRAWABLE_TOP = 1;
                                    final int DRAWABLE_RIGHT = 2;
                                    final int DRAWABLE_BOTTOM = 3;

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        if (event.getRawX() >= (edit_date.getRight() - edit_date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                            final Calendar mCurrentTime = Calendar.getInstance();
                                            int mTime = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                                            int mMin = mCurrentTime.get(Calendar.MINUTE);
                                            timePickerDialog = new TimePickerDialog(OptionActivity.this, THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                                    String m_hour = "";
                                                    String m_minute = "";
                                                    if (selectedHour < 10)
                                                        m_hour += "0";
                                                    if (selectedMinute < 10)
                                                        m_minute += "0";
                                                    edit_time.setText(m_hour + selectedHour + m_minute + selectedMinute);
                                                }
                                            }, mTime, mMin, true);

                                            timePickerDialog.show();
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            });


                            alert.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        vr_date = edit_date.getText().toString();
                                        vr_time = edit_time.getText().toString();
                                        tv_time_to.setText("20"+vr_date.substring(0,2)+"."+vr_date.substring(2,4)+"."+vr_date.substring(4,6)+"("+getparticularday(Integer.parseInt("20"+vr_date.substring(0,2)),Integer.parseInt(vr_date.substring(2,4)),Integer.parseInt(vr_date.substring(4,6)))+")"+"\n"+vr_time.substring(0,2)+":"+vr_time.substring(2,4));

                                    } catch (Exception ex) {
                                        Log.e("OptionActivity", "Exception : ", ex);
                                    }
                                }
                            });


                            alert.setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            return;// Canceled.
                                        }
                                    });

                            alert.show();


                        }

                    }



                });
    }






    public void option_cancel(View v)
    {
        finish();
    }



    public String filedelete(String s,String s2) {
        String delete_res=null;
        try
        {
            String getURL = (SERVER_ADDRESS + "/vr/filedelete.php?" + "id=" + URLEncoder.encode(s, "UTF-8") + "&nickname=" + URLEncoder.encode(s2, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            delete_res = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }

        } catch (Exception e) { e.printStackTrace(); }

        return delete_res;
    }


    public String filenamechange(String s,String s2,String s3) {
        String res=null;
        try
        {
            String getURL = (SERVER_ADDRESS + "/vr/filenamechange.php?" + "id=" + URLEncoder.encode(s, "UTF-8") + "&nickname=" + URLEncoder.encode(s2, "UTF-8")+"&newname=" + URLEncoder.encode(s3, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            res = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }

        } catch (Exception e) { e.printStackTrace(); }

        return res;
    }


    public void onDataChanged(String[] s)
    {
        OptionList.clear();
        OptionList.addAll(s);
        OptionList.notifyDataSetChanged();
    }

    public void onData2Changed(String[] s)
    {
        OptionList2.clear();
        OptionList2.addAll(s);
        OptionList2.notifyDataSetChanged();
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

    public void getparticulartime(String s)
    {

        try
        {
            String getURL = (SERVER_ADDRESS + "/vr/onehourlater.php?" + "time=" + URLEncoder.encode(s, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            ParticularTime = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }

        } catch (Exception e) { e.printStackTrace(); }

    }

    public String getcurrentday()
    {
        Calendar oCalendar = Calendar.getInstance( );  // 현재 날짜/시간 등의 각종 정보 얻기

        // 1     2     3     4     5     6     7
        final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

        return week[oCalendar.get(Calendar.DAY_OF_WEEK) - 1];


    }

    public String getparticularday(int year, int month, int day)
    {
        Calendar pCalendar = Calendar.getInstance();
        pCalendar.set(Calendar.YEAR,year);
        pCalendar.set(Calendar.MONTH,month-1);
        pCalendar.set(Calendar.DATE,day);

        // 1     2     3     4     5     6     7
        final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

        return week[pCalendar.get(Calendar.DAY_OF_WEEK) - 1];


    }

    public int getcurrentday_int()
    {
        Calendar oCalendar = Calendar.getInstance( );  // 현재 날짜/시간 등의 각종 정보 얻기

        // 1     2     3     4     5     6     7
        final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

        return oCalendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public int getparticularday_int(int year, int month, int day)
    {
        Calendar pCalendar = Calendar.getInstance();
        pCalendar.set(Calendar.YEAR,year);
        pCalendar.set(Calendar.MONTH,month-1);
        pCalendar.set(Calendar.DATE,day);

        // 1     2     3     4     5     6     7
        final String[] week = { "일", "월", "화", "수", "목", "금", "토" };

        return pCalendar.get(Calendar.DAY_OF_WEEK) - 1;


    }




}
