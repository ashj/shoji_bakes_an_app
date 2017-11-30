package com.example.shoji.bakingapp.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shoji.bakingapp.R;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

public class SimpleImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImage;


    public SimpleImageViewHolder(View itemView,
                                 int resIdImage) {
        super(itemView);

        mImage = itemView.findViewById(resIdImage);

    }

    public void bindViewHolder(Context context, String imageUrl) {
        // set image here
        //mImage.setText(title);
        Timber.d("Load image: %s", imageUrl);
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_highlight_off)
                .error(R.drawable.ic_highlight_off)
                .into(mImage);
    }

}
