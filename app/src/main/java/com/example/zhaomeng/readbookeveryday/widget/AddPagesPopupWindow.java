package com.example.zhaomeng.readbookeveryday.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.zhaomeng.readbookeveryday.R;

/**
 * Created by zhaomeng on 2016/1/20.
 */
abstract public class AddPagesPopupWindow {
    protected Context context;
    private View layoutView;
    private EditText startPage;
    private EditText stopPage;
    protected PopupWindow pop;

    public AddPagesPopupWindow(Context context) {
        this.context = context;
        layoutView = LayoutInflater.from(context).inflate(R.layout.popup_window_add_has_read_pages, null);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        startPage = (EditText) layoutView.findViewById(R.id.startPage);
        stopPage = (EditText) layoutView.findViewById(R.id.stopPage);
        Button cancel = (Button) layoutView.findViewById(R.id.cancel_action);
        Button add = (Button) layoutView.findViewById(R.id.add_action);
        pop = new PopupWindow(layoutView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, false);
        pop.setFocusable(true);
        layoutView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    pop.dismiss();
                    return true;
                }
                return false;
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPositiveButtonClick();
                pop.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNegativeButtonClick();
                pop.dismiss();
            }
        });
    }

    public void show() {
        stopPage.setText("");
        startPage.setText("");
        startPage.requestFocus();
        pop.showAtLocation(layoutView, Gravity.CENTER, 0, 0);
    }

    abstract protected void onPositiveButtonClick();

    abstract protected void onNegativeButtonClick();

    /**
     * 在获取起始页和截止页钱检查输入是否合法
     *
     * @return
     */
    protected boolean checkPageRange() {

        String startPageString = startPage.getText().toString();
        String stopPageString = stopPage.getText().toString();
        if (startPageString.equals("") || stopPageString.equals("")) {
            Toast.makeText(context, "请输入起止页", Toast.LENGTH_LONG).show();
            return false;
        }

        int startPageInt;
        int stopPageInt;
        try {
            startPageInt = Integer.parseInt(startPageString);
            stopPageInt = Integer.parseInt(stopPageString);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "含有非法字符", Toast.LENGTH_LONG).show();
            return false;
        }

        if (startPageInt > stopPageInt) {
            Toast.makeText(context, "截止页应小于起始页", Toast.LENGTH_LONG).show();
            return false;
        }

        //其他监测
        return otherCheck(startPageInt, stopPageInt);
    }

    abstract protected boolean otherCheck(int startPageInt, int stopPageInt);

    public int getStartPage() {
        return Integer.parseInt(startPage.getText().toString());
    }

    public int getStopPage() {
        return Integer.parseInt(stopPage.getText().toString());
    }
}
