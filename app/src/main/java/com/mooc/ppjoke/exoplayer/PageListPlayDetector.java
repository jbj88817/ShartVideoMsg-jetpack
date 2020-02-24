package com.mooc.ppjoke.exoplayer;

import android.graphics.Point;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

public class PageListPlayDetector {

    private List<IPlayTarget> mTargets = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private IPlayTarget playingTarget;

    public void addTarget(IPlayTarget target) {
        mTargets.add(target);
    }

    public void removeTarget(IPlayTarget target) {
        mTargets.remove(target);
    }

    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    playingTarget = null;
                    mTargets.clear();
                    mRecyclerView.removeCallbacks(delayAutoPlay);
                    recyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });

        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        recyclerView.addOnScrollListener(scrollListener);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener(){

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dx == 0 && dy == 0) {
                //When AdapterDataObserver#onItemRangeInserted, maybe it's not on RecyclerView。
                //So now recyclerview.getChildCount() may equals 0。
                //When childView inflated on RecyclerView，it would call onScrolled()
                //and dx,dy are both equals 0
                autoPlay();
            } else {
                if (playingTarget != null && playingTarget.isPlaying() && !isTargetInBounds(playingTarget)) {
                    playingTarget.inActive();
                }
            }
        }
    };

    Runnable delayAutoPlay = () -> autoPlay();

    private void postAutoPlay() {
        mRecyclerView.post(delayAutoPlay);
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            postAutoPlay();
        }
    };

    private void autoPlay() {
        if (mTargets.size() <= 0 || mRecyclerView.getChildCount() <= 0) {
            return;
        }

        if (playingTarget != null && playingTarget.isPlaying() && isTargetInBounds(playingTarget)) {
            return;
        }

        IPlayTarget activeTarget = null;
        for (IPlayTarget target : mTargets) {
            boolean inBounds = isTargetInBounds(target);
            if (inBounds) {
                activeTarget = target;
                break;
            }
        }
        if (activeTarget != null) {
            if (playingTarget != null && playingTarget.isPlaying()) {
                playingTarget.inActive();
            }
            playingTarget = activeTarget;
            activeTarget.onActive();
        }
    }

    private boolean isTargetInBounds(IPlayTarget target) {
        ViewGroup owner = target.getOwner();
        ensureRecyclerViewLocation();
        if (!owner.isShown() || !owner.isAttachedToWindow()) {
            return false;
        }

        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        int center = location[1] + owner.getHeight() / 2;

        return center >= rvLocation.x && center <= rvLocation.y;
    }

    private Point rvLocation = null;

    private Point ensureRecyclerViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            mRecyclerView.getLocationOnScreen(location);

            int top = location[1];
            int bottom = top + mRecyclerView.getHeight();

            rvLocation = new Point(top, bottom);
        }
        return rvLocation;
    }

    public void onResume() {
        if (playingTarget != null) {
            playingTarget.onActive();
        }
    }

    public void onPause() {
        if (playingTarget != null) {
            playingTarget.inActive();
        }
    }
}
