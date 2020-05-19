package com.wellee.irregularview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IrregularDraw2View extends View {

    private Paint paint;
    private int[] colors = {0xFFD21D22, 0xFFFBD109, 0xFF4BB748, 0xFF2F7ABB};
    private Bitmap bitmap;
    private Canvas canvasTemp;
    private int width;
    private int height;
    private int cx, cy;
    private Path path;
    private int innerCr;
    private int outerCr;
    private RectF innerRectF;
    private RectF outerRectF;
    private Matrix matrix;
    private List<Region> regions;
    private Region clip;

    public IrregularDraw2View(Context context) {
        this(context, null);
    }

    public IrregularDraw2View(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IrregularDraw2View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();

        matrix = new Matrix();
        regions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            regions.add(new Region());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //如果是精确测量 则直接返回值
        if (modeWidth == MeasureSpec.EXACTLY) {
            width = sizeWidth;
        } else {//指定宽度的大小
            width = 100;
            //如果是最大值模式  取当中的小值  防止超出父类控件的最大值
            if (modeWidth == MeasureSpec.AT_MOST) {
                width = Math.min(width, sizeWidth);
            } else {
                // unspecified 取最大值
                width = Math.max(width, sizeWidth);
            }
        }
        //如果是精确测量 则直接返回值
        if (modeHeight == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else {//指定高度的大小
            height = 100;
            //如果是最大值模式  取当中的小值  防止超出父类控件的最大值
            if (modeHeight == MeasureSpec.AT_MOST) {
                height = Math.min(height, sizeHeight);
            } else {
                // unspecified 取最大值
                height = Math.max(height, sizeHeight);
            }
        }
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        cx = width / 2;
        cy = height / 2;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasTemp = new Canvas(bitmap);

        innerCr = cx / 2;
        outerCr = cx;
        innerRectF = new RectF(cx - innerCr, cy - innerCr, cx + innerCr, cy + innerCr);
        outerRectF = new RectF(cx - outerCr, cy - outerCr, cx + outerCr, cy + outerCr);

        clip = new Region(cx - outerCr, cy - outerCr, cx + outerCr, cy + outerCr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset();
        path.addArc(innerRectF, 150, 120);
        path.lineTo((float) (cx + innerCr * Math.sqrt(3)), cy - innerCr);
        path.addArc(outerRectF, -30, -120);
        path.lineTo((float) (cx - innerCr * Math.sqrt(3) / 2), cy + innerCr / 2);
        paint.setColor(colors[0]);
        canvasTemp.drawPath(path, paint);
        regions.get(0).setPath(path, clip);

        matrix.setRotate(120, cx, cy);

        path.transform(matrix);
        paint.setColor(colors[1]);
        canvasTemp.drawPath(path, paint);
        regions.get(1).setPath(path, clip);

        path.transform(matrix);
        paint.setColor(colors[2]);
        canvasTemp.drawPath(path, paint);
        regions.get(2).setPath(path, clip);

        paint.setColor(Color.WHITE);
        canvasTemp.drawCircle(cx, cy, innerCr, paint);

        paint.setColor(colors[3]);
        int inner2Cr = innerCr - outerCr / 10;
        canvasTemp.drawCircle(cx, cy, inner2Cr, paint);

        path.reset();
        path.addCircle(cx, cy, inner2Cr, Path.Direction.CW);
        regions.get(3).setPath(path, clip);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (bitmap == null) {
            return false;
        }
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            if (region.contains(x, y)) {
                this.setTag(this.getId(), i);
                return super.onTouchEvent(event);
            }
        }
        return false;
    }
}
