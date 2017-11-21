package com.example.shoji.bakingapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeIngredient implements Parcelable {
    private String mQuantity;
    private String mMeasure;
    private String mDescription;

    public  RecipeIngredient() {}

    protected RecipeIngredient(Parcel in) {
        mQuantity = in.readString();
        mMeasure = in.readString();
        mDescription = in.readString();
    }

    public static final Creator<RecipeIngredient> CREATOR = new Creator<RecipeIngredient>() {
        @Override
        public RecipeIngredient createFromParcel(Parcel in) {
            return new RecipeIngredient(in);
        }

        @Override
        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(mDescription);
    }



    public String getQuantity() {
        return mQuantity;
    }

    public void setQuantity(String quantity) {
        this.mQuantity = quantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public void setMeasure(String measure) {
        this.mMeasure = measure;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    @Override
    public String toString() {
        return "quantity: " + mQuantity
                + "\nmeasure: " + mMeasure
                + "\ndescription: " + mDescription;
    }

}
