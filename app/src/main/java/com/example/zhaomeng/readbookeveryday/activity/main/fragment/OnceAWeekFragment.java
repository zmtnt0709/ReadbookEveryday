package com.example.zhaomeng.readbookeveryday.activity.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.zhaomeng.readbookeveryday.R;

/**
 * Created by zhaomeng on 2016/1/19.
 */
public class OnceAWeekFragment extends Fragment {
    private ListView onceAWeekListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_once_a_week, container, false);
        onceAWeekListView = (ListView) rootView.findViewById(R.id.once_a_week_list);
        onceAWeekListView.addHeaderView(inflater.inflate(R.layout.list_header_once_a_week, null));
        return rootView;
    }
}
