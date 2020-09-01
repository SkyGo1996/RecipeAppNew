package com.example.recipeappnew.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface RecipeDatabaseDao {
    @Insert
    fun insert(recipe: Recipe)

    @Update
    fun update(recipe: Recipe)

    @Query("SELECT * FROM recipe_table WHERE recipeId = :key")
    fun getRecipe(key: Long): Recipe

    @Query("SELECT * FROM recipe_table")
    fun getRecipes(): LiveData<List<Recipe>>

    /*@Query("SELECT * FROM recipe_table WHERE recipe_type = :type")
    fun getRecipesByType(type: String):LiveData<List<Recipe>>*/

    @Delete
    fun delete(recipe: Recipe)
}