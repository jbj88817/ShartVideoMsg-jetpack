package com.mooc.ppjoke.ui.find;

import android.os.Bundle;

import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.AbsListFragment;
import com.mooc.ppjoke.ui.MutableItemKeyedDataSource;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TagListFragment extends AbsListFragment<TagList, TagListViewModel> {

    public static final String KEY_TAG_TYPE = "tag_type";

    public static TagListFragment newInstance(String tagType) {
        Bundle args = new Bundle();
        args.putString(KEY_TAG_TYPE, tagType);
        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PagedListAdapter<TagList, RecyclerView.ViewHolder> getAdapter() {
        return null;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList<TagList> currentList = getAdapter().getCurrentList();
        long tagId = currentList == null ? 0 : currentList.get(currentList.size() - 1).tagId;
        mViewModel.loadData(tagId, new ItemKeyedDataSource.LoadCallback() {
            @Override
            public void onResult(@NonNull List data) {
                if (data.size() > 0) {
                    MutableItemKeyedDataSource<Long, TagList> mutableItemKeyedDataSource =
                            new MutableItemKeyedDataSource<Long, TagList>((ItemKeyedDataSource) mViewModel.getDataSource()) {

                                @NonNull
                                @Override
                                public Long getKey(@NonNull TagList item) {
                                    return item.tagId;
                                }
                            };
                    mutableItemKeyedDataSource.data.addAll(currentList);
                    mutableItemKeyedDataSource.data.addAll(data);
                    PagedList<TagList> pagedList = mutableItemKeyedDataSource.buildNewPagedList(currentList.getConfig());
                    submitList(pagedList);
                } else {
                    finishRefresh(false);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }
}
