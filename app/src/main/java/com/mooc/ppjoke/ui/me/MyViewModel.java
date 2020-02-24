package com.mooc.ppjoke.ui.me;

import com.alibaba.fastjson.TypeReference;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.AbsViewModel;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;

public class MyViewModel extends AbsViewModel<Feed> {

    private String profileType;

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    @Override
    protected DataSource createDataSource() {
        return null;
    }

    private class DataSource extends ItemKeyedDataSource<Long, Feed> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, callback);
        }

        private void loadData(Long key, LoadCallback<Feed> callback) {
            ApiResponse<List<Feed>> response = ApiService.get("/feeds/queryProfileFeeds")
                    .addParam("inId", key)
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("pageCount", 10)
                    .addParam("profileType", profileType)
                    .responseType(new TypeReference<ArrayList<Feed>>() {
                    }.getType())
                    .execute();

            List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);

            if (key > 0) {
                getBoundaryPageData().postValue(result.size() > 0);
            }
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Long getKey(@NonNull Feed item) {
            return item.getItemId();
        }
    }
}