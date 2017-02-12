package com.example.zhaomeng.readbookeveryday.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.zhaomeng.readbookeveryday.R;
import com.example.zhaomeng.readbookeveryday.util.DisplayUtil;

/**
 * 创建时间：2017/02/09 17:45 <br>
 * 作者：zhaomeng <br>
 * 描述：仿遥控器自定义Button
 */

public class RemoteControlButton extends View {

    private static final String TAG = RemoteControlButton.class.getSimpleName();

    private static final int TOUCH_REGION_NONE = 0;
    private static final int TOUCH_REGION_CENTER = 1;
    private static final int TOUCH_REGION_RIGHT = 2;
    private static final int TOUCH_REGION_BOTTOM = 3;
    private static final int TOUCH_REGION_LEFT = 4;
    private static final int TOUCH_REGION_TOP = 5;

    private Paint paint;
    private Paint textPaint;
    private int colorNormal;
    private int colorPressed;
    private Path centerPath;
    private Region centerRegion;
    private Region globalRegion;
    private RectF bigSector;
    private RectF smallSector;
    private Path rightPath;
    private Region rightRegion;
    private Path bottomPath;
    private Region bottomRegion;
    private Path leftPath;
    private Region leftRegion;
    private Path topPath;
    private Region topRegion;
    private int touchRegion;
    boolean intoTouchMode;

