package com.example.zhaomeng.readbookeveryday.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.zhaomeng.readbookeveryday.R;

/**
 * Created by zhaomeng on 2016/1/8.
 */
abstract public class ConfirmPopupWindow {
    private View layoutView;
    private PopupWindow pop;

    public ConfirmPopupWindow(Context context, String hintText, String positiveButtonText, String negativeButtonText) {
        layoutView = LayoutInflater.from(context).inflate(R.layout.popup_window_confirm_window, null);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        TextView messageText = (TextView) layoutView.findViewById(R.id.message_text);
        Button cancel = (Button) layoutView.findViewById(R.id.cancel_action);
        Button confirm = (Button) layoutView.findViewById(R.id.confirm_action);

        messageText.setText(hintText);
        confirm.setText(positiveButtonText);
        cancel.setText(negativeButtonText);

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
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnPositiveButtonClick();
                pop.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnNegativeButtonClick();
                pop.dismiss();
            }
        });
    }

    public void show() {
        pop.showAtLocation(layoutView, Gravity.CENTER, 0, 0);
    }

    abstract protected void OnPositiveButtonClick();

    abstract protected void OnNegativeButtonClick();
}
