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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.nbonnec.mediaseb.MediasebApp;
import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;

public class SearchActivity extends AppCompatActivity {
    private static final String MEDIALIST_FRAGMENT_TAG = "medialist_fragment";
    private String search;
    MediaListFragment resFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        MediasebApp app = MediasebApp.get(getApplicationContext());
        app.inject(this);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            MediaListFragment firstFragment = new MediaListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, firstFragment, MEDIALIST_FRAGMENT_TAG)
                    .commit();
        }

        handleIntent(getIntent());
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

        resFragment = (MediaListFragment) getSupportFragmentManager()
                .findFragmentByTag(MEDIALIST_FRAGMENT_TAG);

        if (search != null) {
            resFragment.loadResults(search);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);

        if (search != null) {
            resFragment.loadResults(search);
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search = intent.getStringExtra(SearchManager.QUERY);
        }
    }
}
