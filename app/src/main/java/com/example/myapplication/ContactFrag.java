package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ContactFrag extends Fragment {

    public ContactFrag(String userID) {
        UserID = userID;
    }

    View view;
    ListView listView;
    ListViewAdapter listViewAdapter;
    ArrayList<ListViewItem> data = new ArrayList<>();
    String UserID;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contact_main, container, false);
        listView =(ListView) view.findViewById(R.id.listView);
        data = new ArrayList<>();
        listViewAdapter = new ListViewAdapter();

        Context context = getContext();

        File file = new File(getActivity().getFilesDir(), "contactList.json");

        if (!file.exists()){
            try {
                file.createNewFile();
                try{
                    File fileOut = new File(getContext().getFilesDir(),"contactList.json");
                    FileOutputStream fos = new FileOutputStream(fileOut);
                    String input = "[\n]\n";
                    fos.write(input.getBytes());
                    fos.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // contactList.json 안에 facebookID 있나 확인하기 0: 없음, 1: 있음
        int i = 0;
        File fileIn = new File(getContext().getFilesDir(),"contactList.json");
        try {
            FileInputStream fis = new FileInputStream(fileIn);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            line = reader.readLine();
            while(line != null){
                if (line.contains(UserID)){
                    i = 1;
                    break;
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (i==0){ // 없을 때
            FileInputStream fis = null;
            try { // contactList.json 파일에 document 만들어줌
                fis = new FileInputStream(file);
                InputStreamReader isr= new InputStreamReader(fis);
                BufferedReader reader= new BufferedReader(isr);
                StringBuffer buffer= new StringBuffer();
                String line= reader.readLine();
                buffer.append(line+"\n");
                buffer.append("{\n\"facebookID\": \""+UserID+"\",\n\"contactList\":\n[\n]\n},\n");
                line = reader.readLine();
                while (line != null){
                    buffer.append(line+"\n");
                    line = reader.readLine();
                }
                FileOutputStream fos = new FileOutputStream(file);
                String bufferstr = buffer.toString();
                fos.write(bufferstr.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // DB에 있으면 파싱해서 내부 저장소에 넣어주기, 없으면 DB에 document 생성
            // DB에서 데이터 가져오기
            String wait = "hi";
            try {
                // DB에서 데이터를 가져옴
                wait = new JSONTask_GetFromDB().execute("http://192.249.19.244:2980/api/contacts/facebookID/"+UserID).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // DB에서 가져온 데이터를 분석해서 -> (1) 없으면 DB에 doc 생성 (2) 있으면 데이터 파싱하기
            String wait2 = "hihi";
            if (wait == "hi") {
                Log.d("Data from DB", "hihi");
            } else if (wait == "NoDATA") { // db에 데이터가 없는 경우, doc 생성하기
                try {
                    // JSONTask_CreateDocument는 꼭 기다릴 필요는 없음
                    wait2 = new JSONTask_CreateDocument().execute("http://192.249.19.244:2980/api/contacts/").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else { // db에 데이터가 있는 경우, 파싱해서 넣어주기 (내부 저장소에는 없고, DB에는 있고)
                String dbdb = wait;
                Log.d("DataFromDB", dbdb);
                // 데이터(wait) 파싱해서 strParsed 에 String 타입으로 저장
                String strParsed;
                StringBuffer stringBuffer = new StringBuffer();
                int index = wait.indexOf(":[{");
                int index2 = wait.indexOf("]}]");
                String subStr = wait.substring(index+2, index2); // {},{},{}
                String[] subStrArray = subStr.split(",");
                for (int j=0; j<subStrArray.length - 2; j++){
                    stringBuffer.append(subStrArray[j]+",");
                    j++;
                    stringBuffer.append(subStrArray[j]+",\n");
                }
                stringBuffer.append(subStrArray[subStrArray.length-2]+",");
                stringBuffer.append(subStrArray[subStrArray.length-1]+"\n");
                strParsed = stringBuffer.toString();

                // 파싱한 데이터를 포함하여 다시 contactList.json에 넣기
                FileInputStream fisD = null;
                try {
                    fisD = new FileInputStream(file);
                    InputStreamReader isrD = new InputStreamReader(fisD);
                    BufferedReader readerD = new BufferedReader(isrD);
                    StringBuffer bufferD = new StringBuffer();
                    String lineD = readerD.readLine(); // read [
                    bufferD.append(lineD+'\n');
                    for (int k=0; k<4; k++){
                        lineD = readerD.readLine();
                        bufferD.append(lineD+'\n');
                    }
                    bufferD.append(strParsed);
                    lineD = readerD.readLine();
                    while (lineD != null){
                        bufferD.append(lineD+"\n");
                        lineD = readerD.readLine();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    String bufferstrD = bufferD.toString();
                    fos.write(bufferstrD.getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // contactList.json에서 id에 맞는 data 가져와서 adapter에 전달하기

        readJsonFromDir();

        //버튼 뷰 추가
        Button addButton = (Button)view.findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactAdd.class);
                intent.putExtra("UserID", UserID);
                startActivityForResult(intent, 10001);
            }
        });
        // 버튼 클릭 시 ContactAdd액티비티 실행 - (해당 액티비티 manifest에 추가하기)

        return view;
    }

    private void readJsonFromDir() {
        // 내부 저장소의 facebookID 폴더에서 contact.json 파일을 가져와서 adpater에 정보 전달
        try {
            //내부 저장소의 UserID(=facebookID) 폴더의 contact.json 파일을 가져옴
            File file = new File(getActivity().getFilesDir(),"contactList.json");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr= new InputStreamReader(fis);
            BufferedReader reader= new BufferedReader(isr);

            StringBuffer buffer= new StringBuffer();
            String line= reader.readLine();
            while (line != null && !line.contains(UserID)){
                line = reader.readLine();
            }

            line = reader.readLine();
            line = reader.readLine(); // read [
            buffer.append(line+"\n");
            line = reader.readLine();
            while (!line.contains("]")){
                buffer.append(line+"\n");
                line = reader.readLine();
            }
            buffer.append(line+"\n");

            String jsonData= buffer.toString();

            //json 데이터가 []로 시작하는 배열일때..
            JSONArray jsonArray= new JSONArray(jsonData);

            for(int i=0; i<jsonArray.length();i++) {
                JSONObject jo = jsonArray.getJSONObject(i);

                String name = jo.getString("name");
                String dial = jo.getString("dial");
                data.add(listViewAdapter.addItem(name, dial));
            }

            //리스트뷰에 어뎁터 set
            listView.setAdapter(listViewAdapter);

            //Item 클릭 시 이벤트
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    Intent intent = new Intent(getActivity(), ItemClicked.class);
                    intent.putExtra("name", data.get(position).getName());
                    intent.putExtra("dial", data.get(position).getNumber());
                    intent.putExtra("UserID", UserID);

                    startActivityForResult(intent,10001);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class JSONTask_GetFromDB extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect(); // connect

                    if(con.getResponseCode() == 404) { // DB에도 없음
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

    public class JSONTask_CreateDocument extends AsyncTask<String, String, String> {
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
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    String json = "{\"facebookID\": \""+UserID+"\",\"contactList\": [{\"name\": \"test1\",\"phone_num\": \"11111111\"}]}";
                    OutputStream os = con.getOutputStream();
                    os.write(json.getBytes("UTF-8"));
                    os.close();

                    con.connect(); // connect

                    return null;

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

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // DATA 어떻게 받아오는지 test
//            String json_contact = result;
//            Log.i("JSON",json_contact);
//
//            // 내부 저장소에 DB 내용 저장
//            String DataFromServer = result;
//            try{
//                File fileOut = new File(getContext().getFilesDir()+UserID,"contact.json");
//                FileOutputStream fos = new FileOutputStream(fileOut);
//                fos.write(DataFromServer.getBytes());
//                fos.close();
//            } catch(IOException e){
//                e.printStackTrace();
//            }
        }
    }



} // Fragment
