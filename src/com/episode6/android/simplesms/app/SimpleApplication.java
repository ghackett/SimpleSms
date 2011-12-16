package com.episode6.android.simplesms.app;

import org.droidkit.DroidKit;
import org.droidkit.ref.CacheManager;
import org.droidkit.ref.WeakBitmapCache;
import org.droidkit.ref.WeakCache;

import com.episode6.android.smiley.SmileyParser;

import android.app.Application;

public class SimpleApplication extends Application {
    
    private static SimpleApplication sInstance = null;
       
    public static SimpleApplication get() {
        return sInstance;
    }
    
    private WeakBitmapCache mImageCache = null;
    private WeakCache<String> mNameCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
        DroidKit.onApplicationCreate(this);
        SmileyParser.init(getApplicationContext());
        sInstance = this;
        mImageCache = new WeakBitmapCache();
        mNameCache = new WeakCache<String>();
    }
    
    public WeakBitmapCache getImageCache() {
        return mImageCache; 
    }
    
    public WeakCache<String> getNameCache() {
        return mNameCache;
    }

    @Override
    public void onTerminate() {
        CacheManager.clearAllCaches();
        SmileyParser.destroyInstance();
        DroidKit.onApplicationTerminate();
        sInstance = null;
        mImageCache = null;
        mNameCache = null;
        super.onTerminate();
    }

}
