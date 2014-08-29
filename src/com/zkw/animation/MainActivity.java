package com.zkw.animation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;

import com.zkw.drawCore.PaintingSurface;
import com.zkw.drawCore.Stroke;
import com.zkw.playCore.PlaySurface;

public class MainActivity extends Activity {

    private PaintingSurface surfaceView;

    private PlaySurface playSurface;

    private final List<List<Stroke>> allData = new ArrayList<List<Stroke>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main_layout);

        // this.mPaintingLayer = this.getView(R.id.main_painting_layer);
        // this.mDisplayLayer = this.getView(R.id.main_display_layer);

        this.surfaceView = this.getView(R.id.main_surface_layer);
        this.playSurface = this.getView(R.id.main_play_surface_layer);

        // this.surfaceView.setBackgroundColor(Color.WHITE);
        this.surfaceView.setZOrderOnTop(true);
        this.surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        this.playSurface.setZOrderOnTop(true);
        this.playSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public void onPlayClick(View v) {
        this.surfaceView.setVisibility(View.GONE);
        this.playSurface.setVisibility(View.VISIBLE);
        this.playSurface.setPlayData(this.allData);
        this.playSurface.startPlay();
    }

    public void onEraserClick(View v) {
        this.surfaceView.changeToEraser();
    }

    public void onPaintingClick(View v) {
        this.surfaceView.changeToPaint();
    }

    public void onNextClick(View v) {
        List<Stroke> frameData = this.surfaceView.getFrameData();
        this.allData.add(frameData);
    }

    /**
     * 更方便的"findViewById"
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    private final <E extends View> E getView(int id) {
        try {
            return (E) this.findViewById(id);
        } catch (ClassCastException ex) {
            throw ex;
        }
    }

}
