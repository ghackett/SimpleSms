package com.episode6.android.simplesms.app;

import org.droidkit.DroidKit;
import org.droidkit.ref.CacheResult;
import org.droidkit.ref.WeakBitmapCache;
import org.droidkit.ref.WeakCache;
import org.droidkit.util.LazyLoaderTask;
import org.droidkit.util.tricks.ImageTricks;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.episode6.android.simplesms.app.util.ImageUtil;
import com.episode6.android.simplesms.provider.Telephony.Mms;

public class LazyLoadAvatarAndNameTask implements LazyLoaderTask {
    
    private static WeakBitmapCache sImageCache = new WeakBitmapCache();
    private static WeakCache<String> sNameCache = new WeakCache<String>();
    
    private String mAddress = null;
    private ImageView mImageView = null;
    private TextView mNameView = null;
    private Bitmap mImageResult = null;
    private String mNameResult = null;
    private int mMaxDimensionDp;
    
    public LazyLoadAvatarAndNameTask(String address, ImageView imageView, TextView nameView, int maxDimensionDp) {
        mAddress = address;
        mImageView = imageView;
        mNameView = nameView;
        mMaxDimensionDp = maxDimensionDp;
    }

    @Override
    public boolean shouldAddTask() {
        if (mImageView == null)
            return false;
        if (mNameView == null)
            return false;
        
        if (TextUtils.isEmpty(mAddress)) {
            onLoadComplete();
            return false;
        }
        
        CacheResult<Bitmap> bitmapResult = sImageCache.get(mAddress);
        CacheResult<String> nameResult = sNameCache.get(mAddress);
        if (bitmapResult.isCached() && nameResult.isCached()) {
            mImageResult = bitmapResult.getCachedObject();
            mNameResult = nameResult.getCachedObject();
            onLoadComplete();
            return false;
        }
        
        
        mImageView.setImageResource(ImageUtil.getDefaultContactIcon());
        mNameView.setText(mAddress);
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
        Cursor c;
        Uri lookupUri = null;
        
        if (Mms.isEmailAddress(mAddress)) {
            c = DroidKit.getContentResolver().query(Uri.withAppendedPath(Email.CONTENT_FILTER_URI, mAddress), 
                    new String[] {Email.DISPLAY_NAME, Email.CONTACT_ID}, 
                    null, null, null);
        } else {
            c = DroidKit.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, mAddress), 
                    new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup._ID}, 
                    null, null, null);
        }
        
        if (c != null) {
            if (c.moveToFirst()) {
                mNameResult = c.getString(0);
                sNameCache.put(mAddress, mNameResult);
                lookupUri = Uri.withAppendedPath(Contacts.CONTENT_URI, c.getString(1));
            }
            c.close();
        }
        
        if (lookupUri != null) {
            mImageResult = ImageTricks.scaleDownContactPhoto(lookupUri, DroidKit.getPixels(mMaxDimensionDp));
            sImageCache.put(mAddress, mImageResult);
        }
    }

    @Override
    public void onLoadComplete() {
        if (mImageResult == null || mImageResult.isRecycled())
            mImageView.setImageResource(ImageUtil.getDefaultContactIcon());
        else
            mImageView.setImageBitmap(mImageResult);
        
        if (TextUtils.isEmpty(mNameResult)) {
            mNameView.setText(mAddress);
        } else {
            mNameView.setText(mNameResult);
        }
    }

}
