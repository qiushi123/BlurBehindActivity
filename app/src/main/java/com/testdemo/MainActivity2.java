package com.testdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.testdemo.blurbehind.BlurBehind;
import com.testdemo.broken_lib.BrokenCallback;
import com.testdemo.broken_lib.BrokenTouchListener;
import com.testdemo.broken_lib.BrokenView;

public class MainActivity2 extends Activity {

    private RelativeLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blurred);

        BlurBehind.getInstance()
                .withAlpha(50)
                .withFilterColor(Color.parseColor("#0075c0"))
                .setBackground(this);
        background = (RelativeLayout) findViewById(R.id.background);
        initBroken();
    }

    public static class MyView extends View {
        private Paint paint;
        private final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

        public MyView(Context context) {
            this(context, null);
        }

        public MyView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            // If don't close hardware acceleration int Android 4.4,
            // the ViewGroup will not clip canvas for child when create bitmap from it(R.id.demo_parent)
            // therefore the canvas has wrong width and height.
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xffff0000);
            canvas.drawRect(dp2px(16), dp2px(16), dp2px(50), dp2px(50), paint);
            paint.setColor(0xffcc9900);
            canvas.drawRect(dp2px(100), dp2px(16), dp2px(133), dp2px(50), paint);
            paint.setColor(0xff00ff00);
            canvas.drawRect(dp2px(16), dp2px(106), dp2px(50), dp2px(140), paint);
            paint.setColor(0xff6600ff);
            canvas.drawRect(dp2px(100), dp2px(106), dp2px(133), dp2px(140), paint);

            canvas.translate(canvasWidth / 2, canvasHeight / 2);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextSize(dp2px(18));
            paint.setColor(0xffffff00);
            canvas.drawText("Custom View", -dp2px(53), dp2px(3), paint);
        }

        private int dp2px(int dp) {
            return Math.round(dp * DENSITY);
        }


    }

    //    下面是破冰效果
    private BrokenView mBrokenView;
    private BrokenTouchListener colorfulListener;
    private BrokenTouchListener whiteListener;
    private Paint whitePaint;

    //    mBrokenView.setEnable(false);//设置不具备破冰效果
    private void initBroken() {
        mBrokenView = BrokenView.add2Window(this);

        whitePaint = new Paint();
        whitePaint.setColor(0xffffffff);//裂缝颜色

        colorfulListener = new BrokenTouchListener.Builder(mBrokenView).
                build();

        whiteListener = new BrokenTouchListener.Builder(mBrokenView).
                setComplexity(50).//破裂成8块
                setBreakDuration(500).//破裂持续事件
                setFallDuration(1000).//掉下时间
                setCircleRiftsRadius(100).//裂纹宽度
                //                setEnableArea(button).//作用域，只有点击button按钮时才会有破冰效果
                //                        setEnableArea(back).//作用域，只有点击button按钮时才会有破冰效果
                        setPaint(whitePaint).
                        build();
        //        cancel.setOnTouchListener(new View.OnTouchListener() {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                return false;
        //            }
        //        });
        //        cancel.setClickable(false);
      /*下面代码用来设置一些自定义破冰事件
       BrokenTouchListener.Builder(brokenView).
                setComplexity(...).          // default 12
        setBreakDuration(...).       // in milliseconds, default 700ms
        setFallDuration(...).        // in milliseconds, default 2000ms
        setCircleRiftsRadius(...).   // in dp, default 66dp, you can disable circle-rifts effect by set it to 0
        setEnableArea(...).          // set the region or childview that can enable break effect,
        // be sure the childView or childView in region doesn't intercept any touch event
        setPaint(...). build();              // the paint to draw rifts*/


        background.setOnTouchListener(whiteListener);

        mBrokenView.setCallback(new MyCallBack());

    }

    //    下面是用来监听破冰效果的进度
    private class MyCallBack extends BrokenCallback {
        @Override
        public void onStart(View v) {
            //            showCallback(v, "onStart");
        }

        @Override
        public void onCancel(View v) {
            //            showCallback(v, "onCancel");
        }

        @Override
        public void onRestart(View v) {
            //            showCallback(v, "onRestart");
        }

        @Override
        public void onFalling(View v) {
            //            showCallback(v, "onFalling");
        }

        @Override
        public void onFallingEnd(View v) {
            //            showCallback(v, "onFallingEnd");
            finish();
        }

        @Override
        public void onCancelEnd(View v) {
            //            showCallback(v, "onCancelEnd");
        }
    }
}
