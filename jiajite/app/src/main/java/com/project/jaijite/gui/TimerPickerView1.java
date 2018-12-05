package com.project.jaijite.gui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.project.jaijite.R;

public class TimerPickerView1 extends View {
    float mWidth = 0;
    float mHeight = 0;
    Paint mPaint;
    SweepGradient btnGradient;
    Path mBtnPath;
    Matrix mBtnMatrix;

    public TimerPickerView1(Context context) {
        super(context);
        initView();
    }

    public TimerPickerView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TimerPickerView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBtnMatrix = new Matrix();
        mBtnPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cWidth = mWidth / 3 * 2;
        float left = (mWidth - cWidth) / 2;
        float top = (mHeight - cWidth) / 2;
        drawTimer(left, top, cWidth, canvas);
        //按钮比例是外圈时间的五分之三
        cWidth = cWidth / 5 * 3;
        left = (mWidth - cWidth) / 2;
        top = (mHeight - cWidth) / 2;
        drawButton(left, top, cWidth, canvas);
        //按钮比例是外圈时间的五分之三
        cWidth = cWidth / 5 * 2;
        left = (mWidth - cWidth) / 2;
        top = (mHeight - cWidth) / 2;
        drawSwitch(left, top, cWidth, canvas);
    }

    private void drawTimer(float left, float top, float cWidth, Canvas canvas) {
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_circle_time),
                null,
                new RectF(left, top, cWidth + left, cWidth + top), mPaint);
    }

    private void drawButton(float left, float top, float cWidth, Canvas canvas) {
        mPaint.setColor(Color.parseColor("#A1A09F"));
        mPaint.setStyle(Paint.Style.FILL);
        if (btnGradient == null)
            btnGradient = new SweepGradient(mWidth / 2, mHeight / 2, new int[]{
                    Color.GRAY, Color.LTGRAY, Color.GRAY, Color.LTGRAY, Color.GRAY, Color.LTGRAY}, null);
        mPaint.setShader(btnGradient);
        canvas.drawCircle(mWidth / 2, mHeight / 2, cWidth / 2, mPaint);

        mPaint.setShader(null);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        canvas.drawCircle(mWidth / 2, mHeight / 2, cWidth / 2, mPaint);

        mPaint.setStrokeWidth(10);
        mBtnPath.reset();
        mBtnPath.moveTo(mWidth / 2, mHeight / 2 - cWidth / 2);
        mBtnPath.lineTo(mWidth / 2, mHeight / 2 - cWidth / 2 + 40);
        mBtnMatrix.postRotate(mWaterDegress);
        mBtnPath.transform(mBtnMatrix);
        canvas.drawPath(mBtnPath, mPaint);
    }

    private void drawSwitch(float left, float top, float cWidth, Canvas canvas) {
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.light_open),
                null,
                new RectF(left, top, cWidth + left, cWidth + top), mPaint);
    }

    private int degrees = 90;
    private float mWaterDegress = 0;
    /**
     * 初始水滴指针方向
     */
    private float mLastUnit = (float) Math.toRadians(degrees * 2);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - mWidth / 3;
        float y = event.getY() - mHeight / 3;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float angle = (float) Math.atan2(y, x) - (float) Math.toRadians(degrees);
                mWaterDegress = (float) Math.toDegrees((angle - mLastUnit));
                mLastUnit = angle;
                //将圆周率转换成0-1进行颜色取色范围解析
                float unit = angle / (float) (2 * Math.PI);
                if (unit < 0) {
                    unit += 1;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
