package com.example.shoji.bakingapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    private String mId;
    private String mName;
    private ArrayList<RecipeIngredient> mIngredientList;
    private ArrayList<RecipeStep> mStepList;

    public Recipe() {}

    protected Recipe(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mIngredientList = in.createTypedArrayList(RecipeIngredient.CREATOR);
        mStepList = in.createTypedArrayList(RecipeStep.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeTypedList(mIngredientList);
        dest.writeTypedList(mStepList);
    }



    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public ArrayList<RecipeIngredient> getIngredientList() {
        return mIngredientList;
    }

    public void setIngredientList(ArrayList<RecipeIngredient> ingredientList) {
        this.mIngredientList = ingredientList;
    }

    public ArrayList<RecipeStep> getStepList() {
        return mStepList;
    }

    public void setStepList(ArrayList<RecipeStep> stepList) {
        this.mStepList = stepList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("recipe id: ").append(mId)
                .append("\nname: ").append(mName).append("\n");
        if(mIngredientList != null) {
            for (int i = 0; i < mIngredientList.size(); i++)
                sb.append(mIngredientList.get(i).toString()).append("\n");
            sb.append("\n");
        }
        if(mStepList != null) {
            for (int i = 0; i < mStepList.size(); i++)
                sb.append(mStepList.get(i).toString()).append("\n");
            sb.append("\n");
        }

        return sb.toString();
    }
}
