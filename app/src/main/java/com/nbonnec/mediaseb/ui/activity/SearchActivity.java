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

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragmentBuilder;

import javax.inject.Inject;

public class SearchActivity extends ToolbarActivity implements MediaListFragment.OnClickedListener {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private static final String SEARCH_FRAMENT_TAG = "search_frament_tag";

    private static final String STATE_SEARCH = "state_search";

    @Inject
    MSSEndpoints mssEndpoints;

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
            reload = false;
        } else if (findViewById(R.id.container) != null) {
            handleIntent(getIntent());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,
                            new MediaListFragmentBuilder(mssEndpoints.simpleSearchUrl(search))
                                    .build(),
                            SEARCH_FRAMENT_TAG)
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
                .findFragmentByTag(SEARCH_FRAMENT_TAG);
        if (reload && search != null) {
            reload = false;
            getSearchView().setQuery(search, false);
            getSearchView().clearFocus();
            resFragment.loadPage(mssEndpoints.simpleSearchUrl(search));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getSearchView().setFocusable(false);
        getSearchView().clearFocus();
        MenuItemCompat.expandActionView(getSearchMenuItem());
        MenuItemCompat.setOnActionExpandListener(getSearchMenuItem(), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                navigateUpOrBack(SearchActivity.this, null);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = getSearchView();
        if (!TextUtils.isEmpty(search)) {
            searchView.setQuery(search, false);
            searchView.setIconified(false);
            searchView.clearFocus();
        }
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

    private void loadMedia(View view, Media media) {
        Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.MEDIA, media);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(SEARCH_FRAMENT_TAG)
                .commit();

        Bundle bundle = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    SearchActivity.this,
                    new Pair<>(view.findViewById(R.id.list_item_image), getString(R.string.transition_name_image)),
                    new Pair<>(findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME),
                    new Pair<>(findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
            );
            bundle = options.toBundle();
        }
        ActivityCompat.startActivity(SearchActivity.this, intent, bundle);
    }


    @Override
    public void onItemClicked(View view, Media media) {
        loadMedia(view, media);
    }

    /**
     *  Search menu collapses on finish, disable the listener which contains
     *  {@link com.nbonnec.mediaseb.ui.activity.BaseActivity#navigateUpOrBack(Activity, Class) navigateUpOrBack}
     */
    @Override
    public void onBackPressed() {
        MenuItemCompat.setOnActionExpandListener(getSearchMenuItem(),
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        /*
         * onBackPressed is not called in this activity.
         * TODO find why.
         */
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
