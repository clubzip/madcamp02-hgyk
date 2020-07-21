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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        UserID = intent.getStringExtra("userid");
        UserName = intent.getStringExtra("username");

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