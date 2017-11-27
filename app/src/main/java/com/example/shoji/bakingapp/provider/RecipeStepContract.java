package com.example.shoji.bakingapp.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class RecipeStepContract {

    /* internal database id */
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_RECIPE_ID = "recipe_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_STEP_ID = "step_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_SHORT_DESCRIPTION = "short_description";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_LONG_DESCRIPTION = "long_description";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_VIDEO_URL = "video_url";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

}
