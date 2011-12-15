package com.episode6.android.simplesms.app;

import com.episode6.android.smiley.SmileyParser;

import android.app.Application;

public class SimpleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmileyParser.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        SmileyParser.destroyInstance();
        super.onTerminate();
    }

}
