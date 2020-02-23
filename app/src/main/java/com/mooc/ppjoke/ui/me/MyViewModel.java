package com.mooc.ppjoke.ui.me;

import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.AbsViewModel;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

public class MyViewModel extends AbsViewModel<Feed> {

    @Override
    protected DataSource createDataSource() {
        return null;
    }

    private class DataSource extends ItemKeyedDataSource<Long, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Feed> callback) {

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {

        }

        @NonNull
        @Override
        public Long getKey(@NonNull Feed item) {
            return null;
        }
    }
}