package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class tab3Frag extends Fragment {

    View view;
    ListView listView;
    ListViewAdapter listViewAdapter;
    ArrayList<ListViewItem> data = new ArrayList<>();
    String UserID;

    public tab3Frag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contact_main, container, false);
        listView =(ListView) view.findViewById(R.id.listView);
        data = new ArrayList<>();
        listViewAdapter = new ListViewAdapter();

        return view;
    }


}
