package com.testdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.testdemo.color_lib.ColorPhrase;

public class MainActivityColor extends Activity {
    private EditText editText;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_color);
        editText = (EditText) findViewById(R.id.editText1);
        textView = (TextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String pattern = editText.getText().toString();
                CharSequence chars = ColorPhrase.from(pattern).withSeparator("{}").innerColor(0xFFE6454A).outerColor(0xFF666666).format();
                textView.setText(chars);
            }
        });
    }


}
