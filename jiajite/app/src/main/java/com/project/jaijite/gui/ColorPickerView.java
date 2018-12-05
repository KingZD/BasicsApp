package com.project.jaijite.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.project.jaijite.util.ScreenUtils;


/**
 * Created by zed on 2018/11/6.
 */
public class ColorPickerView extends View {

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    /**
     * 水滴指针的位置
     */
    Path mWaterPath = new Path();
    Matrix mWaterMatrix = new Matrix();
    //颜色选择器自定义View
    private Paint mPaint;//渐变色环画笔
    private Paint mCenterPaint;//中间圆画笔
    //渐变色数组
    private final int[] mColors = new int[]{
            0xFFFF0000,0xFFFF00FF,0xFF0000FF,0xFF00FFFF,0xFF00FF00,0xFFFFFF00,0xFFFF0000
    };
    private OnColorChangedListener mListener;//颜色改变回调

    /**
     * 中心点
     */
    private int CENTER_X = 100;
    private int CENTER_Y = 100;
    /**
     * 中心圆大小
     */
    private int CENTER_RADIUS = 32;
    private int degrees = -90;
    private float mWaterDegress = 0;
    private RectF mColorRectF;
    /**
     * 外圈指针画笔
     */
    Paint mZzPaint = new Paint();
    Path mZzPath = new Path();
    /**
     * 初始水滴指针方向
     */
    private float mLastUnit = (float) Math.toRadians(degrees * 2 + 180);

    public ColorPickerView(Context context) {
        super(context);
        initView();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //初始化渐变色画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);

        Shader mShader = new SweepGradient(0, 0, mColors, null);
        Matrix mMatrix = new Matrix();
        mMatrix.setRotate(degrees, 0, 0);
        mShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mShader);

        //初始化中心园画笔
        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(mColors[3]);
        mCenterPaint.setStrokeWidth(5);
        mColorRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = ScreenUtils.getScreenWidth() / 3 * 2;
        setMeasuredDimension(width, width);
        //取屏幕的一般宽度作为组建范围
        CENTER_X = width / 2;
        CENTER_Y = width / 2;
        mPaint.setStrokeWidth(CENTER_X / 3 * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //移动中心
        canvas.translate(CENTER_X, CENTER_X);
        //画出色环和中心圆
        mColorRectF.set(-CENTER_X + mPaint.getStrokeWidth() / 2,
                -CENTER_X + mPaint.getStrokeWidth() / 2,
                CENTER_X - mPaint.getStrokeWidth() / 2,
                CENTER_X - mPaint.getStrokeWidth() / 2);
        canvas.drawOval(mColorRectF, mPaint);
        canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
        drawWater(canvas);
    }


    /**
     * 画水滴
     */
    private void drawWater(Canvas mCanvas) {
        int c = mCenterPaint.getColor();
        mCenterPaint.setStyle(Paint.Style.FILL);
        mCenterPaint.setAntiAlias(true);
        float mWidth = Math.abs(CENTER_X - mColorRectF.width());
        mWaterMatrix.postRotate(mWaterDegress);
        mWaterPath.reset();
        //外发光
        mCenterPaint.setShadowLayer(15F, 0F, 0F, mCenterPaint.getColor());
        mWaterPath.moveTo(0, -mWidth / 3 * 2);
        mWaterPath.quadTo(-mWidth / 3 * 2, mWidth / 4, 0, mWidth / 3 - 2);
        mWaterPath.moveTo(0, -mWidth / 3 * 2);
        mWaterPath.quadTo(mWidth / 3 * 2, mWidth / 4, 0, mWidth / 3 - 2);
        mWaterPath.close();
        mWaterPath.transform(mWaterMatrix);
        mCanvas.drawPath(mWaterPath, mCenterPaint);

        float radius = mWidth + 10;
        //画圆圈
        mZzPaint.setColor(Color.WHITE);
        mZzPaint.clearShadowLayer();
        mZzPaint.setStyle(Paint.Style.STROKE);
        mZzPaint.setStrokeWidth(5);
        mCanvas.drawCircle(0, 0, radius, mZzPaint);
        // 绘制三角形指针
        //指针顶点
        float sTop = - radius;
        //三角形高度
        float triangleHeight = 40;
        float triangleWidth = 15;
        mZzPath.reset();
        mZzPath.moveTo(0, sTop -triangleHeight);// 此点为多边形的起点
        mZzPath.lineTo(-triangleWidth, sTop);
        mZzPath.lineTo(triangleWidth, sTop);
        mZzPath.close(); // 使这些点构成封闭的多边形
        /**进行旋转**/
        mZzPaint.setStyle(Paint.Style.FILL);
        mZzPath.transform(mWaterMatrix);
        mCanvas.drawPath(mZzPath, mZzPaint);

        /**三角形上面的白色线条**/
        mZzPath.reset();
        mZzPath.moveTo(0, 50 - CENTER_X);
        mZzPath.lineTo(0, sTop - triangleHeight - 20);
        mZzPath.close(); // 使这些点构成封闭的多边形
        /**进行旋转**/
        mZzPaint.setStyle(Paint.Style.STROKE);
        mZzPaint.setStrokeWidth(5);
        mZzPath.transform(mWaterMatrix);
        mCanvas.drawPath(mZzPath, mZzPaint);

        mCenterPaint.setColor(c);
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
     * @param colors 颜色数组
     * @param unit 去色值
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
                mCenterPaint.setColor(interpColor(mColors, unit));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mListener != null)
                    mListener.colorChanged(mCenterPaint.getColor());
                break;
        }
        return true;
    }

    public void setListener(OnColorChangedListener mListener) {
        this.mListener = mListener;
    }
}
