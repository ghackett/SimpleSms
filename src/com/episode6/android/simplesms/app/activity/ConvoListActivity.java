package com.episode6.android.simplesms.app.activity;

import org.droidkit.util.LazyLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.episode6.android.simplesms.R;
import com.episode6.android.simplesms.app.adapter.ConvoListAdapter;
import com.episode6.android.simplesms.app.adapter.ConvoListAdapter.CursorVals;


public class ConvoListActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnItemClickListener {
    
    private static final int LOADER_ID = 78364;
    
    private static final int MENU_COMPOSE = Menu.FIRST+1;
    
    private ListView mListView;
    private ConvoListAdapter mAdapter;
    private LazyLoader mLazyLoader;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLazyLoader = new LazyLoader();
        mListView = (ListView) findViewById(R.id.list);
        mListView.setFadingEdgeLength(0);
        mAdapter = new ConvoListAdapter(this, mLazyLoader);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_COMPOSE, 0, R.string.menu_compose).setIcon(R.drawable.ic_menu_compose).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case MENU_COMPOSE:
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("sms:"));
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_ID:
                return ConvoListAdapter.getNewCursorLoader(this);
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
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        mLazyLoader.shutdown();
        mLazyLoader = null;
        super.onDestroy();
    }



    @Override
    public void onItemClick(AdapterView<?> list, View view, int position, long id) {
        Object tag = view.getTag();
        if (tag instanceof ConvoListAdapter.CursorVals) {
            CursorVals c = (CursorVals)tag;
            if (id == c.id) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("sms:" + Uri.encode(c.address)));
                startActivity(i);
            }
        }
    }
    
}