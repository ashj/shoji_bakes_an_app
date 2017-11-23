package com.example.shoji.bakingapp.media;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.ui.RecipeStepFragment;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import timber.log.Timber;


public class BakerPlayer
    implements ExoPlayer.EventListener {
    private Context mContext;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mExoPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private static MediaSessionCompat sMediaSession;

    private NotificationManager mNotificationManager;

    private static final int NOTIFICATION_MAX_LENGTH = 30;
    private String mNotificationTitle;
    private String mNotificationText;

    public BakerPlayer(Context context,
                       SimpleExoPlayerView simpleExoPlayerView,
                       NotificationManager notificationManager) {
        mContext = context;
        mExoPlayerView = simpleExoPlayerView;
        mNotificationManager = notificationManager;

        initiateMediaSession();
    }

    private void initiateMediaSession() {
        /* Step 1 - create a MediaSessionCompat object*/
        Context context = mContext;
        String tag = RecipeStepFragment.class.getSimpleName();
        sMediaSession = new MediaSessionCompat(context, tag);

        /* Step 2 - set the flags*/
        int flags = MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
        sMediaSession.setFlags(flags);

        /* Step 3 - set an optional MediaButtonReceiver component*/
        PendingIntent mediaButtonReceiver = null;
        sMediaSession.setMediaButtonReceiver(mediaButtonReceiver);

        /* Step 4 - set available actions and initial state*/
        long actions = PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_FAST_FORWARD |
                PlaybackStateCompat.ACTION_REWIND;
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(actions);
        sMediaSession.setPlaybackState(mStateBuilder.build());

        /* Step 5 - set the callbacks*/
        MediaSessionCompat.Callback callback = new MySessionCallback();
        sMediaSession.setCallback(callback);

        /* Step 6 - start the session */
        sMediaSession.setActive(true);
    }

    public void initializePlayer(Uri mediaUri) {
        if(mExoPlayer == null) {
            Context context = mContext;

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);

            ExoPlayer.EventListener eventHandler = this;
            mExoPlayer.addListener(eventHandler);

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
        mNotificationManager.cancelAll();

        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public void mediaSessionSetActive(boolean state) {
        sMediaSession.setActive(state);
    }

    // ExoPlayer Event Listeners

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        float playbackSpeed = 1f;
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), playbackSpeed);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), playbackSpeed);
        }
        PlaybackStateCompat state = mStateBuilder.build();
        sMediaSession.setPlaybackState(state);

        showNotification(mContext, mNotificationManager, sMediaSession, state);

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }


    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("Received broadcast");
            MediaButtonReceiver.handleIntent(sMediaSession, intent);
        }
    }

    private String trimmNotificationString(String string) {
        if(string.length() > NOTIFICATION_MAX_LENGTH) {
            return string.substring(0, NOTIFICATION_MAX_LENGTH);
        }
        return string;
    }

    public void setNotificationTitle(String title) {
        mNotificationTitle = trimmNotificationString(title);
    }

    public void setNotificationText(String text) {
        mNotificationText = trimmNotificationString(text);
    }

    /* FOR APIv26 */
    private void showNotification(Context context,
                                  NotificationManager notificationManager,
                                  MediaSessionCompat mediaSession,
                                  PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        int icon;
        String play_pause;
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
            icon = R.drawable.exo_controls_pause;
            play_pause = context.getString(R.string.media_player_notification_pause);

        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = context.getString(R.string.media_player_notification_play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        builder
                .setContentTitle(mNotificationTitle)
                .setContentText(mNotificationText)
                //.setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0));

        notificationManager.notify(0, builder.build());
    }

    public long onSaveInstanceState() {
    mExoPlayer.
        return mExoPlayer.getCurrentPosition();
    }
    public void onRestoreInstanceState(long position) {

        mExoPlayer.seekTo(position);

    }

}
