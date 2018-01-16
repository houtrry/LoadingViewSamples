package com.houtrry.loadingviewsamples;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: houtrry
 * @date: 2018/1/11 18:03
 * @version: $Rev$
 * @description: 一个简单的loading控件
 */

public class LoadingView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "LoadingView";

    /**
     * 该模式下, 外侧圆环有旋转
     */
    public static final int TYPE_ROTATE = 0x0000;
    /**
     * 该模式下, 外侧圆环没有旋转
     */
    public static final int TYPE_MOTIONLESS = 0x0001;


    @IntDef({TYPE_ROTATE, TYPE_MOTIONLESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadingType {
    }

    private @LoadingType int mLoadingType = TYPE_ROTATE;

    public void setLoadingType(@LoadingType int loadingType) {
        this.mLoadingType = loadingType;
    }

    private int mWidth;
    private int mHeight;
    private float mRadius;
    private Path mClipPath = new Path();
    private int mPaddingLeft;
    private int mPaddingTop;

    private float progress = -1.0f;
    private float rotateProgress = 0.0f;

    private Paint mBorderPaint;
    private RectF mCircleRectF;
    /**
     * 外侧圆环的线条宽度
     */
    private float mBorderWidth = 3f;
    /**
     * 外侧圆环的线条颜色
     */
    private int mBorderColor = Color.RED;
    private ObjectAnimator mObjectAnimator;
    private ObjectAnimator mRotateObjectAnimator;
    /**
     * 动画时长
     */
    private long mAnimatorDuration = 1200;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = Math.min(w, h) * 0.5f;
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mCircleRectF = new RectF(w * 0.5f - mRadius + mBorderWidth * 0.5f, h * 0.5f - mRadius + mBorderWidth * 0.5f, w * 0.5f + mRadius - mBorderWidth, h * 0.5f + mRadius - mBorderWidth);
        createClipPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawArc(canvas);
        canvas.save();
        canvas.clipPath(mClipPath);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setRotateProgress(float progress) {
        this.rotateProgress = progress;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 开启动画
     */
    public void startAnimator() {
        stopAnimator();
        if (mLoadingType == TYPE_ROTATE) {
            startRotateAnimator();
        }
        startProgressAnimator();
    }

    /**
     * 关闭动画
     */
    public void stopAnimator() {
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.cancel();
        }

        if (mRotateObjectAnimator != null && mRotateObjectAnimator.isRunning()) {
            mRotateObjectAnimator.cancel();
        }
    }

    /**
     * 动画是否正在运行
     * @return
     */
    public boolean isRunningAnimator() {
        return mObjectAnimator != null && mObjectAnimator.isRunning();
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.LoadingView_borderWidth, 3);
        mBorderColor = typedArray.getColor(R.styleable.LoadingView_borderColor, Color.RED);
        @LoadingType int[] types = {TYPE_ROTATE, TYPE_MOTIONLESS};
        mLoadingType = types[typedArray.getInt(R.styleable.LoadingView_loadingType, 0)];
        mAnimatorDuration = typedArray.getInt(R.styleable.LoadingView_animatorDuration, 1200);
        typedArray.recycle();
    }

    private void initPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    private void createClipPath() {
        mClipPath.reset();
        mClipPath.moveTo(mPaddingLeft + mRadius, mPaddingTop);
        mClipPath.addCircle(mPaddingLeft + mRadius, mPaddingTop + mRadius, mRadius, Path.Direction.CCW);
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        if (mLoadingType == TYPE_ROTATE) {
            canvas.rotate(240 * rotateProgress, mWidth * 0.5f, mHeight * 0.5f);
            if (progress < 0) {
                canvas.drawArc(mCircleRectF, -90, 120 * (1 - Math.abs(progress)), false, mBorderPaint);
            } else {
                canvas.drawArc(mCircleRectF, 30 - 120 * (1 - Math.abs(progress)), 120 * (1 - Math.abs(progress)), false, mBorderPaint);
            }
        } else {
            canvas.drawArc(mCircleRectF, -90, 360 * progress, false, mBorderPaint);
        }
        canvas.restore();
    }

    private void startProgressAnimator() {
        if (mObjectAnimator == null) {
            final float startValue = mLoadingType == TYPE_ROTATE ? -1.0f : 0f;
            mObjectAnimator = ObjectAnimator.ofFloat(this, "progress", startValue, 1.0f);
            mObjectAnimator.setDuration(mAnimatorDuration);
            mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
            mObjectAnimator.setRepeatCount(-1);
            mObjectAnimator.setInterpolator(new LinearInterpolator());
        }
        mObjectAnimator.start();
    }

    private void startRotateAnimator() {
        if (mRotateObjectAnimator == null) {
            mRotateObjectAnimator = ObjectAnimator.ofFloat(this, "rotateProgress", 0, 1.0f);
            mRotateObjectAnimator.setDuration(mAnimatorDuration);
            mRotateObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
            mRotateObjectAnimator.setRepeatCount(-1);
            mRotateObjectAnimator.setInterpolator(new LinearInterpolator());
        }
        mRotateObjectAnimator.start();
    }


}
