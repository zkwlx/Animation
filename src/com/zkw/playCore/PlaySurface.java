package com.zkw.playCore;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zkw.drawCore.Stroke;

public class PlaySurface extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {

    private List<List<Stroke>> allData;

    private Thread playThread;

    private SurfaceHolder surfaceHolder;

    private Canvas canvas;

    private Paint clearPaint;

    public PlaySurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public PlaySurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PlaySurface(Context context) {
        super(context);
        this.init();
    }

    private void init() {
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);

        this.clearPaint = new Paint();
        this.clearPaint.setAlpha(0);
        this.clearPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.DST_IN));
        this.clearPaint.setAntiAlias(true);
    }

    public void setPlayData(List<List<Stroke>> data) {
        this.allData = data;
    }

    public void startPlay() {
        this.playThread = new Thread(this);
        this.playThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.playThread.interrupt();
    }

    @Override
    public void run() {
        while (true) {
            for (List<Stroke> strokes : this.allData) {
                // 绘制一页的数据
                for (Stroke stroke : strokes) {
                    // XXX 这里会闪屏！
                    this.drawCanvas(stroke.getmPath(), stroke.getmPaint());
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                this.clearPage();
            }
        }
    }

    /**
     * 将mPath绘制到canvas
     */
    private void drawCanvas(Path path, Paint paint) {
        try {
            // 计算需要绘制的区域
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            Rect rect = new Rect();
            rectF.roundOut(rect);

            // 锁住指定区域的画布，并绘制
            this.canvas = this.surfaceHolder.lockCanvas();
            if (this.canvas != null) {
                this.canvas.drawPath(path, paint);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (this.canvas != null) {
                this.surfaceHolder.unlockCanvasAndPost(this.canvas);
            }
        }
    }

    private void clearPage() {
        this.canvas = this.surfaceHolder.lockCanvas();
        this.canvas.drawPaint(this.clearPaint);
        this.surfaceHolder.unlockCanvasAndPost(this.canvas);
    }

}
