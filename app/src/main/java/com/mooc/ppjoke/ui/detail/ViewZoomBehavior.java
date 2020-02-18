package com.mooc.ppjoke.ui.detail;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.view.FullScreenPlayerView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import us.bojie.libcommon.utils.PixUtils;

public class ViewZoomBehavior extends CoordinatorLayout.Behavior<FullScreenPlayerView> {
    private OverScroller overScroller;
    private int minHeight;
    private int scrollingId;
    private ViewDragHelper dragHelper;
    private View scrollingView;
    private FullScreenPlayerView refChild;
    private int childOriginalHeight;
    private boolean canFullscreen;
    private FlingRunnable runnable;
    private ViewZoomCallback callback;

    public ViewZoomBehavior() {

    }

    public ViewZoomBehavior(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.view_zoom_behavior, 0, 0);
        scrollingId = array.getResourceId(R.styleable.view_zoom_behavior_scrolling_id, 0);
        minHeight = array.getDimensionPixelOffset(R.styleable.view_zoom_behavior_min_height, PixUtils.dp2px(200));
        array.recycle();

        overScroller = new OverScroller(context);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent,
                                 @NonNull FullScreenPlayerView child,
                                 int layoutDirection) {
        if (dragHelper == null) {
            dragHelper = ViewDragHelper.create(parent, 1.0f, mCallBack);
            scrollingView = parent.findViewById(scrollingId);
            refChild = child;
            childOriginalHeight = child.getMeasuredHeight();
            canFullscreen = childOriginalHeight > parent.getMeasuredWidth();
        }

        return super.onLayoutChild(parent, child, layoutDirection);
    }

    private ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

        //告诉ViewDragHelper 什么时候 可以拦截 手指触摸的这个View的手势分发
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return canFullscreen
                    && refChild.getBottom() >= minHeight
                    && refChild.getBottom() <= childOriginalHeight;
        }

        //告诉ViewDragHelper 在屏幕上滑动多少距离才算是拖拽
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return dragHelper.getTouchSlop();
        }

        //告诉ViewDragHelper手指拖拽的这个View 本次滑动最终能够移动的距离
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (refChild == null || dy == 0) {
                return 0;
            }

            //dy>0 代表手指从屏幕上放往屏幕下方滑动
            //dy<0 代表手指从屏幕下方 往屏幕上方滑动

            //手指从下往上滑动。dy<0 这意味着refchild的底部 会被向上移动。所以 它的底部的最小值 不能小于minheight
            if (dy < 0 && refChild.getBottom() < minHeight
                    //手指从上往下滑动。dy>0 意味着refChild的底部会被向下移动。所以它的底部的最大值 不能超过父容器的高度 也即childOriginalHeight
                    || (dy > 0 && refChild.getBottom() > childOriginalHeight)
                    //手指 从屏幕上方 往下滑动。如果scrollinghview 还没有滑动到列表的最顶部，
                    // 也意味围着列表还可以向下滑动，此时咱们应该让列表自行滑动，不做拦截
                    || (dy > 0 && (scrollingView != null && scrollingView.canScrollVertically(-1)))) {
                return 0;
            }

            int maxConsumed;
            if (dy > 0) {
                //如果本次滑动的dy值 追加上 refchild的bottom 值会超过 父容器的最大高度值
                //此时 咱们应该 计算一下 本次能够滑动的最大距离
                if (refChild.getBottom() + dy > childOriginalHeight) {
                    maxConsumed = childOriginalHeight - refChild.getBottom();
                } else {
                    maxConsumed = dy;
                }
            } else {
                //else 分支里面 dy的值 是负值。
                //如果本次滑动的值  dy 加上refChild的bottom 会小于minHeight。 那么咱们应该计算一下本次能够滑动的最大距离
                if (refChild.getBottom() + dy < minHeight) {
                    maxConsumed = minHeight - refChild.getBottom();
                } else {
                    maxConsumed = dy;
                }
            }

            ViewGroup.LayoutParams params = refChild.getLayoutParams();
            params.height = params.height + maxConsumed;
            refChild.setLayoutParams(params);
            if (callback != null) {
                callback.onDragZoom(params.height);
            }
            return maxConsumed;
        }

        //指的是 我们的手指 从屏幕上 离开的时候 会被调用
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (refChild.getBottom() > minHeight && refChild.getBottom() < childOriginalHeight && yvel != 0) {
                refChild.removeCallbacks(runnable);
                runnable = new FlingRunnable(refChild);
                runnable.fling((int) xvel, (int) yvel);
            }
        }
    };

    public void setViewZoomCallback(ViewZoomCallback callback) {
        this.callback = callback;
    }

    public interface ViewZoomCallback {
        void onDragZoom(int height);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, @NonNull MotionEvent ev) {
        if (!canFullscreen || dragHelper == null) {
            return super.onTouchEvent(parent, child, ev);
        }

        dragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, @NonNull MotionEvent ev) {
        if (!canFullscreen || dragHelper == null) {
            return super.onInterceptTouchEvent(parent, child, ev);
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    private class FlingRunnable implements Runnable {
        private View mFlingView;

        public FlingRunnable(View flingView) {
            mFlingView = flingView;
        }

        public void fling(int xvel, int yvel) {
            overScroller.fling(0, mFlingView.getBottom(), xvel, yvel,
                    0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            run();
        }

        @Override
        public void run() {
            ViewGroup.LayoutParams params = mFlingView.getLayoutParams();
            int height = params.height;
            if (overScroller.computeScrollOffset() && height >= minHeight && height <= childOriginalHeight) {
                int newHeight = Math.min(overScroller.getCurrY(), childOriginalHeight);
                if (newHeight != height) {
                    params.height = newHeight;
                    mFlingView.setLayoutParams(params);

                    if (callback != null) {
                        callback.onDragZoom(newHeight);
                    }
                }
                ViewCompat.postOnAnimation(mFlingView, this);
            } else {
                mFlingView.removeCallbacks(this);
            }
        }
    }
}
