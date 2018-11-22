package com.smovies.hk.searchmovies.movieDetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smovies.hk.searchmovies.R;
import com.smovies.hk.searchmovies.model.Movie;
import com.smovies.hk.searchmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.smovies.hk.searchmovies.utils.Constants.KEY_MOVIE_ID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrailersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrailersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailersFragment extends Fragment{

    @BindView(R.id.recycler_view_video_grid) RecyclerView rvVideo;
    @BindView(R.id.progress_bar_grid_loading) ProgressBar progressBar;
    @BindView(R.id.text_view_video_grid_error_message) TextView tvErrorMsg;

    private OnFragmentInteractionListener mListener;
    private VideoGridAdapter videoGridAdapter;
    private List<Video> videos;

    public TrailersFragment() {
        // Required empty public constructor
    }


    public static TrailersFragment newInstance(String param1, String param2) {
        TrailersFragment fragment = new TrailersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void showProgress() {
       progressBar.setVisibility(View.VISIBLE);
        rvVideo.setVisibility(View.INVISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }

    public void onResponseFailure(Throwable throwable) {
        tvErrorMsg.setVisibility(View.VISIBLE);
    }


    private void initUI() {
        videos = new ArrayList<>();

        videoGridAdapter = new VideoGridAdapter(this, videos);
        LinearLayoutManager mVideoLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvVideo.setLayoutManager(mVideoLayoutManager);
        rvVideo.setAdapter(videoGridAdapter);
    }

    public void videoGridAdapterInteraction(View view, int position){
        //send activity interaction update.
        mListener.onVideoAdapterItemSelected(view, videos.get(position));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailers, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }


    public void setDataToViews(Movie movie) {
//        tvErrorMsg.setVisibility(View.VISIBLE);
        rvVideo.setVisibility(View.VISIBLE);

        videos.clear();
        videos.addAll(movie.getTrailers().getVideos());
        videoGridAdapter.notifyDataSetChanged();

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.sendFragmentInstance(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void sendFragmentInstance(TrailersFragment fragment);
        void onVideoAdapterItemSelected(View v, Video video);
    }

}
