package com.example.zhaomeng.readbookeveryday.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 创建时间：2017/02/06 17:03 <br>
 * 作者：zhaomeng <br>
 * 描述：圆形图片自定义view
 */

public class CircleImageView extends ImageView {
    private static String TAG = CircleImageView.class.getSimpleName();

    private Paint paint;
    private Canvas circleCanvas;

    public CircleImageView(Context context) {
        super(context);
        initData();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        paint = new Paint();
        circleCanvas = new Canvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        Drawable drawable = getDrawable();
        if (null != drawable) {
            drawCirclePicture(drawable, canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 画圆形图片
     */
    private void drawCirclePicture(Drawable drawable, Canvas canvas) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap b = getCircleBitmap(bitmap);
        paint.reset();
        canvas.drawBitmap(b, 0, 0, paint);
    }

    /**
     * 获取圆形图片方法
     *
     * @param bitmap 传入方形图片
     * @return Bitmap  输出圆形图片
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(getWidth(),
                getHeight(), Bitmap.Config.ARGB_8888);
        circleCanvas.setBitmap(output);
        paint.setAntiAlias(true);
        circleCanvas.drawARGB(0, 0, 0, 0);
        int radius = getWidth() / 2;
        circleCanvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        circleCanvas.drawBitmap(bitmap, getImageMatrix(), paint);
        return output;
    }
}
