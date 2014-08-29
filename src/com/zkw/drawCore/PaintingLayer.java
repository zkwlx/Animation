package com.zkw.drawCore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 绘画层
 * 收集用户绘画数据，并将数据发给展示层
 * @author zkw
 */
public class PaintingLayer extends View {

    private final Path mPath = new Path();

    private final Path mDispathPath = new Path();

    private Dispatchable mDispatcher;

    private Paint mLocalPaint;

    private float tempX, tempY;

    private final int TOUCH_INTERVAL = 2;

    private long startPathTime;

    private static long INTERVAL_PATH_TIME = 600;// 600ms

    public PaintingLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public PaintingLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PaintingLayer(Context context) {
        super(context);
        this.init();
    }

    public void clear() {
        this.mPath.reset();
        this.invalidate();
    }

    public void onRelease() {
        this.mPath.reset();
    }

    public void setDispatcher(Dispatchable dispatcher) {
        this.mDispatcher = dispatcher;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(this.mPath, this.mLocalPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            this.onActionDown(event);
            break;
        case MotionEvent.ACTION_MOVE:
            this.onActionMove(event);
            break;
        case MotionEvent.ACTION_UP:
            this.onActionUp(event.getX(), event.getY());
            break;
        }

        this.refreshView();

        return true;
    }

    private void onActionDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        this.startPathTime = System.currentTimeMillis();

        this.mPath.moveTo(x, y);

        this.tempX = x;
        this.tempY = y;

    }

    private void onActionMove(MotionEvent event) {

        // 当性能低，画很多线时，需要这个来使画线更平滑
        for (int i = 0; i < event.getHistorySize(); i++) {
            float ex = event.getHistoricalX(0, i);
            float ey = event.getHistoricalY(0, i);
            this.onMove(ex, ey);
        }

        float x = event.getX();
        float y = event.getY();

        this.onMove(x, y);

    }

    private void onMove(float x, float y) {
        // TODO 这里可以根据移动速度动态计算间隔距离
        float dx = Math.abs(x - this.tempX);
        float dy = Math.abs(y - this.tempY);
        boolean isInInterval = (dx >= this.TOUCH_INTERVAL || dy >= this.TOUCH_INTERVAL);
        long currentTime = System.currentTimeMillis();

        if (isInInterval) {
            this.mPath.quadTo(this.tempX, this.tempY, (x + this.tempX) / 2,
                    (y + this.tempY) / 2);
            this.tempX = x;
            this.tempY = y;
        }

        // if ((currentTime - this.startPathTime) > INTERVAL_PATH_TIME
        // && isInInterval) {
        // this.onActionUp(x, y);
        // this.mPath.moveTo(x, y);
        // this.startPathTime = System.currentTimeMillis();
        // }
    }

    private void onActionUp(float x, float y) {
        this.mPath.lineTo(x, y);

        this.mDispathPath.set(this.mPath);
        if (this.mDispatcher != null) {
            this.mDispatcher.dispatchDraw(this.mDispathPath, this.mLocalPaint);
        }

        this.mPath.rewind();

    }

    private void refreshView() {

        if (this.mPath.isEmpty()) {
            return;
        }

        RectF rectF = new RectF();
        Rect invalidateArea = new Rect();

        this.mPath.computeBounds(rectF, true);

        rectF.roundOut(invalidateArea);
        this.enlargeRect(invalidateArea, 3);

        this.invalidate(invalidateArea);

    }

    private void init() {
        this.initLocalPaint();
        this.mPath.setFillType(FillType.WINDING);
    }

    private void enlargeRect(Rect src, int de) {
        src.left -= de;
        src.top -= de;
        src.bottom += de;
        src.right += de;
    }

    private void initLocalPaint() {
        this.mLocalPaint = new Paint();
        this.mLocalPaint.setAntiAlias(true);
        this.mLocalPaint.setDither(true);
        this.mLocalPaint.setColor(Color.BLACK);
        this.mLocalPaint.setStyle(Paint.Style.STROKE);
        this.mLocalPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mLocalPaint.setStrokeCap(Paint.Cap.ROUND);
        // this.mLocalPaint.setShadowLayer(2f, 1, 1, Color.GRAY);
        this.mLocalPaint.setStrokeWidth(9);
    }

}
