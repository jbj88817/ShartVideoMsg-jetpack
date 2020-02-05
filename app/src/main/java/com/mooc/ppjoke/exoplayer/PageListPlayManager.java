package com.mooc.ppjoke.exoplayer;

import android.app.Application;
import android.net.Uri;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;

import us.bojie.libcommon.AppGlobals;

public class PageListPlayManager {

    private static final ProgressiveMediaSource.Factory mediaSourceFactory;
    private static HashMap<String, PageListPlay> stringPageListPlayHashMap = new HashMap<>();

    static {
        Application application = AppGlobals.getApplication();
        DefaultHttpDataSourceFactory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(application, application.getPackageName()));
        Cache cache = new SimpleCache(application.getCacheDir(),
                new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200),
                null,null, false, true);
        CacheDataSinkFactory cacheDataSinkFactory = new CacheDataSinkFactory(cache, Long.MAX_VALUE);
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(cache, dataSourceFactory, new FileDataSourceFactory(),
                cacheDataSinkFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE, null);
        mediaSourceFactory = new ProgressiveMediaSource.Factory(cacheDataSourceFactory);
    }

    public static MediaSource createMediaSource(String url) {
        return mediaSourceFactory.createMediaSource(Uri.parse(url));
    }

    public static PageListPlay get(String pageName) {
        PageListPlay pageListPlay = stringPageListPlayHashMap.get(pageName);
        if (pageListPlay == null) {
            pageListPlay = new PageListPlay();
            stringPageListPlayHashMap.put(pageName, pageListPlay);
        }
        return pageListPlay;
    }

    public static void release(String pageName) {
        PageListPlay pageListPlay = stringPageListPlayHashMap.get(pageName);
        if (pageListPlay != null) {
            pageListPlay.release();
        }
    }
}
