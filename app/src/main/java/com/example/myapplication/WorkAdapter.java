package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class WorkAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_TODAY = 0;
    private static final int ITEM_VIEW_TYPE_WORK_RANK = 1;
    private static final int ITEM_VIEW_TYPE_MAX = 2;

    private ArrayList<WorkHolder> workHolderList;

    public WorkAdapter (ArrayList<WorkHolder> workHolderList) {
        this.workHolderList = workHolderList;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        return workHolderList.get(position).getType();
    }

    @Override
    public int getCount() {
        return workHolderList.size();
    }

    @Override
    public Object getItem(int i) {
        return workHolderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WorkHolder workHolder = workHolderList.get(i);

            switch (workHolder.getType()) {
                case ITEM_VIEW_TYPE_TODAY:
                    view = inflater.inflate(R.layout.tab3_today, viewGroup, false);
                    break;
                case ITEM_VIEW_TYPE_WORK_RANK:
                    view = inflater.inflate(R.layout.tab3_work_rank, viewGroup, false);
                    break;
            }

            workHolder.show(view);
        }

        return view;
    }





}
