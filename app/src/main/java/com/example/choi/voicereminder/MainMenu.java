package com.example.choi.voicereminder;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;



public class MainMenu extends AppCompatActivity {


    private Recorder recorder;
    int serverResponseCode = 0;
    public String value = null;
    public String nick = null;
    String upLoadServerUri = "/vr/UploadToServer.php";
    public String uploadFilePath = "/sdcard/vr/";
    public static Activity myActivity;
    private static final String SERVER_ADDRESS = "";
    Chronometer chron;
    String global_id;
    String global_name;
    String global_id_code;
    TextView messageText;
    String musictext ="";
    int i =0;
    int modulecount = 0;
    Boolean playcheck=false;
    Boolean isplaying=false;
    private List<String> modules = new ArrayList<>();
    HttpClient client = new DefaultHttpClient();


    private ImageButton pas,playlist,recordButton;


    public static String[] PermissionsSet = {
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        permissonRequest();
        TextView show_my_id = (TextView)findViewById(R.id.ShowID);
        final TextView show_my_music = (TextView)findViewById(R.id.play_music_text);
        chron = (Chronometer) findViewById(R.id.chronometer1);
        final MediaPlayer mp = new MediaPlayer();
        String my_id = getPreferences();
        pas = (ImageButton)findViewById(R.id.img_play_check);
        playlist = (ImageButton)findViewById(R.id.img_play_list);
        recordButton = (ImageButton)findViewById(R.id.img_record_check);
        global_id = my_id;
        showmodulelist();
        String get_name = httpget(my_id,SERVER_ADDRESS+"/vr/getinfo.php?");
        global_name = get_name;
        show_my_id.setText(" "+String.valueOf(get_name));
        show_my_music.setText(getmusicfilenickname());
        musictext=show_my_music.getText().toString();
        set_id_code();
        chron.setText("00:00");
        myActivity=MainMenu.this;
        setupRecorder();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.reset();

                pas.setSelected(false);
                playcheck=false;
                isplaying=false;
            }

        });



        pas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.isPressed()) {
                    if (playcheck == true) {
                        if (isplaying == true)
                        {mp.pause();
                         }

                        v.setSelected(false);
                        playcheck = false;
                    } else {
                        v.setSelected(true);
                        playcheck = true;
                        final String fullname = getmusicfilename(show_my_music.getText().toString());
                        String url = "http://168.131.151.95:80/vr/uploads/" + fullname + ".wav";
                        try {
                            if (isplaying == true) {
                                mp.start();
                            } else {
                                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mp.setDataSource(url);
                                mp.prepare();
                                mp.start();
                                isplaying = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


            } });


        playlist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(MainMenu.this, R.style.PopupMenu);
                final PopupMenu menu = new PopupMenu(wrapper, v);

                int musiccount = Integer.parseInt(musicfilecount());
                String myfilelist="";

                    for(int i=0; i< musiccount;i++) {
                        myfilelist="";
                        myfilelist+=getmusicfilelist(i);
                        menu.getMenu().add(myfilelist);
                    }

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        show_my_music.setText(item.getTitle());
                        return true;
                    }
                });


                menu.show();
            }
        });
    }




    public void onclick_record(View v){
        if(v.isPressed())
        {
            i++;
            if(i%2!=0)
            {SetFileName();
                v.setSelected(true);

            }
            else
            {v.setSelected(false);

            onclick_stop(v);

            }

        }


    }







    public void onclick_stop(View v) {
        try {
            recorder.stopRecording();
            chron.stop();

            Toast.makeText(MainMenu.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

            moveFile(uploadFilePath,"recordingTemp.wav",uploadFilePath,value+".wav");


            ContentValues values = new ContentValues(10);

            values.put(MediaStore.MediaColumns.TITLE, "recorded");
            values.put(MediaStore.Audio.Media.ALBUM, "Audio Album");
            values.put(MediaStore.Audio.Media.ARTIST, "Mike");
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Audio");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
            values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
            values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
            values.put(MediaStore.Audio.Media.DATA, uploadFilePath+""+value+".wav");
            Uri audioUri = getContentResolver().insert(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

            int up = uploadFile(uploadFilePath+value+".wav");
            if(up==200)
            UploadVoicedata(value, nick);

            if (audioUri == null) {
                Log.d("SampleAudioRecorder", "Audio insert failed.");
                return;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public void SetFileName() {

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.recordfile,null);
        messageText=(TextView)findViewById(R.id.messageText1);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(dialogView);


        final EditText edit_file= (EditText)dialogView.findViewById(R.id.file_edit);
        edit_file.setTextColor(Color.DKGRAY);



        alert.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                value = edit_file.getText().toString();
                nick = edit_file.getText().toString();
                value.toString();
                nick.toString();

                String temp_value = global_id_code+"_"+value;
                value = temp_value;

                 try{

                     setupRecorder();
                     recorder.startRecording();
                     messageText.setText("녹음 중...");
                     chron.setBase(SystemClock.elapsedRealtime());
                     chron.start();

                    Toast.makeText(MainMenu.this, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show();


                } catch (Exception ex) {
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }
        });


        alert.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        i--;
                        recordButton.setSelected(false);
                        return;

                        // Canceled.
                    }
                });

        alert.show();
    }



    public int uploadFile(String sourceFileUri) {
   final String fileName = sourceFileUri;
    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    File sourceFile = new File(fileName);
    if (!sourceFile.isFile()) {

        Log.e("uploadFile", "Source File not exist :"
                +uploadFilePath + value +".wav");

        runOnUiThread(new Runnable() {
            public void run() {
                messageText.setText("Source File not exist :"
                        +uploadFilePath + value +".wav");
            }
        });

        return 0;

    }
    else
    {
        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200){

                runOnUiThread(new Runnable() {
                    public void run() {

                        String msg = "파일 업로드 성공, "+nick;

                        messageText.setText(msg);

                    }
                });
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {

            ex.printStackTrace();

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("MalformedURLException Exception : check script url.");
                    Toast.makeText(MainMenu.this, "MalformedURLException",
                            Toast.LENGTH_SHORT).show();
                }
            });

            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Got Exception : see logcat ");
                    Toast.makeText(MainMenu.this, "Got Exception : see logcat ",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("Upload file to server Exception", "Exception : "
                    + e.getMessage(), e);
        }
        return serverResponseCode;

    } // End else block

    }



    public void onclick2(View v) {
        Intent intent = new Intent(this, VoiceMenu1.class);
        intent.putExtra("USERID",global_id);
        intent.putExtra("USERNAME",global_name);
        intent.putStringArrayListExtra("module",(ArrayList<String>)modules);
        intent.putExtra("IDCODE",global_id_code);

        startActivity(intent);

    }

    public void onclick3(View v) {
        Intent intent = new Intent(this, HubRegisterActivity.class);
        intent.putExtra("USERID",global_id);
        startActivity(intent);
    }

    public void onclick_logout(View v)
    {
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public String getPreferences()
    {
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);


        String s = pref.getString("ID","");
        return s;


    }






    public void UploadVoicedata(String name, String name2)
    {
        String id = getPreferences();
        String file_name = name;
        String nickname = name2;
        String result="";
        try {

            String getURL = (SERVER_ADDRESS+"/vr/voiceupload.php?" + "id=" + URLEncoder.encode(id, "UTF-8")+ "&file_name=" + URLEncoder.encode(file_name, "UTF-8")+ "&nickname=" + URLEncoder.encode(nickname, "UTF-8"));

            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            result = EntityUtils.toString(resEntityGet);
            Log.v("uploaddata",result);
            if (result != null) {
                Toast.makeText(MainMenu.this, "녹음파일이 데이터베이스에 저장되었습니다.", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(MainMenu.this, "녹음파일 데이터베이스 저장 실패", Toast.LENGTH_SHORT).show();
            }


            File f = new File(uploadFilePath+value+".wav");
            f.delete();
            finish();
            startActivity(new Intent(MainMenu.this, MainMenu.class));
        } catch (Exception e) {

            Log.e("error", e.getMessage());
        }

    }

    public String httpget(String s,String address)
    {
        String get_id=null;
        try
        {
            HttpClient client = new DefaultHttpClient();
            String getURL = address + "id=" + URLEncoder.encode(s, "UTF-8");
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            get_id = EntityUtils.toString(resEntityGet);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }
        } catch (Exception e) { e.printStackTrace(); }
        return get_id;
    }





    public void showmodulelist()
    {
        final TextView show_my_module = (TextView)findViewById(R.id.module_present2);
        String modulestring = httpget(global_id,SERVER_ADDRESS+"/vr/modulecount.php?");
        String moduleshow  = "";
        modulecount  = Integer.parseInt(modulestring);
        if(modulecount!=0){
            modulecount  = Integer.parseInt(modulestring);

            for (int i = 0; i < modulecount; i++) {
                modules.add(getmodulename(global_id, i));

            }


            for(int i = 0; i< modulecount; i++){
                moduleshow+=" "+modules.get(i)+" ";
            }


            show_my_module.setText(moduleshow);
        }
        else
        {show_my_module.setText("모듈을 등록하세요");}
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

    public void set_id_code()
    {

        String get_id_code = httpget(global_id,SERVER_ADDRESS+"/vr/getidcode.php?");
        int int_code = Integer.parseInt(get_id_code);

        if(int_code<10)
            get_id_code = "0"+get_id_code;

       global_id_code = get_id_code;



    }

    public String getmusicfilename(String s)
    {

        String get_name=null;
        try
        {

            String getURL = (SERVER_ADDRESS+"/vr/getfullname.php?"+ "id=" + URLEncoder.encode(global_id, "UTF-8")+"&nickname=" + URLEncoder.encode(s, "UTF-8"));
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


    public String getmusicfilenickname()
    {

        String get_name=null;
        try
        {

            String getURL = (SERVER_ADDRESS+"/vr/getnickname.php?"+ "id=" + URLEncoder.encode(global_id, "UTF-8"));
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


    public String getmusicfilelist(int a)
    {
        String count = String.valueOf(a);
        String get_name=null;
        try
        {

            String getURL = (SERVER_ADDRESS+"/vr/getmusicfilelist.php?"+ "id=" + URLEncoder.encode(global_id, "UTF-8")+ "&count="+URLEncoder.encode(count, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            get_name = EntityUtils.toString(resEntityGet);
            Log.v("getmusicfilelist.php",get_name);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }
        } catch (Exception e) { e.printStackTrace(); }
        return get_name;



    }


    public String musicfilecount()
    {

        String get_name=null;
        try
        {

            String getURL = (SERVER_ADDRESS+"/vr/musicfilecount.php?"+ "id=" + URLEncoder.encode(global_id, "UTF-8"));
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            get_name = EntityUtils.toString(resEntityGet);
            Log.v("mfc",get_name);
            if (resEntityGet != null) {
                // 결과를 처리합니다.
                Log.i("RESPONSE", EntityUtils.toString(resEntityGet));

            }
        } catch (Exception e) { e.printStackTrace(); }
        return get_name;



    }

    private void moveFile(String inputPath, String inputFile, String outputPath, String outputFile) {
        InputStream in = null;
        OutputStream out = null;
        try {

            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();

            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


            UpdateGallery(outputPath + outputFile);
            Log.v("UPDATEGALLERY", outputPath + outputFile);
        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }


    private void permissonRequest() {
        if (!permissionGranted(this, PermissionsSet)) {
            ActivityCompat.requestPermissions(this, PermissionsSet, 1);
        }
    }

    public static boolean permissionGranted(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }
    private void animateVoice(final float maxPeak) {
        //recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 8000
                )
        );
    }

    @NonNull
    private File file() {
        return new File(uploadFilePath, "recordingTemp.wav");
    }


    public void UpdateGallery(String file) {
        File f = new File(file);

        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        sendBroadcast(mediaScanIntent);
    }




    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
        }

        public void transferred(int length) {
        }

        public void completed() {
        }

        public void aborted() {
        }

        public void failed() {
            System.out.println(" failed ...");
        }

    }



}