package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ContactAdd extends AppCompatActivity {

    String UserID;
    String nameStr;
    String dialStr;

    public class JSONTaskADD extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("PUT");
                    con.setDoOutput(true); //GET일 때 비활성화
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type","application/json; charset=UTF-8"); // POST, PUT일때 활성화
                    String json ="{\"name\": \""+nameStr+"\",\"dial\": \""+dialStr+"\"}";
//                        String json = "{\"name\": \"test\"}";
                    OutputStream os = con.getOutputStream();
                    os.write(json.getBytes("UTF-8"));
                    os.close();

                    con.connect();//연결 수행
                    /*if(con.getResponseCode() == 404) {

                    }*/

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_add);

        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");

        Button addEnsureButton = (Button)findViewById(R.id.add_ensure);
        Button addCancelButton = (Button)findViewById(R.id.add_cancel);

        EditText number = (EditText) findViewById(R.id.tv_pn);
        number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        addEnsureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //EditText에 입력된 내용을 기반으로 json파일에 추가
                try {
                    EditText name = (EditText) findViewById(R.id.tv_name);
                    EditText number = (EditText) findViewById(R.id.tv_pn);

                    nameStr = name.getText().toString();
                    dialStr = number.getText().toString();

                    //아무것도 입력 안했으면 추가 안됨.
                    if (name.length() == 0 || number.length() == 0) {
                        finish();
                        return;
                    }

                    File file = new File(getFilesDir(),"contactList.json");
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr= new InputStreamReader(fis);
                    BufferedReader reader= new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        if (line.contains(UserID)){
                            buffer.append(line + "\n");
                            for (int i = 0; i < 2; i++){
                                line = reader.readLine();
                                buffer.append(line + "\n");
                            }
                            buffer.append("{\"name\":\"" + nameStr + "\",\"dial\":\"" + dialStr + "\"},\n");
                            line = reader.readLine();
                        }
                        buffer.append(line + "\n");
                        line = reader.readLine();
                    }
                    String jsonData = buffer.toString();

                    fis.close();

                    // json파일에 담기

                    File fileOut = new File(getFilesDir(),"contactList.json");
                    FileOutputStream fos = new FileOutputStream(fileOut);
                    fos.write(jsonData.getBytes());
                    fos.close();

                    setResult(Activity.RESULT_OK);
                    new ContactAdd.JSONTaskADD().execute("http://192.249.19.244:2980/api/contacts/add/facebookID/" + UserID);

                    Intent main = new Intent(view.getContext(),MainActivity.class);
                    startActivity(main);
                    finish();

                    //DB에 정보 {name: "ex", dial: "ex"} 추가

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        addCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}