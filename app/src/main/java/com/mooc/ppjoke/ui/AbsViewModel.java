package com.mooc.ppjoke.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public abstract class AbsViewModel<T> extends ViewModel {
    private final LiveData<PagedList<T>> pageData;
    protected final PagedList.Config config;
    private DataSource dataSource;

    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    DataSource.Factory mFactory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            dataSource = createDataSource();
            return dataSource;
        }
    };

    PagedList.BoundaryCallback<T> mCallback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
            super.onItemAtEndLoaded(itemAtEnd);
        }
    };

    public AbsViewModel() {
        config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
//                .setMaxSize(100)
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance()
                .build();

        pageData = new LivePagedListBuilder(mFactory, config)
                .setInitialLoadKey(0)
//                .setFetchExecutor()
                .setBoundaryCallback(mCallback)
                .build();
    }

    public LiveData<PagedList<T>> getPageData() {
        return pageData;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public MutableLiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    protected abstract DataSource createDataSource();
}
