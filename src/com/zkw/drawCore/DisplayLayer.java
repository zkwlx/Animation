package com.zkw.drawCore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 展示层
 * 真正显示绘画结果的view
 * @author zkw
 */
public class DisplayLayer extends View implements Dispatchable {

    private Bitmap mBitmapContent;

    private Canvas mCanvas;

    private Paint tempPaint;

    private Paint eraserPaint;

    public DisplayLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    public DisplayLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public DisplayLayer(Context context) {
        super(context);
        this.initView();
    }

    public void clear() {
        this.mCanvas.drawPaint(this.eraserPaint);
        this.invalidate();
    }

    private void initView() {
        this.setDrawingCacheEnabled(false);

        this.tempPaint = new Paint();
        this.tempPaint.setAntiAlias(true);
        this.tempPaint.setDither(true);
        this.tempPaint.setColor(Color.RED);
        this.tempPaint.setStyle(Paint.Style.STROKE);
        this.tempPaint.setStrokeJoin(Paint.Join.ROUND);
        this.tempPaint.setStrokeCap(Paint.Cap.ROUND);
        // this.mLocalPaint.setShadowLayer(2f, 1, 1, Color.GRAY);
        this.tempPaint.setStrokeWidth(3);

        this.eraserPaint = new Paint();
        this.eraserPaint.setAlpha(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.DST_IN));
        this.eraserPaint.setAntiAlias(true);
    }

    @Override
    public void dispatchDraw(Path path, Paint paint) {

        this.mCanvas.drawPath(path, this.tempPaint);
        path.reset();

        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Rect dstRect = new Rect();
        bounds.roundOut(dstRect);
        this.enlargeRect(dstRect, 3);

        this.invalidate(dstRect);

    }

    private void enlargeRect(Rect src, int de) {
        src.left -= de;
        src.top -= de;
        src.bottom += de;
        src.right += de;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (this.mBitmapContent != null && !this.mBitmapContent.isRecycled()) {
            canvas.drawBitmap(this.mBitmapContent, 0, 0, null);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.init();
    }

    private void init() {
        this.mBitmapContent = Bitmap.createBitmap(this.getWidth(),
                this.getHeight(), Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mBitmapContent);

    }
}
