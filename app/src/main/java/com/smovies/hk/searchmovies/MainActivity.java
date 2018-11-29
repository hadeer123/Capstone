package com.smovies.hk.searchmovies;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.smovies.hk.searchmovies.movieSorting.MovieListFragment;

import java.util.Objects;

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
    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;

    TextView tvUsername, tvEmail;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.ad_unit_id));

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        navigationDrawerSetup(toolbar);

        tabsSetup();

        userAccountSetup();

        if (fragmentContainer.getVisibility() == View.GONE)
            setTitle(R.string.app_name);

    }

    private void updateViewFromSavedState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(MovieListFragment.ARG_SECTION_NUMBER);
            if (position > TOP_RATED) {
                createFragments(position);
            } else {
                mViewPager.setCurrentItem(position);
            }
            updateNavigationViewSelection(position);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updateViewFromSavedState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MovieListFragment.ARG_SECTION_NUMBER, currentItem);
    }

    private void userAccountSetup() {
        if (getIntent().hasExtra(getString(R.string.key_username))) {
            tvEmail.setText(getIntent().getStringExtra(getString(R.string.key_user_email)));
            tvUsername.setText(getIntent().getStringExtra(getString(R.string.key_username)));
        }
    }

    private void navigationDrawerSetup(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_now_playing);
        View headerView = navigationView.getHeaderView(0);
        tvUsername = headerView.findViewById(R.id.nav_head_username);
        tvEmail = headerView.findViewById(R.id.nav_head_email);
    }

    private void tabsSetup() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link FragmentStatePagerAdapter}.
     */ /**
         * The {@link PagerAdapter} that will provide
         * fragments for each of the sections. We use a
         * {@link FragmentPagerAdapter} derivative, which will keep every
         * loaded fragment in memory. If this becomes too memory intensive, it
         * may be best to switch to a
         * {@link FragmentStatePagerAdapter}.
         */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                updateNavigationViewSelection(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //set icons in tab layout
        Objects.requireNonNull(tabLayout.getTabAt(PLAYING_NOW)).setIcon(R.drawable.ic_now_playing_accent_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(POPULAR)).setIcon(R.drawable.ic_whatshot_accent_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(TOP_RATED)).setIcon(R.drawable.ic_top_rated_accent_24dp);
    }

    private void updateNavigationViewSelection(int position) {
        switch (position) {
            case PLAYING_NOW:
                navigationView.setCheckedItem(R.id.nav_now_playing);
                break;
            case POPULAR:
                navigationView.setCheckedItem(R.id.nav_popular);
                break;
            case TOP_RATED:
                navigationView.setCheckedItem(R.id.nav_toprated);
                break;
            case FAV_LIST:
                navigationView.setCheckedItem(R.id.nav_favorite);
                break;
            case TO_WATCH_LIST:
                navigationView.setCheckedItem(R.id.nav_to_watch);
                break;
        }
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
        setSearchManager(menu);
        return true;
    }

    private void setSearchManager(Menu menu) {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null && searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_now_playing:
                updateVisibly();
                mViewPager.setCurrentItem(PLAYING_NOW);
                currentItem = PLAYING_NOW;
                break;
            case R.id.nav_popular:
                updateVisibly();
                mViewPager.setCurrentItem(POPULAR);
                currentItem = POPULAR;
                break;
            case R.id.nav_toprated:
                updateVisibly();
                mViewPager.setCurrentItem(TOP_RATED);
                currentItem = TOP_RATED;
                break;
            case R.id.nav_favorite:
                createFragments(FAV_LIST);
                currentItem = FAV_LIST;
                break;
            case R.id.nav_to_watch:
                createFragments(TO_WATCH_LIST);
                currentItem = TO_WATCH_LIST;
                break;
            case R.id.nav_signout:
                signOutFromApp();
            default:
                mViewPager.setCurrentItem(PLAYING_NOW);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createFragments(int position) {

        MovieListFragment fragment = MovieListFragment.newInstance(position);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();

        fragmentContainer.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

    }

    private void signOutFromApp() {
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finishAndRemoveTask();
                System.exit(0);
            }
        });
    }

    private void updateVisibly() {
        if (mViewPager.getVisibility() == View.GONE) {
            mViewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            setTitle(R.string.app_name);
        }
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
            updateVisibly();
            currentItem = position;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MovieListFragment.newInstance(position);
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
