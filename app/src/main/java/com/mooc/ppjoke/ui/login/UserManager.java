package com.mooc.ppjoke.ui.login;

import android.content.Context;
import android.content.Intent;

import com.mooc.ppjoke.model.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import us.bojie.libcommon.AppGlobals;
import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;
import us.bojie.libnetwork.cache.CacheManager;

import static us.bojie.libcommon.utils.ToastUtils.showToast;

public class UserManager {

    private static final String KEY_CACHE_USER = "cache_user";
    private MutableLiveData<User> mUserLiveData = new MutableLiveData<>();

    private static UserManager mUserManager = new UserManager();
    private User mUser;

    private UserManager() {
        User cache = (User) CacheManager.getCache(KEY_CACHE_USER);
        if (cache != null && cache.getExpiresTime() > System.currentTimeMillis()) {
            mUser = cache;
        }
    }

    public static UserManager get() {
        return mUserManager;
    }

    public void save(User user) {
        mUser = user;
        CacheManager.save(KEY_CACHE_USER, user);
        if (mUserLiveData.hasObservers()) {
            mUserLiveData.postValue(user);
        }
    }

    public LiveData<User> login(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return mUserLiveData;
    }

    public boolean isLogin() {
        return mUser != null && mUser.getExpiresTime() > System.currentTimeMillis();
    }

    public User getUser() {
        return isLogin() ? mUser : null;
    }

    public long getUserId() {
        return isLogin() ? mUser.getUserId() : 0;
    }

    public LiveData<User> refresh() {
        if (!isLogin()) {
            return login(AppGlobals.getApplication());
        }
        MutableLiveData<User> liveData = new MutableLiveData<>();
        ApiService.get("/user/query")
                .addParam("userId", getUserId())
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        save(response.body);
                        liveData.postValue(getUser());
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        showToast(response.message);
                        liveData.postValue(null);
                    }
                });

        return liveData;
    }

    public void logout() {
        CacheManager.delete(KEY_CACHE_USER, mUser);
        mUser = null;
    }
}