    private OnMenuClickListener listener;
    private float textTop_x;
    private float textTop_y;
    private float textBottom_x;
    private float textBottom_y;

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
        colorNormal = getContext().getResources().getColor(R.color.button_normal);
        colorPressed = getContext().getResources().getColor(R.color.button_pressed);
        paint = new Paint();
        paint.setColor(colorNormal);
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), 16));
        textPaint.setColor(getContext().getResources().getColor(R.color.text_color_white));
        globalRegion = new Region();
        bigSector = new RectF();
        smallSector = new RectF();
        centerPath = new Path();
        centerRegion = new Region();
        rightPath = new Path();
        rightRegion = new Region();
        bottomPath = new Path();
        bottomRegion = new Region();
        leftPath = new Path();
        leftRegion = new Region();
        topPath = new Path();
        topRegion = new Region();
    }

    public void setMenuClickListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float event_x = event.getX();
        float event_y = event.getY();
        int currentTouchRegion;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentTouchRegion = getTouchRegion((int) event_x, (int) event_y);
                if (currentTouchRegion != TOUCH_REGION_NONE) {
                    intoTouchMode = true;
                    touchRegion = currentTouchRegion;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!intoTouchMode) return true;

                currentTouchRegion = getTouchRegion((int) event_x, (int) event_y);
                if (currentTouchRegion == TOUCH_REGION_NONE || currentTouchRegion != touchRegion) {
                    intoTouchMode = false;
                    touchRegion = TOUCH_REGION_NONE;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (!intoTouchMode) return true;

                if (listener != null) {
                    switch (touchRegion) {
                        case TOUCH_REGION_CENTER:
                            listener.onCenterCliched();
                            break;
                        case TOUCH_REGION_RIGHT:
                            listener.onRightCliched();
                            break;
                        case TOUCH_REGION_BOTTOM:
                            listener.onBottomCliched();
                            break;
                        case TOUCH_REGION_LEFT:
                            listener.onLeftCliched();
                            break;
                        case TOUCH_REGION_TOP:
                            listener.onTopCliched();
                            break;
                    }
                }
                intoTouchMode = false;
                touchRegion = TOUCH_REGION_NONE;
                invalidate();
                return true;
        }

        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout: changed = " + changed + ";left = " + left + ";top = " + top
                + ";right = " + right + ";bottom = " + bottom);
        if (!changed) return;

        int width = right - left;
        int height = bottom - top;
        int radius = width > height ? height / 2 : width / 2;
        //onDraw时画布左上角的起点是0,0
        int center_x = width / 2;
        int center_y = height / 2;
        int bigSweepAngle = 84;
        int smallSweepAngle = -80;

        globalRegion.set(0, 0, width, height);
        bigSector.set(center_x - radius, center_y - radius,
                center_x + radius, center_y + radius);
        smallSector.set(center_x - 0.5f * radius, center_y - 0.5f * radius,
                center_x + 0.5f * radius, center_y + 0.5f * radius);

        //中间圆形
        centerPath.addCircle(center_x, center_y, 0.4f * radius, Path.Direction.CW);
        centerRegion.setPath(centerPath, globalRegion);
        // 右边圆环
        rightPath.addArc(bigSector, -42, bigSweepAngle);
        rightPath.arcTo(smallSector, 40, smallSweepAngle);
        rightPath.close();
        rightRegion.setPath(rightPath, globalRegion);
        //底部圆环
        bottomPath.addArc(bigSector, 48, bigSweepAngle);
        bottomPath.arcTo(smallSector, 130, smallSweepAngle);
        bottomPath.close();
        bottomRegion.setPath(bottomPath, globalRegion);
        textBottom_x = center_x;
        textBottom_y = center_y + 0.8f * radius;
        //左边圆环
        leftPath.addArc(bigSector, 138, bigSweepAngle);
        leftPath.arcTo(smallSector, 220, smallSweepAngle);
        leftPath.close();
        leftRegion.setPath(leftPath, globalRegion);
        //顶部圆环
        topPath.addArc(bigSector, 228, bigSweepAngle);
        topPath.arcTo(smallSector, 310, smallSweepAngle);
        topPath.close();
        topRegion.setPath(topPath, globalRegion);
        textTop_x = center_x;
        textTop_y = center_y - 0.7f * radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        if (touchRegion == TOUCH_REGION_CENTER) {
            paint.setColor(colorPressed);
        } else {
            paint.setColor(colorNormal);
        }
        canvas.drawPath(centerPath, paint);
        if (touchRegion == TOUCH_REGION_RIGHT) {
            paint.setColor(colorPressed);
        } else {
            paint.setColor(colorNormal);
        }
        canvas.drawPath(rightPath, paint);

        if (touchRegion == TOUCH_REGION_BOTTOM) {
            paint.setColor(colorPressed);
        } else {
            paint.setColor(colorNormal);
        }
        canvas.drawPath(bottomPath, paint);

        if (touchRegion == TOUCH_REGION_LEFT) {
            paint.setColor(colorPressed);
        } else {
            paint.setColor(colorNormal);
        }
        canvas.drawPath(leftPath, paint);

        if (touchRegion == TOUCH_REGION_TOP) {
            paint.setColor(colorPressed);
        } else {
            paint.setColor(colorNormal);
        }
        canvas.drawPath(topPath, paint);
        canvas.drawText(getContext().getText(R.string.save_button_text).toString(),
                textTop_x, textTop_y, textPaint);
        canvas.drawText(getContext().getText(R.string.restore_button_text).toString(),
                textBottom_x, textBottom_y, textPaint);
    }

    /**
     * 获取被按下的区域
     */
    private int getTouchRegion(int event_x, int event_y) {
        if (centerRegion.contains(event_x, event_y)) {
            return TOUCH_REGION_CENTER;
        } else if (rightRegion.contains(event_x, event_y)) {
            return TOUCH_REGION_RIGHT;
        } else if (bottomRegion.contains(event_x, event_y)) {
            return TOUCH_REGION_BOTTOM;
        } else if (leftRegion.contains(event_x, event_y)) {
            return TOUCH_REGION_LEFT;
        } else if (topRegion.contains(event_x, event_y)) {
            return TOUCH_REGION_TOP;
        }
        return TOUCH_REGION_NONE;
    }

    public interface OnMenuClickListener {
        void onCenterCliched();

        void onTopCliched();

        void onRightCliched();

        void onBottomCliched();

        void onLeftCliched();
    }
}