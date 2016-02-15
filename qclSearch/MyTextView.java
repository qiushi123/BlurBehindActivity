package com.testdemo.searchText_red_lib;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//制定字体变色，自定义textview
public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSpecifiedTextsColor(String text, String specifiedTexts, int color) {
        List<Integer> sTextsStartList = new ArrayList<>();

        int sTextLength = specifiedTexts.length();
        String temp = text;
        int lengthFront = 0;//记录被找出后前面的字段的长度
        int start = -1;
        do {
            start = temp.indexOf(specifiedTexts);

            if (start != -1) {
                start = start + lengthFront;
                sTextsStartList.add(start);
                lengthFront = start + sTextLength;
                temp = text.substring(lengthFront);
            }

        } while (start != -1);

        SpannableStringBuilder styledText = new SpannableStringBuilder(text);
        for (Integer i : sTextsStartList) {
            styledText.setSpan(
                    new ForegroundColorSpan(color),
                    i,
                    i + sTextLength,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        setText(styledText);
    }
}