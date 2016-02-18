package com.yellow.widgetdemo.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by hjx on 16-1-11.
 * 一个弧形进度条，暂时只可以可以设置进度、进度条颜色和进度条宽度
 */
public class ArcProgressBar extends View {

    private static final String TAG = "ArcProgressBar";

    private static final int BASE_ARC_COLOR = Color.rgb(142, 158, 172);

    private static final int PROGRESS_ARC_COLOR_TERRIBLE = Color.rgb(1, 1, 1);
    private static final int PROGRESS_ARC_COLOR_NORMAL = Color.rgb(2, 2, 2);
    private static final int PROGRESS_ARC_COLOR_GOOD = Color.rgb(200, 300, 300);
    private static final int PROGRESS_ARC_COLOR_AWESOME = Color.rgb(4, 4, 4);

    private static final int START_ANGLE = 130;// 暂定开启角度为130度
    //由于圆弧绘制默认从水平0度顺时针绘制，这里为保持两边对称
    private static final int SWEEP_ANGLE = 360 - (START_ANGLE - 90) * 2;

    private static int ARC_PAINT_STROKE = 25;

    private float mProgress = .0001f;
    private float mProgressSweepAngle = .0001f;

    private ProgressAnimation mProgressAnimation;
    private long mDuration = 2000;// 需动态变化？


    public ArcProgressBar(Context context) {
        super(context);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init() {
        mProgressAnimation = new ProgressAnimation();
        mProgressAnimation.setDuration(mDuration);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCanvas(canvas);
    }

    private void drawCanvas(Canvas canvas) {

        Paint paint = new Paint();
        /*canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(180);*/
        paint.setColor(BASE_ARC_COLOR);
        // 设置画笔为样式为圆形
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(ARC_PAINT_STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawArc(new RectF(ARC_PAINT_STROKE, ARC_PAINT_STROKE,
                getWidth() - ARC_PAINT_STROKE, getHeight() - ARC_PAINT_STROKE), START_ANGLE, SWEEP_ANGLE, false, paint);
        // canvas.restore();
        paint.setColor(PROGRESS_ARC_COLOR_GOOD);
        if (mProgressSweepAngle > 0.01) {
            canvas.drawArc(new RectF(ARC_PAINT_STROKE, ARC_PAINT_STROKE,
                    getWidth() - ARC_PAINT_STROKE, getHeight() - ARC_PAINT_STROKE), START_ANGLE, mProgressSweepAngle, false, paint);
        }

    }

    /**
     * 设置画笔的宽度，即设置进度条宽度
     * @param stroke
     */
    public void setBaseArcPaintStroke(int stroke) {
        ARC_PAINT_STROKE = stroke;
    }

    /**
     * 设置弧形进度条的Progress
     *
     * @param progress
     */
    public void setProgress(float progress) {
        mProgress = progress < 0.0001f ? 0.0001f : progress;
        Log.i("hjx", "mProgress = " + mProgress);
        startAnimation(mProgressAnimation);
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
            Log.i("hjx", "interpolatedTime = " + interpolatedTime);
            if (interpolatedTime < 1.0f) {
                mProgressSweepAngle = SWEEP_ANGLE * mProgress * interpolatedTime;
                Log.i("hjx", "mProgressSweepAngle = " + mProgressSweepAngle);
                invalidate();
            }
        }
    }

}
