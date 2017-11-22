package com.example.shoji.bakingapp.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import timber.log.Timber;


public class RecipeStepFragment extends Fragment {

    private static final int POSITION_INVALID = -1;

    private int mStepPosition;

    private Recipe mRecipe;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mExoPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaSessionCompat mMediaSession;

    private TextView mLongDescription;


    public RecipeStepFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        boolean attachToRoot = false;
        View rootView = inflater.inflate(
                R.layout.fragment_recipe_step_list,
                container,
                attachToRoot);

        mRecipe = getRecipeFromActivity();
        mStepPosition = getStepNumberFromActivity();
        if(mStepPosition == POSITION_INVALID) {
            Timber.d("No position extra from activity. Try from getArguments");
            mStepPosition = getStepNumberFromBundle(getArguments());
        }
        Timber.d("Got position -- %d", mStepPosition);


        createGeneralViews(rootView);

        createMediaPlayerView(rootView);


        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.setTitle(mRecipe.getName());

        return rootView;
    }



    private void createMediaPlayerView(View rootView) {
        mExoPlayerView = rootView.findViewById(R.id.fragment_recipe_step_media_player);

        initiateMediaSession();

        String urlString = mRecipe.getStepList().get(mStepPosition).getVideoUrl();
        if(urlString != null) {
            Uri videoUri = Uri.parse(urlString);
            initializePlayer(videoUri);
        }
    }

    private void createGeneralViews(View rootView) {
        Timber.d("createViews");

        mLongDescription = rootView.findViewById(R.id.fragment_recipe_step_long_description);
        mLongDescription.setText(mRecipe.getStepList().get(mStepPosition).getLongDescription());

    }


    
    private Recipe getRecipeFromActivity() {
        FragmentActivity activity = getActivity();
        if(activity == null)
            return null;

        Intent intent = activity.getIntent();
        if(intent == null)
            return null;

        if(!intent.hasExtra(RecipeActivity.EXTRA_RECIPE_DATA))
            return null;

        return intent.getParcelableExtra(RecipeActivity.EXTRA_RECIPE_DATA);
    }



    private int getStepNumberFromActivity() {
        String extra_key = RecipeStepActivity.EXTRA_STEP_NUMBER;
        FragmentActivity activity = getActivity();
        int position = POSITION_INVALID;
        if(activity != null) {
            Intent intent = activity.getIntent();
            if(intent.hasExtra(extra_key)) {
                Bundle args = intent.getBundleExtra(extra_key);
                position = getStepNumberFromBundle(args);
                Timber.d("getStepNumberFromActivity -- %d", position);
            }
        }

        return position;
    }

    private int getStepNumberFromBundle(Bundle args) {
        String extra_key = RecipeStepActivity.EXTRA_STEP_NUMBER;
        int position = POSITION_INVALID;
        if(args != null && args.containsKey(extra_key)) {
            position = args.getInt(extra_key);
            Timber.d("getStepNumberFromBundle -- %d", position);
        }
        return position;
    }




    private void initiateMediaSession() {
        /* Step 1 - create a MediaSessionCompat object*/
        Context context = getContext();
        String tag = RecipeStepFragment.class.getSimpleName();
        mMediaSession = new MediaSessionCompat(context, tag);

        /* Step 2 - set the flags*/
        int flags = MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
        mMediaSession.setFlags(flags);

        /* Step 3 - set an optional MediaButtonReceiver component*/
        PendingIntent mediaButtonReceiver = null;
        mMediaSession.setMediaButtonReceiver(mediaButtonReceiver);

        /* Step 4 - set available actions and initial state*/
        long actions = PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(actions);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        /* Step 5 - set the callbacks*/
        MediaSessionCompat.Callback callback = new MySessionCallback();
        mMediaSession.setCallback(callback);

        /* Step 6 - start the session */
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if(mExoPlayer == null) {
            Context context = getContext();

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);


            String userAgent = Util.getUserAgent(context, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(context , userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

}
