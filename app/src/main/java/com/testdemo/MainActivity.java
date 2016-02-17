package com.testdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                Intent intent = new Intent(this, MainActivity_Blur.class);
                startActivity(intent);
                break;

            case R.id.button2:
                intent = new Intent(this, MainActivity_Broken.class);
                startActivity(intent);
                break;

            case R.id.button3:
                intent = new Intent(this, MainActivityColor.class);
                startActivity(intent);
                break;

            case R.id.button4:
                intent = new Intent(this, MainActivity3DListview.class);
                startActivity(intent);
                break;
            case R.id.button5:
                intent = new Intent(this, MainActivitySearchTextColor.class);
                startActivity(intent);
                break;
            case R.id.button6:
                intent = new Intent(this, MainActivityImgFliter.class);
                startActivity(intent);
                break;
            case R.id.button7:
                intent = new Intent(this, MainActivityImgFliter_JuZhen.class);
                startActivity(intent);
                break;
        }
    }
}
