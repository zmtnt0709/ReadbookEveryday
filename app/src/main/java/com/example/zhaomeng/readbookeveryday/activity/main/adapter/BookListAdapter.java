package com.example.zhaomeng.readbookeveryday.activity.main.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaomeng on 2015/10/20.
 */
public class BookListAdapter extends BaseAdapter {
    private List<BookDto> bookDtoList;
    private Context context;
    private LayoutInflater inflater;

    public BookListAdapter(Context context, List<BookDto> bookDtoList) {
        this.bookDtoList = bookDtoList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bookDtoList.size();
    }

    @Override
    public Object getItem(int i) {
        return bookDtoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_book_list, null);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.book_title);
            holder.bookPoster = (SimpleDraweeView) view.findViewById(R.id.book_poster);
            holder.progress = (TextView) view.findViewById(R.id.progress);
            holder.createTime = (TextView) view.findViewById(R.id.create_time);
            holder.updateTime = (TextView) view.findViewById(R.id.last_update_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BookDto bookDto = bookDtoList.get(i);
        holder.title.setText(bookDto.getTitle());
        if(bookDto.getImagePath() != null) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse("file://" + bookDto.getImagePath()))
                    .build();
            holder.bookPoster.setController(controller);
        }
        holder.progress.setText(bookDto.getHasReadPageNum() + "/" + bookDto.getTotalPageNum());
        holder.createTime.setText(getTimeString(bookDto.getCreateTime()));
        holder.updateTime.setText(getTimeString(bookDto.getUpdateTime()) + context.getResources().getString(R.string.book_list_adapter_update_time_title));
        return view;
    }

    public String getTimeString(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return sDateFormat.format(new Date(time));
    }

    static class ViewHolder {
        TextView title;
        SimpleDraweeView bookPoster;
        TextView progress;
        TextView createTime;
        TextView updateTime;
    }
}
