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

import com.example.zhaomeng.readbookeveryday.util.DisplayUtil;

/**
 * 创建时间：2017/02/06 17:03 <br>
 * 作者：zhaomeng <br>
 * 描述：圆形图片自定义view
 */

public class CircleImageView extends ImageView {
    private static String TAG = CircleImageView.class.getSimpleName();

    private static int RING_WIDTH = 3;//外圈圆环宽度，单位dp；

    private int centerX;
    private int centerY;
    private int ringRadius;
    private int imageRadius;
    private Paint paintBitmap;
    private Paint paintImage;
    private Paint paintRing;
    private Canvas circleCanvas;
    private Bitmap circleBitmap;

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
        paintBitmap = new Paint();
        paintImage = new Paint();
        paintImage.setAntiAlias(true);
        paintRing = new Paint();
        paintRing.setAntiAlias(true);
        paintRing.setColor(0xffffffff);
        paintRing.setStrokeWidth(DisplayUtil.dip2px(getContext(), RING_WIDTH));
        paintRing.setStyle(Paint.Style.STROKE);
        circleCanvas = new Canvas();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged");
        centerX = w / 2;
        centerY = h / 2;
        int radius = w > h ? h / 2 : w / 2;
        ringRadius = radius - DisplayUtil.dip2px(getContext(), RING_WIDTH) / 2;
        imageRadius = radius - DisplayUtil.dip2px(getContext(), RING_WIDTH) + 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawCirclePicture(drawable, canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 画圆形图片
     */
    private void drawCirclePicture(Drawable drawable, Canvas canvas) {
        //画圆形图片
        circleBitmap = getCircleBitmap(drawable);
        canvas.drawBitmap(circleBitmap, 0, 0, paintImage);
        //画白色圆环
        canvas.drawCircle(centerX, centerY, ringRadius, paintRing);
    }

    /**
     * 获取圆形图片方法
     *
     * @param drawable 传入方形图片
     * @return Bitmap  输出圆形图片
     */
    private Bitmap getCircleBitmap(Drawable drawable) {
        Log.d(TAG, "width = " + getWidth() + ", height = " + getHeight());
        Bitmap input = ((BitmapDrawable) drawable).getBitmap();
        Bitmap output = Bitmap.createBitmap(getWidth(),
                getHeight(), Bitmap.Config.ARGB_8888);
        paintBitmap.reset();
        paintBitmap.setAntiAlias(true);
        circleCanvas.setBitmap(output);
        circleCanvas.drawARGB(0, 0, 0, 0);
        circleCanvas.drawCircle(centerX, centerY, imageRadius, paintBitmap);
        paintBitmap.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        circleCanvas.drawBitmap(input, getImageMatrix(), paintBitmap);
        return output;
    }
}
