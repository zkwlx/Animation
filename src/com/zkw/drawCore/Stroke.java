package com.zkw.drawCore;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * 代表一个笔画，包括连线的点(Path)，笔画样式颜色(Paint)
 * @author zkw
 */
public class Stroke {

    private final Path mPath;
    private final Paint mPaint;

    public Stroke(Path path, Paint paint) {
        this.mPath = new Path(path);
        this.mPaint = new Paint(paint);
    }

    public Path getmPath() {
        return this.mPath;
    }

    public Paint getmPaint() {
        return this.mPaint;
    }

}
