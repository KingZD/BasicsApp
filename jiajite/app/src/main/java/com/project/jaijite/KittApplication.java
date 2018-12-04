package com.project.jaijite;

import android.app.Application;

public class KittApplication extends Application {
    private static KittApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static KittApplication getApplication() {
        return application;
    }
}
