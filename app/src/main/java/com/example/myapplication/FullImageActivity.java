package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;


import java.util.ArrayList;

public class FullImageActivity extends Activity {
    ArrayList<Image> mImageArrayList;
    int mHeight, mWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment2_full_image);

        /* Get image index and image array list */
        Intent i = getIntent();
        int position = i.getExtras().getInt("id");
        String imageDirPath = i.getStringExtra("imageDirPath");
        String[] imagePaths = i.getStringArrayExtra("imagePaths");

        mHeight = getResources().getDisplayMetrics().heightPixels;
        mWidth = getResources().getDisplayMetrics().widthPixels;

        /* Make an array list of IMAGEs */
        mImageArrayList = new ArrayList<>();
        if (imagePaths != null) {
            for (String path : imagePaths) {
                mImageArrayList.add(new Image(imageDirPath + "/" + path, mHeight, mWidth));
            }
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PageImageAdapter adapter = new PageImageAdapter(this, mImageArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

}