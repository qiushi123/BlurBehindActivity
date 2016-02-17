package com.testdemo.fliter_lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.testdemo.R;

/*用来演示用颜色矩阵设置滤镜的效果*/
public class ColorView extends ImageView {

    private Paint myPaint = null;
    private Bitmap bitmap = null;
    private ColorMatrix myColorMatrix = null;
    private float[] colorArray = {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //新建画笔对象  
        myPaint = new Paint();
        //描画（原始图片）      
        canvas.drawBitmap(bitmap, 0, 0, myPaint);
        //新建颜色矩阵对象      
        myColorMatrix = new ColorMatrix();
        //设置颜色矩阵的值  
        myColorMatrix.set(colorArray);
        //设置画笔颜色过滤器      
        myPaint.setColorFilter(new ColorMatrixColorFilter(myColorMatrix));
        //描画（处理后的图片）
        canvas.drawBitmap(bitmap, 0, 0, myPaint);
        invalidate();
    }

    //设置颜色数值  
    public void setColorArray(float[] colorArray) {
        this.colorArray = colorArray;
    }

    //设置图片  
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}  