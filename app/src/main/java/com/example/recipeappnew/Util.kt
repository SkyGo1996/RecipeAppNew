package com.example.recipeappnew

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Html
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.room.TypeConverter
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.database.RecipeDatabase
import com.example.recipeappnew.database.RecipeDatabaseDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.StringBuilder

class StringListConverters{
    private val gson = Gson()

    @TypeConverter
    fun stringToStringList(str:String?): List<String>{
        if(str == null){
            return listOf()
        } else {
            val listType = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(str, listType)
        }
    }

    @TypeConverter
    fun stringListToString(list: List<String>): String{
        return gson.toJson(list)
    }

}

/*fun getRealPathFromUri(imageUri: Uri, context: Fragment): String{
    val projection = arrayOf(MediaStore.Images.Media.DATA)

    val cursor = context.activity?.managedQuery(imageUri, projection, null, null, null)

    val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()

    return cursor.getString(columnIndex)
}*/

fun formatIngredient(recipe: Recipe): Spanned{
    val sb = StringBuilder()
    sb.apply {
        recipe.recipeIngredient.forEach {
            append(it)
            append("<br>")
        }
    }
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

fun formatStep(recipe: Recipe): Spanned{
    val sb = StringBuilder()
    var stepCount = 1
    sb.apply {
        recipe.recipeSteps.forEach {
            append("$stepCount.\t\t$it")
            append("<br>")
            stepCount++
        }
    }
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

fun hideSoftKeyboard(activity: Activity){
    val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if(inputMethodManager.isAcceptingText){
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

object DatabaseInstance{
    lateinit var instance:RecipeDatabaseDao
    fun initializeInstance(context: Context){
        instance = RecipeDatabase.getInstance(context).recipeDatabaseDao
    }
}