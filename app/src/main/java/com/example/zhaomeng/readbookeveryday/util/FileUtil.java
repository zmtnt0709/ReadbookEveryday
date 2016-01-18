package com.example.zhaomeng.readbookeveryday.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by zhaomeng on 2016/1/18.
 */
public class FileUtil {
    private static FileUtil instance;

    public static FileUtil getInstance() {
        if (instance == null) {
            synchronized (FileUtil.class) {
                if (instance == null) {
                    instance = new FileUtil();
                }
            }
        }
        return instance;
    }

    private FileUtil() {

    }

//    public String saveImage(Context context, Uri contentUri) {
//        String oldImagePath = getAbsolutePathByUri(context, contentUri);
//        String newImagePath =
//    }

    public String getAbsolutePathByUri(Context context, Uri contentUri) {
        String res = null;
        String[] project = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, project, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

}
