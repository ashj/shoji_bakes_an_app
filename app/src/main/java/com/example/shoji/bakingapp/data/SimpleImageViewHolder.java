package com.example.shoji.bakingapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import timber.log.Timber;

public class SimpleImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImage;


    public SimpleImageViewHolder(View itemView,
                                 int resIdImage) {
        super(itemView);

        mImage = itemView.findViewById(resIdImage);

    }

    public void bindViewHolder(String imageUrl) {
        // set image here
        //mImage.setText(title);

    }

}
