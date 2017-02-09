package com.example.zhaomeng.readbookeveryday.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * 创建时间：2017/02/09 17:45 <br>
 * 作者：zhaomeng <br>
 * 描述：仿遥控器自定义Button
 */

public class RemoteControlButton extends View {

    private Path circlePath;
    private Region circleRegion;
    private Paint paint;

    public RemoteControlButton(Context context) {
        super(context);
        init();
    }

    public RemoteControlButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RemoteControlButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePath = new Path();
        circleRegion = new Region();
        paint = new Paint();
        paint.setColor(0xff4e5268);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getWidth();
        int height = getHeight();
        int radius = width > height ? height / 2 : width / 2;
        circlePath.addCircle(width / 2, height / 2, radius, Path.Direction.CW);
        Region globalRegion = new Region(0, 0, w, h);
        circleRegion.setPath(circlePath, globalRegion);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path circle = circlePath; // 绘制圆
        canvas.drawPath(circle, paint);
    }
}