package com.example.zhaomeng.readbookeveryday.activity.bookdetail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.module.BookModule;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaomeng on 2015/11/26.
 */
public class HasReadPageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<PageRange> hasReadPageRangeList;

    public HasReadPageAdapter(Context context, BookModule bookModule) {
        this.context = context;
        hasReadPageRangeList = bookModule.getHasReadPageList();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return hasReadPageRangeList.size();
    }

    @Override
    public Object getItem(int i) {
        return hasReadPageRangeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_has_read_page_range, null);
            holder = new ViewHolder();
            holder.startPage = (TextView) view.findViewById(R.id.start_page);
            holder.stopPage = (TextView) view.findViewById(R.id.stop_page);
            holder.readDate = (TextView) view.findViewById(R.id.read_date);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.startPage.setText(hasReadPageRangeList.get(i).getStartPage() + context.getResources().getString(R.string.page_range_adapter_page_title));
        holder.stopPage.setText(hasReadPageRangeList.get(i).getStopPage() + context.getResources().getString(R.string.page_range_adapter_page_title));
        holder.readDate.setText(getTimeString(BookUtil.getInstance(context).dateToTimeMill(hasReadPageRangeList.get(i).getCreateDate())));
        return view;
    }

    public String getTimeString(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return sDateFormat.format(new Date(time));
    }

    static class ViewHolder {
        TextView startPage;
        TextView stopPage;
        TextView readDate;
    }
}
