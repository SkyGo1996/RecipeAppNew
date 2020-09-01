package com.example.recipeappnew.recipeadd

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabaseDao
import kotlinx.coroutines.*

class RecipeAddViewModel(private val dataSource:RecipeDatabaseDao): ViewModel() {

    var recipeName = ""
    var imageURI: Uri? = null

    private val _recipeAddNavigateBackToMain = MutableLiveData<Boolean>()
    val recipeAddNavigateBackToMain: LiveData<Boolean>
    get() = _recipeAddNavigateBackToMain

    private val _recipeAddAddClickToMain = MutableLiveData<Boolean>()
    val recipeAddAddClickToMain:LiveData<Boolean>
    get() = _recipeAddAddClickToMain

    private val _recipeAddToGallery = MutableLiveData<Boolean>()
    val recipeAddToGallery:LiveData<Boolean>
    get() = _recipeAddToGallery

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _recipeAddNavigateBackToMain.value = false
    }

    fun addRecipe(recipeType:String, recipeIngredients:List<String>, recipeSteps:List<String>){
        var imageURIString:String? = null

        if(imageURI!=null){
            imageURIString = imageURI.toString()
        }

        uiScope.launch {
            val recipe = Recipe(recipeName = recipeName, recipeType = recipeType, recipeImageUri = imageURIString, recipeIngredient = recipeIngredients, recipeSteps = recipeSteps)
            addRecipeToDatabase(recipe)
            _recipeAddNavigateBackToMain.value = true
        }
    }

    private suspend fun addRecipeToDatabase(recipe: Recipe){
        withContext(Dispatchers.IO){
            dataSource.insert(recipe)
        }
    }

    fun onAddClicked(){
        _recipeAddAddClickToMain.value = true
    }

    fun onAddClickedFinished(){
        _recipeAddAddClickToMain.value = false
    }

    fun onRecipeAddToGallery(){
        _recipeAddToGallery.value = true
    }

    fun recipeAddToGalleryFinished(){
        _recipeAddToGallery.value = false
    }

    fun onRecipeAddToMain(){
        _recipeAddNavigateBackToMain.value = true
    }

    fun recipeAddToMainFinished(){
        _recipeAddNavigateBackToMain.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}