package com.nbonnec.mediaseb.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.AccountFragment;
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

        setContentView(R.layout.activity_main);

        initializeTabs();
    }

    @Override
    public void onItemClicked(View view, Media media) {
        loadMedia(view, media);
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

    private void setActionBarTitle(int position) {
        if (getSupportActionBar() != null) {
            switch (position) {
                case MainActivityPageAdapter.TAB_NEWS_POSITION:
                    getSupportActionBar().setTitle(getString(R.string.news));
                    break;
                case MainActivityPageAdapter.TAB_ACCOUNT_POSITION:
                    getSupportActionBar().setTitle(R.string.account);
                    break;
                default:
                    getSupportActionBar().setTitle("");
            }
        }
    }

    private void initializeTabs() {
        MainActivityPageAdapter mainActivityPageAdapter = new MainActivityPageAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        viewPager.setAdapter(mainActivityPageAdapter);
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setActionBarTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout.getTabCount() >= MainActivityPageAdapter.TAB_MAX) {
            tabLayout.getTabAt(MainActivityPageAdapter.TAB_NEWS_POSITION).setIcon(R.drawable.ic_whatshot_white);
            tabLayout.getTabAt(MainActivityPageAdapter.TAB_ACCOUNT_POSITION).setIcon(R.drawable.ic_person_white);
        }

        setActionBarTitle(viewPager.getCurrentItem());
    }

    private class MainActivityPageAdapter extends FragmentPagerAdapter {
        public final static int TAB_NEWS_POSITION = 0;
        public final static int TAB_ACCOUNT_POSITION = 1;
        public final static int TAB_MAX = 2;

        public MainActivityPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_NEWS_POSITION:
                    return new MediaListFragmentBuilder(mssEndpoints.newsUrl()).build();
                case TAB_ACCOUNT_POSITION:
                    return new AccountFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
