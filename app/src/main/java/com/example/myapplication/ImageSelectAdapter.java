package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;



import java.io.File;
import java.util.ArrayList;

public class ImageSelectAdapter extends BaseAdapter {
    private Context mContext;
    private int mCellSize;
    private ArrayList<Image> mImageArrayList;
    Bitmap circle;
    Bitmap check;
    SparseBooleanArray checked = new SparseBooleanArray();

    public ImageSelectAdapter(Context c, int cellSize, ArrayList<Image> imageArrayList){
        mContext = c;
        mCellSize = cellSize;
        mImageArrayList = imageArrayList;

        circle = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.uncheck_circle);
        circle = Bitmap.createScaledBitmap(circle, mCellSize/8, mCellSize/8, true);

        check = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.check_circle);
        check = Bitmap.createScaledBitmap(check, mCellSize/8, mCellSize/8, true);
    }

    @Override
    public int getCount() {
        return mImageArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return mImageArrayList.get(i).getScaledImage();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View newView = LayoutInflater.from(mContext).inflate(R.layout.fragment2_delete, null);

        final ImageView imageView = newView.findViewById(R.id.image);
        final ImageView checkView = newView.findViewById(R.id.check);
        final ImageView unCheckView = newView.findViewById(R.id.uncheck);

        imageView.setImageBitmap(mImageArrayList.get(i).getScaledImage());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(mCellSize, mCellSize));

        checkView.setLayoutParams(new ConstraintLayout.LayoutParams(mCellSize/8, mCellSize/8));
        unCheckView.setLayoutParams(new ConstraintLayout.LayoutParams(mCellSize/8, mCellSize/8));

        changeVisibility(i, checkView, unCheckView, false);

        imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(i, checkView, unCheckView, true);
            }
        });

        return newView;
    }

    private void changeVisibility(int i, View checkView, View unCheckView, boolean clicked) {
        if (clicked){
            if (checked.get(i)) {
                checked.delete(i);
                unCheckView.setVisibility(View.VISIBLE);
                checkView.setVisibility(View.INVISIBLE);
            } else {
                checked.put(i, true);
                unCheckView.setVisibility(View.INVISIBLE);
                checkView.setVisibility(View.VISIBLE);
            }
        } else {
            if (checked.get(i)) {
                unCheckView.setVisibility(View.INVISIBLE);
                checkView.setVisibility(View.VISIBLE);
            } else {
                unCheckView.setVisibility(View.VISIBLE);
                checkView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void deleteChecked() {
        ArrayList<Image> removeList = new ArrayList<>();
        for (int i = 0; i < mImageArrayList.size(); i++) {
            if (checked.get(i)) {
                File fdelete = new File(mImageArrayList.get(i).getAbsolutePath());
                fdelete.delete();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(fdelete);
                mediaScanIntent.setData(contentUri);
                mContext.sendBroadcast(mediaScanIntent);

                removeList.add(mImageArrayList.get(i));
            }
        }
        mImageArrayList.removeAll(removeList);
    }

    public void getChecked() {

    }
}