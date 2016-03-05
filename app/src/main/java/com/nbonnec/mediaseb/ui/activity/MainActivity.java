package com.nbonnec.mediaseb.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.nbonnec.mediaseb.MediasebApp;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.services.MSSService;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private static final String MEDIALIST_FRAGMENT_TAG = "medialist_fragment";

    @Inject
    MSSService mssService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        MediasebApp app = MediasebApp.get(getApplicationContext());
        app.inject(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MediaListFragment firstFragment = new MediaListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, firstFragment, MEDIALIST_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        MediaListFragment fragment;
        fragment = (MediaListFragment) getSupportFragmentManager()
                .findFragmentByTag(MEDIALIST_FRAGMENT_TAG);
        fragment.loadNews();
    }
}
