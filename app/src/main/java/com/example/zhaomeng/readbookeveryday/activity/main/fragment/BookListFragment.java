package com.example.zhaomeng.readbookeveryday.activity.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.zhaomeng.readbookeveryday.util.FileUtil;
import com.example.zhaomeng.readbookeveryday.widget.ConfirmPopupWindow;

import java.util.List;

/**
 * Created by zhaomeng on 2015/10/19.
 */
public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = BookListFragment.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private SwipeRefreshLayout swipeRefresh;
    private ListView bookListView;
    private BookListAdapter bookListAdapter;
    private boolean isRefreshing;
    private Handler handler;
    private boolean isJustCreate;
    private List<BookDto> bookList;
    private int changePosterBookId; //要更换封面的bookid
    private int changePosterBookNum;//要更换封面的book在list中的序号

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
        bookListView.setOnItemLongClickListener(this);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        BookDto bookDto = bookList.get(i);
        new DeleteConfirm(getContext(), "确认删除已读区间？", "确认", "取消", bookDto).show();
        return false;
    }

    public void chosePosterImage(int bookId, int bookNum) {
        changePosterBookId = bookId;
        changePosterBookNum = bookNum;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null && data.getData() != null) {
            String imagePath = FileUtil.getInstance().saveImage(getActivity(), data.getData());
            if (imagePath == null) return;

            BookDto bookDto = bookList.get(changePosterBookNum);
            if (changePosterBookId == bookDto.getId()) {
                bookDto.setImagePath(imagePath);
                BookUtil.getInstance(getActivity()).updateBook(bookDto);
                bookListAdapter.notifyDataSetChanged();
            }
        }
    }

    private class DeleteConfirm extends ConfirmPopupWindow {
        private BookDto bookDto;

        public DeleteConfirm(Context context, String hintText, String positiveButtonText, String negativeButtonText, BookDto bookDto) {
            super(context, hintText, positiveButtonText, negativeButtonText);
            this.bookDto = bookDto;
        }

        @Override
        protected void onPositiveButtonClick() {
            BookUtil.getInstance(getActivity()).deleteBook();
            pop.dismiss();
        }

        @Override
        protected void onNegativeButtonClick() {
            pop.dismiss();
        }
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
            bookListAdapter = new BookListAdapter(getActivity(), bookList, BookListFragment.this);
            bookListView.setAdapter(bookListAdapter);
        }
    }
}
