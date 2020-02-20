package com.mooc.ppjoke.ui.find;

import com.mooc.ppjoke.model.TagList;
import com.mooc.ppjoke.ui.AbsViewModel;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.ItemKeyedDataSource;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;

public class TagListViewModel extends AbsViewModel<TagList> {

    private String tagType;
    private int offset;
    private AtomicBoolean loadAfter = new AtomicBoolean();

    @Override
    protected DataSource createDataSource() {
        return new DataSource();
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    private class DataSource extends ItemKeyedDataSource<Long, TagList> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<TagList> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {
            loadData(params.key, callback);
        }

        private void loadData(Long key, LoadCallback<TagList> callback) {
            if (key > 0) {
                loadAfter.set(true);
            }
            ApiResponse<List<TagList>> response = ApiService.get("/tag/queryTagList")
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("tagId", key)
                    .addParam("tagType", tagType)
                    .addParam("pageCount", 10)
                    .addParam("offset", offset)
                    .execute();

            List<TagList> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);
            if (key > 0) {
                loadAfter.set(false);
                offset += result.size();
                getBoundaryPageData().postValue(result.size() > 0);
            } else {
                offset = result.size();
            }
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Long getKey(@NonNull TagList item) {
            return item.tagId;
        }
    }

    public void loadData(long tagId, ItemKeyedDataSource.LoadCallback callback) {
        if (tagId <= 0 || loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }

        ArchTaskExecutor.getIOThreadExecutor().execute(() ->
                ((DataSource) getDataSource()).loadData(tagId, callback));
    }

}
