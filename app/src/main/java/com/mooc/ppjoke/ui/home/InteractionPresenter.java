package com.mooc.ppjoke.ui.home;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.model.User;
import com.mooc.ppjoke.ui.ShareDialog;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.Date;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import us.bojie.libcommon.AppGlobals;
import us.bojie.libcommon.extention.LiveDataBus;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

public class InteractionPresenter {
    public static final String DATA_FROM_INTERACTION = "data_from_interaction";

    private static final String URL_TOGGLE_FEED_LIK = "/ugc/toggleFeedLike";

    private static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";

    private static final String URL_SHARE = "/ugc/increaseShareCount";

    private static final String URL_TOGGLE_COMMENT_LIKE = "/ugc/toggleCommentLike";

    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> loginLiveData = UserManager.get().login(AppGlobals.getApplication());
            loginLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleFeedLikeInternal(feed);
                    }
                    loginLiveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedLikeInternal(feed);
    }

    private static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIK)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.getItemId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasLiked(hasLiked);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
                        }

                    }
                });
    }

    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> loginLiveData = UserManager.get().login(AppGlobals.getApplication());
            loginLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleFeedDissInternal(feed);
                    }
                    loginLiveData.removeObserver(this);
                }
            });
            return;
        }

        toggleFeedDissInternal(feed);
    }

    private static void toggleFeedDissInternal(Feed feed) {

        ApiService.get(URL_TOGGLE_FEED_DISS).addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.getItemId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasdiss(hasLiked);
                        }
                    }
                });
    }

    public static void openShare(Context context, Feed feed) {
        String url = "https://h5.pipix.com/item/%s?app_id=1319&app=super&timestamp=%s&user_id=%s";
        String format = String.format(url, feed.getItemId(), new Date().getTime(), UserManager.get().getUserId());
        ShareDialog shareDialog = new ShareDialog(context);
        shareDialog.setShareContent(format);
        shareDialog.setShareItemClickListener(v -> ApiService.get(URL_SHARE)
                .addParam("itemId", feed.getItemId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            int count = response.body.getIntValue("count");
                            feed.getUgc().setShareCount(count);
                        }
                    }
                }));
        shareDialog.show();
    }

    public static void toggleCommentLike(LifecycleOwner owner, Comment comment) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> loginLiveData = UserManager.get().login(AppGlobals.getApplication());
            loginLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleCommentLikeInternal(comment);
                    }
                    loginLiveData.removeObserver(this);
                }
            });
            return;
        }

        toggleCommentLikeInternal(comment);
    }

    private static void toggleCommentLikeInternal(Comment comment) {
        ApiService.get(URL_TOGGLE_COMMENT_LIKE)
                .addParam("commentId", comment.getCommentId())
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBooleanValue("hasLiked");
                            comment.getUgc().setHasLiked(hasLiked);
                        }
                    }
                });
    }

    public static void toggleFeedFavorite(LifecycleOwner owner, Feed feed) {

        if (!UserManager.get().isLogin()) {
            UserManager.get().login(AppGlobals.getApplication()).observe(owner, user -> {
                if (user != null) {
                    toggleFeedFavorite(feed);
                }
            });
        } else {
            toggleFeedFavorite(feed);
        }
    }

    private static void toggleFeedFavorite(Feed feed) {
        ApiService.get("/ugc/toggleFavorite")
                .addParam("itemId", feed.getItemId())
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasFavorite = response.body.getBooleanValue("hasFavorite");
                            feed.getUgc().setHasFavorite(hasFavorite);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }

    public static void toggleFollowUser(LifecycleOwner owner, Feed feed) {
        if (!UserManager.get().isLogin()) {
            UserManager.get().login(AppGlobals.getApplication()).observe(owner, user -> toggleFollowUser(feed));
        } else {
            toggleFollowUser(feed);
        }
    }

    private static void toggleFollowUser(Feed feed) {
        ApiService.get("/ugc/toggleUserFollow")
                .addParam("followUserId", UserManager.get().getUserId())
                .addParam("userId", feed.getAuthor().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasFollow = response.body.getBooleanValue("hasLiked");
                            feed.getAuthor().setHasFollow(hasFollow);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }
}
