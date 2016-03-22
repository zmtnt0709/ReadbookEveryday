package com.example.zhaomeng.readbookeveryday.activity.save;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.util.FileUtil;

public class SaveAndRestoreActivity extends AppCompatActivity {
    private static String TAG = SaveAndRestoreActivity.class.getSimpleName();
    private static int REQUEST_CODE_RESTORE_FILE_PATH = 1;
    private FileUtil fileUtil;
    private Toolbar toolbar;
    private Button saveButton;
    private Button restoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_and_restore);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        fileUtil = FileUtil.getInstance();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        saveButton = (Button) findViewById(R.id.save_button);
        restoreButton = (Button) findViewById(R.id.restore_button);
    }

    private void initEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_CODE_RESTORE_FILE_PATH);
            }
        });
    }

    private void saveData() {
        String savePath = fileUtil.getSavePath();
        String saveFileName = fileUtil.getSaveFileName();
        boolean success = fileUtil.saveData(this, savePath, saveFileName);
        if (success) {
            Toast.makeText(this, "备份成功，文件已备份至\n\r" + savePath + saveFileName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "备份失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESTORE_FILE_PATH) {
            restoreData(data.getData().getPath());
        }
    }

    private void restoreData(String filePath) {
        boolean success = fileUtil.restoreData(this, filePath);
        if(success){
            Toast.makeText(this, "还原成功 重启应用后生效", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "还原失败", Toast.LENGTH_LONG).show();
        }
    }
}
