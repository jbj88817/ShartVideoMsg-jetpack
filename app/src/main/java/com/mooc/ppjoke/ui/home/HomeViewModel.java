package com.mooc.ppjoke.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.AbsViewModel;
import com.mooc.ppjoke.ui.MutablePageKeyedDataSource;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;
import us.bojie.libnetwork.Request;

public class HomeViewModel extends AbsViewModel<Feed> {

    private static final String TAG = "HomeViewModel";
    private volatile boolean withCache = true;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);
    private String mFeedType;

    @Override
    protected DataSource createDataSource() {
        return new FeedDataSource();
    }

    public void setFeedType(String feedType) {
        mFeedType = feedType;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(0, params.requestedLoadSize, callback);
            withCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.getId();
        }
    }

    private void loadData(int key, int pageSize, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key > 0) {
            loadAfter.set(true);
        }

//        /feeds/queryHotFeedsList
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", pageSize)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if (withCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.d(TAG, "onCacheSuccess: ");
                    List<Feed> body = response.body;
                    MutablePageKeyedDataSource<Integer, Feed> dataSource = new MutablePageKeyedDataSource<>();
                    dataSource.data.addAll(body);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);
                }
            });
        }

        try {
            Request netRequest = withCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(data);

            if (key > 0) {
                getBoundaryPageData().postValue(data.size() > 0);
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "loadData: key" + key);
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(() -> loadData(id, config.pageSize, callback));
    }
}