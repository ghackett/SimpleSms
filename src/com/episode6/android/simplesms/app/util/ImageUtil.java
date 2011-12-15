package com.episode6.android.simplesms.app.util;

import com.episode6.android.simplesms.R;


public class ImageUtil {
    private static int IMAGE_COUNTER = 0;
    
    public static int getDefaultContactIcon() {
        int mod = (++IMAGE_COUNTER)%3;
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
