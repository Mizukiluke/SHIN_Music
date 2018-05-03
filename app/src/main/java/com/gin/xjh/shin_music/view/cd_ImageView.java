package com.gin.xjh.shin_music.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class cd_ImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint; //画笔

    private int mRadius; //圆形图片的半径

    private float mScale; //图片的缩放比例
    private PorterDuffXfermode xfermode;//填充模式

    public cd_ImageView(Context context) {
        super(context);
    }

    public cd_ImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public cd_ImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为是圆形图片，所以应该让宽高保持一致
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mRadius = size / 2;

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint = new Paint();
        Bitmap bitmap = drawableToBitmap(getDrawable());

        //初始化BitmapShader，传入bitmap对象
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        //计算缩放比例
        mScale = (mRadius * 0.7f * 2.0f) / Math.min(bitmap.getHeight(), bitmap.getWidth());

        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);
        bitmapShader.setLocalMatrix(matrix);
        mPaint.setAntiAlias(true);

        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        //画圆形，指定好中心点坐标、半径、画笔
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        mPaint.setShader(bitmapShader);
        mPaint.setXfermode(xfermode);
        canvas.translate(mRadius * 0.30f, mRadius * 0.30f);
        canvas.drawCircle(mRadius * 0.7f, mRadius * 0.7f, mRadius * 0.7f, mPaint);
    }

    //写一个drawble转BitMap的方法
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
}