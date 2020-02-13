package com.mooc.ppjoke.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

public abstract class MutableItemKeyedDataSource<Key, Value> extends ItemKeyedDataSource<Key, Value> {

    private ItemKeyedDataSource mDataSource;

    public List<Value> data = new ArrayList<>();

    public PagedList<Value> buildNewPagedList(PagedList.Config config) {
        return new PagedList.Builder<>(this, config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();
    }

    public MutableItemKeyedDataSource(ItemKeyedDataSource dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Value> callback) {
        callback.onResult(data);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        mDataSource.loadAfter(params, callback);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        callback.onResult(Collections.emptyList());
    }

    @NonNull
    @Override
    public abstract Key getKey(@NonNull Value item);
}
