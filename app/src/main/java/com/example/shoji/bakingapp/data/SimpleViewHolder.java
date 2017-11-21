package com.example.shoji.bakingapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.example.shoji.bakingapp.pojo.Recipe;

import timber.log.Timber;

public class SimpleViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private OnClickListener mOnClickHandler;

    private TextView mTitle;

    interface OnClickListener {
        void onClick(int position);
    }

    public SimpleViewHolder(View itemView,
                            int resIdTitle,
                            OnClickListener onClickHandler) {
        super(itemView);
        mOnClickHandler = onClickHandler;

        mTitle = itemView.findViewById(resIdTitle);

        View.OnClickListener viewOnClickListener = this;
        mTitle.setOnClickListener(viewOnClickListener);

    }

    public void bindViewHolder(String title) {
        // set view here
        mTitle.setText(title);

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Timber.d("TAPPED ON POSITION (holder): %d", position);
        mOnClickHandler.onClick(position);
    }
}
