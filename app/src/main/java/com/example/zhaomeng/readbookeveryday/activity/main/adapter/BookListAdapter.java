package com.example.zhaomeng.readbookeveryday.activity.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.main.fragment.BookListFragment;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;
import com.example.zhaomeng.readbookeveryday.util.FileUtil;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaomeng on 2015/10/20.
 */
public class BookListAdapter extends BaseAdapter {
    public static final String BOOK_LIST_IMAGE = "book_list_image";

    private List<BookDto> bookDtoList;
    private Context context;
    private LayoutInflater inflater;
    private BookListFragment bookListFragment;

    public BookListAdapter(Context context, List<BookDto> bookDtoList, BookListFragment bookListFragment) {
        this.bookDtoList = bookDtoList;
        this.bookListFragment = bookListFragment;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<BookDto> bookDtoList) {
        this.bookDtoList = bookDtoList;
        notifyDataSetChanged();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_book_list, viewGroup, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.book_title);
            holder.bookPoster = (ImageView) view.findViewById(R.id.book_poster);
            holder.progress = (TextView) view.findViewById(R.id.progress);
            holder.createTime = (TextView) view.findViewById(R.id.create_time);
            holder.updateTime = (TextView) view.findViewById(R.id.last_update_time);
            holder.complete = (ImageView) view.findViewById(R.id.read_complete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final BookDto bookDto = bookDtoList.get(i);
        holder.title.setText(bookDto.getTitle());
        if (bookDto.getImagePath() != null) {
            if(!bookDto.getImagePath().startsWith(FileUtil.FILE_SCHEME)){
                bookDto.setImagePath(FileUtil.FILE_SCHEME +bookDto.getImagePath());
                BookUtil.getInstance(context).createOrUpdateBookDto(bookDto);
            }
            Picasso.with(context).load(bookDto.getImagePath()).tag(BOOK_LIST_IMAGE).into(holder.bookPoster);
            holder.bookPoster.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            Picasso.with(context).load(R.mipmap.camera).into(holder.bookPoster);
            holder.bookPoster.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        holder.bookPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookListFragment.chosePosterImage(bookDto.getId(), i);
            }
        });
        holder.progress.setText(bookDto.getHasReadPageNum() + "/" + bookDto.getTotalPageNum());
        holder.createTime.setText(getTimeString(bookDto.getCreateTime()));
        holder.updateTime.setText(getTimeString(bookDto.getUpdateTime()) + context.getResources().getString(R.string.book_list_adapter_update_time_title));
        if (bookDto.getHasReadPageNum() == bookDto.getTotalPageNum()) {
            holder.complete.setVisibility(View.VISIBLE);
        } else {
            holder.complete.setVisibility(View.GONE);
        }
        return view;
    }

    public String getTimeString(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return sDateFormat.format(new Date(time));
    }

    static class ViewHolder {
        TextView title;
        ImageView bookPoster;
        TextView progress;
        TextView createTime;
        TextView updateTime;
        ImageView complete;
    }
}
