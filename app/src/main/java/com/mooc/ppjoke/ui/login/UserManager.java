package com.mooc.ppjoke.ui.login;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import us.bojie.libnetwork.cache.CacheManager;
import com.mooc.ppjoke.model.User;

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
        return mUser == null ? false : mUser.getExpiresTime() > System.currentTimeMillis();
    }

    public User getUser() {
        return isLogin() ? mUser : null;
    }

    public long getUserId() {
        return isLogin() ? mUser.getUserId() : 0;
    }

}
