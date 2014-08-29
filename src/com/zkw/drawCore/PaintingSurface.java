package com.zkw.drawCore;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PaintingSurface extends SurfaceView {

    /**
     * 用于保存下一个touch点的起点
     */
    private float tempX;
    private float tempY;

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;

    private final Paint mPaint = new Paint();
    private final Paint eraserPaint = new Paint();
    private final Path mPath = new Path();

    private final List<Stroke> frameData = new ArrayList<Stroke>();

    private boolean eraserMode = false;

    public PaintingSurface(Context context) {
        super(context);
        this.init();
    }

    public PaintingSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public PaintingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public List<Stroke> getFrameData() {
        return new ArrayList<Stroke>(this.frameData);
    }

    public void resetSurface() {
        this.eraserMode = false;
    }

    public void changeToEraser() {
        this.eraserMode = true;
    }

    public void changeToPaint() {
        this.eraserMode = false;
    }

    private void init() {
        this.surfaceHolder = this.getHolder();

        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeWidth(8);
        this.mPaint.setColor(Color.BLACK);

        this.eraserPaint.setAntiAlias(true);
        this.eraserPaint.setStyle(Style.STROKE);
        this.eraserPaint.setAlpha(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.DST_IN));
        this.eraserPaint.setStrokeWidth(35);
    }

    /**
     * 将mPath绘制到canvas
     */
    private void drawCanvas() {
        try {
            // 计算需要绘制的区域
            RectF rectF = new RectF();
            this.mPath.computeBounds(rectF, true);
            Rect rect = new Rect();
            rectF.roundOut(rect);
            this.enlargeRect(rect, 5);

            // 锁住指定区域的画布，并绘制
            this.canvas = this.surfaceHolder.lockCanvas(rect);
            if (this.canvas != null) {
                if (this.eraserMode) {
                    this.canvas.drawPath(this.mPath, this.eraserPaint);
                } else {
                    this.canvas.drawPath(this.mPath, this.mPaint);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (this.canvas != null) {
                this.surfaceHolder.unlockCanvasAndPost(this.canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            this.touchDown(event);
            return true;
        case MotionEvent.ACTION_MOVE:
            this.touchMove(event);
            return true;
        case MotionEvent.ACTION_UP:
            this.touchUp(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void touchDown(MotionEvent event) {
        this.tempX = event.getX();
        this.tempY = event.getY();

        this.mPath.moveTo(this.tempX, this.tempY);
    }

    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x - this.tempX);
        final float dy = Math.abs(y - this.tempY);

        if (dx >= 3 || dy >= 3) {

            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + this.tempX) / 2;
            float cY = (y + this.tempY) / 2;

            // 实现绘制贝塞尔平滑曲线；tempX, tempY为操作点，cX, cY为终点
            this.mPath.quadTo(this.tempX, this.tempY, cX, cY);

            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            this.tempX = x;
            this.tempY = y;
            this.drawCanvas();
        }
    }

    private void touchUp(MotionEvent event) {
        Stroke stroke;
        if (this.eraserMode) {
            stroke = new Stroke(this.mPath, this.eraserPaint);
        } else {
            stroke = new Stroke(this.mPath, this.mPaint);
        }

        this.frameData.add(stroke);

        this.mPath.rewind();
    }

    private void enlargeRect(Rect src, int de) {
        src.left -= de;
        src.top -= de;
        src.bottom += de;
        src.right += de;
    }

}