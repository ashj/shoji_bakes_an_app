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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.media.BakerPlayer;
import com.example.shoji.bakingapp.pojo.Recipe;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.squareup.picasso.Picasso;

import timber.log.Timber;


public class RecipeStepFragment extends Fragment
    implements View.OnClickListener {
    private static final String SAVE_INSTANCE_STATE_PLAYER_STATE = "player_state";
    private static final String SAVE_INSTANCE_STATE_DESCRIPTION_POSITION = "description_position";


    private static final int POSITION_INVALID = -1;

    private boolean mIsPhoneLandscape;
    private int mStepPosition;

    private Recipe mRecipe;

    private SimpleExoPlayerView mExoPlayerView;
    private BakerPlayer mBakerPlayer;

    private ImageView mStepIllustration;

    private TextView mLongDescription;
    private TextView mPreviousStepButton;
    private TextView mNextStepButton;

    private OnClickNavButtonListener mOnClickHandler;

    private ScrollView mLongDescriptionScrollView;
    private Bundle mSavedInstanceState;
    private View mRootView;


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

        mRootView = inflater.inflate(
                R.layout.fragment_recipe_step_list,
                container,
                attachToRoot);

        mRecipe = getRecipeFromActivity();
        mStepPosition = getStepNumberFromBundle(getArguments());
        Timber.d("Got currentPosition -- %d", mStepPosition);

        createGeneralViews();

        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.setTitle(mRecipe.getName());

        Timber.d("onCreateView");

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        createMultimediaView();
    }

    private void createMultimediaView() {
        String illustrationUrl = mRecipe.getStepList().get(mStepPosition).getThumbnailUrl();

        String videoUrl = mRecipe.getStepList().get(mStepPosition).getVideoUrl();

        if(videoUrl != null && videoUrl.length() != 0) {
            createMediaPlayerView();
        }
        else if(illustrationUrl != null && illustrationUrl.length() != 0) {
            createIllustrationView();
        }
        else {
            mStepIllustration.setVisibility(View.GONE);
            mExoPlayerView.setVisibility(View.GONE);
        }

    }

    private void createIllustrationView() {
        mStepIllustration.setVisibility(View.VISIBLE);
        mExoPlayerView.setVisibility(View.GONE);

        Picasso.with(getContext())
                .load(mRecipe.getStepList().get(mStepPosition).getThumbnailUrl())
                .placeholder(R.drawable.ic_highlight_off)
                .error(R.drawable.ic_highlight_off)
                .into(mStepIllustration);
    }


    private void createMediaPlayerView() {
        mStepIllustration.setVisibility(View.GONE);
        mExoPlayerView.setVisibility(View.VISIBLE);

        Context context = getContext();

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);


        mBakerPlayer = new BakerPlayer(context, mExoPlayerView, notificationManager);
        mBakerPlayer.setNotificationTitle(mRecipe.getName());
        mBakerPlayer.setNotificationText(mRecipe.getStepList().get(mStepPosition).getShortDescription());

        String urlString = mRecipe.getStepList().get(mStepPosition).getVideoUrl();
        if(urlString != null) {
            Uri videoUri = Uri.parse(urlString);
            mBakerPlayer.initializePlayer(videoUri);
            restoreMediaPlayerInstanceState(mSavedInstanceState);
        }

    }

    private void createGeneralViews() {
        Timber.d("createViews");
        mStepIllustration = mRootView.findViewById(R.id.fragment_recipe_step_illustration);
        mExoPlayerView = mRootView.findViewById(R.id.fragment_recipe_step_media_player);

        /* fragment_recipe_step_long_description is not defined in phone landscape layout */
        mLongDescription = mRootView.findViewById(R.id.fragment_recipe_step_long_description);
        mIsPhoneLandscape = mLongDescription == null;


        if(!mIsPhoneLandscape) {
            mLongDescriptionScrollView = mRootView.findViewById(R.id.fragment_recipe_step_long_description_scrollview);
            mLongDescription.setText(mRecipe.getStepList().get(mStepPosition).getLongDescription());

            createNavButtonView(mRootView);

            mLongDescriptionScrollView.post(new Runnable() {
                @Override
                public void run() {
                    restoreScrollPosition(mSavedInstanceState);
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
    public void onStop() {
        super.onStop();
        if(mBakerPlayer != null) {
            if(mSavedInstanceState == null)
                mSavedInstanceState = new Bundle();

            onSaveInstanceState(mSavedInstanceState);
            mBakerPlayer.releasePlayer();
            mBakerPlayer.mediaSessionSetActive(false);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mBakerPlayer != null) {
            outState.putBundle(SAVE_INSTANCE_STATE_PLAYER_STATE, mBakerPlayer.onSaveInstanceState());

        }
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

    private void restoreMediaPlayerInstanceState(Bundle state) {
        if(state != null && state.containsKey(SAVE_INSTANCE_STATE_PLAYER_STATE)) {
            Bundle playerState = state.getBundle(SAVE_INSTANCE_STATE_PLAYER_STATE);
            mBakerPlayer.onRestoreInstanceState(playerState);
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
