package com.mooc.ppjoke.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityFeedDetailTypeImageBinding;
import com.mooc.ppjoke.databinding.LayoutFeedDetailTypeImageHeaderBinding;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.view.SVMImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ImageViewHandler extends ViewHandler {

    protected ActivityFeedDetailTypeImageBinding mImageBinding;
    protected LayoutFeedDetailTypeImageHeaderBinding mHeaderBinding;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);
        mImageBinding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_detail_type_image);
        mRecyclerView = mImageBinding.recyclerView;
        mInteractionBinding = mImageBinding.bottomInteractionLayout;
        mImageBinding.actionClose.setOnClickListener(v -> mActivity.finish());
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mImageBinding.setFeed(mFeed);

        mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding.inflate(LayoutInflater.from(mActivity), mRecyclerView, false);
        mHeaderBinding.setFeed(mFeed);

        SVMImageView headerImage = mHeaderBinding.headerImage;
        int width = mFeed.getWidth();
        int height = mFeed.getHeight();
        headerImage.bindData(width, height, width > height ? 0 : 16, mFeed.getCover());
        listAdapter.addHeaderView(mHeaderBinding.getRoot());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean visible = mHeaderBinding.getRoot().getTop() <= -mImageBinding.titleLayout.getMeasuredHeight();
                mImageBinding.authorInfoLayout.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);
                mImageBinding.title.setVisibility(visible ? View.GONE : View.VISIBLE);
            }
        });
        handleEmpty(false);
    }
}
