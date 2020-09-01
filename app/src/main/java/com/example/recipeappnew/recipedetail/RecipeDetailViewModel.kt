package com.example.recipeappnew.recipedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabaseDao
import kotlinx.coroutines.*

class RecipeDetailViewModel(private val dataSource: RecipeDatabaseDao, val selectedRecipe: Recipe): ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateDetailToMain = MutableLiveData<Boolean>()
    val navigateDetailToMain:LiveData<Boolean>
    get() = _navigateDetailToMain

    private val _navigateDetailToEdit = MutableLiveData<Boolean>()
    val navigateDetailToEdit:LiveData<Boolean>
    get() = _navigateDetailToEdit

    init {
        _navigateDetailToMain.value = false
        _navigateDetailToEdit.value = false
    }

    fun deleteRecipe(recipe: Recipe){
        uiScope.launch {
            deleteRecipeFromDatabase(selectedRecipe)
        }
    }

    private suspend fun deleteRecipeFromDatabase(recipe: Recipe){
        withContext(Dispatchers.IO){
            dataSource.delete(recipe)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
    }

    fun onRecipeDetailToMain(){
        _navigateDetailToMain.value = true
    }

    fun recipeDetailToMainCompleted(){
        _navigateDetailToMain.value = false
    }

    fun onRecipeDetailToEdit(){
        _navigateDetailToEdit.value = true
    }

    fun recipeDEtailToEditCompleted(){
        _navigateDetailToEdit.value = false
    }
}