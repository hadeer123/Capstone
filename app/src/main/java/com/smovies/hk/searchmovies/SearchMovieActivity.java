package com.smovies.hk.searchmovies;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.smovies.hk.searchmovies.movieSorting.MovieListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.GET_MOVIES;

public class SearchMovieActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.text_view_toolbar_title) TextView tvToolbar;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movies);

        ButterKnife.bind(this);
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        tvToolbar.setText(getString(R.string.action_search));
        toolbar.setTitle(getString(R.string.action_search));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mQuery = query;
            setTitle(query);
            searchMovies();
        }
    }

    private void searchMovies() {
        // Create a new Fragment to be placed in the activity layout
        MovieListFragment fragment = MovieListFragment.newInstance(GET_MOVIES, mQuery);

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
//            fragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();
    }
}
