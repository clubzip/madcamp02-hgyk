package com.example.myapplication;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class LeaveThread extends Thread {
    String ServerUrl;
    String Today;
    String Time;

    LeaveThread(String serverPath,  String today, String time) {
        ServerUrl = serverPath;
        Today = today;
        Time = time;
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
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");


            String json = "{ \"date\": \"" + Today + "\", \"leave\": \"" + Time + "\"}";

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