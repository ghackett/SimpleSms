package com.episode6.android.simplesms.app.util;

import org.droidkit.ref.CacheResult;
import org.droidkit.util.LazyLoaderTask;

import com.episode6.android.simplesms.app.SimpleApplication;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class LazyLoadNameTask implements LazyLoaderTask {
    
    private String mAddress = null;
    private TextView mNameView = null;
    private String mNameResult = null;
    
    public LazyLoadNameTask(String address, TextView nameView) {
        mAddress = address;
        mNameView = nameView;
    }

    @Override
    public boolean shouldAddTask() {
        if (mNameView == null)
            return false;
        
        if (TextUtils.isEmpty(mAddress)) {
            onLoadComplete();
            return false;
        }
        
        CacheResult<String> cacheResult = SimpleApplication.get().getNameCache().get(mAddress);
        if (cacheResult.isCached()) {
            mNameResult = cacheResult.getCachedObject();
            onLoadComplete();
            return false;
        }
        
        mNameView.setText(mAddress);
        return true;
    }

    @Override
    public View getView() {
        return mNameView;
    }

    @Override
    public String getViewTag() {
        return mAddress;
    }

    @Override
    public void loadInBackground() {
        mNameResult = ContactUtil.getContactName(mAddress);
    }

    @Override
    public void onLoadComplete() {
        if (TextUtils.isEmpty(mNameResult)) {
            mNameView.setText(mAddress);
        } else {
            mNameView.setText(mNameResult);
        }
    }

}
