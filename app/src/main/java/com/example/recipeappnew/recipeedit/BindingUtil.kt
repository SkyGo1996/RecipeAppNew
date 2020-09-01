package com.example.recipeappnew.recipeedit

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipeappnew.R
import com.example.recipeappnew.database.Recipe

@BindingAdapter("loadRecipeImageInEdit")
fun loadImage(imageView: ImageView, recipe: Recipe){
    val requestOptions = RequestOptions()
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_baseline_add_photo_alternate_24)

    Log.i("test","image url = ${recipe.recipeImageUri}")

    if (recipe.recipeImageUri != null) {
        val imageUri = Uri.parse(recipe.recipeImageUri)

        Glide.with(imageView.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(imageUri)
            .into(imageView)
    } else {
        Log.i("test", "else is called in load image")
        Glide.with(imageView.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(R.drawable.ic_baseline_add_photo_alternate_24)
            .into(imageView)
    }
}