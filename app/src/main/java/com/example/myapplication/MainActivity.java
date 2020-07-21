package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;


    private GalleryFrag galleryFrag;
    private ContactFrag contactFrag;
    private tab3Frag tab3Frag;

    private String UserID;
    private String UserName;
    private String DataFromServer;
    public boolean selectingImage = false;
    public String startTimeID;


    public boolean isSelection() {
        return selectingImage;
    }



//    public class JSONTask extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            try {
//                HttpURLConnection con = null;
//                BufferedReader reader = null;
//
//                try{
//                    URL url = new URL(urls[0]);//url을 가져온다.
//                    con = (HttpURLConnection) url.openConnection();
////                    con.setRequestMethod("POST");
//                    con.setRequestMethod("GET");
////                    con.setDoOutput(true); //GET일 때 비활성화
//                    con.setDoInput(true);
////                    con.setRequestProperty("Content-Type","application/json; charset=UTF-8"); // POST, PUT일때 활성화
////                    String json = "{\"facebookID\": \"test\",\"contactList\": [{\"name\": \"test1\",\"phone_num\": \"11111111\"}]}"; - POST body
////                    String json = "{\"name\": \"test\",\"dial\": \"010-1551-4541\"}"; - PUT add body
//                    /*String json = "{\"name\": \"test\"}";
//                    OutputStream os = con.getOutputStream();
//                    os.write(json.getBytes("UTF-8"));
//                    os.close();*/
//
//                    con.connect();//연결 수행
//                    /*if(con.getResponseCode() == 404) {
//
//                    }*/
//
//                    //입력 스트림 생성
//                    InputStream stream = con.getInputStream();
//
//                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
//                    reader = new BufferedReader(new InputStreamReader(stream));
//
//                    //실제 데이터를 받는곳
//                    StringBuffer buffer = new StringBuffer();
//
//                    //line별 스트링을 받기 위한 temp 변수
//                    String line = "";
//
//                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
//                    while((line = reader.readLine()) != null){
//                        buffer.append(line);
//                    }
//
//                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
//                    return buffer.toString();
//
//                    //아래는 예외처리 부분이다.
//                } catch (MalformedURLException e){
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    //종료가 되면 disconnect메소드를 호출한다.
//                    if(con != null){
//                        con.disconnect();
//                    }
//                    try {
//                        //버퍼를 닫아준다.
//                        if(reader != null){
//                            reader.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }//finally 부분
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            /*json_contact = result;
//            tvData.setText(result);*/
////            Log.i("JSON",result);
//            DataFromServer = result;
//            //캐시에 저장
//            File storage = getCacheDir();
//            File tempFile = new File(storage,"contact.json");
//            try {
//                tempFile.createNewFile();
//                FileOutputStream out = new FileOutputStream(tempFile);
//                out.write(result.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();


        UserID = intent.getStringExtra("userid");

        galleryFrag = new GalleryFrag(UserID);//UserID넣는 걸로 바꾸고 Frag안에서 네트워킹 구현
        contactFrag = new ContactFrag(UserID);
        tab3Frag = new tab3Frag(UserID, UserName);

        //UserID = "TESTID76";


        //setContent view를 login으로

        //splash 부분
                                /*Intent intent0 = new Intent(this, SplashActivity.class);
                                startActivity(intent0);*/

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tap_layout);

        tabLayout.setupWithViewPager(viewPager);


        viewPagerAdapter = new MainActivity.ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewPagerAdapter.addFragment(galleryFrag, "Gallery");
        viewPagerAdapter.addFragment(contactFrag, "Contact");
        viewPagerAdapter.addFragment(tab3Frag, "tab3");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_collections_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_contact_phone_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_calendar_today_24);



        /*//contact.json파일이 존재하지 않으면
        File contact = new File(getFilesDir(), "contact.json");
        //        contact.delete();
        if(!contact.exists()){
            try {
                contact.createNewFile();
                FileOutputStream fos = openFileOutput("contact.json",MODE_APPEND);
                fos.write("[\n]".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}