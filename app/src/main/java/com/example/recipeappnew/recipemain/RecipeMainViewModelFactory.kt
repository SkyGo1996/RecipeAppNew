package com.example.recipeappnew.recipemain

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipeappnew.database.RecipeDatabaseDao

class RecipeMainViewModelFactory(private val dataSource: RecipeDatabaseDao): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecipeMainViewModel::class.java)){
            return RecipeMainViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown View Model")
    }

}