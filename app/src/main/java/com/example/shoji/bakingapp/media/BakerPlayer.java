package com.example.shoji.bakingapp.media;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.ui.RecipeStepFragment;
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


public class BakerPlayer {
    private Context mContext;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mExoPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaSessionCompat mMediaSession;

    public BakerPlayer(Context context, SimpleExoPlayerView simpleExoPlayerView) {
        mContext = context;
        mExoPlayerView = simpleExoPlayerView;

        initiateMediaSession();
    }

    private void initiateMediaSession() {
        /* Step 1 - create a MediaSessionCompat object*/
        Context context = mContext;
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
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_FAST_FORWARD |
                PlaybackStateCompat.ACTION_REWIND;
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(actions);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        /* Step 5 - set the callbacks*/
        MediaSessionCompat.Callback callback = new MySessionCallback();
        mMediaSession.setCallback(callback);

        /* Step 6 - start the session */
        mMediaSession.setActive(true);
    }

    public void initializePlayer(Uri mediaUri) {
        if(mExoPlayer == null) {
            Context context = mContext;

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);


            String userAgent = Util.getUserAgent(context,
                    mContext.getString(R.string.app_name));
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

    public void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public void mediaSessionSetActive(boolean state) {
        mMediaSession.setActive(state);
    }
}
