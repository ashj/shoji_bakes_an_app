package com.example.shoji.bakingapp.media;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import com.example.shoji.bakingapp.BuildConfig;
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
    //[START] Android O - notification channel
    private static final String BAKERPLAYER_CHANNEL_MEDIA_CONTROL_ID = "bakerplayer-media-control-id";
    private static final String BAKERPLAYER_CHANNEL_MEDIA_CONTROL_NAME = "bakerplayer-media-control-name";
    //[END] Android O - notification channel
    public static final String SAVE_INSTANCE_STATE_PLAYER_POSITION = "player_position";
    public static final String SAVE_INSTANCE_STATE_PLAY_PAUSE = "state_play_pause";


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
        mNotificationManager = notificationManager;
        mExoPlayerView = simpleExoPlayerView;
        initiateExoPlayer();
        initiateMediaSession();
    }

    private void initiateExoPlayer() {
        Context context = mContext;

        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                context,
                trackSelector,
                loadControl);

        mExoPlayerView.setPlayer(mExoPlayer);
        ExoPlayer.EventListener eventHandler = this;
        mExoPlayer.addListener(eventHandler);
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
        Context context = mContext;

        String userAgent = Util.getUserAgent(context,
                mContext.getString(R.string.app_name));
        MediaSource mediaSource = new ExtractorMediaSource(
                mediaUri,
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null);

        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    // Callbacks

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
        if(mNotificationManager != null)
            mNotificationManager.cancelAll();
        if(mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
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

        //showNotification(mContext, mNotificationManager, sMediaSession, state);

    }

    @Override
    // https://stackoverflow.com/questions/42948036/how-to-catch-all-errors-for-exoplayer
    public void onPlayerError(ExoPlaybackException error) {
        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                Timber.e("TYPE_SOURCE: %s", error.getSourceException().getMessage());
                break;

            case ExoPlaybackException.TYPE_RENDERER:
                Timber.e("TYPE_RENDERER: %s", error.getRendererException().getMessage());
                break;

            case ExoPlaybackException.TYPE_UNEXPECTED:
                Timber.e("TYPE_UNEXPECTED: %s", error.getUnexpectedException().getMessage());
                break;
        }
        /* disable player controller and release it */
        mExoPlayerView.setUseController(false);
        //mExoPlayerView.setVisibility(View.GONE);
        releasePlayer();
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

        //[START] Android O - notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel bakerplayerChannel = new NotificationChannel(
                    BAKERPLAYER_CHANNEL_MEDIA_CONTROL_ID,
                    BAKERPLAYER_CHANNEL_MEDIA_CONTROL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            bakerplayerChannel.setLightColor(Color.GREEN);
            notificationManager.createNotificationChannel(bakerplayerChannel);

            builder.setChannelId(BAKERPLAYER_CHANNEL_MEDIA_CONTROL_ID);
        }
        //[END] Android O - notification channel

        notificationManager.notify(0, builder.build());
    }

    public Bundle onSaveInstanceState() {
        Bundle outState = new Bundle();
        if(mExoPlayer != null) {
            outState.putLong(SAVE_INSTANCE_STATE_PLAYER_POSITION,
                    mExoPlayer.getCurrentPosition());
            outState.putBoolean(SAVE_INSTANCE_STATE_PLAY_PAUSE,
                    mExoPlayer.getPlayWhenReady());
        }
        return outState;
    }

    public void onRestoreInstanceState(Bundle savedState) {
        if(savedState != null) {
            if(savedState.containsKey(SAVE_INSTANCE_STATE_PLAYER_POSITION)) {
                mExoPlayer.seekTo(savedState.getLong(SAVE_INSTANCE_STATE_PLAYER_POSITION));
            }
            if(savedState.containsKey(SAVE_INSTANCE_STATE_PLAY_PAUSE)) {
                mExoPlayer.setPlayWhenReady(savedState.getBoolean(SAVE_INSTANCE_STATE_PLAY_PAUSE));
            }

        }
    }

}
