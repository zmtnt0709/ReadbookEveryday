package com.example.zhaomeng.readbookeveryday.activity.main.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.main.adapter.ReadProgressAdapter;
import com.example.zhaomeng.readbookeveryday.module.ReadProgressToShow;
import com.example.zhaomeng.readbookeveryday.util.ReadProgressUtil;

import java.util.List;

/**
 * Created by zhaomeng on 2015/11/30.
 */
public class ReadProgressFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = ReadProgressFragment.class.getSimpleName();

    private TextView hasReadTextView;
    private TextView averagePagesTextView;
    private ListView readTaskListView;
    private SwipeRefreshLayout swipeRefresh;

    private ReadProgressUtil readProgressUtil;
    private boolean isRefreshing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_read_progress, container, false);
        initView(rootView);
        initDate();
        onRefresh();
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "onHiddenChanged " + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onRefresh();
        }
    }

    private void initView(View rootView) {
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.tool_bar);
        hasReadTextView = (TextView) rootView.findViewById(R.id.has_read);
        averagePagesTextView = (TextView) rootView.findViewById(R.id.average_page);
        readTaskListView = (ListView) rootView.findViewById(R.id.read_progress_list_view);
    }

    private void initDate() {
        readProgressUtil = ReadProgressUtil.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing) {
            new GetReadProgressTask().execute();
        }
    }

    class GetReadProgressTask extends AsyncTask<Void, Void, Boolean> {
        private String hasReadPagesString;
        private String averagePagesString;
        private ReadProgressAdapter readProgressAdapter;

        @Override
        protected void onPreExecute() {
            isRefreshing = true;
            swipeRefresh.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<ReadProgressToShow> readProgressToShowList = readProgressUtil.getAllReadProgressToShow();
            if (readProgressToShowList == null || readProgressToShowList.isEmpty()) {
                return false;
            } else {
                int hasReadPages = 0;
                int firstReadDate = -1;
                int maxReadPages = -1;
                for (ReadProgressToShow readProgressToShow : readProgressToShowList) {
                    hasReadPages += readProgressToShow.getReadPageNumTotal();
                    if (firstReadDate == -1 || firstReadDate > readProgressToShow.getReadDate()) {
                        firstReadDate = readProgressToShow.getReadDate();
                    }
                    if (maxReadPages < readProgressToShow.getReadPageNumTotal()) {
                        maxReadPages = readProgressToShow.getReadPageNumTotal();
                    }
                }
                hasReadPagesString = String.valueOf(hasReadPages);
                int currentDate = (int) (System.currentTimeMillis() / 1000 / 3600 / 24);
                int readDate = currentDate - firstReadDate + 1;
                if (readDate < 0) return false;

                float averagePages = hasReadPages / (readDate * 1.0f);
                averagePagesString = String.format("%.2f",averagePages);
                readProgressAdapter = new ReadProgressAdapter(getActivity(), readProgressToShowList, maxReadPages);
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean hasDate) {
            if (!hasDate) {
                hasReadTextView.setText("0");
                averagePagesTextView.setText("0");
                readTaskListView.setVisibility(View.GONE);
            } else {
                hasReadTextView.setText(hasReadPagesString);
                averagePagesTextView.setText(averagePagesString);
                readTaskListView.setAdapter(readProgressAdapter);
                readTaskListView.setVisibility(View.VISIBLE);
            }
            swipeRefresh.setRefreshing(false);
            isRefreshing = false;
        }
    }
}
