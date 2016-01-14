package com.example.zhaomeng.readbookeveryday.activity.addbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.util.BookUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaomeng on 2015/10/19.
 */
public class AddBookActivity extends AppCompatActivity {
    private static final String TAG = AddBookActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TextView addPagesTask;
    private EditText bookTitle;
    private TextView hasReadPage;
    private TextView totalPage;
    private BookUtil bookUtil;
    private List<PageRange> pageRangeList;
    private ListView pageRangeListView;
    private PageRangeAdapter pageRangeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initData();
        initView();
        initClickListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addPagesTask = (TextView) findViewById(R.id.add_pages);
        bookTitle = (EditText) findViewById(R.id.book_title);
        hasReadPage = (TextView) findViewById(R.id.has_read_page);
        totalPage = (TextView) findViewById(R.id.total_page);
        pageRangeListView = (ListView) findViewById(R.id.page_range_list_view);

        pageRangeAdapter = new PageRangeAdapter(this, pageRangeList);
        pageRangeListView.setAdapter(pageRangeAdapter);
        setSupportActionBar(toolbar);

        hasReadPage.setText("0");
        totalPage.setText("0");
    }

    private void initClickListener() {
        addPagesTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPagesPopupWindow();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        bookUtil = BookUtil.getInstance(this);
        pageRangeList = new LinkedList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Log.d(TAG, "action save");
            saveBook();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBook() {
        String bookNameString = bookTitle.getText().toString();
        if (!checkBookInput(bookNameString)) return;

        bookUtil.createBook(bookNameString, pageRangeList);
        Toast.makeText(this, bookNameString + "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean checkBookInput(String bookNameString) {
        if (bookNameString.equals("")) {
            Toast.makeText(this, "请输入书名", Toast.LENGTH_LONG).show();
            return false;
        }
        if (pageRangeList.isEmpty()) {
            Toast.makeText(this, "请添加读书区间", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showAddPagesPopupWindow() {
        View layoutView = LayoutInflater.from(this).inflate(R.layout.popup_window_add_book, null);
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
                PageRange pageRange = new PageRange(pageStartInt, pageStopInt);
                boolean addSuccess = bookUtil.addPageRangeList(pageRangeList, pageRange);
                if (!addSuccess) {
                    Toast.makeText(AddBookActivity.this, "输入区间重叠", Toast.LENGTH_LONG).show();
                    return;
                }

                updateListView();
                Toast.makeText(AddBookActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
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
        return true;
    }


    private void updateListView() {
        if (pageRangeAdapter != null) {
            int totalPages = bookUtil.getTotalPages(pageRangeList);
            totalPage.setText(String.valueOf(totalPages));
            pageRangeAdapter.notifyDataSetChanged();
        }
    }
}
