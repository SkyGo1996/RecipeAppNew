package com.example.recipeappnew.database

import android.content.Context
import androidx.room.*
import com.example.recipeappnew.StringListConverters

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverters::class)
abstract class RecipeDatabase: RoomDatabase() {
    abstract val recipeDatabaseDao: RecipeDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecipeDatabase::class.java,
                        "recipe_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }

                return instance
            }
        }
    }
}