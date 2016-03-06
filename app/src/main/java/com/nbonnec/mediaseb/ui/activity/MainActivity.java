package com.nbonnec.mediaseb.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.nbonnec.mediaseb.MediasebApp;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;

public class MainActivity extends AppCompatActivity implements MediaListFragment.OnFragmentStartedListener {
    private static final String NEWS_FRAGMENT_TAG = "news_fragment_tag";

    private MediaListFragment resFragment;

    private boolean restoredInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        MediasebApp app = MediasebApp.get(getApplicationContext());
        app.inject(this);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            restoredInstanceState = true;
            resFragment = (MediaListFragment) getSupportFragmentManager()
                    .findFragmentByTag(NEWS_FRAGMENT_TAG);
        } else if (findViewById(R.id.container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MediaListFragment(), NEWS_FRAGMENT_TAG)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.news));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentStarted() {
        if (!restoredInstanceState) {
            resFragment = (MediaListFragment) getSupportFragmentManager()
                    .findFragmentByTag(NEWS_FRAGMENT_TAG);
            resFragment.loadNews();
        }
    }
}
