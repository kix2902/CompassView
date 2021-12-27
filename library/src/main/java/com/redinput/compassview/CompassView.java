package com.redinput.compassview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class CompassView extends View {
    OnCompassDragListener mListener;

    public interface OnCompassDragListener {
        /**
         * Indicates when a drag event has ocurred
         *
         * @param degrees Actual value of the compass
         */
        public void onCompassDragListener(float degrees);
    }

    private Paint mTextPaint, mMainLinePaint, mSecondaryLinePaint, mTerciaryLinePaint, mMarkerPaint, mDegreeLinePaint;
    private Path pathMarker;

    private int mTextColor, mBackgroundColor, mLineColor, mMarkerColor, mTimerPeriod;
    private float mDegrees, mTextSize, mRangeDegrees, mTargetDegrees, mStep;
    private boolean mShowMarker;
    private Activity mActivity;
    private Timer mTimer;

    private GestureDetector mDetector;

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CompassView, 0, 0);

        mBackgroundColor = a.getColor(R.styleable.CompassView_backgroundColor, Color.BLACK);
        mMarkerColor = a.getColor(R.styleable.CompassView_markerColor, Color.RED);
        mShowMarker = a.getBoolean(R.styleable.CompassView_showMarker, true);
        mLineColor = a.getColor(R.styleable.CompassView_lineColor, Color.WHITE);
        mTextColor = a.getColor(R.styleable.CompassView_textColor, Color.WHITE);
        mTextSize = a.getDimension(R.styleable.CompassView_textSize, 15 * getResources().getDisplayMetrics().scaledDensity);
        mDegrees = a.getFloat(R.styleable.CompassView_degrees, 0);
        mRangeDegrees = a.getFloat(R.styleable.CompassView_rangeDegrees, 180f);

        a.recycle();

        checkValues();
        init();
    }

    private void checkValues() {
        if ((mDegrees < 0) || (mDegrees > 359))
            throw new IndexOutOfBoundsException(getResources()
                    .getString(R.string.out_index_degrees));

        if ((mRangeDegrees < 10) || (mRangeDegrees > 360))
            throw new IndexOutOfBoundsException(getResources().getString(
                    R.string.out_index_range_degrees));
    }

    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mMainLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMainLinePaint.setStrokeWidth(8f);

        mSecondaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondaryLinePaint.setStrokeWidth(6f);

        mTerciaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTerciaryLinePaint.setStrokeWidth(3f);

        mDegreeLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreeLinePaint.setStrokeWidth(1f);

        mMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkerPaint.setStyle(Paint.Style.FILL);
        pathMarker = new Path();

        mDetector = new GestureDetector(getContext(), new mGestureListener());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable("instanceState", super.onSaveInstanceState());
        b.putFloat("degrees", mDegrees);

        return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle b = (Bundle) state;
            mDegrees = b.getFloat("degrees", 0);

            state = b.getParcelable("instanceState");
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int minWidth = (int) Math.floor(50 * getResources().getDisplayMetrics().density);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        } else {
            result = minWidth + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int minHeight = (int) Math.floor(5 * getResources().getDisplayMetrics().density) + (int) (2*mTextSize);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        } else {
            result = minHeight + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mMainLinePaint.setColor(mLineColor);
        mSecondaryLinePaint.setColor(mLineColor);
        mTerciaryLinePaint.setColor(mLineColor);
        mDegreeLinePaint.setColor(mLineColor);

        mMarkerPaint.setColor(mMarkerColor);

        canvas.drawColor(mBackgroundColor);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int unitHeight = (height - paddingTop - paddingBottom) / 12;

        float pixDeg = (width - paddingLeft - paddingRight) / mRangeDegrees;

        int minDegrees = Math.round(mDegrees - mRangeDegrees / 2), maxDegrees = Math.round(mDegrees
                + mRangeDegrees / 2);

        if (mRangeDegrees>50){

            for (int i = -180; i < 540; i += 15) {
                if ((i >= minDegrees) && (i <= maxDegrees)) {
                    canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees), height - paddingBottom,
                            paddingLeft + pixDeg * (i - minDegrees), 10 * unitHeight + paddingTop,
                            mTerciaryLinePaint);

                    if (i % 45 == 0) {


                        if (i % 90 == 0) {
                            canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
                                    height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
                                    6 * unitHeight + paddingTop, mMainLinePaint);
                        } else {
                            canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
                                    height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
                                    8 * unitHeight + paddingTop, mSecondaryLinePaint);
                        }

                        String coord = "";
                        switch (i) {
                            case -90:
                            case 270:
                                coord = getResources().getString(R.string.compass_west);
                                break;
                            case -45:
                            case 315:
                                coord = getResources().getString(R.string.compass_northwest);
                                break;
                            case 0:
                            case 360:
                                coord = getResources().getString(R.string.compass_north);
                                break;

                            case 45:
                            case 405:
                                coord = getResources().getString(R.string.compass_northeast);
                                break;
                            case 90:
                            case 450:
                                coord = getResources().getString(R.string.compass_east);
                                break;

                            case 135:
                            case 495:
                                coord = getResources().getString(R.string.compass_southeast);
                                break;

                            case -180:
                            case 180:
                                coord = getResources().getString(R.string.compass_south);
                                break;

                            case -135:
                            case 225:
                                coord = getResources().getString(R.string.compass_southwest);
                                break;
                        }

                        canvas.drawText(coord, paddingLeft + pixDeg * (i - minDegrees), 5 * unitHeight
                                + paddingTop, mTextPaint);

                    }
                }
            }
        }else{
            for (int i = -180; i < 540; i ++) {
                if ((i >= minDegrees) && (i <= maxDegrees)) {
                    canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
                            height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
                            10 * unitHeight + paddingTop, mDegreeLinePaint);

                    if (i%5==0){
                        canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
                                height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
                                8 * unitHeight + paddingTop, mSecondaryLinePaint);
                    }

                    if (i%10==0) {
                        canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
                                height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
                                6 * unitHeight + paddingTop, mMainLinePaint);
                        canvas.drawText(Integer.toString((i+360)%360), paddingLeft + pixDeg * (i - minDegrees), 5 * unitHeight
                                + paddingTop, mTextPaint);
                    }

                }
            }
        }

        if (mShowMarker) {
            pathMarker.reset();
            pathMarker.moveTo(width / 2, 3 * unitHeight + paddingTop);
            pathMarker.lineTo((width / 2) + 20, paddingTop);
            pathMarker.lineTo((width / 2) - 20, paddingTop);
            pathMarker.close();
            canvas.drawPath(pathMarker, mMarkerPaint);
        }
    }

    public void setDegrees(float degrees){
        setDegrees(degrees,false);
    }

    public void setDegrees(float degrees,boolean animation) {
        if (mTimer!=null) mTimer.cancel();
        if (animation){
            mTargetDegrees = (degrees+360) % 360;  //add 360 to make sure modulo operation returns positive value
            mStep = (((mTargetDegrees - mDegrees + 360) % 360) <= 180) ? 1 : -1;

            //change compass speed depending on difference to target value. Fast if big change required, slow if only minor change
            if (Math.abs(mTargetDegrees-mDegrees)<=15||Math.abs(mTargetDegrees-mDegrees)>=345) mTimerPeriod = 60;
            else if (Math.abs(mTargetDegrees-mDegrees)<=30||Math.abs(mTargetDegrees-mDegrees)>=330) mTimerPeriod = 45;
            else if (Math.abs(mTargetDegrees-mDegrees)<=60||Math.abs(mTargetDegrees-mDegrees)>=300) mTimerPeriod = 30;
            else mTimerPeriod=15;

            TimerTask timerTask;
            mTimer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (Math.abs(mTargetDegrees-mDegrees)<=0.5||Math.abs(mTargetDegrees-mDegrees)>=359.5) {
                        mTimer.cancel();
                        mDegrees = mTargetDegrees;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                                requestLayout();
                            }
                        });
                    }else{
                        mDegrees = (mDegrees +360 + mStep) % 360;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                                requestLayout();
                            }
                        });
                    }
                }
            };
            mTimer.schedule(timerTask,0,mTimerPeriod);
        }else{
            mDegrees=(degrees+360) % 360;
            invalidate();
            requestLayout();
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
        requestLayout();
    }

    public void setLineColor(int color) {
        mLineColor = color;
        invalidate();
        requestLayout();
    }

    public void setMarkerColor(int color) {
        mMarkerColor = color;
        invalidate();
        requestLayout();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        invalidate();
        requestLayout();
    }

    public void setShowMarker(boolean show) {
        mShowMarker = show;
        invalidate();
        requestLayout();
    }

    public void setTextSize(int size) {
        mTextSize = size;
        invalidate();
        requestLayout();
    }

    public void setRangeDegrees(float range) {
        if ((mRangeDegrees < 10) || (mRangeDegrees > 360))
            throw new IndexOutOfBoundsException(getResources().getString(
                    R.string.out_index_range_degrees)
                    + mRangeDegrees);

        mRangeDegrees = range;
        invalidate();
        requestLayout();
    }

    public float getDegrees(){
        return mDegrees;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener != null) {
            boolean result = mDetector.onTouchEvent(event);
            if (!result) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    result = true;
                }
            }
            return result;
        } else {
            return true;
        }
    }

    private class mGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mDegrees += distanceX / 5;
            if (mDegrees < 0) {
                mDegrees += 360;
            } else if (mDegrees >= 360) {
                mDegrees -= 360;
            }

            if (mListener != null) {
                mListener.onCompassDragListener(mDegrees);
            }

            postInvalidate();
            return true;
        }
    }

    public void setOnCompassDragListener(OnCompassDragListener onCompassDragListener) {
        this.mListener = onCompassDragListener;
    }
}
