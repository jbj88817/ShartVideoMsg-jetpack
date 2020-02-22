package com.mooc.ppjoke.ui.find;

import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.AbsViewModel;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;

public class TagFeedListViewModel extends AbsViewModel<Feed> {

    private String feedType;

    @Override
    protected DataSource createDataSource() {
        return new DataSource();
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    private class DataSource extends ItemKeyedDataSource<Integer, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, callback);
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

    private void loadData(Integer feedId, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        ApiResponse<List<Feed>> response = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("pageCount", 10)
                .addParam("feedType", feedType)
                .addParam("feedId", feedId)
                .execute();

        List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
        callback.onResult(result);

    }
}
