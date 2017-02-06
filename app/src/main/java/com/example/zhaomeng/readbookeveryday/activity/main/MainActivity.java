package com.example.zhaomeng.readbookeveryday.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.activity.addbook.AddBookActivity;
import com.example.zhaomeng.readbookeveryday.activity.main.fragment.BookListFragment;
import com.example.zhaomeng.readbookeveryday.activity.main.fragment.OnceAWeekFragment;
import com.example.zhaomeng.readbookeveryday.activity.main.fragment.ReadProgressFragment;
import com.example.zhaomeng.readbookeveryday.activity.save.SaveAndRestoreActivity;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String BOOK_LIST_FRAGMENT_TAG = "book_list_fragment_tag";
    private static final String READ_PROGRESS_FRAGMENT_TAG = "read_progress_fragment_tag";
    private static final String ONCE_A_WEEK_FRAGMENT_TAG = "once_a_week_fragment_tag";

    private NavigationView navigationView;
    private FloatingActionButton fab;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private BookListFragment bookListFragment;
    private ReadProgressFragment readProgressFragment;
    private OnceAWeekFragment onceAWeekFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    private void initViews(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnFloatingActionButtonClick());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        initFragment(savedInstanceState);
    }

    private void initFragment(Bundle savedInstanceState) {
        Log.d(TAG, "click nav_book_list");
        if (savedInstanceState != null) {
            bookListFragment = (BookListFragment) fragmentManager.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);
            readProgressFragment = (ReadProgressFragment) fragmentManager.findFragmentByTag(READ_PROGRESS_FRAGMENT_TAG);
            onceAWeekFragment = (OnceAWeekFragment) fragmentManager.findFragmentByTag(ONCE_A_WEEK_FRAGMENT_TAG);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        if (bookListFragment == null) {
            bookListFragment = new BookListFragment();
        }
        fragmentTransaction.add(R.id.main_fragment, bookListFragment, BOOK_LIST_FRAGMENT_TAG);
        currentFragment = bookListFragment;
        fragmentTransaction.show(bookListFragment);
        fragmentTransaction.commit();
        navigationView.setCheckedItem(R.id.nav_book_list);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        if (id == R.id.nav_book_list) {
            Log.d(TAG, "click nav_book_list");
            if (bookListFragment == null) {
                bookListFragment = new BookListFragment();
                fragmentTransaction.add(R.id.main_fragment, bookListFragment, BOOK_LIST_FRAGMENT_TAG);
            }

            currentFragment = bookListFragment;
            fragmentTransaction.show(bookListFragment);
            fragmentTransaction.commit();
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_read_progress) {
            Log.d(TAG, "click nav_read_progress");
            if (readProgressFragment == null) {
                readProgressFragment = new ReadProgressFragment();
                fragmentTransaction.add(R.id.main_fragment, readProgressFragment, READ_PROGRESS_FRAGMENT_TAG);
            }
            currentFragment = readProgressFragment;
            fragmentTransaction.show(readProgressFragment);
            fragmentTransaction.commit();
            fab.setVisibility(View.GONE);
        } else if (id == R.id.nav_once_a_week) {
            Log.d(TAG, "click nav_once_a_week");
            if (onceAWeekFragment == null) {
                onceAWeekFragment = new OnceAWeekFragment();
                fragmentTransaction.add(R.id.main_fragment, onceAWeekFragment, ONCE_A_WEEK_FRAGMENT_TAG);
            }
            currentFragment = onceAWeekFragment;
            fragmentTransaction.show(onceAWeekFragment);
            fragmentTransaction.commit();
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_save_restore) {
            Intent intent = new Intent(MainActivity.this, SaveAndRestoreActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class OnFloatingActionButtonClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d(TAG, "OnFloatingActionButtonClick");
            Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
            startActivity(intent);
        }
    }
}
