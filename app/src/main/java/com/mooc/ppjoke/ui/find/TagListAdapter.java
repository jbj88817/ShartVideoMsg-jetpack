package com.mooc.ppjoke.ui.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mooc.ppjoke.databinding.LayoutTagListItemBinding;
import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.home.InteractionPresenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.extention.AbsPagedListAdapter;

public class TagListAdapter extends AbsPagedListAdapter<TagList, TagListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;

    protected TagListAdapter(Context context) {
        super(new DiffUtil.ItemCallback<TagList>() {
            @Override
            public boolean areItemsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.tagId == newItem.tagId;
            }

            @Override
            public boolean areContentsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        LayoutTagListItemBinding binding = LayoutTagListItemBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        TagList item = getItem(position);
        holder.bindData(item);
        holder.mBinding.actionFollow.setOnClickListener(v ->
                InteractionPresenter.toggleTagLike((LifecycleOwner) mContext, item));
        holder.itemView.setOnClickListener(v -> TagFeedListActivity.startActivity(mContext, item));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutTagListItemBinding mBinding;

        public ViewHolder(@NonNull View itemView, LayoutTagListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(TagList item) {
            mBinding.setTagList(item);
        }
    }
}
