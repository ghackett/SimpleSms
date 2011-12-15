package com.episode6.android.simplesms.app;

import org.droidkit.DroidKit;

import com.episode6.android.smiley.SmileyParser;

import android.app.Application;

public class SimpleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DroidKit.onApplicationCreate(this);
        SmileyParser.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        SmileyParser.destroyInstance();
        DroidKit.onApplicationTerminate();
        super.onTerminate();
    }

}
