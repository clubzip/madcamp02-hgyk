package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class tab3Frag extends Fragment {

    View view;
    Button stampWork;
    Button stampLeave;

    String UserID;
    String UserName;
    String today;
    String time;

    private ArrayList<WorkHolder> mWorkHolderList;


    SimpleDateFormat sdftoday = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdftime = new SimpleDateFormat("HHmmss");

    public tab3Frag(String userid, String username) {
        UserID = userid;
        UserName = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab3_main, container, false);
        stampWork = (Button) view.findViewById(R.id.stamp_work);
        stampLeave = (Button) view.findViewById(R.id.stamp_leave);


        stampWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("출근하셨나요?");
                builder.setPositiveButton("네...", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "오늘도 화이팅!!", Toast.LENGTH_SHORT).show();

                        long now = System.currentTimeMillis();

                        Date date = new Date(now);

                        today = sdftoday.format(date);
                        time = sdftime.format(date);

                        WorkThread wThread = new WorkThread("http://192.249.19.244:2980/api/work/"+UserID, UserName,today,time);
                        wThread.start();
                    }
                });

                builder.setNegativeButton("아니요!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "쉬세요~", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        stampLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("퇴근하시나요?");
                builder.setPositiveButton("네!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "고생하셨어요~", Toast.LENGTH_SHORT).show();
                        long now = System.currentTimeMillis();

                        Date date = new Date(now);

                        today = sdftoday.format(date);
                        time = sdftime.format(date);

        mWorkHolderList = new ArrayList<>();
        mWorkHolderList.add(new tab3_todayHolder());
        mWorkHolderList.add(new tab3_rankHolder());

        // show contents
        WorkAdapter workAdapter = new WorkAdapter(mWorkHolderList);
        ListView listView = (ListView) view.findViewById(R.id.listView_work);
        listView.setAdapter(workAdapter);



                        LeaveThread lThread = new LeaveThread("http://192.249.19.244:2980/api/leave/"+UserID, today,time);
                        lThread.start();
                    }
                });

                builder.setNegativeButton("아니요...", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "힘내세요 ㅠ", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;
    }




}
