package com.testdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testdemo.blurbehind.BlurBehind;
import com.testdemo.blurbehind.OnBlurCompleteListener;
import com.testdemo.broken_lib.BrokenCallback;
import com.testdemo.broken_lib.BrokenTouchListener;
import com.testdemo.broken_lib.BrokenView;

public class MainActivity_Blur extends Activity {
    private ImageView imageview;
    private TextView back;
    private RelativeLayout all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_blur);
        findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BlurBehind.getInstance().execute(MainActivity_Blur.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(MainActivity_Blur.this, MainActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(intent);
                    }
                });
            }
        });


        //        下面的所有代码都是玻璃破碎效果
        imageview = (ImageView) findViewById(R.id.imageview);
        back = (TextView) findViewById(R.id.back);
        all = (RelativeLayout) findViewById(R.id.all);
        initBroken();

    }

    private BrokenView mBrokenView;
    private BrokenTouchListener colorfulListener;
    private BrokenTouchListener whiteListener;
    private Paint whitePaint;
    private boolean effectEnable = true;

    //    mBrokenView.setEnable(false);//设置不具备破冰效果
    private void initBroken() {
        mBrokenView = BrokenView.add2Window(this);

        whitePaint = new Paint();
        whitePaint.setColor(0xffffffff);//裂缝颜色

        colorfulListener = new BrokenTouchListener.Builder(mBrokenView).
                build();

        whiteListener = new BrokenTouchListener.Builder(mBrokenView).
                setComplexity(50).//破裂成8块
                setBreakDuration(100).//破裂持续事件
                setFallDuration(1000).//掉下时间
                setCircleRiftsRadius(100).//裂纹宽度
                //                setEnableArea(button).//作用域，只有点击button按钮时才会有破冰效果
                        setEnableArea(back).//作用域，只有点击button按钮时才会有破冰效果
                setPaint(whitePaint).
                        build();
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        //        back.setClickable(false);
      /*下面代码用来设置一些自定义破冰事件
       BrokenTouchListener.Builder(brokenView).
                setComplexity(...).          // default 12
        setBreakDuration(...).       // in milliseconds, default 700ms
        setFallDuration(...).        // in milliseconds, default 2000ms
        setCircleRiftsRadius(...).   // in dp, default 66dp, you can disable circle-rifts effect by set it to 0
        setEnableArea(...).          // set the region or childview that can enable break effect,
        // be sure the childView or childView in region doesn't intercept any touch event
        setPaint(...). build();              // the paint to draw rifts*/


        setOnTouchListener();

        mBrokenView.setCallback(new MyCallBack());

    }

    public void setOnTouchListener() {
    /*
        if you don't want the childView of parentLayout intercept touch event
        set like this:
        childView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        childView.setClickable(false);

        if you want only click the button can break the parentLayout,
        set like this:
        listener = new BrokenTouchListener.Builder(mBrokenView).
                setEnableArea(button).
                build();
        and set the button don't intercept touch event at the same time
    */
        //        imageview.setOnTouchListener(whiteListener);
        //        back.setOnTouchListener(whiteListener);
        all.setOnTouchListener(whiteListener);
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

  /*下面代码属于初始化破冰事件
    colorfulListener = new BrokenTouchListener.Builder(mBrokenView).
    setComplexity(complexitySeekbar.getProgress() + 8).
    setBreakDuration(breakSeekbar.getProgress() + 500).
    setFallDuration(fallSeekbar.getProgress() + 1000).
    setCircleRiftsRadius(radiusSeekbar.getProgress() + 20).
    build();
    whiteListener = new BrokenTouchListener.Builder(mBrokenView).
    setComplexity(complexitySeekbar.getProgress() + 8).
    setBreakDuration(breakSeekbar.getProgress() + 500).
    setFallDuration(fallSeekbar.getProgress() + 1000).
    setCircleRiftsRadius(radiusSeekbar.getProgress() + 20).
    setPaint(whitePaint).
    build();

    setOnTouchListener();

    mBrokenView.setEnable(effectEnable);*/
}
