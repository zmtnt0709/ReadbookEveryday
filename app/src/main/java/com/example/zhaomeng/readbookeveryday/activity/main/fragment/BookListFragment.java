package com.example.zhaomeng.readbookeveryday.activity.main.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.bookdetail.BookDetailActivity;
import com.example.zhaomeng.readbookeveryday.activity.main.adapter.BookListAdapter;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;

import java.util.List;

/**
 * Created by zhaomeng on 2015/10/19.
 */
public class BookListFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private static final String TAG = BookListFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefresh;
    private ListView bookListView;
    private boolean isRefreshing;
    private Handler handler;
    private boolean isJustCreate;
    private List<BookDto> bookList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        handler = new Handler();
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.toolBar);
        bookListView = (ListView) rootView.findViewById(R.id.book_list_view);
        bookListView.setOnItemClickListener(this);
        isJustCreate = true;
        new UpdateBookListTask().execute();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isJustCreate) {
            isJustCreate = false;
        } else {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            handler.postDelayed(new Runnable() {
                public void run() {
                    new UpdateBookListTask().execute();
                    swipeRefresh.setRefreshing(false);
                    isRefreshing = false;
                }
            }, 1000);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BookDto bookDto = bookList.get(i);
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra("id", bookDto.getId());
        startActivity(intent);
    }

    class UpdateBookListTask extends AsyncTask<Void, Void, List<BookDto>> {

        @Override
        protected List<BookDto> doInBackground(Void... voids) {
            return BookUtil.getInstance(getActivity()).getAllBookList();
        }

        @Override
        protected void onPostExecute(List<BookDto> bookDtoList) {
            Log.d(TAG, "UpdateBookListTask onPostExecute");
            if (bookDtoList == null || bookDtoList.isEmpty()) return;

            bookList = bookDtoList;
            BookListAdapter bookListAdapter = new BookListAdapter(getActivity(), bookList);
            bookListView.setAdapter(bookListAdapter);
        }
    }
}
