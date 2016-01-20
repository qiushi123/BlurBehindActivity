package com.testdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.testdemo.blurbehind.BlurBehind;

public class MainActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blurred);

        BlurBehind.getInstance()
                .withAlpha(50)
                .withFilterColor(Color.parseColor("#0075c0"))
                .setBackground(this);
    }
}
