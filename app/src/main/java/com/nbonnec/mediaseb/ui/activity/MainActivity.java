package com.nbonnec.mediaseb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragmentBuilder;

import javax.inject.Inject;

public class MainActivity extends ToolbarActivity implements MediaListFragment.OnClickedListener {
    private static final String NEWS_FRAGMENT_TAG = "news_fragment_tag";

    @Inject
    MSSEndpoints mssEndpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        if (savedInstanceState == null && findViewById(R.id.container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,
                            new MediaListFragmentBuilder(mssEndpoints.newsUrl()).build(),
                            NEWS_FRAGMENT_TAG)
                    .commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.news);
        }
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
        MenuItemCompat.collapseActionView(getSearchMenuItem());
        startActivity(intent);
    }

    @Override
    public void onItemClicked(Media media) {
        loadMedia(media);
    }
}
