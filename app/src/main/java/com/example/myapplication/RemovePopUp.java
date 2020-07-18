package com.example.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RemovePopUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.remove_popup);
    }

    //제거 버튼 클릭
    public void mOnRemove(View v) throws IOException, JSONException {
        //데이터 받아오기
        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        String UserID = intent.getStringExtra("UserID");


        //JSON파일에서 해당라인 지우기
        File file = new File(this.getFilesDir()+UserID,"contact.json");
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr= new InputStreamReader(fis);
        BufferedReader reader= new BufferedReader(isr);

        StringBuffer buffer= new StringBuffer();
        String line= reader.readLine();
        while (line!=null){ // name을 포함하지 않는 라인만 buffer에 넣기.
            if(line.contains(name)) {
                line=reader.readLine();
                continue;
            }
            buffer.append(line+"\n");
            line=reader.readLine();
        }
        fis.close();
        String jsonData= buffer.toString();
        if(jsonData.contains(",\n]")){
            jsonData = jsonData.replace(",\n]","\n]");
        }
        //      정보 삭제한 JSON파일을 다시 파일에 넣어주기

        File fileOut = new File(this.getFilesDir()+UserID,"contact.json");
        FileOutputStream fos= new FileOutputStream(fileOut);//openFileOutput("contact.json",MODE_PRIVATE);
        fos.write(jsonData.getBytes());
        fos.close();

        // DB 연락처 정보 삭제하기
        try {
            HttpURLConnection con = null;
            BufferedReader readerdb = null;

            try{
                URL url = new URL("http://192.249.19.244:2980/api/contacts/delete/facebookID/"+UserID);//url을 가져온다.
                con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("POST");
                con.setRequestMethod("PUT");
                con.setDoOutput(true); //GET일 때 비활성화
                con.setDoInput(true);
                con.setRequestProperty("Content-Type","application/json; charset=UTF-8"); // POST, PUT일때 활성화
                String json = "{\"name\": \""+name+"\"}";
                OutputStream os = con.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.close();

                con.connect();//연결 수행
                    /*if(con.getResponseCode() == 404) {

                    }*/

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

//        return null;

        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    //취소 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}