package com.project.jaijite.gui;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.project.jaijite.util.ScreenUtils;

public class TimerPickerView extends View {
    int mWidth = 0;
    int mHeight = 0;
    Paint mPaint;
    SweepGradient btnGradient;
    Path mBtnPath;
    Matrix mBtnMatrix;
    Bitmap lightOpen;

    private int degrees = 90;
    private float mWaterDegress = 0;
    /**
     * 中心点
     */
    private int CENTER_X = 100;
    private int CENTER_Y = 100;
    Bitmap circleTime;

    public TimerPickerView(Context context) {
        super(context);
        initView();
    }

    public TimerPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TimerPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBtnMatrix = new Matrix();
        mBtnPath = new Path();
        circleTime = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_circle_time);
        lightOpen = BitmapFactory.decodeResource(getResources(), R.mipmap.light_open);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = ScreenUtils.getScreenWidth() / 3 * 2;
        setMeasuredDimension(mWidth, mWidth);
        //取屏幕的一般宽度作为组建范围
        CENTER_X = mWidth / 2;
        CENTER_Y = mWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动中心
        canvas.translate(CENTER_X, CENTER_X);
        drawTimer(canvas);
        //按钮比例是外圈时间的五分之三
        float cWidth = mWidth / 5 * 3;
        drawButton(cWidth, canvas);
        //按钮比例是外圈时间的五分之三
        cWidth = mWidth / 5;
        drawSwitch(cWidth, canvas);
    }

    private void drawTimer(Canvas canvas) {
        canvas.drawBitmap(circleTime,
                null,
                new RectF(-CENTER_X,
                        -CENTER_X,
                        CENTER_X,
                        CENTER_X), mPaint);
    }

    private void drawButton(float cWidth, Canvas canvas) {
        mPaint.setColor(Color.parseColor("#A1A09F"));
        mPaint.setStyle(Paint.Style.FILL);
        if (btnGradient == null)
            btnGradient = new SweepGradient(mWidth / 2, mHeight / 2, new int[]{
                    Color.GRAY, Color.LTGRAY, Color.GRAY, Color.LTGRAY, Color.GRAY, Color.LTGRAY}, new float[]{0f,0.2f,0.4f,0.6f,0.8f,1f});
        mPaint.setShader(btnGradient);
        canvas.drawCircle(0, 0, cWidth / 2, mPaint);

        mPaint.setShader(null);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        canvas.drawCircle(0, 0, cWidth / 2, mPaint);

        mPaint.setStrokeWidth(10);
        mBtnPath.reset();
        mBtnPath.moveTo(0, mHeight / 2 - cWidth / 2);
        mBtnPath.lineTo(0, mHeight / 2 - cWidth / 2 + 40);
        mBtnMatrix.postRotate(mWaterDegress);
        mBtnPath.transform(mBtnMatrix);
        canvas.drawPath(mBtnPath, mPaint);
    }

    private void drawSwitch(float cWidth, Canvas canvas) {
        canvas.drawBitmap(lightOpen,
                null,
                new RectF(-cWidth / 2, -cWidth / 2, cWidth / 2, cWidth / 2), mPaint);
    }

    /**
     * 初始水滴指针方向
     */
    private float mLastUnit = (float) Math.toRadians(degrees * 2);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - CENTER_X;
        float y = event.getY() - CENTER_Y;

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
