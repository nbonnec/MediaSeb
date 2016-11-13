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
import android.support.v4.view.MenuItemCompat;
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

        searchMenuItem = menu.findItem(R.id.menu_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        /* fill horizontally in landscape */
        searchView.setMaxWidth(Integer.MAX_VALUE);

        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            /* prevent search to be thrown twice.
             * http://stackoverflow.com/a/19655124/857620 */
                getSearchView().clearFocus();
                searchMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.menu_log_in_out);

        if (menuItem != null) {
            int stringId = isSignIn() ? R.string.menu_log_out : R.string.menu_log_in;
            menuItem.setTitle(stringId);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_in_out:
                if (isSignIn()) {
                    logout();
                } else {
                    Intent intent = new Intent(ToolbarActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getActionBarToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        return toolbar;
    }

    public MenuItem getSearchMenuItem() {
        return searchMenuItem;
    }

    public SearchView getSearchView() {
        return searchView;
    }
}
