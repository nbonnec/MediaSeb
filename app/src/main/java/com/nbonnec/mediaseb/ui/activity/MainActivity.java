package com.nbonnec.mediaseb.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.DetailsFragment;
import com.nbonnec.mediaseb.ui.fragment.DetailsFragmentBuilder;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragmentBuilder;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MediaListFragment.OnClickedListener {
    private static final String NEWS_FRAGMENT_TAG = "news_fragment_tag";
    private static final String DETAILS_FRAGMENT_TAG = "details_fragmet_tag";

    private MediaListFragment resFragment;

    private MenuItem searchItem;

    private boolean restoredInstanceState = false;

    @Inject
    MSSEndpoints mssEndpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            restoredInstanceState = true;
            resFragment = (MediaListFragment) getSupportFragmentManager()
                    .findFragmentByTag(NEWS_FRAGMENT_TAG);
        } else if (findViewById(R.id.container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,
                            new MediaListFragmentBuilder(mssEndpoints.newsUrl()).build(),
                            NEWS_FRAGMENT_TAG)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.news));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        searchItem = menu.findItem(R.id.item_search);
        MenuItemCompat.collapseActionView(searchItem);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    private void loadMedia(Media media) {
        DetailsFragment fragment = new DetailsFragmentBuilder(media).build();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        getSupportActionBar().setTitle(media.getTitle());
        transaction.replace(R.id.container, fragment, DETAILS_FRAGMENT_TAG);
        transaction.addToBackStack(DETAILS_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    public void onItemClicked(Media media) {
        Log.d(TAG, "ITEM CLICKED, LOL");
        loadMedia(media);
    }
}
