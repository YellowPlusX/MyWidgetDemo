package com.yellow.widgetdemo.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.yellow.widgetdemo.utils.LogUtil;

/**
 * Created by Freeman on 16-1-11.
 * 一个弧形进度条，暂时只可以可以设置进度、进度条颜色和进度条宽度
 */
public class ArcProgressBar extends View {

    private static final String TAG = "ArcProgressBar";

    private static final int BASE_ARC_COLOR = Color.rgb(142, 158, 172);

    private static final int PROGRESS_ARC_COLOR_PROGRESS = Color.rgb(200, 255, 255);

    private static final int START_ANGLE = 130;// 暂定开启角度为130度
    //由于圆弧绘制默认从水平0度顺时针绘制，这里为保持两边对称
    private static final int SWEEP_ANGLE = 360 - (START_ANGLE - 90) * 2;

    private static int ARC_PAINT_STROKE = 25;

    private float mProgress = .0001f;
    private float mProgressSweepAngle = .0001f;

    private ProgressAnimation mProgressAnimation;
    private long mDuration = 2000;
    private boolean needAnim = true;

    private Paint mPaint;
    private int paintStrokeWidth = ARC_PAINT_STROKE;
    private int baseArcColor = BASE_ARC_COLOR;
    private int progressArcColor = PROGRESS_ARC_COLOR_PROGRESS;

    private RectF backGroundRectF, progressRectF;

    public ArcProgressBar(Context context) {
        super(context);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mProgressAnimation = new ProgressAnimation();
        mProgressAnimation.setDuration(mDuration);

        // 设置画笔为样式为圆形
        mPaint = new Paint();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(paintStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        backGroundRectF = new RectF(paintStrokeWidth, paintStrokeWidth,
                getWidth() - paintStrokeWidth, getHeight() - paintStrokeWidth);
        progressRectF = new RectF(paintStrokeWidth, paintStrokeWidth,
                getWidth() - paintStrokeWidth, getHeight() - paintStrokeWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCanvas(canvas);
    }

    private void drawCanvas(Canvas canvas) {
        mPaint.setColor(baseArcColor);
        canvas.drawArc(backGroundRectF, START_ANGLE, SWEEP_ANGLE, false, mPaint);
        mPaint.setColor(progressArcColor);
        if (mProgressSweepAngle > 0.01) {
            canvas.drawArc(progressRectF, START_ANGLE, mProgressSweepAngle, false, mPaint);
        }
    }

    /**
     * 设置画笔的宽度，即设置进度条宽度
     * @param stroke
     */
    public void setBaseArcPaintStroke(int stroke) {
        paintStrokeWidth = stroke;
        backGroundRectF = new RectF(paintStrokeWidth, paintStrokeWidth,
                getWidth() - paintStrokeWidth, getHeight() - paintStrokeWidth);
        progressRectF = new RectF(paintStrokeWidth, paintStrokeWidth,
                getWidth() - paintStrokeWidth, getHeight() - paintStrokeWidth);
    }

    /**
     * Set the progress animation duration
     * @param duration progress animation duration
     */
    public void setDuration(long duration) {
        mDuration = duration;
        mProgressAnimation.setDuration(mDuration);
    }
    
    public void needProgressAnim(boolean anim) {
        needAnim = anim;
    }

    public void setBaseArcColor(int color) {
        baseArcColor = color;
    }

    public void setProgressArcColor(int color) {
        progressArcColor = color;
    }

    /**
     * 设置弧形进度条的Progress
     *
     * @param progress
     */
    public void setProgress(float progress) {
        mProgress = Math.max(progress, 0.0001f);
        LogUtil.i(TAG, "mProgress = " + mProgress);
        if (needAnim) {
            startAnimation(mProgressAnimation);
        } else {
            mProgressSweepAngle = SWEEP_ANGLE * mProgress;
            invalidate();
        }
    }

    /**
     * 集成animation的一个动画类
     *
     * @author
     */
    private class ProgressAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            LogUtil.i(TAG, "interpolatedTime = " + interpolatedTime);
            if (interpolatedTime < 1.0f) {
                mProgressSweepAngle = SWEEP_ANGLE * mProgress * interpolatedTime;
                LogUtil.i(TAG, "mProgressSweepAngle = " + mProgressSweepAngle);
                invalidate();
            }
        }
    }

}
