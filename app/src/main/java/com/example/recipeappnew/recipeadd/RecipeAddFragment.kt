package com.example.recipeappnew.recipeadd

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.recipeappnew.DatabaseInstance
import com.example.recipeappnew.R
import com.example.recipeappnew.databinding.FragmentRecipeAddBinding
import com.example.recipeappnew.hideSoftKeyboard
import com.google.android.material.textfield.TextInputLayout


const val IMAGE_PICKER_REQ = 100

class RecipeAddFragment : Fragment() {

    private lateinit var ingredientLayout:LinearLayout
    private lateinit var stepsLayout:LinearLayout
    private lateinit var viewModel:RecipeAddViewModel
    private lateinit var bindingFragment:FragmentRecipeAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //declare view binding
        bindingFragment = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_add, container, false)

        //create or get database instance
        val dataSource = DatabaseInstance.instance

        //create view model factory and use it to create view model
        val viewModelFactory = RecipeAddViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeAddViewModel::class.java)

        //data bind the view model in xml and fragment
        bindingFragment.addRecipeViewModel = viewModel
        bindingFragment.lifecycleOwner = this

        initializeUI()

        ingredientLayout = bindingFragment.recipeAddIngredientLayout
        stepsLayout = bindingFragment.recipeAddStepsLayout

        viewModel.recipeAddNavigateBackToMain.observe(viewLifecycleOwner, Observer {
            if(it==true){
                this.findNavController().navigateUp()
                this.activity?.let { it1 -> hideSoftKeyboard(it1) }
                viewModel.recipeAddToMainFinished()
            }
        })

        viewModel.recipeAddToGallery.observe(viewLifecycleOwner, Observer {
            if(it==true){
                val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, IMAGE_PICKER_REQ)
                viewModel.recipeAddToGalleryFinished()
            }
        })

        viewModel.recipeAddAddClickToMain.observe(viewLifecycleOwner, Observer {
            if(it==true){
                if(allUIIsValid()){
                    val recipeType = bindingFragment.recipeAddTypeSpinner.selectedItem.toString()
                    val recipeIngredientsList = mutableListOf<String>()
                    for(i in 1..bindingFragment.recipeAddIngredientLayout.childCount){
                        val ingredientText = (ingredientLayout.getChildAt(i-1) as TextInputLayout).editText!!.text.toString()
                        if(ingredientText.isNotBlank()){
                            recipeIngredientsList.add(ingredientText)
                        }
                    }
                    val recipeStepsList = mutableListOf<String>()
                    for (i in 1..bindingFragment.recipeAddStepsLayout.childCount){
                        val stepText = (stepsLayout.getChildAt(i-1) as TextInputLayout).editText!!.text.toString()
                        if(stepText.isNotBlank()){
                            recipeStepsList.add(stepText)
                        }
                    }
                    viewModel.addRecipe(recipeType, recipeIngredientsList, recipeStepsList)
                }
                viewModel.onAddClickedFinished()
            }
        })

        return bindingFragment.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            when(requestCode){
                IMAGE_PICKER_REQ->{
                    viewModel.imageURI = data!!.data
                    //var imageUriString = getRealPathFromUri(imageUri, this)
                    //imageUriString = "file://$imageUriString"
                    Log.i("test", "imageURI = ${viewModel.imageURI}")
                    //val imageUriNew = File(imageUriString.)

                    this.activity?.contentResolver?.takePersistableUriPermission(viewModel.imageURI!!, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    Glide.with(this)
                        .load(viewModel.imageURI)
                        .into(bindingFragment.recipeAddImage)
                }
            }
        }
    }

    private fun initializeUI(){
        //set spinner item
        val recipeTypeList = resources.getStringArray(R.array.recipe_type).toMutableList()
        recipeTypeList.remove("All")
        val typeAdapter = ArrayAdapter<String>(this.requireActivity().applicationContext, android.R.layout.simple_spinner_item, recipeTypeList)
        bindingFragment.recipeAddTypeSpinner.adapter = typeAdapter

        //set end icon onClick for both ingredient and steps fields
        bindingFragment.recipeAddIngredientTextLayout.setEndIconOnClickListener {
            addNewTextField(bindingFragment.recipeAddIngredientTextLayout.id, bindingFragment)
        }
        bindingFragment.recipeAddStepsTextLayout.setEndIconOnClickListener {
            addNewTextField(bindingFragment.recipeAddStepsTextLayout.id, bindingFragment)
        }
    }

    private fun allUIIsValid(): Boolean{
        var isNameValid = false
        var isIngredientValid = false
        var isStepValid = false

        //check recipe name is empty
        if(bindingFragment.recipeAddName.editText?.text.isNullOrBlank()){
            bindingFragment.recipeAddName.isErrorEnabled = true
            bindingFragment.recipeAddName.error = getString(R.string.recipe_add_name_is_empty)
            isNameValid = false
        } else {
            bindingFragment.recipeAddName.error = null
            bindingFragment.recipeAddName.isErrorEnabled = false
            isNameValid = true
        }

        //check ingredient is empty
        var ingredientEmptyFieldCount = 0
        for(i in 1..bindingFragment.recipeAddIngredientLayout.childCount){
            val textInputLayout = bindingFragment.recipeAddIngredientLayout.getChildAt(i-1) as TextInputLayout
            if(textInputLayout.editText?.text.isNullOrBlank()){
                ingredientEmptyFieldCount++
            }
        }
        if(ingredientEmptyFieldCount==bindingFragment.recipeAddIngredientLayout.childCount){
            bindingFragment.recipeAddIngredientTextLayout.isErrorEnabled = true
            bindingFragment.recipeAddIngredientTextLayout.error = getString(R.string.recipe_add_ingredient_is_empty)
            isIngredientValid = false
        } else {
            bindingFragment.recipeAddIngredientTextLayout.error = null
            bindingFragment.recipeAddIngredientTextLayout.isErrorEnabled = false
            isIngredientValid = true
        }

        //check step is empty
        var stepEmptyFieldCount = 0
        for(i in 1..bindingFragment.recipeAddStepsLayout.childCount){
            val textInputLayout = bindingFragment.recipeAddStepsLayout.getChildAt(i-1) as TextInputLayout
            if(textInputLayout.editText?.text.isNullOrBlank()){
                stepEmptyFieldCount++
            }
        }
        if(stepEmptyFieldCount==bindingFragment.recipeAddStepsLayout.childCount){
            bindingFragment.recipeAddStepsTextLayout.isErrorEnabled = true
            bindingFragment.recipeAddStepsTextLayout.error = getString(R.string.recipe_add_step_is_empty)
            isStepValid = false
        } else {
            bindingFragment.recipeAddStepsTextLayout.error = null
            bindingFragment.recipeAddStepsTextLayout.isErrorEnabled = false
            isStepValid = true
        }

        return (isNameValid && isIngredientValid && isStepValid)
    }

    private fun addNewTextField(id: Int, bindingFragment: FragmentRecipeAddBinding){
        val layoutInflater = layoutInflater.inflate(R.layout.edit_text_layout, null, false)
        //val frameLayout = layoutInflater.findViewById(R.id.recipeNewTextFrameLayout) as FrameLayout
        val subTextInputLayout = layoutInflater.findViewById(R.id.recipeNewTextInputLayout) as TextInputLayout

        //remove text field if user click remove icon
        subTextInputLayout.setEndIconOnClickListener {
            val layout = (subTextInputLayout.parent as ViewGroup)
            layout.removeView(subTextInputLayout)
        }

        when(id){
            R.id.recipeAddIngredientTextLayout->{
                subTextInputLayout.hint = getString(R.string.recipe_add_ingredient_label)
                val recipeAddIngredientTextInputEditText = bindingFragment.recipeAddIngredientTextLayout.editText

                //reassign text
                subTextInputLayout.editText!!.text = recipeAddIngredientTextInputEditText!!.text
                recipeAddIngredientTextInputEditText.setText("")

                //add the view to second last row
                ingredientLayout.addView(layoutInflater,ingredientLayout.childCount-1)
            }
            R.id.recipeAddStepsTextLayout->{
                subTextInputLayout.hint = getString(R.string.recipe_add_steps_label)
                val recipeAddStepsTextInputEditText = bindingFragment.recipeAddStepsTextLayout.editText

                //reassign text
                subTextInputLayout.editText!!.text = recipeAddStepsTextInputEditText!!.text
                recipeAddStepsTextInputEditText.setText("")

                stepsLayout.addView(layoutInflater, stepsLayout.childCount-1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                this.activity?.let { it1 -> hideSoftKeyboard(it1) }
                this.findNavController().navigateUp()
            }
        }

        return true
    }
}
