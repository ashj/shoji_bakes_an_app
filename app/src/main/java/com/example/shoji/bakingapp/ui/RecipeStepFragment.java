package com.example.shoji.bakingapp.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.media.BakerPlayer;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import timber.log.Timber;


public class RecipeStepFragment extends Fragment {

    private static final int POSITION_INVALID = -1;

    private int mStepPosition;

    private Recipe mRecipe;

    private SimpleExoPlayerView mExoPlayerView;
    private BakerPlayer mBakerPlayer;

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
        Context context = getContext();
        mExoPlayerView = rootView.findViewById(R.id.fragment_recipe_step_media_player);
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);

        mBakerPlayer = new BakerPlayer(context, mExoPlayerView, notificationManager);
        mBakerPlayer.setNotificationTitle(mRecipe.getName());
        mBakerPlayer.setNotificationText(mRecipe.getStepList().get(mStepPosition).getShortDescription());


        String urlString = mRecipe.getStepList().get(mStepPosition).getVideoUrl();
        if(urlString != null) {
            Uri videoUri = Uri.parse(urlString);
            mBakerPlayer.initializePlayer(videoUri);
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






    @Override
    public void onDestroy() {
        super.onDestroy();
        mBakerPlayer.releasePlayer();
        mBakerPlayer.mediaSessionSetActive(false);
    }











}
