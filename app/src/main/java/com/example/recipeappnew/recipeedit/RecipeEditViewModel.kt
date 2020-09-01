package com.example.recipeappnew.recipeedit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabaseDao
import kotlinx.coroutines.*

class RecipeEditViewModel(val dataSource: RecipeDatabaseDao, val selectedRecipe: Recipe): ViewModel() {

    var imageUri:Uri? = null

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateEditToDetail = MutableLiveData<Boolean>()
    val navigateEditToDetail:LiveData<Boolean>
    get() = _navigateEditToDetail

    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToGallery:LiveData<Boolean>
    get() = _navigateToGallery

    private val _recipeEditToDetail = MutableLiveData<Boolean>()
    val recipeEditToDetail:LiveData<Boolean>
    get() = _recipeEditToDetail

    init {
        _navigateEditToDetail.value = false
        _navigateToGallery.value = false
        _recipeEditToDetail.value = false
    }

    fun updateRecipe(recipe: Recipe){
        uiScope.launch {
            updateRecipeToDatabase(recipe)
            onNavigateBackToDetail()
        }
    }

    private suspend fun updateRecipeToDatabase(recipe: Recipe){
        withContext(Dispatchers.IO){
            dataSource.update(recipe)
        }
    }

    fun onNavigateBackToDetail(){
        _navigateEditToDetail.value = true
    }

    fun navigateToDetailCompleted(){
        _navigateEditToDetail.value = false
    }

    fun onNavigateToGallery(){
        _navigateToGallery.value = true
    }

    fun navigateToGalleryCompleted(){
        _navigateToGallery.value = false
    }

    fun onUpdateClicked(){
        _recipeEditToDetail.value = true
    }

    fun updateClickedCompleted(){
        _recipeEditToDetail.value = false
    }

    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
    }
}