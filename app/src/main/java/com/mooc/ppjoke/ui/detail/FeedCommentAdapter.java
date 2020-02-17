package com.mooc.ppjoke.ui.detail;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mooc.ppjoke.databinding.LayoutFeedCommentListItemBinding;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.ui.MutableItemKeyedDataSource;
import com.mooc.ppjoke.ui.home.InteractionPresenter;
import com.mooc.ppjoke.ui.login.UserManager;
import com.mooc.ppjoke.ui.publish.PreviewActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.utils.PixUtils;
import us.bojie.libcommon.extention.AbsPagedListAdapter;

public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {
    private Context mContext;

    protected FeedCommentAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        LayoutFeedCommentListItemBinding binding = LayoutFeedCommentListItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);
        holder.mBinding.commentDelete.setOnClickListener(v ->
                InteractionPresenter.deleteFeedComment(mContext, item.getItemId(), item.getCommentId())
                        .observe((LifecycleOwner) mContext, success -> {
                            if (success) {
                                final PagedList<Comment> currentList = getCurrentList();
                                MutableItemKeyedDataSource<Integer, Comment> dataSource =
                                        new MutableItemKeyedDataSource<Integer, Comment>((ItemKeyedDataSource) currentList.getDataSource()) {
                                            @NonNull
                                            @Override
                                            public Integer getKey(@NonNull Comment item1) {
                                                return item1.getId();
                                            }
                                        };
                                for (Comment comment : currentList) {
                                    if (comment != getItem(position)) {
                                        dataSource.data.add(comment);
                                    }
                                }
                                PagedList<Comment> pagedList = dataSource.buildNewPagedList(currentList.getConfig());
                                submitList(pagedList);
                            }
                        }));
        holder.mBinding.commentCover.setOnClickListener(v -> {
            boolean isVideo = item.getCommentType() == Comment.COMMENT_TYPE_VIDEO;
            PreviewActivity.startActivityForResult((Activity) mContext,
                    isVideo ? item.getVideoUrl() : item.getImageUrl(),
                    isVideo,
                    null);
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutFeedCommentListItemBinding mBinding;

        public ViewHolder(@NonNull View itemView, LayoutFeedCommentListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Comment item) {
            mBinding.setComment(item);
            boolean self = item.getAuthor() != null && UserManager.get().getUserId() == item.getAuthor().getUserId();
            mBinding.labelAuthor.setVisibility(self ? View.VISIBLE : View.GONE);
            mBinding.commentDelete.setVisibility(self ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(item.getImageUrl())) {
                mBinding.commentCover.setVisibility(View.VISIBLE);
                mBinding.commentCover.bindData(item.getWidth(), item.getHeight(), 0,
                        PixUtils.dp2px(200), PixUtils.dp2px(200), item.getImageUrl());
                if (!TextUtils.isEmpty(item.getVideoUrl())) {
                    mBinding.videoIcon.setVisibility(View.VISIBLE);
                } else {
                    mBinding.videoIcon.setVisibility(View.GONE);
                }
            } else {
                mBinding.commentCover.setVisibility(View.GONE);
                mBinding.videoIcon.setVisibility(View.GONE);
                mBinding.commentExt.setVisibility(View.GONE);
            }
        }
    }
}
