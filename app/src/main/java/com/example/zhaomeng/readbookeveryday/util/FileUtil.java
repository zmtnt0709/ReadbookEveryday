package com.example.zhaomeng.readbookeveryday.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhaomeng on 2016/1/18.
 */
public class FileUtil {
    private static String TAG = FileUtil.class.getSimpleName();
    private static FileUtil instance;
    private static String SAVE_PATH = "/ReadBookEveryDay/save/";
    private static String NEW_LINE = "\n";
    private static String BOOK_DTO = "BookDto";
    private static String READ_PROGRESS_DTO = "ReadProgressDto";
    public static String FILE_SCHEME = "file://";

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
        opt.inSampleSize = height / size;
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
        //如果返回的就是Path直接返回
        if(contentUri != null&& new File(contentUri.getPath()).exists()){
          return contentUri.getPath();
        }
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

    public String getSavePath() {
        File sdCardFile = Environment.getExternalStorageDirectory();
        return sdCardFile.getPath() + SAVE_PATH;
    }

    public String getSaveFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String date = sdf.format(new java.util.Date());
        return "saveFile_" + date + ".bak";
    }

    public boolean saveData(Context context, String savePath, String fileName) {
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs())
                return false;
        }
        File saveFile = new File(savePath + fileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(saveFile);
            saveBookDto(context, fileWriter);
            saveReadProgressDto(context, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(fileWriter);
        }
        return true;
    }

    private void saveBookDto(Context context, FileWriter fileWriter) throws IOException {
        fileWriter.write(BOOK_DTO);
        fileWriter.write(NEW_LINE);
        BookUtil bookUtil = BookUtil.getInstance(context.getApplicationContext());
        List<BookDto> bookList = bookUtil.getBookDao().getAllBookList();
        JacksonConverterUtil jacksonConverterUtil = JacksonConverterUtil.getInstance();
        for (BookDto bookDto : bookList) {
            fileWriter.write(jacksonConverterUtil.toJsonString(bookDto));
            fileWriter.write(NEW_LINE);
        }
    }

    private void saveReadProgressDto(Context context, FileWriter fileWriter) throws IOException {
        fileWriter.write(READ_PROGRESS_DTO);
        fileWriter.write(NEW_LINE);
        ReadProgressUtil readProgressUtil = ReadProgressUtil.getInstance(context.getApplicationContext());
        List<ReadProgressDto> readProgressDtoList = readProgressUtil.getReadProgressDao().getAllReadProgress();
        JacksonConverterUtil jacksonConverterUtil = JacksonConverterUtil.getInstance();
        for (ReadProgressDto readProgressDto : readProgressDtoList) {
            fileWriter.write(jacksonConverterUtil.toJsonString(readProgressDto));
            fileWriter.write(NEW_LINE);
        }
    }


    public boolean restoreData(Context context, String filePath) {
        int index = filePath.indexOf(":");
        if (index > 0) {
            String subString = filePath.substring(index + 1);
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + subString;
        }
        //适配最新小米系统
        if(filePath.contains("/external_files")){
            filePath = filePath.replace("/external_files", Environment.getExternalStorageDirectory().getPath());
        }
        File restoreFile = new File(filePath);
        if (!restoreFile.exists()) return false;

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(restoreFile);
            bufferedReader = new BufferedReader(fileReader);
            restoreDB(context, bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(fileReader);
            close(bufferedReader);
        }
        return true;
    }

    private boolean restoreDB(Context context, BufferedReader bufferedReader) throws IOException {
        String aLine;
        String DBFlag = "";
        BookUtil bookUtil = BookUtil.getInstance(context.getApplicationContext());
        ReadProgressUtil readProgressUtil = ReadProgressUtil.getInstance(context.getApplicationContext());
        JacksonConverterUtil jacksonConverterUtil = JacksonConverterUtil.getInstance();
        BookDto bookDto;
        ReadProgressDto readProgressDto;
        List<BookDto> bookDtoList = new ArrayList<>();
        List<ReadProgressDto> readProgressDtoList = new ArrayList<>();
        while ((aLine = bufferedReader.readLine()) != null) {
            if (aLine.equals(BOOK_DTO)) {
                DBFlag = BOOK_DTO;
            } else if (aLine.equals(READ_PROGRESS_DTO)) {
                DBFlag = READ_PROGRESS_DTO;
            } else if (DBFlag.equals(BOOK_DTO)) {
                bookDto = jacksonConverterUtil.jsonStringToObject(aLine, BookDto.class);
                if (bookDto == null) return false;

                bookDtoList.add(bookDto);
            } else if (DBFlag.equals(READ_PROGRESS_DTO)) {
                readProgressDto = jacksonConverterUtil.jsonStringToObject(aLine, ReadProgressDto.class);
                if (readProgressDto == null) return false;

                readProgressDtoList.add(readProgressDto);
            } else {
                return false;
            }
        }
        Collections.sort(bookDtoList, new SortBookList());
        Collections.sort(readProgressDtoList, new SortReadProgressList());
        for (BookDto book : bookDtoList) {
            bookUtil.createOrUpdateBookDto(book);
        }
        for (ReadProgressDto readProgress : readProgressDtoList) {
            readProgressUtil.createOrUpdateReadProgress(readProgress);
        }
        return true;
    }


    private void close(Closeable closeable) {
        if (null == closeable) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SortBookList implements Comparator<BookDto> {

        @Override
        public int compare(BookDto left, BookDto right) {
            if (left.getId() < right.getId()) {
                return -1;
            } else if (left.getId() > right.getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private class SortReadProgressList implements Comparator<ReadProgressDto> {

        @Override
        public int compare(ReadProgressDto left, ReadProgressDto right) {
            if (left.getId() < right.getId()) {
                return -1;
            } else if (left.getId() > right.getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
