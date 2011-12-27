package com.episode6.android.simplesms.app.adapter;

import org.droidkit.DroidKit;
import org.droidkit.util.LazyLoader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.episode6.android.simplesms.R;
import com.episode6.android.simplesms.app.util.ContactUtil;
import com.episode6.android.simplesms.app.util.LazyLoadAvatarTask;
import com.episode6.android.simplesms.app.util.LazyLoadNameTask;
import com.episode6.android.simplesms.provider.Telephony;
import com.episode6.android.simplesms.provider.Telephony.Mms;
import com.episode6.android.simplesms.provider.Telephony.Sms;
import com.episode6.android.simplesms.provider.Telephony.Sms.Conversations;
import com.episode6.android.simplesms.provider.Telephony.Threads;

public class ConversationAdapter extends CursorAdapter {
    
    public static CursorLoader getNewCursorLoader(Context ctx, long threadId) {
        return new CursorLoader(ctx, 
                Uri.withAppendedPath(Threads.CONTENT_URI, String.valueOf(threadId)), 
                PROJECTION,
                null,null,
//                Conversations.THREAD_ID + "=?", 
//                new String[] {String.valueOf(threadId)}, 
                Conversations.DATE);
    }
    
    public static final String[] PROJECTION = new String[] {
        Conversations._ID,
        Conversations.ADDRESS,
        Conversations.BODY,
        Conversations.DATE,
        Conversations.TYPE
    };
    
    public static final int COL_ID = 0;
    public static final int COL_ADDR = 1;
    public static final int COL_BODY = 2;
    public static final int COL_DATE = 3;
    public static final int COL_TYPE = 4;

    private LazyLoader mLazyLoader;
    
    public ConversationAdapter(Context context, LazyLoader lazyLoader) {
        super(context, null, false);
        mLazyLoader = lazyLoader;
    }

    
    @Override
    public int getItemViewType(int position) {
        try {
            Cursor c = (Cursor) getItem(position);
            if (isOutgoing(c))
                return 1;
            return 0;
        } catch (Throwable t) {
            return 0;
        }
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (isOutgoing(cursor))
            return LayoutInflater.from(context).inflate(R.layout.item_message_mine, parent, false);
        else
            return LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
    }
    
    private boolean isOutgoing(Cursor cursor) {
        int type = cursor.getInt(COL_TYPE);
        return Telephony.Sms.isOutgoingFolder(type);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CursorVals c = new CursorVals(cursor);
        TaggedViews v = getTaggedViews(view);
        view.setTag(c);
        
        v.msgView.setText(c.body);
        
//        if (Mms.isEmailAddress(c.address)) {
//            v.badge.assignContactFromEmail(c.address, true);
//        } else {
//            v.badge.assignContactFromPhone(c.address, true);
//        }
        
        if (Sms.isOutgoingFolder(c.type)) {
//            v.nameView.setText("Me");
            v.badge.assignContactFromPhone(ContactUtil.getDevicePhoneNumber(), true);
            mLazyLoader.addTask(new LazyLoadAvatarTask(ContactUtil.getDevicePhoneNumber(), v.badge));
        } else {
            if (Mms.isEmailAddress(c.address)) {
                v.badge.assignContactFromEmail(c.address, true);
            } else {
                v.badge.assignContactFromPhone(c.address, true);
            }
            mLazyLoader.addTask(new LazyLoadAvatarTask(c.address, v.badge));
            mLazyLoader.addTask(new LazyLoadNameTask(c.address, v.nameView));            
        }
        
    }

    private static TaggedViews getTaggedViews(View parent) {
        TaggedViews v = (TaggedViews) parent.getTag(R.layout.item_message);
        if (v == null) {
            v = new TaggedViews(parent);
            parent.setTag(R.layout.item_message, v);
        }
        return v;
    }
    
    private static class TaggedViews {
        public QuickContactBadge badge;
        public TextView nameView;
        public TextView msgView;
        
        public TaggedViews(View parent) {
            badge = (QuickContactBadge) parent.findViewById(R.id.badge);
            nameView = (TextView) parent.findViewById(R.id.name);
            msgView = (TextView) parent.findViewById(R.id.message);
        }
    }
    
    public static class CursorVals {
        public long id;
        public String address;
        public String body;
        public long date;
        public int type;
        
        public CursorVals(Cursor c) {
            id = c.getLong(COL_ID);
            address = c.getString(COL_ADDR);
            body = c.getString(COL_BODY);
            date = c.getLong(COL_DATE);
            type = c.getInt(COL_TYPE);
        }
    }
}
