package com.zkw.drawCore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 展示层
 * 真正显示绘画结果的view
 * @author zkw
 */
public class DisplayLayer extends View {

    public DisplayLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DisplayLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisplayLayer(Context context) {
        super(context);
    }

}
