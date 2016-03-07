package com.example.zhaomeng.readbookeveryday.activity.addbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.module.PageRange;

import java.util.List;

/**
 * Created by zhaomeng on 2015/11/24.
 */
public class PageRangeAdapter extends BaseAdapter {
    private List<PageRange> pageRangeList;
    private LayoutInflater inflater;
    private Context context;

    public PageRangeAdapter(Context context, List<PageRange> pageRangeList) {
        this.pageRangeList = pageRangeList;
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return pageRangeList.size();
    }

    @Override
    public Object getItem(int i) {
        return pageRangeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_page_range, viewGroup, false);
            holder = new ViewHolder();
            holder.startPage = (TextView) view.findViewById(R.id.start_page);
            holder.stopPage = (TextView) view.findViewById(R.id.stop_page);
            holder.totalPage = (TextView) view.findViewById(R.id.total_page);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.startPage.setText(pageRangeList.get(i).getStartPage() + context.getResources().getString(R.string.page_range_adapter_page_title));
        holder.stopPage.setText(pageRangeList.get(i).getStopPage() + context.getResources().getString(R.string.page_range_adapter_page_title));
        holder.totalPage.setText(pageRangeList.get(i).getTotalPage() + context.getResources().getString(R.string.page_range_adapter_page_title));
        return view;
    }

    static class ViewHolder {
        TextView startPage;
        TextView stopPage;
        TextView totalPage;
    }
}
