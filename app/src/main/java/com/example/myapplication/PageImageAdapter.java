package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.util.ArrayList;

public class PageImageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Image> mImageArrayList;

    PageImageAdapter(Context context, ArrayList<Image> imageArrayList){
        mContext=context;
        mImageArrayList = imageArrayList;
    }

    @Override
    public int getCount() {
        return mImageArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ImageView) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        int padding = (int) mContext.getResources().getDimension(R.dimen.activity_vertical_margin);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap (mImageArrayList.get(position).getScaledImage());
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
