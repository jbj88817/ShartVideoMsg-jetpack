package com.mooc.ppjoke;

import android.app.Application;

import us.bojie.libnetwork.ApiService;

public class SVMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://192.168.86.46:8080/serverdemo", null);
    }
}
