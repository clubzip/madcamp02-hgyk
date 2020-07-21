package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.Thread.sleep;

public class GalleryFrag extends Fragment {
    private GalleryFrag mFragment = this;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Context mContext;
    private GridView mGridView;
    FloatingActionButton mFloatButton;
    private int mCellSize;

    private String mCurrentPhotoPath;
    private String mImageDirPath;

    private String[] mImagePaths;
    private ImageAdapter mImageAdapter;
    private ArrayList<Image> mImageArrayList;

    private int click_enable;
    private String UserID;
    private String DataFromServer;
    private ArrayList<String> imagelistinDB;
    private ArrayList<String> imagelistinSD;
//    boolean Synch;

    public class JSONTaskGET extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("POST");
                    con.setRequestMethod("GET");
//                    con.setDoOutput(true); //GET일 때 비활성화
                    con.setDoInput(true);
//                    con.setRequestProperty("Content-Type","application/json; charset=UTF-8"); // POST, PUT일때 활성화
//                    String json = "{\"facebookID\": \"test\",\"contactList\": [{\"name\": \"test1\",\"phone_num\": \"11111111\"}]}"; - POST body
//                    String json = "{\"name\": \"test\",\"dial\": \"010-1551-4541\"}"; - PUT add body
                    /*String json = "{\"name\": \"test\"}";
                    OutputStream os = con.getOutputStream();
                    os.write(json.getBytes("UTF-8"));
                    os.close();*/

                    con.connect();//연결 수행
                    if(con.getResponseCode() == 404) {
                        return "Not Found";
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
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    DataFromServer = buffer.toString();
                    /*if(Synch) {

                        Synch = false;
                    }*/
                    return DataFromServer;

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if (reader != null) {
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
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //UI 갱신하려면 스레드 선언후 뭐 해줘야 함. -- 건식
        }
    }

    public GalleryFrag(String userID) {
        UserID = userID;
    }

    private void DBSync() throws ExecutionException, InterruptedException {
        // DB synchronization
//        Synch = true;
        String result = new JSONTaskGET().execute("http://192.249.19.244:2980/api/images/facebookID/"+UserID).get();
        /*try{
            Thread.sleep(1000);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }*/
        DataFromServer = DataFromServer.replaceAll("\\\"","");
        String[] imagesinDB = DataFromServer.substring(DataFromServer.indexOf(":[")+2,DataFromServer.indexOf("]")).split(",");
        imagelistinDB = new ArrayList<>(Arrays.asList(imagesinDB));
        //외부 저장소에 저장된 파일 리스트와 비교.
        File dir = new File(mImageDirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        String[] imagesinSD = dir.list();
        imagelistinSD = new ArrayList<>(Arrays.asList(imagesinSD));
        ArrayList<String> temp = new ArrayList<>(imagelistinDB);

        //빈 array를 removeAll하는 경우 에러가 떴었는데 그거 고쳐졌는지 확인!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        imagelistinDB.removeAll(imagelistinSD); // files that were added in DB
        imagelistinSD.removeAll(temp); // files that were deleted in DB
        // 똑같으면 그냥 넘어가고
        // 다르면
        if(!imagelistinDB.isEmpty()){// 1. 추가된 이미지는 서버에 요청후 외부저장소에 저장
            for(String tempValue: imagelistinDB){
                DownloadThread dThread = new DownloadThread("http://192.249.19.244:2980/api/images/getimage/facebookID/"+UserID,tempValue,mImageDirPath+"/"+tempValue);
                dThread.start();
                synchronized (dThread){
                    dThread.wait();
                }
            }
        }
        if(!imagelistinSD.isEmpty()){// 2. 제거된 이미지는 외부저장소에서 삭제
            for(String tempValue: imagelistinSD) {
                File file = new File(mImageDirPath + "/" + tempValue);
                file.delete();
            }
        }
//        Log.i("Data From Server",DataFromServer);
    }
    private boolean isFirstLogin() {
        File dir = new File(mImageDirPath);
        return !dir.exists();
        // Check if this is the first login
    }
    private boolean isExistinDB() throws ExecutionException, InterruptedException {
        String result = new JSONTaskGET().execute("http://192.249.19.244:2980/api/images/facebookID/"+UserID).get();
        return !result.equals("Not Found");
        // Check if the document for userID in Gallery collection is existed.
    }
    private void FirstStep() throws ExecutionException, InterruptedException {
        if(isFirstLogin()){
            if(isExistinDB()){
                DBSync();
            } else {
                String result = new JSONTaskGET().execute("http://192.249.19.244:2980/api/images/create/facebookID/"+UserID).get();
                DBSync();
            }
        } else {
            DBSync();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {


        initRetrofitClient();
        View view = (View) inflater.inflate(R.layout.fragment2, container, false);

        mContext = view.getContext();
        mImageDirPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + UserID; //<-----------------------바꿨음.
//        mImageDirPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/MadCampApp";

        try {
            FirstStep();// FirstStep으로 바꿔야 함.
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Get GRID_VIEW */
        mGridView = (GridView) view.findViewById(R.id.grid_view);

        if (mImagePaths == null) {
            initFragment();
        }
        mGridView.setAdapter(mImageAdapter);

        /* Floating camera button */
        mFloatButton = view.findViewById(R.id.cameraIcon);
        mFloatButton.setVisibility(View.VISIBLE);

        if (((MainActivity) getActivity()).isSelection()) {
            mFloatButton.setVisibility(View.GONE);
            mGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    mGridView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            if (click_enable == 1) {
                                Intent i = new Intent(getActivity(), FullImageActivity.class);
                                i.putExtra("id", position);
                                i.putExtra("imagePaths", mImagePaths);
                                i.putExtra("imageDirPath", mImageDirPath);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        }
                    });

//                    ((MainActivity) mContext).finishSelectImage(mImageArrayList.get(position));
                }
            });

            return view;
        }

