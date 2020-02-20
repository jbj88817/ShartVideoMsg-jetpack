package com.mooc.ppjoke.ui.find;

import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.AbsViewModel;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

public class TagListViewModel extends AbsViewModel<TagList> {

    @Override
    protected DataSource createDataSource() {
        return null;
    }

    private class DataSource extends ItemKeyedDataSource<Long, TagList> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<TagList> callback) {

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {

        }

        @NonNull
        @Override
        public Long getKey(@NonNull TagList item) {
            return null;
        }
    }
}
