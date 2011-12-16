package com.episode6.android.simplesms.app.adapter;

import org.droidkit.DroidKit;
import org.droidkit.util.LazyLoader;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.episode6.android.simplesms.R;
import com.episode6.android.simplesms.app.util.DateUtil;
import com.episode6.android.simplesms.app.util.LazyLoadAvatarAndNameTask;
import com.episode6.android.simplesms.provider.Telephony.Mms;
import com.episode6.android.simplesms.provider.Telephony.TextBasedSmsColumns;
import com.episode6.android.simplesms.provider.Telephony.Threads;

public class ConvoListAdapter extends CursorAdapter {
    
    public static CursorLoader getNewCursorLoader(Context context) {
        return new CursorLoader(context, Threads.CONTENT_URI, PROJECTION, null, null, Threads.DATE + " DESC");
    }
    
    public static final String[] PROJECTION = new String[] {
        Threads._ID,
        TextBasedSmsColumns.THREAD_ID,
        TextBasedSmsColumns.ADDRESS,
        TextBasedSmsColumns.DATE,
        TextBasedSmsColumns.BODY,
        TextBasedSmsColumns.READ
    };
    
    public static final int COL_ID = 0;
    public static final int COL_THREAD_ID = 1;
    public static final int COL_ADDRESS = 2;
    public static final int COL_DATE = 3;
    public static final int COL_BODY = 4;
    public static final int COL_READ = 5;
    
    private LazyLoader mLazyLoader;

    public ConvoListAdapter(Context context, LazyLoader lazyLoader) {
        super(context, null, false);
        mLazyLoader = lazyLoader;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TaggedViews v = getTaggedViews(view);
        CursorVals c = new CursorVals(cursor);
        
        view.setTag(c);
        
        v.title.setText(c.address);
        v.subtitle.setText(c.body);
//        v.timestamp.setText(String.valueOf(c.date));
        if (Mms.isEmailAddress(c.address)) {
            v.badge.assignContactFromEmail(c.address, true);
        } else {
            v.badge.assignContactFromPhone(c.address, true);
        }
        if (c.read) {
            view.setBackgroundResource(R.drawable.conversation_item_background_read);
            v.title.setTypeface(Typeface.DEFAULT);
        } else {
            view.setBackgroundResource(R.drawable.conversation_item_background_unread);
            v.title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        
        if (DateUtil.isToday(c.date)) {
            v.timestamp.setText(DateFormat.getTimeFormat(DroidKit.getContext()).format(c.date));
        } else {
            v.timestamp.setText(DateFormat.getMediumDateFormat(DroidKit.getContext()).format(c.date));
        }
        
        mLazyLoader.addTask(new LazyLoadAvatarAndNameTask(c.address, v.badge, v.title, 50));
        
    }
    
    public static TaggedViews getTaggedViews(View parent) {
        TaggedViews v = (TaggedViews) parent.getTag(R.layout.item_conversation);
        if (v == null) {
            v = new TaggedViews(parent);
            parent.setTag(R.layout.item_conversation, v);
        }
        return v;
    }

    public static class CursorVals {
        public long id;
        public String threadId;
        public String address;
        public long date;
        public String body;
        public boolean read;
        
        public CursorVals(Cursor c) {
            id = c.getLong(COL_ID);
            threadId = c.getString(COL_THREAD_ID);
            address = c.getString(COL_ADDRESS);
            date = c.getLong(COL_DATE);
            body = c.getString(COL_BODY);
            read = c.getInt(COL_READ) == 1;
        }
    }
    
    public static class TaggedViews {
        public QuickContactBadge badge;
        public TextView title;
        public TextView subtitle;
        public TextView timestamp;
        
        public TaggedViews(View parent) {
            badge = (QuickContactBadge) parent.findViewById(R.id.badge);
            title = (TextView) parent.findViewById(R.id.title);
            subtitle = (TextView) parent.findViewById(R.id.subtitle);
            timestamp = (TextView) parent.findViewById(R.id.timestamp);
        }
    }
}
