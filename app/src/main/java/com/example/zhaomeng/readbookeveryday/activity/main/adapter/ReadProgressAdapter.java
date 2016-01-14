package com.example.zhaomeng.readbookeveryday.activity.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.module.ReadProgressToShow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaomeng on 2015/12/16.
 */
public class ReadProgressAdapter extends BaseAdapter {
    private List<ReadProgressToShow> readProgressToShowList;
    private LayoutInflater inflater;
    private Context context;
    private int maxReadPage;

    public ReadProgressAdapter(Context context, List<ReadProgressToShow> readProgressToShowList, int maxReadPage) {
        this.context = context;
        this.readProgressToShowList = readProgressToShowList;
        this.inflater = LayoutInflater.from(context);
        this.maxReadPage = maxReadPage;
    }

    @Override
    public int getCount() {
        return readProgressToShowList.size();
    }

    @Override
    public Object getItem(int i) {
        return readProgressToShowList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_read_progress, null);
            holder = new ViewHolder();
            holder.readDateTextView = (TextView) view.findViewById(R.id.read_date);
            holder.readPagesTextView = (TextView) view.findViewById(R.id.read_pages);
            holder.readProgressBar = (ProgressBar) view.findViewById(R.id.read_progress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ReadProgressToShow readProgressToShow = readProgressToShowList.get(i);
        holder.readDateTextView.setText(getTimeString(readProgressToShow.getReadDate()));
        holder.readPagesTextView.setText(String.valueOf(readProgressToShow.getReadPageNumTotal()));
        holder.readProgressBar.setProgress((int) (readProgressToShow.getReadPageNumTotal() * 90.0f / maxReadPage) + 10);
        return view;
    }

    public String getTimeString(long date) {
        long time = date * 24 * 3600 * 1000;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sDateFormat.format(new Date(time));
    }

    static class ViewHolder {
        ProgressBar readProgressBar;
        TextView readDateTextView;
        TextView readPagesTextView;
    }
}
