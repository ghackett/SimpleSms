package com.episode6.android.simplesms.app;

import org.droidkit.DroidKit;
import org.droidkit.ref.CacheManager;
import org.droidkit.ref.SoftBitmapCache;
import org.droidkit.ref.SoftCache;

import android.app.Application;

import com.episode6.android.smiley.SmileyParser;

public class SimpleApplication extends Application {
    
    private static SimpleApplication sInstance = null;
       
    public static SimpleApplication get() {
        return sInstance;
    }
    
    private SoftBitmapCache mImageCache = null;
    private SoftCache<String> mNameCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
        DroidKit.onApplicationCreate(this);
        SmileyParser.init(getApplicationContext());
        sInstance = this;
        mImageCache = new SoftBitmapCache();
        mNameCache = new SoftCache<String>();
    }
    
    public SoftBitmapCache getImageCache() {
        return mImageCache; 
    }
    
    public SoftCache<String> getNameCache() {
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
