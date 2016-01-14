package com.example.zhaomeng.readbookeveryday.activity.bookdetail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.bookdetail.adapter.HasReadPageAdapter;
import com.example.zhaomeng.readbookeveryday.activity.bookdetail.adapter.ShouldReadPageAdapter;
import com.example.zhaomeng.readbookeveryday.activity.bookdetail.adapter.TotalPageRangeAdapter;
import com.example.zhaomeng.readbookeveryday.module.BookModule;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;
import com.example.zhaomeng.readbookeveryday.widget.ConfirmPopupWindow;

import java.lang.reflect.Field;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView bookTitleTextView;
    private int id;
    private BookUtil bookUtil;
    private TextView totalPageTextView;
    private TextView hasReadPageTextView;
    private ListView totalPageRangeListView;
    private ListView shouldReadPageRangeListView;
    private BookModule bookModule;
    private TotalPageRangeAdapter totalPageRangeAdapter;
    private ShouldReadPageAdapter shouldReadPageRangeAdapter;
    private HasReadPageAdapter hasReadPageRangeAdapter;
    private ListView hasReadPageRangeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        initEventListener();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        new GetBookDetailTask().execute();
    }

    private void initData() {
        bookUtil = BookUtil.getInstance(this);
    }

    private void initViews() {
        setContentView(R.layout.activity_book_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        bookTitleTextView = (TextView) findViewById(R.id.book_title);
        totalPageTextView = (TextView) findViewById(R.id.total_page);
        hasReadPageTextView = (TextView) findViewById(R.id.has_read_page);
        hasReadPageRangeListView = (ListView) findViewById(R.id.has_read_page_range_list);
        hasReadPageRangeListView.setOnItemLongClickListener(new HasReadListOnItemLongClick());
        totalPageRangeListView = (ListView) findViewById(R.id.total_page_range_list);
        shouldReadPageRangeListView = (ListView) findViewById(R.id.should_read_page_range_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initEventListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddHasReadPagesPopupWindow();
            }
        });
    }

    private void showAddHasReadPagesPopupWindow() {
        View layoutView = LayoutInflater.from(this).inflate(R.layout.popup_window_add_has_read_pages, null);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        final EditText startPage = (EditText) layoutView.findViewById(R.id.startPage);
        final EditText stopPage = (EditText) layoutView.findViewById(R.id.stopPage);
        Button cancel = (Button) layoutView.findViewById(R.id.cancel_action);
        Button add = (Button) layoutView.findViewById(R.id.add_action);
        final PopupWindow pop = new PopupWindow(layoutView,
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
                if (!checkPageRange(startPage, stopPage)) return;

                int pageStartInt = Integer.parseInt(startPage.getText().toString());
                int pageStopInt = Integer.parseInt(stopPage.getText().toString());

                pop.dismiss();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    showSetReadDataPopupWindow(pageStartInt, pageStopInt);
                } else {
                    showSetReadDataPopupWindowLollipop(pageStartInt, pageStopInt);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });
        pop.showAtLocation(layoutView, Gravity.CENTER, 0, 0);
    }

    private boolean checkPageRange(EditText startPage, EditText stopPage) {

        String startPageString = startPage.getText().toString();
        String stopPageString = stopPage.getText().toString();
        if (startPageString.equals("") || stopPageString.equals("")) {
            Toast.makeText(this, "请输入起止页", Toast.LENGTH_LONG).show();
            return false;
        }

        int startPageInt;
        int stopPageInt;
        try {
            startPageInt = Integer.parseInt(startPageString);
            stopPageInt = Integer.parseInt(stopPageString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "含有非法字符", Toast.LENGTH_LONG).show();
            return false;
        }

        if (startPageInt > stopPageInt) {
            Toast.makeText(this, "截止页应小于起始页", Toast.LENGTH_LONG).show();
            return false;
        }

        //监测输入的已读区间，是否包含在未读区间中。
        List<PageRange> shouldReadPageRangeList = bookModule.getShouldReadPageList();
        for (PageRange pageRange : shouldReadPageRangeList) {
            if (startPageInt >= pageRange.getStartPage() && stopPageInt <= pageRange.getStopPage()) {
                return true;
            }
        }
        Toast.makeText(this, "输入区间非法", Toast.LENGTH_LONG).show();
        return false;
    }

    private void showSetReadDataPopupWindow(final int pageStartInt, final int pageStopInt) {
        View layoutView = LayoutInflater.from(this).inflate(R.layout.popup_window_set_read_data, null);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        final DatePicker datePicker = (DatePicker) layoutView.findViewById(R.id.read_pages_date);
        setDatePickerDividerColor(datePicker);
        Button cancel = (Button) layoutView.findViewById(R.id.cancel_action);
        Button add = (Button) layoutView.findViewById(R.id.add_action);
        final PopupWindow pop = new PopupWindow(layoutView,
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
                bookUtil.addHasReadPageRange(bookModule, pageStartInt, pageStopInt, datePicker);
                hasReadPageRangeAdapter.notifyDataSetChanged();
                shouldReadPageRangeAdapter.notifyDataSetChanged();
                hasReadPageTextView.setText(String.valueOf(bookModule.getBookDto().getHasReadPageNum()));
                pop.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });
        pop.showAtLocation(layoutView, Gravity.CENTER, 0, 0);
    }

    private void showSetReadDataPopupWindowLollipop(final int pageStartInt, final int pageStopInt) {
        View layoutView = LayoutInflater.from(this).inflate(R.layout.popup_window_set_read_data_lollipop, null);
        layoutView.setFocusable(true);
        layoutView.setFocusableInTouchMode(true);
        final DatePicker datePicker = (DatePicker) layoutView.findViewById(R.id.read_pages_date);
        Button cancel = (Button) layoutView.findViewById(R.id.cancel_action);
        Button add = (Button) layoutView.findViewById(R.id.add_action);
        final PopupWindow pop = new PopupWindow(layoutView,
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
                bookUtil.addHasReadPageRange(bookModule, pageStartInt, pageStopInt, datePicker);
                hasReadPageRangeAdapter.notifyDataSetChanged();
                shouldReadPageRangeAdapter.notifyDataSetChanged();
                hasReadPageTextView.setText(String.valueOf(bookModule.getBookDto().getHasReadPageNum()));
                pop.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });
        pop.showAtLocation(layoutView, Gravity.CENTER, 0, 0);
    }

    /**
     * 设置时间选择器的分割线颜色
     *
     * @param datePicker
     */
    private void setDatePickerDividerColor(DatePicker datePicker) {
        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);
        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            picker.setLayoutParams(params);
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(getResources().getColor(R.color.toolBar)));
                    } catch (IllegalArgumentException | IllegalAccessException | Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private class HasReadListOnItemLongClick implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            new DeleteConfirm(BookDetailActivity.this,"确认删除已读区间？","确认","取消",bookModule.getHasReadPageList().get(i)).show();
            return true;
        }
    }

    private class DeleteConfirm extends ConfirmPopupWindow{
        private PageRange deleteReadPageRange;

        public DeleteConfirm(Context context, String hintText, String positiveButtonText, String negativeButtonText, PageRange deleteReadPageRange) {
            super(context, hintText, positiveButtonText, negativeButtonText);
            this.deleteReadPageRange = deleteReadPageRange;
        }

        @Override
        protected void OnPositiveButtonClick() {
            bookUtil.deleteHasReadPageRange(deleteReadPageRange,bookModule);
            hasReadPageRangeAdapter.notifyDataSetChanged();
            shouldReadPageRangeAdapter.notifyDataSetChanged();
        }

        @Override
        protected void OnNegativeButtonClick() {

        }
    }

    private class GetBookDetailTask extends AsyncTask<Void, Void, BookDto> {
        @Override
        protected BookDto doInBackground(Void... voids) {
            return bookUtil.getBookById(id);
        }

        @Override
        protected void onPostExecute(BookDto bookDto) {
            if (bookDto == null) {
                Toast.makeText(BookDetailActivity.this, "数据获取失败", Toast.LENGTH_LONG).show();
                return;
            }
            bookTitleTextView.setText(bookDto.getTitle());
            hasReadPageTextView.setText(String.valueOf(bookDto.getHasReadPageNum()));
            totalPageTextView.setText(String.valueOf(bookDto.getTotalPageNum()));
            bookModule = new BookModule(bookDto);

            totalPageRangeAdapter = new TotalPageRangeAdapter(BookDetailActivity.this, bookModule);
            shouldReadPageRangeAdapter = new ShouldReadPageAdapter(BookDetailActivity.this, bookModule);
            hasReadPageRangeAdapter = new HasReadPageAdapter(BookDetailActivity.this, bookModule);

            hasReadPageRangeListView.setAdapter(hasReadPageRangeAdapter);
            totalPageRangeListView.setAdapter(totalPageRangeAdapter);
            shouldReadPageRangeListView.setAdapter(shouldReadPageRangeAdapter);
        }
    }
}
