#这个应用中有多个项目
	##1，仿ios给activity背景设置模糊度
	##2，搜索关键字变红，指定字段变色
#一， BlurBehindActivity
	仿ios给activity背景设置模糊度，可以设置任意透明度（只需要一行代码简单集成）
安卓模糊背景，半透明背景，任意透明度背景


#使用步骤，只需下面简单2步。
##1，把项目中的qclCopy文件夹里的类直接复制到你的项目就行

![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/qcl.png?raw=true)
##2，完成好第一步就直接使用了
###比如你从MainActivity跳转到activity2
	public class MainActivity extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					BlurBehind.getInstance().execute(MainActivity.this, new OnBlurCompleteListener() {
						@Override
						public void onBlurComplete() {
							Intent intent = new Intent(MainActivity.this, MainActivity2.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							startActivity(intent);
						}
					});
				}
			});

		}
	}
	
###MainActivity2 只需要添加下面简单一行代码就可以设置模糊效果（也可以设置任意透明度）
	public class MainActivity2 extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_blurred);

			BlurBehind.getInstance()//在你需要添加模糊或者透明的背景中只需要设置这几行简单的代码就可以了
					.withAlpha(50)
					.withFilterColor(Color.parseColor("#0075c0"))
					.setBackground(this);
		}
	}

#下面是效果图

![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/blur-behind-before.png?raw=true) ![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/blur-behind-after.png?raw=true)


#二， 搜索关键字变红，指定字段变色
	有时候我们搜索中的关键字需要变红或者变为别的颜色，我自己重写了textview。使用起来特别方便

##使用步骤
1，把下面自定义的MyTextView 复制到项目中

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

2，使用只需要一行代码就可以完事
public class TextActivity04 extends Activity
{
    private MyTextView textView;
     
    public String result = "关键字变色，特别简单，只需要一行代码，就可以实现关键字变红";
     
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_04_layout);
         
        textView = (MyTextView)findViewById(R.id.tv);
        textView.setSpecifiedTextsColor(result, "关键字", Color.parseColor("#FF0000"));
		/*
			textView.setSpecifiedTextsColor(result, "关键字", Color.parseColor("#FF0000"))中result可以换成你的搜索结果
			关键字直接换成你的EditText中输入的关键字就可以啦
		
		*/
    }
}




3，附加布局文件，特别简单
	 <com.huxiu.yd.viewmine.MyTextView
        android:id="@+id/tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_toRightOf="@id/image"
        android:maxLines="2"
        android:textColor="@color/gray5"
        android:textSize="14dp"
        tools:text="测试搜索结果标题" />

#下面是效果图

![image](https://github.com/qiushi123/BlurBehindActivity/blob/master/images_qcl/2015031808522296.png) 
	
	
	
	
	
