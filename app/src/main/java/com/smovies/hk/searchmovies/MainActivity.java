package com.smovies.hk.searchmovies;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smovies.hk.searchmovies.movieSorting.MovieListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.FAV_LIST;
import static com.smovies.hk.searchmovies.utils.Constants.PLAYING_NOW;
import static com.smovies.hk.searchmovies.utils.Constants.POPULAR;
import static com.smovies.hk.searchmovies.utils.Constants.TOP_RATED;
import static com.smovies.hk.searchmovies.utils.Constants.TO_WATCH_LIST;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        navigationDrawerSetup(toolbar);

        tabsSetup();
    }

    private void navigationDrawerSetup(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_now_playing);
    }

    private void tabsSetup() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case FAV_LIST:
                        setTitle(getString(R.string.nav_favorites_t));
                        break;
                    case TO_WATCH_LIST:
                        setTitle(getString(R.string.nav_to_watch_t));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //set icons in tab layout
        tabLayout.getTabAt(PLAYING_NOW).setIcon(R.drawable.ic_menu_slideshow);
        tabLayout.getTabAt(POPULAR).setIcon(R.drawable.ic_whatshot_black_24dp);
        tabLayout.getTabAt(TOP_RATED).setIcon(R.drawable.ic_top_rated_yellow_24dp);

        //remove last two tabs (Favorites & TO Watch)
        tabLayout.removeTabAt(tabLayout.getTabCount() - 1);
        tabLayout.removeTabAt(tabLayout.getTabCount() - 1);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_now_playing:
                mViewPager.setCurrentItem(PLAYING_NOW);
                updateTabVisibility();
                break;
            case R.id.nav_popular:
                mViewPager.setCurrentItem(POPULAR);
                updateTabVisibility();
                break;
            case R.id.nav_toprated:
                mViewPager.setCurrentItem(TOP_RATED);
                updateTabVisibility();
                break;
            case R.id.nav_favorite:
                mViewPager.setCurrentItem(FAV_LIST);
                tabLayout.setVisibility(View.GONE);
                navigationView.setCheckedItem(R.id.nav_favorite);
                break;
            case R.id.nav_to_watch:
                mViewPager.setCurrentItem(TO_WATCH_LIST);
                tabLayout.setVisibility(View.GONE);
                navigationView.setCheckedItem(R.id.nav_to_watch);
                break;
            default:
                mViewPager.setCurrentItem(PLAYING_NOW);
                updateTabVisibility();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateTabVisibility() {
        if (tabLayout.getVisibility() == View.GONE)
            tabLayout.setVisibility(View.VISIBLE);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            updateVisibility(position);

            return MovieListFragment.newInstance(position);
        }

        public void updateVisibility(int position) {
            if (position > 3) {
                tabLayout.setVisibility(View.GONE);
            } else {
                updateTabVisibility();
            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PLAYING_NOW:
                    return getString(R.string.nav_now_playing_t);
                case POPULAR:
                    return getString(R.string.nav_popular_t);
                case TOP_RATED:
                    return getString(R.string.nav_top_rated_t);
            }
            return null;
        }
    }
}
