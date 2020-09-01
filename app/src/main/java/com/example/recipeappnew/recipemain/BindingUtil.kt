package com.example.recipeappnew.recipemain

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipeappnew.R
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.formatIngredient
import com.example.recipeappnew.formatStep

@BindingAdapter("loadRecipeItemImage")
fun loadImage(imageView: ImageView, recipe: Recipe) {
    val requestOptions = RequestOptions()
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_baseline_local_dining_24)

    Log.i("test","image url = ${recipe.recipeImageUri}")

    if (recipe.recipeImageUri != null) {
        val imageUri = Uri.parse(recipe.recipeImageUri)

        Glide.with(imageView.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(imageUri)
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load(R.drawable.ic_baseline_local_dining_24)
            .into(imageView)
    }
}

@BindingAdapter("setTextInDetailIngredient")
fun setTextIngredient(textView: TextView, recipe: Recipe){
    textView.text = formatIngredient(recipe)
}

@BindingAdapter("setTextInDetailStep")
fun setTextStep(textView: TextView, recipe: Recipe){
    textView.text = formatStep(recipe)
}
