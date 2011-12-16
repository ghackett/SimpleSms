package com.episode6.android.simplesms.app.adapter;

import org.droidkit.util.LazyLoader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ConversationAdapter extends CursorAdapter {

    private LazyLoader mLazyLoader;
    
    public ConversationAdapter(Context context, LazyLoader lazyLoader) {
        super(context, null, false);
        mLazyLoader = lazyLoader;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO Auto-generated method stub

    }

}
