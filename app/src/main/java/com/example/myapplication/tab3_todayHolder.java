package com.example.myapplication;

import android.content.Context;
import android.view.View;

public class tab3_todayHolder extends WorkHolder {
    private static final int type = 0;
    private Context mContext;
    private View mView;

    public tab3_todayHolder() {
        // add parameters if needed
    }

    void show(View view) {
        mView = view;

    }

    int getType() {return type;}
}
