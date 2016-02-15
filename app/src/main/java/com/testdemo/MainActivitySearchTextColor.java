package com.testdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.testdemo.searchText_red_lib.MyTextView;

public class MainActivitySearchTextColor extends Activity {
    private EditText editText;
    private MyTextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5_search_text_color);
        editText = (EditText) findViewById(R.id.editText1);
        textView = (MyTextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String pattern = editText.getText().toString();
                textView.setSpecifiedTextsColor(pattern, "Android", Color.parseColor("#FF0000"));
            }
        });
    }


}
