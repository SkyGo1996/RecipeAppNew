package com.example.recipeappnew.recipeadd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappnew.database.RecipeDatabaseDao
import java.lang.IllegalArgumentException

class RecipeAddViewModelFactory(private val dataSource: RecipeDatabaseDao): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecipeAddViewModel::class.java)){
            return RecipeAddViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown View Model")
    }
}