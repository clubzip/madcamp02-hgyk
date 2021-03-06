package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
    String time_screen;

    ListView listView;
    ListView listViewTime;
    RankViewAdapter rankViewAdapter;
    TimeViewAdapter timeViewAdapter;
    ArrayList<RankViewItem> data = new ArrayList<>();
    ArrayList<TimeViewItem> dataTime = new ArrayList<>();

    long now= System.currentTimeMillis();
    Date date = new Date(now);
    TextView datetoday;
    TextView starttime;
    TextView endtime;
    TextView nobody;
    TextView nobody_leave;
    TextView worktime;
    private tab3Frag mFragment = this;



    SimpleDateFormat sdftoday_screen = new SimpleDateFormat("yyyy년 MM월 dd일");
    SimpleDateFormat sdftime_screen = new SimpleDateFormat("HH시 mm분 ss초");
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
        listView = view.findViewById(R.id.listView_workRank);
        listViewTime = view.findViewById(R.id.listView_workTimeRank);
        rankViewAdapter = new RankViewAdapter();
        timeViewAdapter = new TimeViewAdapter();
        data = new ArrayList<>();
        dataTime = new ArrayList<>();
        stampWork = (Button) view.findViewById(R.id.stamp_work);
        stampLeave = (Button) view.findViewById(R.id.stamp_leave);
        datetoday = (TextView) view.findViewById(R.id.dateOfToday);
        starttime = (TextView) view.findViewById(R.id.Text_workTime);
        endtime = (TextView) view.findViewById(R.id.Text_leaveTime);
        worktime = (TextView) view.findViewById(R.id.Text_Time);
        nobody = (TextView) view.findViewById(R.id.nobody);
        nobody_leave = (TextView) view.findViewById(R.id.nobody_leave);


        datetoday.setText(sdftoday_screen.format(date));

        // GET RANK
        String wait = "failToGetRANK";
        try {
            wait = new JSONTaskRank().execute("http://192.249.19.244:2980/api/work/get/"+sdftoday.format(date)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(wait == "failToGetRANK"){
            Log.d("JSONTaskRank", "failToGetRANK");
        } else if (wait == "NoDATA"){
            nobody.setVisibility(View.VISIBLE);
            // visibility
        } else {
            nobody.setVisibility(View.INVISIBLE);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(wait);
                // sort jsonArray
                List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                for (int i = 0; i < jsonArray.length(); i++){
                    jsonObjectList.add(jsonArray.getJSONObject(i));
                }

                Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                    public int compare(JSONObject a, JSONObject b) {
                        String valA = new String();
                        String valB = new String();

                        try{
                            valA = (String) a.get("start");
                            valB = (String) b.get("start");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        return valA.compareTo(valB);
                    }
                });

                JSONArray sortedJsonArray = new JSONArray();

                for (int jsonI = 0; jsonI < jsonArray.length();jsonI++){
                    sortedJsonArray.put(jsonObjectList.get(jsonI));
                }


                for(int i=0; i<sortedJsonArray.length();i++) { // 안되면 -1 빼고 쉼표 빼기
                    JSONObject jo = sortedJsonArray.getJSONObject(i);
                    String name = jo.getString("name");
                    String dial = jo.getString("start");
                    data.add(rankViewAdapter.addItem(name, dial));
                }

                //리스트뷰에 어뎁터 set
                listView.setAdapter(rankViewAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        // GET TIME RANK
        String waitTime = "failToGetRANK";
        try {
            waitTime = new JSONTaskRank().execute("http://192.249.19.244:2980/api/work/get/"+sdftoday.format(date)).get(); //바꾸기
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(waitTime == "failToGetRANK"){
            Log.d("JSONTaskRank", "failToGetRANK");
        } else if (waitTime == "NoDATA"){
            nobody_leave.setVisibility(View.VISIBLE);
            // visibility
        } else {
            nobody_leave.setVisibility(View.INVISIBLE);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(waitTime);
                // sort jsonArray
                List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
                for (int i = 0; i < jsonArray.length(); i++){
                    if (jsonArray.getJSONObject((i)).has("end")) {
                        jsonObjectList.add(jsonArray.getJSONObject(i));
                    }
                }

                List<JSONObject> jsonObjectList_TIME = new ArrayList<JSONObject>();
                for (int i = 0; i < jsonObjectList.size(); i++){
                    JSONObject obj = new JSONObject();
                    String stamp_start = (String) jsonObjectList.get(i).get("start");
                    String stamp_end = (String) jsonObjectList.get(i).get("end");
                    int start_ = Integer.parseInt(stamp_start.substring(0,2))*3600 + Integer.parseInt(stamp_start.substring(2,4))*60 + Integer.parseInt(stamp_start.substring(4));
                    int end_ = Integer.parseInt(stamp_end.substring(0,2))*3600 + Integer.parseInt(stamp_end.substring(2,4))*60 + Integer.parseInt(stamp_end.substring(4));
                    int total_ = end_ - start_;
                    obj.put("time", total_);
                    obj.put("name", jsonObjectList.get(i).get("name"));
                    jsonObjectList_TIME.add(obj);
                }

                Collections.sort(jsonObjectList_TIME, new Comparator<JSONObject>() {
                    public int compare(JSONObject a, JSONObject b) {
                        int valA = 0;
                        int valB = 0;

                        try{
                            valA = (int) a.get("time");
                            valB = (int) b.get("time");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        if (valA > valB) return -1;
                        else return 1;
                    }
                });

                JSONArray sortedJsonArray_TIME = new JSONArray();

                for (int jsonI = 0; jsonI < jsonObjectList_TIME.size();jsonI++){
                    sortedJsonArray_TIME.put(jsonObjectList_TIME.get(jsonI));
                }


                for(int i=0; i<sortedJsonArray_TIME.length();i++) { // 안되면 -1 빼고 쉼표 빼기
                    JSONObject jo = sortedJsonArray_TIME.getJSONObject(i);
                    String name = jo.getString("name");
                    String time = jo.getString("time");
                    dataTime.add(timeViewAdapter.addItem(name, time));
                }

                //리스트뷰에 어뎁터 set
                listViewTime.setAdapter(timeViewAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



        // GET TODAY'S PERSONAL DATA
        String waitTODAY = "failToGetDATA";
        try {
            today = sdftoday.format(date);
            waitTODAY = new JSONTask_WorkDataFromDB().execute("http://192.249.19.244:2980/api/work/get/"+today+"/"+UserID).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (waitTODAY == "failToGetDATA"){
            Log.d("JSONTask_WorkDataFromDB", "failToGetDATA");
        } else if (waitTODAY == "NoDATA"){
            starttime.setText("아직 출근 전이에요!!");
            endtime.setText("아직 퇴근 전이에요ㅜㅜ");
        } else {
            if(!waitTODAY.contains("\"end\"")){ // 퇴근 전
                try {
                    JSONArray jsonArray = new JSONArray(waitTODAY);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String getStartTime = (String) jsonObject.get("start");
                    starttime.setText(getStartTime.substring(0,2)+"시 "+getStartTime.substring(2,4)+"분 "+getStartTime.substring(4)+"초");
                    endtime.setText("아직 퇴근 전이에요ㅜㅜ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else { // 퇴근 후
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(waitTODAY);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String getStartTime = (String) jsonObject.get("start");
                    String getEndTime = (String) jsonObject.get("end");
                    int start_time = Integer.parseInt(getStartTime.substring(0,2))*3600 +
                                    Integer.parseInt(getStartTime.substring(2,4))*60 +
                                    Integer.parseInt(getStartTime.substring(4));
                    int end_time = Integer.parseInt(getEndTime.substring(0,2))*3600 +
                            Integer.parseInt(getEndTime.substring(2,4))*60 +
                            Integer.parseInt(getEndTime.substring(4));
                    int time = end_time - start_time;
                    String timestr = Integer.toString(time/3600)+"시간 "
                                        +Integer.toString((time%3600)/60)+"분 "
                                        +Integer.toString((time%3600)%60)+"초";

                    starttime.setText(getStartTime.substring(0,2)+"시 "+getStartTime.substring(2,4)+"분 "+getStartTime.substring(4)+"초");
                    endtime.setText(getEndTime.substring(0,2)+"시 "+getEndTime.substring(2,4)+"분 "+getEndTime.substring(4)+"초");
                    worktime.setText(timestr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        stampWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!starttime.getText().equals("아직 출근 전이에요!!")){
                    Toast.makeText(getApplicationContext(), "이미 출근하셨네요ㅠ", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("출근하셨나요?");
                builder.setPositiveButton("네...", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "오늘도 화이팅!!", Toast.LENGTH_SHORT).show();
                        now = System.currentTimeMillis();
                        date = new Date(now);

                        time_screen = sdftime_screen.format(date);
                        starttime.setText(time_screen);

                        today = sdftoday.format(date);
                        time = sdftime.format(date);


                        WorkThread wThread = new WorkThread("http://192.249.19.244:2980/api/work/"+UserID, UserName,today,time);
                        wThread.start();
                        synchronized (wThread){
                            try {
                                wThread.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(mFragment).attach(mFragment).commit();
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
                if(starttime.getText().equals("아직 출근 전이에요!!")){
                    Toast.makeText(getApplicationContext(), "아직 출근도 안하셨는데..?", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!endtime.getText().equals("아직 퇴근 전이에요ㅜㅜ")){
                    Toast.makeText(getApplicationContext(), "이미 퇴근하셨네요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("퇴근하시나요?");
                builder.setPositiveButton("네!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "고생하셨어요~", Toast.LENGTH_SHORT).show();
                        now = System.currentTimeMillis();

                        date = new Date(now);

                        time_screen = sdftime_screen.format(date);
                        endtime.setText(time_screen);

                        today = sdftoday.format(date);
                        time = sdftime.format(date);


                        LeaveThread lThread = new LeaveThread("http://192.249.19.244:2980/api/leave/"+UserID, today,time);
                        lThread.start();
                        synchronized (lThread){
                            try {
                                lThread.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(mFragment).attach(mFragment).commit();
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

    public class JSONTask_WorkDataFromDB extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.connect(); // connect

                    if(con.getResponseCode() == 404) { // DB에도 데이터 없음 - 출근 전
                        return "NoDATA";
                    } else {
                        // DB에서 document 정보 가져오기
                        InputStream stream = con.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuffer buffer = new StringBuffer();
                        String line = "";

                        //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                        while((line = reader.readLine()) != null){
                            buffer.append(line);
                        }
                        String bufferStr = buffer.toString();
                        Log.d("PRINTgetinfo", bufferStr);

                        return bufferStr; // json 형태의 string 반환
                    }

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class JSONTaskRank extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true); //GET일 때 비활성화
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type","application/json; charset=UTF-8"); // POST, PUT일때 활성화

                    con.connect();//연결 수행
                    if(con.getResponseCode() == 404) {
                        return "NoDATA";
                        //아직 아무도 출근 안함.
                    }

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }




}
