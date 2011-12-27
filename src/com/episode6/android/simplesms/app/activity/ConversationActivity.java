package com.episode6.android.simplesms.app.activity;

import java.lang.ref.WeakReference;

import org.droidkit.DroidKit;
import org.droidkit.util.LazyLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.episode6.android.simplesms.R;
import com.episode6.android.simplesms.app.adapter.ConversationAdapter;
import com.episode6.android.simplesms.app.util.ContactUtil;
import com.episode6.android.simplesms.provider.Telephony;

public class ConversationActivity extends FragmentActivity implements OnItemClickListener, LoaderCallbacks<Cursor> {
    
    private static final int LOADER_ID = 78365;
    
    private ListView mListView;
    private ConversationAdapter mAdapter;
    private LazyLoader mLazyLoader;
    private boolean mResumedOnce = false;
    private String mAddress;
    private long mThreadId = 0;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread);
        
        mLazyLoader = new LazyLoader(10);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setFadingEdgeLength(0);
        mAdapter = new ConversationAdapter(this, mLazyLoader);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        
        initWithIntent(getIntent());
    }

    private void initWithIntent(Intent intent) {
        Uri intentData = intent.getData();
        if (intentData != null) {
            if (intentData.isOpaque()) {
                String address = intentData.getSchemeSpecificPart();
                setAddress(address);
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        mLazyLoader.shutdown();
        mLazyLoader = null;
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, ConvoListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mResumedOnce) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            mResumedOnce = true;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_ID:
                if (mThreadId != 0)
                    return ConversationAdapter.getNewCursorLoader(this, mThreadId);
                break;
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(data);
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(null);
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> list, View view, int position, long id) {
        // TODO Auto-generated method stub
        
    }
    
    private void setAddress(String address) {
        mAddress = address;
        getSupportActionBar().setTitle(mAddress);
        getSupportActionBar().setSubtitle(null);
        new ThreadLoader(address, this).execute((Void)null);
    }
    
    private void setNameAndThreadId(String name, long threadId) {
        if (TextUtils.isEmpty(name) || mAddress.equals(name)) {
            getSupportActionBar().setTitle(mAddress);
            getSupportActionBar().setSubtitle(null);
        } else {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setSubtitle(mAddress);
        }
        mThreadId = threadId;
        
        try {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        } catch (NullPointerException e) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }
    
    private static class ThreadLoader extends AsyncTask<Void, Void, Void> {

        private String mAddress = null;
        private WeakReference<ConversationActivity> mActivity = null;
        
        private String mName = null;
        private long mThreadId = 0;
        
        public ThreadLoader(String address, ConversationActivity activity) {
            mAddress = address;
            mActivity = new WeakReference<ConversationActivity>(activity);
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            mName = ContactUtil.getContactName(mAddress);
            mThreadId = Telephony.Threads.getOrCreateThreadId(DroidKit.getContext(), mAddress);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ConversationActivity a = mActivity.get();
            if (a != null) {
                a.setNameAndThreadId(mName, mThreadId);
            }
            super.onPostExecute(result);
        }
        
        
        
    }

}
