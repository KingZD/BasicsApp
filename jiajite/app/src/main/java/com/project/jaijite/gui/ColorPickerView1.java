package com.project.jaijite.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.project.jaijite.R;
import com.project.jaijite.util.ScreenUtils;

public class ColorPickerView1 extends View {
    int mWidth = 0;
    int mHeight = 0;
    Paint mPaint;
    SweepGradient btnGradient;
    Path mBtnPath;
    Matrix mBtnMatrix;
    Bitmap light, lightOpen, lightClose;
    //渐变色数组
    private final int[] mColors = new int[]{
            0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000
    };
    private int mDefDegress = -90;
    private float mWaterDegress = 0;
    private boolean currentLightStatus = false;
    /**
     * 中心点
     */
    private int CENTER_X = 100;
    private int CENTER_Y = 100;
    Bitmap circleTime;

    public ColorPickerView1(Context context) {
        super(context);
        initView();
    }

    public ColorPickerView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ColorPickerView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBtnMatrix = new Matrix();
        mBtnPath = new Path();
        circleTime = BitmapFactory.decodeResource(getResources(), R.mipmap.circle_rgb);
        lightOpen = BitmapFactory.decodeResource(getResources(), R.mipmap.light_open);
        lightClose = BitmapFactory.decodeResource(getResources(), R.mipmap.light_close);
        light = lightClose;
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
        mWaterDegress = 0;
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
                    Color.GRAY, Color.LTGRAY, Color.GRAY, Color.LTGRAY, Color.GRAY, Color.LTGRAY}, new float[]{0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f});
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
        canvas.drawBitmap(light,
                null,
                new RectF(-cWidth / 2, -cWidth / 2, cWidth / 2, cWidth / 2), mPaint);
    }

    private int floatToByte(float x) {
        int n = Math.round(x);
        return n;
    }

    private int pinToByte(int n) {
        if (n < 0) {
            n = 0;
        } else if (n > 255) {
            n = 255;
        }
        return n;
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    /**
     * 取色
     *
     * @param colors 颜色数组
     * @param unit   去色值
     * @return 颜色
     */
    private int interpColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private int rotateColor(int color, float rad) {
        float deg = rad * 180 / 3.1415927f;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        ColorMatrix cm = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();

        cm.setRGB2YUV();
        tmp.setRotate(0, deg);
        cm.postConcat(tmp);
        tmp.setYUV2RGB();
        cm.postConcat(tmp);

        final float[] a = cm.getArray();

        int ir = floatToByte(a[0] * r + a[1] * g + a[2] * b);
        int ig = floatToByte(a[5] * r + a[6] * g + a[7] * b);
        int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

        return Color.argb(Color.alpha(color), pinToByte(ir),
                pinToByte(ig), pinToByte(ib));
    }

    /**
     * 初始水滴指针方向
     */
    private float mLastUnit = 0;

    private int defaultColor = 0;
    private int oldColor = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - CENTER_X;
        float y = event.getY() - CENTER_Y;
        boolean inCenter = Math.sqrt(x * x + y * y) <= mWidth / 5 / 2;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ScreenUtils.shake();
            case MotionEvent.ACTION_MOVE:

                float touch = (float) Math.atan2(y, x);
                float angle = touch - (float) Math.toRadians(mDefDegress);
                mWaterDegress = (float) Math.toDegrees((angle - mLastUnit));
                mLastUnit = angle;

                //将圆周率转换成0-1进行颜色取色范围解析
                float unit = angle / (float) (2 * Math.PI);
                if (unit < 0) {
                    unit += 1;
                }
                defaultColor = interpColor(mColors, unit);
                postInvalidate();
                if (!inCenter) {
                    if (listener != null && handler != null)
                        handler.sendEmptyMessageDelayed(0, 20);

//                    if (listener != null && oldColor != defaultColor)
//                        listener.colorChanged(toHexEncoding(defaultColor));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (inCenter) {
                    //自己点击图片自动切换状态
                    boolean autoCheck = true;
                    if (listener != null) {
                        autoCheck = listener.lightStatus(currentLightStatus, new ClickStatus() {
                            @Override
                            public void clickStatus(boolean status) {
                                updateSwitch(status);
                            }
                        });
                    }
                    if (autoCheck) {
                        if (currentLightStatus) {
                            light = lightClose;
                            currentLightStatus = false;
                        } else {
                            light = lightOpen;
                            currentLightStatus = true;
                        }
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public interface ClickStatus {
        void clickStatus(boolean status);
    }

    public void setCurrentLightStatus(boolean currentLightStatus) {
        updateSwitch(currentLightStatus);
    }

    public void updateSwitch(boolean isOPen) {
        currentLightStatus = isOPen;
        if (currentLightStatus) {
            light = lightOpen;
        } else {
            light = lightClose;
        }
        invalidate();
    }

    /**
     * 将10进制颜色转换为16进制
     */
    private String toHexEncoding(int color) {
        String r = Integer.toHexString(Color.red(color));
        String g = Integer.toHexString(Color.green(color));
        String b = Integer.toHexString(Color.blue(color));
        StringBuffer sb = new StringBuffer();
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        if (r.length() == 1) {
            r = "0".concat(r);
        }
        if (g.length() == 1) {
            g = "0".concat(g);
        }
        if (b.length() == 1) {
            b = "0".concat(b);
        }
        sb.append("#");
        sb.append(r);
        sb.append(g);
        sb.append(b);
        return sb.toString();
    }

    public interface OnTimerListener {
        void colorChanged(int color);

        boolean lightStatus(boolean isOpen, ClickStatus clickStatus);
    }

    private OnTimerListener listener;

    public void setListener(OnTimerListener listener) {
        this.listener = listener;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener != null && oldColor != defaultColor)
                listener.colorChanged(defaultColor);
            oldColor = defaultColor;
        }
    };

}
