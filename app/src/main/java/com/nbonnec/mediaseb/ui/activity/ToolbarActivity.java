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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nbonnec.mediaseb.R;

/**
 * Activity with a toolbar and a searchview.
 */
public class ToolbarActivity extends BaseActivity {

    private Toolbar toolbar;

    private MenuItem searchMenuItem;

    private SearchView searchView;

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        searchMenuItem = menu.findItem(R.id.item_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.item_search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return super.onCreateOptionsMenu(menu);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MenuItem getSearchMenuItem() {
        return searchMenuItem;
    }

    public SearchView getSearchView() {
        return searchView;
    }
}
