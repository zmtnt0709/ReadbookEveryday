package com.example.zhaomeng.readbookeveryday.activity.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.bookdetail.BookDetailActivity;
import com.example.zhaomeng.readbookeveryday.activity.main.adapter.BookListAdapter;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;
import com.example.zhaomeng.readbookeveryday.util.FileUtil;
import com.example.zhaomeng.readbookeveryday.widget.ConfirmPopupWindow;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zhaomeng on 2015/10/19.
 */
public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,AbsListView.OnScrollListener{
    private static final String TAG = BookListFragment.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private SwipeRefreshLayout swipeRefresh;
    private ListView bookListView;
    private BookListAdapter bookListAdapter;
    private boolean isRefreshing;
    private List<BookDto> bookList;
    private int changePosterBookId; //要更换封面的bookId
    private int changePosterBookNum;//要更换封面的book在list中的序号

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.tool_bar);
        bookListView = (ListView) rootView.findViewById(R.id.book_list_view);
        bookListView.setOnItemClickListener(this);
        bookListView.setOnItemLongClickListener(this);
        bookListView.setOnScrollListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing) {
            new UpdateBookListTask().execute();
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
        new DeleteConfirm(getContext(), R.string.delete_book_popup, R.string.confirm, R.string.cancel, i).show();
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
                BookUtil.getInstance(getActivity().getApplicationContext()).createOrUpdateBookDto(bookDto);
                bookListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Picasso picasso = Picasso.with(getContext());
        if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL){
            picasso.pauseTag(BookListAdapter.BOOK_LIST_IMAGE);
        }else {
            picasso.resumeTag(BookListAdapter.BOOK_LIST_IMAGE);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class DeleteConfirm extends ConfirmPopupWindow {
        private int num;

        public DeleteConfirm(Context context, int hintText, int positiveButtonText, int negativeButtonText, int num) {
            super(context, hintText, positiveButtonText, negativeButtonText);
            this.num = num;
        }

        @Override
        protected void onPositiveButtonClick() {
            BookUtil.getInstance(getActivity().getApplicationContext()).deleteBook(bookList.get(num));
            bookList.remove(num);
            bookListAdapter.notifyDataSetChanged();
            pop.dismiss();
        }

        @Override
        protected void onNegativeButtonClick() {
            pop.dismiss();
        }
    }

    class UpdateBookListTask extends AsyncTask<Void, Void, List<BookDto>> {
        @Override
        protected void onPreExecute() {
            swipeRefresh.setRefreshing(true);
            isRefreshing = true;
        }

        @Override
        protected List<BookDto> doInBackground(Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(getActivity() != null ) {
                return BookUtil.getInstance(getActivity().getApplicationContext()).getAllBookListToShow();
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<BookDto> bookDtoList) {
            Log.d(TAG, "UpdateBookListTask onPostExecute");
            if (bookDtoList == null || bookDtoList.isEmpty()) return;

            bookList = bookDtoList;
            bookListAdapter = new BookListAdapter(getActivity(), bookList, BookListFragment.this);
            bookListView.setAdapter(bookListAdapter);
            swipeRefresh.setRefreshing(false);
            isRefreshing = false;
        }
    }
}
