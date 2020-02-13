package com.mooc.ppjoke.ui.detail;

import android.view.ViewGroup;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutFeedDetailBottomInteractionBinding;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.MutableItemKeyedDataSource;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.EmptyView;

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
        listAdapter = new FeedCommentAdapter();
        mRecyclerView.setAdapter(listAdapter);

        viewModel.setItemId(mFeed.getItemId());
        viewModel.getPageData().observe(mActivity, comments -> {
            listAdapter.submitList(comments);
            handleEmpty(comments.size() > 0);
        });

        mInteractionBinding.inputView.setOnClickListener(v -> {
            if (mCommentDialog == null) {
                mCommentDialog = CommentDialog.newInstance(mFeed.getItemId());
            }

            mCommentDialog.setCommentAddListener(comment -> {
                MutableItemKeyedDataSource<Integer, Comment> mutableItemKeyedDataSource =
                        new MutableItemKeyedDataSource<Integer, Comment>((ItemKeyedDataSource) viewModel.getDataSource()) {
                            @NonNull
                            @Override
                            public Integer getKey(@NonNull Comment item) {
                                return item.getId();
                            }
                        };
                mutableItemKeyedDataSource.data.add(comment);
                PagedList<Comment> currentList = listAdapter.getCurrentList();
                mutableItemKeyedDataSource.data.addAll(currentList);
                PagedList<Comment> comments = mutableItemKeyedDataSource.buildNewPagedList(currentList.getConfig());
                listAdapter.submitList(comments);
            });
            mCommentDialog.show(mActivity.getSupportFragmentManager(), "comment_dialog");
        });
    }


    protected void handleEmpty(boolean hasData) {
        if (hasData) {
            if (mEmptyView != null) {
                listAdapter.removeHeaderView(mEmptyView);
            }
        } else {
            if (mEmptyView == null) {
                mEmptyView = new EmptyView(mActivity);
                mEmptyView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mEmptyView.setTitle(mActivity.getString(R.string.feed_comment_empty));
                listAdapter.addHeaderView(mEmptyView);
            }
        }
    }
}
