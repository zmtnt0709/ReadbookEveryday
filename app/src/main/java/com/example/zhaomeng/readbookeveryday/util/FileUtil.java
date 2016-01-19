package com.example.zhaomeng.readbookeveryday.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public String saveImage(Context context, Uri contentUri) {
        String oldImagePath = getAbsolutePathByUri(context, contentUri);
        String newImagePath = getNewImagePath(context, oldImagePath);
        if (newImagePath == null) return null;

        File file = new File(newImagePath);
        if (file.exists()) {
            return newImagePath;
        } else {
            if (compressImage(oldImagePath, file)) {
                return newImagePath;
            } else {
                return null;
            }
        }
    }

    /**
     * 压缩图片
     *
     * @param oldImagePath 图片路径
     * @return
     */
    private boolean compressImage(String oldImagePath, File newImageFile) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(oldImagePath, opt);
        int height = opt.outHeight;
        int size = 200;
        opt.inSampleSize = height/size;
        opt.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFile(oldImagePath, opt);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newImageFile);
            baos.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(fos);
        }
        return true;
    }

    private String getNewImagePath(Context context, String oldImagePath) {
        String newImagePath;
        File externalFile = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFile == null) return null;

        newImagePath = externalFile.getAbsolutePath();
        int index = oldImagePath.lastIndexOf(File.separator);
        String fileName = oldImagePath.substring(index);
        newImagePath = newImagePath + fileName;
        return newImagePath;
    }

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

    private void close(Closeable closeable) {
        if (null == closeable) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
