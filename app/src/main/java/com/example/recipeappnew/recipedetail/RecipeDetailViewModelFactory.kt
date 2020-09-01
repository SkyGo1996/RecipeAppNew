package com.example.recipeappnew.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabaseDao
import java.lang.IllegalArgumentException

class RecipeDetailViewModelFactory(private val dataSource: RecipeDatabaseDao, private val selectedRecipe: Recipe):ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)){
            return RecipeDetailViewModel(dataSource, selectedRecipe) as T
        }
        throw IllegalArgumentException("Unknown View Model")
    }

}