package com.segway.robot.host.coreservice.vision.sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.segway.robot.host.coreservice.vision.sample.R;

public class ShowPhotoActivity  extends Activity{

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);

        Intent intent = getIntent();

        final Bitmap bitmap = (Bitmap)intent.getParcelableExtra("bitmap");

        Log.v("test",bitmap.toString());

        imageView = (ImageView) findViewById(R.id.image);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // mColorImageView.setImageBitmap(mColorBitmap);

                Log.v("test", "" + bitmap.getHeight() + " " + bitmap.getWidth());
                imageView.setImageBitmap(bitmap);


            }
        };

        runOnUiThread(runnable);
    }
}
