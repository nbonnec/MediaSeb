package com.nbonnec.mediaseb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
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


    private boolean restoredInstanceState = false;

    @Inject
    MSSEndpoints mssEndpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

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

        getToolbar().setTitle(R.string.news);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MenuItem searchMenuItem = getSearchMenuItem();
                if (searchMenuItem != null) {
                    searchMenuItem.collapseActionView();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

   private void loadMedia(Media media) {
       Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
       intent.putExtra(DetailsActivity.MEDIA, media);
       /*
       ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
             MainActivity.this, transitionView, DetailActivity.EXTRA_IMAGE);
       ActivityCompat.startActivity(activity, new Intent(activity, DetailActivity.class),
             options.toBundle());
             */
       startActivity(intent);
    }

    @Override
    public void onItemClicked(Media media) {
        Log.d(TAG, "ITEM CLICKED, LOL");
        loadMedia(media);
    }
}
