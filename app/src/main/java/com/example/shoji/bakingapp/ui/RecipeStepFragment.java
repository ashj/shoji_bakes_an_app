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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.media.BakerPlayer;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import timber.log.Timber;


public class RecipeStepFragment extends Fragment
    implements View.OnClickListener {
    private static final String SAVE_INSTANCE_STATE_PLAYER_POSITION = "player_position";
    private static final String SAVE_INSTANCE_STATE_DESCRIPTION_POSITION = "description_position";

    private static final int POSITION_INVALID = -1;

    private boolean mIsPhoneLandscape;
    private int mStepPosition;

    private Recipe mRecipe;

    private SimpleExoPlayerView mExoPlayerView;
    private BakerPlayer mBakerPlayer;

    private TextView mLongDescription;
    private TextView mPreviousStepButton;
    private TextView mNextStepButton;

    private OnClickNavButtonListener mOnClickHandler;

    private ScrollView mLongDescriptionScrollView;
    Bundle mSavedInstanceState;


    public RecipeStepFragment() {
    }

    public interface OnClickNavButtonListener {
        void onClickPrev (int currentPosition);
        void onClickNext (int currentPosition);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        boolean attachToRoot = false;
        mSavedInstanceState = savedInstanceState;

        View rootView = inflater.inflate(
                R.layout.fragment_recipe_step_list,
                container,
                attachToRoot);

        mRecipe = getRecipeFromActivity();
        mStepPosition = getStepNumberFromBundle(getArguments());
        Timber.d("Got currentPosition -- %d", mStepPosition);


        createGeneralViews(rootView, savedInstanceState);

        createMediaPlayerView(rootView, savedInstanceState);


        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.setTitle(mRecipe.getName());

        return rootView;
    }


    private void createMediaPlayerView(View rootView, Bundle state) {
        Context context = getContext();

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
        mExoPlayerView = rootView.findViewById(R.id.fragment_recipe_step_media_player);

        mBakerPlayer = new BakerPlayer(context, mExoPlayerView, notificationManager);
        mBakerPlayer.setNotificationTitle(mRecipe.getName());
        mBakerPlayer.setNotificationText(mRecipe.getStepList().get(mStepPosition).getShortDescription());


        String urlString = mRecipe.getStepList().get(mStepPosition).getVideoUrl();
        if(urlString != null) {
            Uri videoUri = Uri.parse(urlString);
            mBakerPlayer.initializePlayer(videoUri);
            restoreInstanceState(state);
        }

    }

    private void createGeneralViews(View rootView, final Bundle state) {
        Timber.d("createViews");

        /* fragment_recipe_step_long_description is not defined in phone landscape layout */
        mLongDescription = rootView.findViewById(R.id.fragment_recipe_step_long_description);
        mIsPhoneLandscape = mLongDescription == null;


        if(!mIsPhoneLandscape) {
            mLongDescriptionScrollView = rootView.findViewById(R.id.fragment_recipe_step_long_description_scrollview);
            mLongDescription.setText(mRecipe.getStepList().get(mStepPosition).getLongDescription());

            createNavButtonView(rootView);

            mLongDescriptionScrollView.post(new Runnable() {
                @Override
                public void run() {
                    restoreScrollPosition(state);
                }
            });

        }
        Timber.d("NOT NULL?: mPreviousStepButton: %b, mNextStepButton: %b", (mPreviousStepButton !=null), (mNextStepButton!=null));

    }

    private void createNavButtonView(View rootView) {
        mPreviousStepButton = rootView.findViewById(R.id.navbutton_recipe_previous_button);
        mNextStepButton = rootView.findViewById(R.id.navbutton_recipe_next_button);
        View.OnClickListener clickHandler = this;

        if(mPreviousStepButton != null) {
            /* first step does not have a previous step */
            if(mStepPosition == 0) {
                mPreviousStepButton.setVisibility(View.GONE);
            }
            else {
                mPreviousStepButton.setOnClickListener(clickHandler);
            }
        }

        if(mNextStepButton != null) {
            if(mStepPosition == mRecipe.getStepList().size() - 1) {
                mNextStepButton.setVisibility(View.GONE);
            }
            else {
                mNextStepButton.setOnClickListener(clickHandler);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.navbutton_recipe_previous_button:
                mOnClickHandler.onClickPrev(mStepPosition);
                break;
            case R.id.navbutton_recipe_next_button:
                mOnClickHandler.onClickNext(mStepPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnClickHandler = (OnClickNavButtonListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnClickNavButtonListener");
        }
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
        if(mBakerPlayer != null) {
            mBakerPlayer.releasePlayer();
            mBakerPlayer.mediaSessionSetActive(false);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mBakerPlayer != null)
            outState.putLong(SAVE_INSTANCE_STATE_PLAYER_POSITION, mBakerPlayer.onSaveInstanceState());
        if(mLongDescriptionScrollView != null) {
            int[] position = new int[]{
                    mLongDescriptionScrollView.getScrollX(),
                    mLongDescriptionScrollView.getScrollY() };
            outState.putIntArray(SAVE_INSTANCE_STATE_DESCRIPTION_POSITION, position);
            Timber.d("Saved scrollview [from View] pos: %d, %d", position[0], position[1]);
        }
        else if(mSavedInstanceState != null && mSavedInstanceState.containsKey(SAVE_INSTANCE_STATE_DESCRIPTION_POSITION)) {
            int[] position = mSavedInstanceState.getIntArray(SAVE_INSTANCE_STATE_DESCRIPTION_POSITION);
            outState.putIntArray(SAVE_INSTANCE_STATE_DESCRIPTION_POSITION, position);
            Timber.d("Saved scrollview [from savedState] pos: %d, %d", position[0], position[1]);
        }
    }

    private void restoreInstanceState(Bundle state) {
        if(state != null) {
            long position = state.getLong(SAVE_INSTANCE_STATE_PLAYER_POSITION, 0);
            Timber.d("restoreInstanceState - position: %d", position);
            if(position != BakerPlayer.INVALID_POSITION)
            mBakerPlayer.onRestoreInstanceState(position);
        }
    }



    private void restoreScrollPosition(Bundle state) {
        if(mLongDescriptionScrollView != null && state != null && state.containsKey(SAVE_INSTANCE_STATE_DESCRIPTION_POSITION)) {
            int[] position = state.getIntArray(SAVE_INSTANCE_STATE_DESCRIPTION_POSITION);
            if(position.length == 2) {
                mLongDescriptionScrollView.scrollTo(position[0], position[1]);
                Timber.d("Restored scrollview pos: %d, %d", position[0], position[1]);
            }
        }

    }
}
