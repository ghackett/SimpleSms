package com.episode6.android.simplesms.app.util;

import org.droidkit.ref.CacheResult;
import org.droidkit.util.LazyLoaderTask;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.episode6.android.simplesms.app.SimpleApplication;

public class LazyLoadAvatarTask implements LazyLoaderTask {

    
    private String mAddress = null;
    private ImageView mImageView = null;
    private Bitmap mImageResult = null;
    
    public LazyLoadAvatarTask(String address, ImageView imageView) {
        mAddress = address;
        mImageView = imageView;
    }

    @Override
    public boolean shouldAddTask() {
        if (mImageView == null)
            return false;
        
        if (TextUtils.isEmpty(mAddress)) {
            onLoadComplete();
            return false;
        }
        
        CacheResult<Bitmap> bitmapResult = SimpleApplication.get().getImageCache().get(mAddress);
        if (bitmapResult.isCached()) {
            mImageResult = bitmapResult.getCachedObject();
            onLoadComplete();
            return false;
        }
        
        
        mImageView.setImageResource(ImageUtil.getDefaultContactIcon(mAddress));
        return true;
    }

    @Override
    public View getView() {
        return mImageView;
    }

    @Override
    public String getViewTag() {
        return mAddress;
    }

    @Override
    public void loadInBackground() {
        mImageResult = ContactUtil.getContactIcon(mAddress);
    }

    @Override
    public void onLoadComplete() {
        if (mImageResult == null || mImageResult.isRecycled())
            mImageView.setImageResource(ImageUtil.getDefaultContactIcon(mAddress));
        else
            mImageView.setImageBitmap(mImageResult);
    }

}
