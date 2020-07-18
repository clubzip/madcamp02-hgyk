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
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.PhoneNumberFormattingTextWatcher;
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

//    String profile_name = "default_image";

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
//        super.onActivityResult(requestCode, resultCode, resultIntent);
//        if (requestCode == 10002 && resultCode == Activity.RESULT_OK) {
//            profile_name=resultIntent.getStringExtra("profile"); // SelectProfile에서 프로필 파일명 받아오기
//            // 해당 프로필 파일로 프로필 이미지 설정
//            ImageView profile = (ImageView) findViewById(R.id.profile);
//
//            String resName = "@drawable/"+profile_name;
//            String packName = this.getPackageName(); // 패키지명
//            int resID = getResources().getIdentifier(resName, "drawable", packName);
//            profile.setImageResource(resID);
//        }
//        // Check which request we're responding to
//        if (requestCode == 1) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                try {
//                    // 선택한 이미지에서 비트맵 생성
//                    ImageView profile = (ImageView) findViewById(R.id.profile);
//                    InputStream in = getContentResolver().openInputStream(Uri.parse(resultIntent.getData().toString()));
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    in.close();
//                    // 이미지 표시
//                    profile.setImageBitmap(img);
//
//                    //profile_name을 파일명으로 저장 & 내부저장소에 저장
//                    profile_name = getFileName(resultIntent.getData());
//                    File profile_file = new File(getFilesDir(), profile_name);
//                    profile_file.createNewFile();
//                    FileOutputStream fos = openFileOutput(profile_name,MODE_APPEND);
//                    //Bitmap을 byte배열로 변환 후 저장.
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
//                    fos.write(baos.toByteArray());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    public String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }
//    public void onClick_profile(View v){
//        /*Intent intent = new Intent(v.getContext(),SelectProfile.class);
//        startActivityForResult(intent,10002);*/
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 1);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_add);

        Intent intent = getIntent();
        final String UserID = intent.getStringExtra("UserID");

//        ImageView profile = (ImageView) findViewById(R.id.profile);
//        profile.setImageResource(R.drawable.default_image);

        Button addEnsureButton = (Button)findViewById(R.id.add_ensure);
        Button addCancelButton = (Button)findViewById(R.id.add_cancel);

        EditText number = (EditText) findViewById(R.id.tv_pn);
        number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        addEnsureButton.setOnClickListener(new View.OnClickListener() {
            String nameStr;
            String dialStr;
            @Override
            public void onClick(View view) {
                //EditText에 입력된 내용을 기반으로 json파일에 추가
                try {
                    EditText name = (EditText) findViewById(R.id.tv_name);
                    EditText number = (EditText) findViewById(R.id.tv_pn);

                    nameStr = name.getText().toString();
                    dialStr = number.getText().toString();

                    //아무것도 입력 안했으면 추가 안됨.
                    if(name.length() == 0 || number.length() == 0) {
                        finish();
                        return;
                    }

                    // JSON파일에 추가
                    File file = new File(getFilesDir()+UserID,"contact.json");
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr= new InputStreamReader(fis);
                    BufferedReader reader= new BufferedReader(isr);

                    StringBuffer buffer= new StringBuffer();
                    String line= reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line=reader.readLine();
                    }
                    String jsonData= buffer.toString();
                    JSONArray jsonArray= new JSONArray(jsonData);
                    JSONObject jo = new JSONObject();
                    jo.put("name",name.getText());
                    jo.put("dial",number.getText());
//                    jo.put("profile",profile_name); // 이미지뷰 profile의 파일명으로 바꾸기.
                    jsonArray.put(jo);

                    jsonData = "[\n";
                    for(int i=0; i<jsonArray.length()-1; i++) {
                        JSONObject order = jsonArray.getJSONObject(i);
                        jsonData += "{\"name\":\"" + order.getString("name") + "\",\"mobile\":\"" + order.getString("mobile") + "\",\"profile\":\"" + order.getString("profile") + "\"},\n";
                    }
                    JSONObject jsonlast = jsonArray.getJSONObject(jsonArray.length()-1);
                    jsonData += "{\"name\":\"" + jsonlast.getString("name") + "\",\"mobile\":\"" + jsonlast.getString("mobile") + "\",\"profile\":\"" + jsonlast.getString("profile") + "\"}\n]";

                    //이 jsonData의 마지막 문자인 ]앞에 새로운 라인을 추가하고


                    fis.close();

                    // json파일에 담기
                    File fileOut = new File(getFilesDir()+UserID,"contact.json");
                    FileOutputStream fos= new FileOutputStream(fileOut);//openFileOutput("contact.json",MODE_PRIVATE);
                    fos.write(jsonData.getBytes());
                    fos.close();

                    setResult(Activity.RESULT_OK);

                    finish();


                    //intent MainActivity
                }catch (IOException e) {e.printStackTrace();} catch (JSONException e) {e.printStackTrace();}
                /*Intent main = new Intent(view.getContext(),MainActivity.class);
                startActivity(main);*/

                /////////////////DB에서 contactList에 {name, dial} 추가하기///////////////
                try {
                    HttpURLConnection con = null;
                    BufferedReader reader = null;

                    try{
                        URL url = new URL("\"http://192.249.19.244:2980/api/contacts/add/facebookID/\"+UserID");//url을 가져온다.
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("PUT");
                        con.setDoOutput(true); //GET일 때 비활성화
                        con.setDoInput(true);
                        String json = "{\"name\": \""+nameStr+"\",\"dial\": \""+dialStr+"\"}"; // PUT add body
                        OutputStream os = con.getOutputStream();
                        os.write(json.getBytes("UTF-8"));
                        os.close();

                        con.connect();//연결 수행
                    /*if(con.getResponseCode() == 404) {

                    }*/
//
//                        //입력 스트림 생성
//                        InputStream stream = con.getInputStream();
//
//                        //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
//                        reader = new BufferedReader(new InputStreamReader(stream));
//
//                        //실제 데이터를 받는곳
//                        StringBuffer buffer = new StringBuffer();
//
//                        //line별 스트링을 받기 위한 temp 변수
//                        String line = "";
//
//                        //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
//                        while((line = reader.readLine()) != null){
//                            buffer.append(line);
//                        }
//
//                        //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
//                        return buffer.toString();

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

                // 메인을 다시 불러오기 보단 메인에 변경사항을 반영시키고 현재 액티비티 종료하는 게 좋을 듯.
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