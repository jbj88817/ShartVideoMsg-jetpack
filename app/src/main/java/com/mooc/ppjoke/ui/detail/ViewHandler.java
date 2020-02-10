package com.mooc.ppjoke.ui.detail;

import com.mooc.ppjoke.databinding.LayoutFeedDetailBottomInteractionBinding;
import com.mooc.ppjoke.model.Feed;

import androidx.annotation.CallSuper;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ViewHandler {

    protected FragmentActivity mActivity;
    protected Feed mFeed;
    protected RecyclerView mRecyclerView;
    protected LayoutFeedDetailBottomInteractionBinding mInteractionBinding;
    protected FeedCommentAdapter listAdapter;

    public ViewHandler(FragmentActivity activity) {
        mActivity = activity;
    }

    @CallSuper
    public void bindInitData(Feed feed) {
        mFeed = feed;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);
        listAdapter = new FeedCommentAdapter();
        mRecyclerView.setAdapter(listAdapter);
    }
}
