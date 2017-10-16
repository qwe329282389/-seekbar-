package com.example.yy.temperaturedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 竖向SeekBar
 * Created by yy on 2017/9/11.
 */

public class VerticalSeekBar extends View {
    private int mThumbColor = Color.WHITE;
    private int mThumbBorderColor = Color.TRANSPARENT;
    private int[] colorArray;
    private float x, y;
    private float mRadius;
    private float progress;
    private float sLeft, sTop, sRight, sBottom;
    private float sWidth, sHeight;
    private LinearGradient linearGradient;
    private Paint paint = new Paint();
    protected OnStateChangeListener onStateChangeListener;
    private int mMaxNumber = -1;//把此seekbar分成多少份
    private float mItemHeight = -1;//把此seekbar分成多少份后每份的高度
    private Context mContext;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setmThumbColor(int mThumbColor) {
        this.mThumbColor = mThumbColor;
    }

    public void setmThumbBorderColor(int mThumbBorderColor) {
        this.mThumbBorderColor = mThumbBorderColor;
    }

    public void setColorArray(int[] colorArray) {
        this.colorArray = colorArray;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    /**
     * 初始化
     */
    private void init() {
        mMaxNumber = TemperatureView.MAX_TEMPERATURE-TemperatureView.MIN_TEMPERATURE+3;
        mItemHeight = sHeight/mMaxNumber;
        colorArray = new int[3];
        colorArray[0] = mContext.getResources().getColor(R.color.temperature_color_first);
        colorArray[1] = mContext.getResources().getColor(R.color.temperature_color_second);
        colorArray[2] = mContext.getResources().getColor(R.color.temperature_color_third);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getMeasuredHeight();
        int w = getMeasuredWidth();
        mRadius = (float) w / 2;
        sLeft = w * 0.35f; // 背景左边缘坐标
        sRight = w * 0.65f;// 背景右边缘坐标
        sTop = 0;
        sBottom = h;
        sWidth = sRight - sLeft; // 背景宽度
        sHeight = sBottom - sTop; // 背景高度
        x = (float) w / 2;//圆心的x坐标
        y = (float) (1 - 0.01 * progress) * sHeight;//圆心y坐标
        drawBackground(canvas);
        drawCircle(canvas);
        paint.reset();
    }

    private void drawBackground(Canvas canvas) {
        RectF rectBlackBg = new RectF(sLeft, sTop, sRight, sBottom);
        linearGradient = new LinearGradient(sLeft, sTop, sWidth, sHeight, colorArray, null, Shader.TileMode.MIRROR);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        //设置渲染器
        paint.setShader(linearGradient);
        canvas.drawRoundRect(rectBlackBg, sWidth / 3, sWidth / 3, paint);
    }

    private void drawCircle(Canvas canvas) {
        Paint thumbPaint = new Paint();
        y = y < mRadius ? mRadius : y;//判断thumb边界
        y = y > sHeight - mRadius ? sHeight - mRadius : y;
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(mThumbColor);
        canvas.drawCircle(x, y, mRadius, thumbPaint);
        thumbPaint.setStyle(Paint.Style.STROKE);
        thumbPaint.setColor(mThumbBorderColor);
        thumbPaint.setStrokeWidth(20);
        canvas.drawCircle(x, y, mRadius, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.y = event.getY();
        if (mMaxNumber==-1||mItemHeight==mItemHeight){
            init();
        }
        //为了解决LOW,HIGH的问题
        if (y>=mItemHeight&&y<=sHeight-mItemHeight){
            progress = (sHeight - y) / sHeight * 100;
            float listProgress = (sHeight-y-mItemHeight)/(sHeight-mItemHeight*2)*100;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if (onStateChangeListener != null) {
                        onStateChangeListener.onStopTrackingTouch(this, listProgress);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (onStateChangeListener != null) {
                        onStateChangeListener.onStateChangeListener(this, listProgress);
                    }
                    setProgress(progress);
                    this.invalidate();
                    break;
            }
        }
        return true;
    }


    public interface OnStateChangeListener {
        void onStateChangeListener(View view, float progress);

        void onStopTrackingTouch(View view, float progress);
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }
}
