package com.mooc.ppjoke.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutFeedDetailTypeVideoBinding;
import com.mooc.ppjoke.databinding.LayoutFeedDetailTypeVideoHeaderBinding;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.view.FullScreenPlayerView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

public class VideoViewHandler extends ViewHandler {

    private final LayoutFeedDetailTypeVideoBinding mBinding;
    private final CoordinatorLayout mCoordinator;
    private FullScreenPlayerView playerView;
    private String category;
    private boolean backPressed;

    public VideoViewHandler(FragmentActivity activity) {
        super(activity);

        mBinding = DataBindingUtil.setContentView(activity, R.layout.layout_feed_detail_type_video);
        mInteractionBinding = mBinding.bottomInteraction;
        mRecyclerView = mBinding.recyclerView;
        playerView = mBinding.playerView;
        mCoordinator = mBinding.coordinator;


        View authorInfoView = mBinding.authorInfo.getRoot();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) authorInfoView.getLayoutParams();
        params.setBehavior(new ViewAnchorBehavior(R.id.player_view));

        mBinding.actionClose.setOnClickListener(v -> mActivity.finish());

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) playerView.getLayoutParams();
        ViewZoomBehavior behavior = (ViewZoomBehavior) layoutParams.getBehavior();
        behavior.setViewZoomCallback(height -> {
            int bottom = playerView.getBottom();
            boolean moveUp = height < bottom;
            boolean fullscreen = moveUp ?
                    height >= mCoordinator.getBottom() - mInteractionBinding.getRoot().getHeight()
                    : height >= mCoordinator.getBottom();
            setViewAppearance(fullscreen);
        });

    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mBinding.setFeed(feed);
        category = mActivity.getIntent().getStringExtra(FeedDetailActivity.KEY_CATEGORY);
        mBinding.playerView.bindData(category, mFeed.getWidth(), mFeed.getHeight(),
                mFeed.getCover(), mFeed.getUrl());

        mBinding.playerView.post(() -> {
            boolean fullscreen = mBinding.playerView.getBottom() >= mBinding.coordinator.getBottom();
            setViewAppearance(fullscreen);
        });

        LayoutFeedDetailTypeVideoHeaderBinding headerBinding =
                LayoutFeedDetailTypeVideoHeaderBinding.inflate(LayoutInflater.from(mActivity), mRecyclerView, false);
        headerBinding.setFeed(feed);
        listAdapter.addHeaderView(headerBinding.getRoot());
    }

    private void setViewAppearance(boolean fullscreen) {
        mBinding.setFullscreen(fullscreen);
        mInteractionBinding.setFullscreen(fullscreen);
        mBinding.fullscreenAuthorInfo.getRoot().setVisibility(fullscreen ? View.VISIBLE : View.GONE);

        int inputHeight = mInteractionBinding.getRoot().getMeasuredHeight();
        int ctrlViewHeight = mBinding.playerView.getPlayController().getMeasuredHeight();
        int bottom = mBinding.playerView.getPlayController().getBottom();
        mBinding.playerView.getPlayController()
                .setY(fullscreen ? bottom - inputHeight - ctrlViewHeight : bottom - ctrlViewHeight);
        mInteractionBinding.inputView.setBackgroundResource(fullscreen ? R.drawable.bg_edit_view2 : R.drawable.bg_edit_view);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
        mBinding.playerView.getPlayController().setTranslationY(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!backPressed) {
            mBinding.playerView.inActive();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressed = false;
        mBinding.playerView.onActive();
    }
}
