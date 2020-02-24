package com.mooc.ppjoke.ui.me;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.MutableItemKeyedDataSource;
import com.mooc.ppjoke.ui.home.FeedAdapter;
import com.mooc.ppjoke.ui.home.InteractionPresenter;
import com.mooc.ppjoke.utils.TimeUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

public class ProfileListAdapter extends FeedAdapter {
    protected ProfileListAdapter(Context context, String category) {
        super(context, category);
    }

    @Override
    public int getItemViewType2(int position) {
        if (TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT)) {
            return R.layout.layout_feed_type_comment;
        }
        return super.getItemViewType2(position);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        super.onBindViewHolder2(holder, position);
        View dissView = holder.itemView.findViewById(R.id.diss);
        View deleteView = holder.itemView.findViewById(R.id.feed_delete);
        TextView createTime = holder.itemView.findViewById(R.id.create_time);

        Feed feed = getItem(position);
        createTime.setVisibility(View.VISIBLE);
        createTime.setText(TimeUtils.calculate(feed.getCreateTime()));

        boolean isCommentTab = TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT);
        if (isCommentTab) {
            dissView.setVisibility(View.GONE);
        }

        deleteView.setOnClickListener(v -> {
            if (isCommentTab) {
                InteractionPresenter.deleteFeedComment(mContext, feed.getItemId(), feed.getTopComment().getCommentId())
                        .observe((LifecycleOwner) mContext, success -> refreshList(feed));
            } else {
                InteractionPresenter.deleteFeed(mContext, feed.getItemId())
                        .observe((LifecycleOwner) mContext, success -> refreshList(feed));
            }
        });
    }

    private void refreshList(Feed delete) {
        PagedList<Feed> currentList = getCurrentList();
        MutableItemKeyedDataSource<Long, Feed> dataSource = new MutableItemKeyedDataSource<Long, Feed>((ItemKeyedDataSource) currentList.getDataSource()) {
            @NonNull
            @Override
            public Long getKey(@NonNull Feed item) {
                return item.getItemId();
            }
        };

        for (Feed feed : currentList) {
            if (feed != delete) {
                dataSource.data.add(feed);
            }
        }
        PagedList<Feed> pagedList = dataSource.buildNewPagedList(currentList.getConfig());
        submitList(pagedList);
    }
}
