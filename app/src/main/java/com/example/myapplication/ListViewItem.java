package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ListViewItem {

    private String name;
    private String dial;
//    private String profile;

//    public void setProfile(String profile_image) {
//        this.profile = profile_image;
//    }

//    public String getProfile() {
//        return this.profile;
//    }

    public String getName() {
        return this.name;
    }

    public void setName(String rowtext1) {
        this.name = rowtext1;
    }

    public String getNumber() {
        return this.dial;
    }

    public void setNumber(String rowtext2) {
        this.dial = rowtext2;
    }
}
