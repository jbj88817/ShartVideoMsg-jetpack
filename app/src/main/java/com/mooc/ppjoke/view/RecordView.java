package com.mooc.ppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.mooc.ppjoke.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import us.bojie.libcommon.utils.PixUtils;

public class RecordView extends View implements View.OnClickListener, View.OnLongClickListener {
    private static final int PROGRESS_INTERVAL = 100;
    private final int radius;
    private final int progressWidth;
    private final int progressColor;
    private final int fillColor;
    private final int maxDuration;
    private final Paint fillPaint;
    private final Paint progressPaint;
    private int progressMaxValue;
    private int progressValue;
    private boolean isRecording;
    private long startRecordTime;
    private OnRecordListener mListener;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView, defStyleAttr, defStyleRes);
        radius = typedArray.getDimensionPixelOffset(R.styleable.RecordView_radius, 0);
        progressWidth = typedArray.getDimensionPixelOffset(R.styleable.RecordView_progress_width, PixUtils.dp2px(3));
        progressColor = typedArray.getColor(R.styleable.RecordView_progress_color, Color.RED);
        fillColor = typedArray.getColor(R.styleable.RecordView_fill_color, Color.WHITE);
        maxDuration = typedArray.getInteger(R.styleable.RecordView_duration, 10);
        setMaxDuration(maxDuration);
        typedArray.recycle();

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(fillColor);
        fillPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                progressValue++;
                postInvalidate();
                if (progressValue <= progressMaxValue) {
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL);
                } else {
                    finishRecord();
                }
            }
        };

        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isRecording = true;
                startRecordTime = System.currentTimeMillis();
                handler.sendEmptyMessage(0);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                long now = System.currentTimeMillis();
                if (now - startRecordTime > ViewConfiguration.getLongPressTimeout()) {
                    finishRecord();
                }
                handler.removeCallbacksAndMessages(null);
                isRecording = false;
                startRecordTime = 0;
                progressValue = 0;
                postInvalidate();
            }
            return true;
        });


        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    private void finishRecord() {
        if (mListener != null) {
            mListener.onFinish();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int hight = getHeight();
        if (isRecording) {
            canvas.drawCircle(width / 2, hight / 2, width / 2, fillPaint);
            int left = 0;
            int top = 0;
            int right = width;
            int bottom = hight;
            float sweepAngle = (progressValue / progressMaxValue) * 360;
            canvas.drawArc(left, top, right, bottom, -90, sweepAngle, false, progressPaint);
        } else {
            canvas.drawCircle(width / 2, hight / 2, radius / 2, fillPaint);
        }
    }

    private void setMaxDuration(int maxDuration) {
        progressMaxValue = maxDuration * 1000 / PROGRESS_INTERVAL;
    }

    public void setOnRecordListener(OnRecordListener listener){
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mListener != null) {
            mListener.onLongClick();
        }
        return true;
    }

    public interface OnRecordListener {
        void onClick();

        void onLongClick();

        void onFinish();
    }
}
