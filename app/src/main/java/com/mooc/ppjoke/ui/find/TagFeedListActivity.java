package com.mooc.ppjoke.ui.find;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityLayoutTagFeedListBinding;
import com.mooc.ppjoke.databinding.LayoutTagFeedListHeaderBinding;
import com.mooc.ppjoke.exoplayer.PageListPlayDetector;
import com.mooc.ppjoke.exoplayer.PageListPlayManager;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.home.FeedAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.EmptyView;
import us.bojie.libcommon.extention.AbsPagedListAdapter;
import us.bojie.libcommon.utils.PixUtils;

public class TagFeedListActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    public static final String KEY_TAG_LIST = "tag_list";
    public static final String KEY_FEED_TYPE = "tag_feed_type";
    private ActivityLayoutTagFeedListBinding mBinding;
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private SmartRefreshLayout refreshLayout;
    private TagList tagList;
    private PageListPlayDetector playDetector;
    private boolean shouldPause = true;
    private AbsPagedListAdapter adapter;
    private int totalScrollY;
    private TagFeedListViewModel tagFeedListViewModel;

    public static void startActivity(Context context, TagList tagList) {
        Intent intent = new Intent(context, TagFeedListActivity.class);
        intent.putExtra(KEY_TAG_LIST, tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_tag_feed_list);
        recyclerView = mBinding.refreshLayout.recyclerView;
        emptyView = mBinding.refreshLayout.emptyView;
        refreshLayout = mBinding.refreshLayout.refreshLayout;

        mBinding.actionBack.setOnClickListener(this);

        tagList = getIntent().getParcelableExtra(KEY_TAG_LIST);
        mBinding.setTagList(tagList);
        mBinding.setLifecycleOwner(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = (AbsPagedListAdapter) getAdapter();
        recyclerView.setAdapter(adapter);
        playDetector = new PageListPlayDetector(this, recyclerView);

        addHeaderView();

        tagFeedListViewModel = ViewModelProviders.of(this).get(TagFeedListViewModel.class);
        tagFeedListViewModel.setFeedType(tagList.title);
        tagFeedListViewModel.getPageData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);

    }

    private void submitList(PagedList<Feed> feeds) {
        if (feeds.size() > 0) {
            adapter.submitList(feeds);
        }

        finishRefresh(feeds.size() > 0);
    }

    private void finishRefresh(boolean hasData) {
        PagedList currentList = adapter.getCurrentList();
        hasData = currentList != null && currentList.size() > 0 || hasData;
        if (hasData) {
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        RefreshState state = refreshLayout.getState();
        if (state.isOpening&&state.isHeader) {
            refreshLayout.finishRefresh();
        } else if (state.isOpening&& state.isFooter) {
            refreshLayout.finishLoadMore();
        }
    }

    private void addHeaderView() {
        LayoutTagFeedListHeaderBinding headerBinding = LayoutTagFeedListHeaderBinding
                .inflate(LayoutInflater.from(this), recyclerView, false);
        headerBinding.setTagList(tagList);
        headerBinding.setOwner(this);
        adapter.addHeaderView(headerBinding.getRoot());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY += dy;
                boolean overHeight = totalScrollY > PixUtils.dp2px(48);
                mBinding.tagLogo.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                mBinding.tagTitle.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                mBinding.topBarFollow.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                mBinding.actionBack.setImageResource(overHeight ? R.drawable.icon_back_black : R.drawable.icon_back_white);
                mBinding.topBar.setBackgroundColor(overHeight ? Color.WHITE : Color.TRANSPARENT);
                mBinding.topLine.setVisibility(overHeight ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public PagedListAdapter getAdapter() {
        return new FeedAdapter(this, KEY_FEED_TYPE) {
            @Override
            public void onViewAttachedToWindow(@NonNull FeedAdapter.ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                playDetector.removeTarget(holder.getListPlayerView());
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                boolean isVideo = feed.getItemType() == Feed.TYPE_VIDEO;
                shouldPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldPause) {
            playDetector.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playDetector.onResume();
    }

    @Override
    protected void onDestroy() {
        PageListPlayManager.release(KEY_FEED_TYPE);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        tagFeedListViewModel.getDataSource().invalidate();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }
}
