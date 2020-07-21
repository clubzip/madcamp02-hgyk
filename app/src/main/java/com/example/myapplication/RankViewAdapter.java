package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class RankViewAdapter extends BaseAdapter implements Filterable {



    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    public ArrayList<RankViewItem> rankViewItemList = new ArrayList<RankViewItem>();

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<RankViewItem> filteredItemList = rankViewItemList ;
    Filter listFilter;

    public RankViewAdapter() {

    }

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = rankViewItemList ;
                results.count = rankViewItemList.size() ;
            } else {
                ArrayList<RankViewItem> itemList = new ArrayList<RankViewItem>() ;

                for (RankViewItem item : rankViewItemList) {
                    if (item.getName().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getNumber().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }
                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<RankViewItem>) results.values ;
            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }
        return listFilter ;
    }


    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tab3_work_rank_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView textView1 = (TextView) convertView.findViewById(R.id.name_rank);
        TextView textView2 = (TextView) convertView.findViewById(R.id.Text_workTime_rank);
        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        RankViewItem rankViewItem = filteredItemList.get(position);

        textView1.setText(rankViewItem.getName());
        textView2.setText(rankViewItem.getNumber().substring(0,2)+"시 "+rankViewItem.getNumber().substring(2,4)+"분 "+rankViewItem.getNumber().substring(4)+"초");

        return convertView;
    }

    public RankViewItem addItem(String name, String number) { //String profile (프로필 지정 시 3rd arg)
        RankViewItem item = new RankViewItem();
        item.setName(name);
        item.setNumber(number);
//        item.setProfile(profile);

        rankViewItemList.add(item);
        return item;
    }

    public void clearItem(){
        rankViewItemList.clear();
    }

}
