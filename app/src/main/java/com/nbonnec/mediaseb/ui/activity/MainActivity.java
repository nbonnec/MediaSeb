package com.nbonnec.mediaseb.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.view.Window;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragmentBuilder;

import java.util.ArrayList;
import java.util.List;

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
            /* we want the fragment quickly for lollipop transitions. */
            getSupportFragmentManager().executePendingTransactions();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.news);
        }
    }

    private void loadMedia(View view, Media media) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.MEDIA, media);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(NEWS_FRAGMENT_TAG)
                .commit();

        MenuItemCompat.collapseActionView(getSearchMenuItem());

        Bundle bundle = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<Pair<View, String>> transitions = new ArrayList<>();

            transitions.add(Pair.create(view.findViewById(R.id.list_item_image), getString(R.string.transition_name_image)));
            transitions.add(Pair.create(findViewById(R.id.toolbar), getString(R.string.transition_name_toolbar)));
            transitions.add(Pair.create(findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
            /* does not exists in landscape */
            if (findViewById(android.R.id.navigationBarBackground) != null) {
                transitions.add(Pair.create(findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
            }

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    MainActivity.this, transitions.toArray(new Pair[transitions.size()])
            );
            bundle = options.toBundle();
        }
        ActivityCompat.startActivity(MainActivity.this, intent, bundle);
    }

    @Override
    public void onItemClicked(View view, Media media) {
        loadMedia(view, media);
    }
}
