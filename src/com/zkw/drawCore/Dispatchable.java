package com.zkw.drawCore;

import android.graphics.Paint;
import android.graphics.Path;

public interface Dispatchable {
    public void dispatchDraw(Path path, Paint paint);
}
