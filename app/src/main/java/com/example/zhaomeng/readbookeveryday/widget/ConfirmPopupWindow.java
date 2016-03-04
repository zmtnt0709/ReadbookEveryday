package com.example.zhaomeng.readbookeveryday.widget;

import android.content.Context;
import android.content.res.Resources;
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
    protected PopupWindow pop;

    public ConfirmPopupWindow(Context context, int hintText, int positiveButtonText, int negativeButtonText) {
        layoutView = LayoutInflater.from(context).inflate(R.layout.popup_window_confirm_window, null);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        TextView messageText = (TextView) layoutView.findViewById(R.id.message_text);
        Button cancel = (Button) layoutView.findViewById(R.id.cancel_action);
        Button confirm = (Button) layoutView.findViewById(R.id.confirm_action);

        Resources resources = context.getResources();
        messageText.setText(resources.getString(hintText));
        confirm.setText(resources.getString(positiveButtonText));
        cancel.setText(resources.getString(negativeButtonText));

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
        pop.showAtLocation(layoutView, Gravity.CENTER, 0, 0);
    }

    abstract protected void onPositiveButtonClick();

    abstract protected void onNegativeButtonClick();
}