        setListener();

        return view;
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Timestamp(System.currentTimeMillis()));
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(mImageDirPath);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
            imageFileName, ".jpg", storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("captureCamera Error", ex.toString());
                return;
            }

            Uri photoURI = FileProvider.getUriForFile(mContext, "com.example.myapplication.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                galleryAddPic();
                /* Update an array of image paths */
                File dir = new File(mImageDirPath);
                mImagePaths = dir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        boolean bOK = false;
                        if(s.toLowerCase().endsWith(".jpg")) bOK = true;

                        return bOK;
                    }
                });

                /* Insert new image */
                mImageArrayList.add(new Image(mCurrentPhotoPath, mCellSize));

                /* Update View */
                mImageAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("Request Take Photo", e.toString());
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
        Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
        multipartImageUpload();//-------------------------------------------------------------multipart called here!!!!
    }

    private void initFragment() {
        /* Make an array of image paths */
        File dir = new File(mImageDirPath);
        mImagePaths = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                boolean bOK = false;
                if(s.toLowerCase().endsWith(".jpg")) bOK = true;

                return bOK;
            }
        });

        /* Get proper CELL_SIZE which is (width pixels - space between cells) / 3 */
        mCellSize = (getResources().getDisplayMetrics().widthPixels - mGridView.getRequestedHorizontalSpacing()) / 3;

        /* Make an array list of IMAGEs */
        mImageArrayList = new ArrayList<>();
        if (mImagePaths != null) {
            for (String path : mImagePaths) {
                mImageArrayList.add(new Image(mImageDirPath + "/" + path, mCellSize));
            }
        }

        /* Set new image adapter to GRIDVIEW */
        mImageAdapter = new ImageAdapter(getActivity(), mCellSize, mImageArrayList);

        click_enable = 1;
    }

    private void setListener() {
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        /* Set click listener. Start FULL_IMAGE_ACTIVITY with POSITION which
            indicates an clicked image */
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (click_enable == 1) {
                    Intent i = new Intent(getActivity(), FullImageActivity.class);
                    i.putExtra("id", position);
                    i.putExtra("imagePaths", mImagePaths);
                    i.putExtra("imageDirPath", mImageDirPath);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
             @SuppressLint("FragmentBackPressedCallback")
             @Override
             public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                 click_enable = 0;
                 final ImageSelectAdapter deleteAdapter = new ImageSelectAdapter(mContext, mCellSize, mImageArrayList,UserID);
                 mGridView.setAdapter(deleteAdapter);
                 mFloatButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.trash));

                 final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                     @Override
                     public void handleOnBackPressed() {
                         mGridView.setAdapter(mImageAdapter);
                         mFloatButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.camera));

                         click_enable = 1;
                         mFloatButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 dispatchTakePictureIntent();
                             }
                         });

                         this.remove();
                     }
                 };
                 requireActivity().getOnBackPressedDispatcher().addCallback(mFragment, callback);

                 mFloatButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         deleteAdapter.deleteChecked();
                         mGridView.setAdapter(mImageAdapter);
                         mFloatButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.camera));

                         click_enable = 1;
                         mFloatButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 dispatchTakePictureIntent();
                             }
                         });

                         callback.remove();
                     }
                 });
                 return false;
             }
         }
        );
    }

    public ArrayList<Image> getImageArrayList() {
        return mImageArrayList;
    }

    ApiService apiService;
    Bitmap mBitmap;

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl("http://192.249.19.244:2980/"+UserID+"/").client(client).build().create(ApiService.class);
    }

    private void multipartImageUpload() {
        try {
            File file = new File(mCurrentPhotoPath);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();


            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");

            Call<ResponseBody> req = apiService.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
