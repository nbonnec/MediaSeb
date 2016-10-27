package com.nbonnec.mediaseb.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;

import com.nbonnec.mediaseb.R;
import com.nbonnec.mediaseb.data.api.endpoints.MSSEndpoints;
import com.nbonnec.mediaseb.models.Media;
import com.nbonnec.mediaseb.ui.fragment.AccountFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragment;
import com.nbonnec.mediaseb.ui.fragment.MediaListFragmentBuilder;

import javax.inject.Inject;


public class MainActivity extends ToolbarActivity implements MediaListFragment.OnClickedListener,
        AccountFragment.OnClickListener, AccountFragment.OnIsSignedInListener {
    private static String POSITION = "POSITION";

    private TabLayout tabLayout;
    private MainActivityPageAdapter mainActivityPageAdapter;

    private int savedPosition;

    @Inject
    MSSEndpoints mssEndpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            savedPosition = savedInstanceState.getInt(POSITION);
        } else {
            savedPosition = 0;
        }

        initializeTabs(MainActivityPageAdapter.TAB_MIN);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();

        /*
        if (mainActivityPageAdapter.getCount() == 2 && isSignIn()) {
            initializeTabs(MainActivityPageAdapter.TAB_MIN + 1);
        } else if (mainActivityPageAdapter.getCount() == 3 && !isSignIn()) {
            initializeTabs(MainActivityPageAdapter.TAB_MIN);
        }
        */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    public void onItemClicked(View view, Media media) {
        loadMedia(view, media);
    }

    @Override
    public void onNotLoggedButtonClicked() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onIsSignedIn() {
        return isSignIn();
    }

    private void loadMedia(View view, Media media) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.MEDIA, media);

        MenuItemCompat.collapseActionView(getSearchMenuItem());

        Bundle bundle =
                makeTransitions(MainActivity.this,
                        Pair.create(view.findViewById(R.id.list_item_image), getString(R.string.transition_name_image)));

        ActivityCompat.startActivity(MainActivity.this, intent, bundle);
    }

    private void setActionBarTitle(int position) {
        if (getSupportActionBar() != null) {
            switch (mainActivityPageAdapter.getVirtualPosition(position)) {
                case MainActivityPageAdapter.TAB_NEWS_POSITION:
                    getSupportActionBar().setTitle(getString(R.string.news));
                    break;
                case MainActivityPageAdapter.TAB_LOANS_POSITION:
                    getSupportActionBar().setTitle(R.string.loans);
                    break;
                case MainActivityPageAdapter.TAB_ACCOUNT_POSITION:
                    getSupportActionBar().setTitle(R.string.account);
                    break;
                default:
                    getSupportActionBar().setTitle("");
            }
        }
    }

    private void initializeTabs(int tabCount) {
        mainActivityPageAdapter = new MainActivityPageAdapter(getSupportFragmentManager(), tabCount);

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

        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.setCurrentItem(savedPosition);
        viewPager.setOffscreenPageLimit(tabCount - 1);

        setActionBarTitle(viewPager.getCurrentItem());
    }

    private class MainActivityPageAdapter extends FragmentStatePagerAdapter {
        final static int TAB_NEWS_POSITION = 0;
        final static int TAB_LOANS_POSITION = 1;
        final static int TAB_ACCOUNT_POSITION = 2;
        final static int TAB_MIN = 2;

        private int count;

        MainActivityPageAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            count = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (getVirtualPosition(position)) {
                case TAB_NEWS_POSITION:
                    return new MediaListFragmentBuilder(mssEndpoints.newsUrl()).build();
                case TAB_LOANS_POSITION:
                    return new MediaListFragmentBuilder(mssEndpoints.simpleSearchUrl("lol")).build();
                case TAB_ACCOUNT_POSITION:
                    return new AccountFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int[] imageResId = {
                    R.drawable.ic_whatshot_white,
                    R.drawable.ic_archive_white,
                    R.drawable.ic_person_white
            };

            Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this,
                    imageResId[getVirtualPosition(position)]);

            if (iconDrawable == null) {
                return null;
            } else {
                iconDrawable.setBounds(0, 0, iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight());

                SpannableString spannableString = new SpannableString(" ");
                ImageSpan imageSpan = new ImageSpan(iconDrawable, ImageSpan.ALIGN_BOTTOM);

                spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                return spannableString;
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        /* virtually always 3 tabs */
        int getVirtualPosition(int position) {
            if (position == 1 && count == 2) {
                position = 2;
            }
            return position;
        }
    }
}
