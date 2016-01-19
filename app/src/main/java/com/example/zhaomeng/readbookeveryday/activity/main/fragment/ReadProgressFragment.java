package com.example.zhaomeng.readbookeveryday.activity.main.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.main.adapter.ReadProgressAdapter;
import com.example.zhaomeng.readbookeveryday.module.ReadProgressToShow;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;

import java.util.List;

/**
 * Created by zhaomeng on 2015/11/30.
 */
public class ReadProgressFragment extends Fragment {
    private static final String TAG = ReadProgressFragment.class.getSimpleName();

    private TextView hasReadTextView;
    private TextView averagePagesTextView;
    private ListView readTaskListView;

    private BookUtil bookUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_read_progress, container, false);
        initView(rootView);
        initDate();
        new GetReadProgressTask().execute();
        return rootView;
    }

    private void initView(View rootView) {
        hasReadTextView = (TextView) rootView.findViewById(R.id.has_read);
        averagePagesTextView = (TextView) rootView.findViewById(R.id.average_page);
        readTaskListView = (ListView) rootView.findViewById(R.id.read_progress_list_view);
    }

    private void initDate() {
        bookUtil = BookUtil.getInstance(getActivity());
    }

    class GetReadProgressTask extends AsyncTask<Void, Void, List<ReadProgressToShow>> {

        @Override
        protected List<ReadProgressToShow> doInBackground(Void... voids) {
            return bookUtil.getAllReadProgressToShow();
        }

        @Override
        protected void onPostExecute(List<ReadProgressToShow> readProgressToShowList) {
            if (readProgressToShowList == null || readProgressToShowList.isEmpty()) return;

            updateTextView(readProgressToShowList);

        }

        private void updateTextView(List<ReadProgressToShow> readProgressToShowList) {

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
            hasReadTextView.setText(String.valueOf(hasReadPages));
            int currentDate = (int) (System.currentTimeMillis() / 1000 / 3600 / 24);
            int readDate = currentDate - firstReadDate + 1;
            if (readDate < 0) return;

            int averagePages = hasReadPages / readDate;
            averagePagesTextView.setText(String.valueOf(averagePages));

            ReadProgressAdapter readProgressAdapter = new ReadProgressAdapter(getActivity(), readProgressToShowList, maxReadPages);
            readTaskListView.setAdapter(readProgressAdapter);
        }
    }
}
