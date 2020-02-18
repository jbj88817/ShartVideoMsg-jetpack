package com.mooc.ppjoke.ui.detail;

import android.content.Intent;
import android.view.ViewGroup;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutFeedDetailBottomInteractionBinding;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.model.Feed;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.EmptyView;
import us.bojie.libcommon.utils.PixUtils;

public abstract class ViewHandler {
    private final FeedDetailViewModel viewModel;
    protected FragmentActivity mActivity;
    protected Feed mFeed;
    protected RecyclerView mRecyclerView;
    protected LayoutFeedDetailBottomInteractionBinding mInteractionBinding;
    protected FeedCommentAdapter listAdapter;
    private EmptyView mEmptyView;
    private CommentDialog mCommentDialog;

    public ViewHandler(FragmentActivity activity) {
        mActivity = activity;
        viewModel = ViewModelProviders.of(activity).get(FeedDetailViewModel.class);
    }


    @CallSuper
    public void bindInitData(Feed feed) {
        mFeed = feed;
        mInteractionBinding.setOwner(mActivity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);
        listAdapter = new FeedCommentAdapter(mActivity) {
            @Override
            public void onCurrentListChanged(@Nullable PagedList<Comment> previousList, @Nullable PagedList<Comment> currentList) {
                boolean empty = currentList.size() <= 0;
                handleEmpty(!empty);
            }
        };
        mRecyclerView.setAdapter(listAdapter);

        viewModel.setItemId(mFeed.getItemId());
        viewModel.getPageData().observe(mActivity, comments -> {
            listAdapter.submitList(comments);
            handleEmpty(comments.size() > 0);
        });
        mInteractionBinding.inputView.setOnClickListener(v -> showCommentDialog());
    }

    private void showCommentDialog() {
        if (mCommentDialog == null) {
            mCommentDialog = CommentDialog.newInstance(mFeed.getItemId());
        }
        mCommentDialog.setCommentAddListener(comment -> {
            handleEmpty(true);
            listAdapter.addAndRefreshList(comment);
        });
        mCommentDialog.show(mActivity.getSupportFragmentManager(), "comment_dialog");
    }

    protected void handleEmpty(boolean hasData) {
        if (hasData) {
            if (mEmptyView != null) {
                listAdapter.removeHeaderView(mEmptyView);
            }
        } else {
            if (mEmptyView == null) {
                mEmptyView = new EmptyView(mActivity);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = PixUtils.dp2px(40);
                mEmptyView.setLayoutParams(layoutParams);
                mEmptyView.setTitle(mActivity.getString(R.string.feed_comment_empty));
            }
            listAdapter.addHeaderView(mEmptyView);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCommentDialog != null && mCommentDialog.isAdded()) {
            mCommentDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onBackPressed() {

    }
}
