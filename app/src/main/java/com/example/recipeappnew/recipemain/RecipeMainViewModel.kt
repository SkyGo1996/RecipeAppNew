package com.example.recipeappnew.recipemain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RecipeMainViewModel(dataSource:RecipeDatabaseDao): ViewModel() {

    private val _navigateToRecipeAdd = MutableLiveData<Boolean>()
    val navigateToRecipeAdd:LiveData<Boolean>
    get() = _navigateToRecipeAdd

    private val _navigateToRecipeDetail = MutableLiveData<Recipe?>()
    val navigateToRecipeDetail:LiveData<Recipe?>
    get() = _navigateToRecipeDetail

    var recipes = dataSource.getRecipes()

    init {
        _navigateToRecipeAdd.value = false
        _navigateToRecipeDetail.value = null
    }

    fun onAddNewRecipe(){
        _navigateToRecipeAdd.value = true
    }

    fun addNewRecipeLayoutTriggered(){
        _navigateToRecipeAdd.value = false
    }

    fun onRecipeItemClicked(recipe: Recipe) {
        _navigateToRecipeDetail.value = recipe
    }

    fun onRecipeItemClickedCompleted(){
        _navigateToRecipeDetail.value = null
    }
}