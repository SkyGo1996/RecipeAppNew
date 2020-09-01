package com.example.recipeappnew.recipeedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabaseDao
import java.lang.IllegalArgumentException

class RecipeEditViewModelFactory(val dataSource: RecipeDatabaseDao, val selectedRecipe: Recipe): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecipeEditViewModel::class.java)){
            return RecipeEditViewModel(dataSource, selectedRecipe ) as T
        }
        throw IllegalArgumentException("Unknown View Model")
    }

}