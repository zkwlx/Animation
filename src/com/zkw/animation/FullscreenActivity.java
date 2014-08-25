package com.zkw.animation;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.zkw.utils.GifEncoder;

public class FullscreenActivity extends Activity {

    private final String TAG = "FullscreenActivity";

    private GifImageView gifImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.gifImage = new GifImageView(this);
        this.setContentView(this.gifImage);

        this.initData();
    }

    private void initData() {

        GifEncoder gifEncoder = new GifEncoder();
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        gifEncoder.start(byteOutput);

        AssetManager asset = this.getAssets();
        try {
            String[] files = asset.list("png");
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                Log.i(this.TAG, "====> " + file);
                Bitmap b = BitmapFactory.decodeStream(this.getAssets().open(
                        "png/" + file));
                gifEncoder.addFrame(b);
            }

        } catch (IOException e2) {
            e2.printStackTrace();
        }

        gifEncoder.finish();
        byte[] content = byteOutput.toByteArray();

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(this.getCacheDir()
                    .getAbsolutePath() + "mm.gif");
            outStream.write(content);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GifDrawable gifDrawable = new GifDrawable(content);
            this.gifImage.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
