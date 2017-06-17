package com.segway.robot.host.coreservice.vision.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.segway.robot.host.coreservice.vision.sample.activity.ShowPhotoActivity;
import com.segway.robot.sdk.base.bind.ServiceBinder;
import com.segway.robot.sdk.vision.Vision;
import com.segway.robot.sdk.vision.frame.Frame;
import com.segway.robot.sdk.vision.stream.StreamInfo;
import com.segway.robot.sdk.vision.stream.StreamType;


/**
 * The Sample Activity demonstrate the main function of Segway Robot VisionService.
 */
public class CameraActivity extends Activity {

    private boolean mBind;

    private Vision mVision;

    private SurfaceView mColorSurfaceView;

    private StreamInfo colorInfo;

    ServiceBinder.BindStateListener mBindStateListener = new ServiceBinder.BindStateListener() {
        @Override
        public void onBind() {
            mBind = true;

            startPreview();
        }

        @Override
        public void onUnbind(String reason) {
            mBind = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        mColorSurfaceView = (SurfaceView) findViewById(R.id.colorSurface);

        // get Vision SDK instance
        mVision = Vision.getInstance();
        mVision.bindService(this, mBindStateListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Start preview color and depth image
     */
    private synchronized void startPreview() {
        // 1. Get activated stream info from Vision Service.
        //    Streams are pre-config.
        StreamInfo[] infos = mVision.getActivatedStreamInfo();
        for(StreamInfo info : infos) {
            // Adjust image ratio for display
            switch (info.getStreamType()) {
                case StreamType.COLOR:
                    float ratio = (float)info.getWidth()/info.getHeight();
                    ViewGroup.LayoutParams layout;
                    colorInfo = info;
                    // Adjust color surface view
                    mColorSurfaceView.getHolder().setFixedSize(info.getWidth(), info.getHeight());
                    layout = mColorSurfaceView.getLayoutParams();
                    layout.width = (int) (mColorSurfaceView.getHeight() * ratio);
                    mColorSurfaceView.setLayoutParams(layout);
                    // preview color stream
                    Log.v("start preview!", "");
                    mVision.startPreview(StreamType.COLOR, mColorSurfaceView.getHolder().getSurface());
                    break;
            }
        }
    }

    /**
     * FrameListener instance for get raw image data form vision service
     */

    int i = 0;

    Vision.FrameListener mFrameListener = new Vision.FrameListener() {
        @Override
        public void onNewFrame(int streamType, Frame frame) {
            Log.v("stream type",  "" + streamType);

            switch (streamType) {
                case StreamType.COLOR:

                    final Bitmap mColorBitmap = Bitmap.createBitmap(colorInfo.getWidth(), colorInfo.getHeight(), Bitmap.Config.ARGB_8888);
                    // draw color image to bitmap and display
                    mColorBitmap.copyPixelsFromBuffer(frame.getByteBuffer());

                    i++;
                    if (i > 20) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(CameraActivity.this, ShowPhotoActivity.class);
                                intent.putExtra("bitmap", mColorBitmap);

                                Log.v("bitmap size", "" + mColorBitmap.getHeight() + " " + mColorBitmap.getWidth());

                                startActivity(intent);

                                colorInfo = null;
                            }
                        };

                        runOnUiThread(runnable);
                    }
            }
            }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mVision.unbindService();
        StreamInfo[] infos = mVision.getActivatedStreamInfo();
        for(StreamInfo info : infos) {
            switch (info.getStreamType()) {
                case StreamType.COLOR:
                    mVision.stopListenFrame(StreamType.COLOR);
                    break;
            }
        }
    }
}
