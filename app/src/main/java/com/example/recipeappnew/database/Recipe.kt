package com.example.recipeappnew.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recipeappnew.StringListConverters
import kotlinx.android.parcel.Parcelize
import java.sql.Blob

@Parcelize
@Entity(tableName = "recipe_table")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    var recipeId: Long = 0L,

    @ColumnInfo(name = "recipe_name")
    var recipeName: String = "",

    @ColumnInfo(name = "recipe_type")
    var recipeType:String = "",

    @ColumnInfo(name = "recipe_img_uri")
    var recipeImageUri:String? = null,

    @ColumnInfo(name = "recipe_ingredient")
    var recipeIngredient:List<String>,

    @ColumnInfo(name = "recipe_steps")
    var recipeSteps:List<String>
): Parcelable