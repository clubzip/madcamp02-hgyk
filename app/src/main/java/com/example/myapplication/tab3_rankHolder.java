package com.example.myapplication;

import android.content.Context;
import android.view.View;

public class tab3_rankHolder extends WorkHolder {
    private static final int type = 1;
    private Context mContext;
    private View mView;

    public tab3_rankHolder() {
        // add parameters if needed
    }

    void show(View view) {
        mView = view;

    }

    int getType() {return type;}
}
