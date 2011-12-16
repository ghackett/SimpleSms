package com.episode6.android.simplesms.app.util;

import android.text.TextUtils;

import com.episode6.android.simplesms.R;


public class ImageUtil {
//    private static int IMAGE_COUNTER = 0;
    
    public static int getDefaultContactIcon(String address) {
        int mod = 0;
        if (!TextUtils.isEmpty(address)) {
            mod = (int)address.charAt(address.length()-1);
            mod = mod%3;
        }
        switch(mod) {
            case 0:
                return R.drawable.ic_contact_picture;
            case 1:
                return R.drawable.ic_contact_picture_2;
            default:
                return R.drawable.ic_contact_picture_3;
        }
    }
}
