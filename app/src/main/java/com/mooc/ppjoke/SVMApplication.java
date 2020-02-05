package com.mooc.ppjoke;

import android.app.Application;

import us.bojie.libnetwork.ApiService;

public class SVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo", null);
    }
}
