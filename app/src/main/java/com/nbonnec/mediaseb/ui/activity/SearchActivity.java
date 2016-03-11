/*
 * Copyright 2016 nbonnec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nbonnec.mediaseb.ui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragmentBuilder;

import javax.inject.Inject;

public class SearchActivity extends BaseActivity implements MediaListFragment.OnClickedListener {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private static final String MEDIALIST_FRAGMENT_TAG = "medialist_fragment";

    private static final String STATE_SEARCH = "state_search";

    @Inject
    MSSEndpoints mssEndpoints;

    private MenuItem searchItem;

    // TODO get rid of
    private String search;

    private boolean reload = false;

    MediaListFragment resFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        if (savedInstanceState != null) {
            search = savedInstanceState.getString(STATE_SEARCH);
        } else if (findViewById(R.id.container) != null) {
            handleIntent(getIntent());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,
                            new MediaListFragmentBuilder(mssEndpoints.simpleSearchUrl(search))
                                    .build(),
                            MEDIALIST_FRAGMENT_TAG)
                    .commit();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resFragment = (MediaListFragment) getSupportFragmentManager()
                .findFragmentByTag(MEDIALIST_FRAGMENT_TAG);

        if (reload && search != null) {
            getSearchView().setQuery(search, false);
            // TODO sometimes search is thrown twice
            resFragment.loadPage(mssEndpoints.simpleSearchUrl(search));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchView().clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        // TODO up as home
        getSearchMenuItem().expandActionView();
        getSearchView().setQuery(search, false);
        getSearchView().clearFocus();

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        reload = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        if (search != null) {
            saveInstanceState.putString(STATE_SEARCH, search);
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    public void onItemClicked(Media media) {
        // TODO
    }
}
