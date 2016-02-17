package com.testdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.testdemo.fliter_lib.GrayFilter;

public class MainActivityImgFliter extends Activity {
    private EditText editText;
    private Button button;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6_imagefliter);
        image = (ImageView) findViewById(R.id.image);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                String pattern = editText.getText().toString();
                int i = 0;
                if (TextUtils.isEmpty(pattern)) {
                    i = 0;
                } else {
                    i = Integer.parseInt(pattern);
                }

                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.galata);
                Bitmap newBitmap = GrayFilter.changeToGray(bitmap, i);

                //把添加滤镜后的效果显示在imageview上
                image.setBackground(new BitmapDrawable(getResources(), newBitmap));
            }
        });
    }


}
