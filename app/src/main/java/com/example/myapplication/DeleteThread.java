package com.example.myapplication;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

class DeleteThread extends Thread {
    String ServerUrl;
    ArrayList<String> FilenameList;

    DeleteThread(String serverPath, ArrayList<String> filenamelist) {
        ServerUrl = serverPath;
        FilenameList = filenamelist;
    }

    @Override
    public void run() {
        URL imgurl;
        int Read;
        try {
            imgurl = new URL(ServerUrl);
            HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");

            String json = "{\"imageList\": \""+FilenameList.toString()+"\"}";

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            conn.connect();

            InputStream is = conn.getInputStream();



        } catch (MalformedURLException e) {
            Log.e("ERROR1", e.getMessage());
        } catch (IOException e) {
            Log.e("ERROR2", e.getMessage());
            e.printStackTrace();
        }
    }
}
