package com.episode6.android.simplesms.app.util;

import org.droidkit.DroidKit;
import org.droidkit.ref.CacheResult;
import org.droidkit.util.tricks.ImageTricks;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.episode6.android.simplesms.app.SimpleApplication;
import com.episode6.android.simplesms.provider.Telephony.Mms;

public class ContactUtil {
    
    private static String sCachedPhoneNumber = null;
    
    public static String getDevicePhoneNumber() {
        if (sCachedPhoneNumber == null ) {
            TelephonyManager manager = DroidKit.getTelephonyManager();
            sCachedPhoneNumber = manager.getLine1Number();
        }
        return sCachedPhoneNumber;
    }

    public static String getContactName(String address) {
        CacheResult<String> cacheResult = SimpleApplication.get().getNameCache().get(address);
        if (cacheResult.isCached()) {
            return cacheResult.getCachedObject();
        }
        
        Cursor c;
        String nameResult = null;
        
        if (Mms.isEmailAddress(address)) {
            c = DroidKit.getContentResolver().query(Uri.withAppendedPath(Email.CONTENT_FILTER_URI, address), 
                    new String[] {Email.DISPLAY_NAME}, 
                    null, null, null);
        } else {
            c = DroidKit.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, address), 
                    new String[] {PhoneLookup.DISPLAY_NAME}, 
                    null, null, null);
        }
        
        if (c != null) {
            if (c.moveToFirst()) {
                nameResult = c.getString(0);
            }
            c.close();
        }
        
        if (!TextUtils.isEmpty(nameResult)) {
            SimpleApplication.get().getNameCache().put(address, nameResult);
        } else {
            nameResult = address;
        }
        return nameResult;
    }
    
    public static Bitmap getContactIcon(String address) {
        CacheResult<Bitmap> cacheResult = SimpleApplication.get().getImageCache().get(address);
        if (cacheResult.isCached()) {
            return cacheResult.getCachedObject();
        }
        
        Cursor c;
        Uri lookupUri = null;
        
        
        if (Mms.isEmailAddress(address)) {
            c = DroidKit.getContentResolver().query(Uri.withAppendedPath(Email.CONTENT_FILTER_URI, address), 
                    new String[] {Email.CONTACT_ID}, 
                    null, null, null);
        } else {
            c = DroidKit.getContentResolver().query(
                    Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, address), 
                    new String[] {PhoneLookup._ID}, 
                    null, null, null);
        }
        
        if (c != null) {
            if (c.moveToFirst()) {
                lookupUri = Uri.withAppendedPath(Contacts.CONTENT_URI, c.getString(0));
            }
            c.close();
        }
        
        if (lookupUri != null) {
            Bitmap result = ImageTricks.scaleDownContactPhoto(lookupUri, DroidKit.getPixels(50));
            if (result != null)
                SimpleApplication.get().getImageCache().put(address, result);
            return result;
        }
        return null;
    }
}
