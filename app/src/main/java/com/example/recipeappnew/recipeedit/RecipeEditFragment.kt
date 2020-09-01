package com.example.recipeappnew.recipeedit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.recipeappnew.DatabaseInstance
import com.example.recipeappnew.R
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.databinding.FragmentRecipeEditBinding
import com.example.recipeappnew.hideSoftKeyboard
import com.example.recipeappnew.recipeadd.IMAGE_PICKER_REQ
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_recipe_edit.*

class RecipeEditFragment : Fragment() {

    private lateinit var viewModel:RecipeEditViewModel
    private lateinit var binding:FragmentRecipeEditBinding
    private lateinit var selectedRecipe: Recipe

    private lateinit var ingredientLayout: LinearLayout
    private lateinit var stepsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_edit, container, false)

        val dataSource = DatabaseInstance.instance

        selectedRecipe = RecipeEditFragmentArgs.fromBundle(requireArguments()).selectedRecipe

        val viewModelFactory = RecipeEditViewModelFactory(dataSource, selectedRecipe)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeEditViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ingredientLayout = binding.recipeEditIngredientLayout
        stepsLayout = binding.recipeEditStepsLayout

        initializeUI()

        viewModel.navigateToGallery.observe(viewLifecycleOwner, Observer {
            if(it==true){
                val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, IMAGE_PICKER_REQ)
                viewModel.navigateToGalleryCompleted()
            }
        })
        viewModel.navigateEditToDetail.observe(viewLifecycleOwner, Observer {
            if(it==true){
                this.findNavController().navigateUp()
                this.activity?.let { it1 -> hideSoftKeyboard(it1) }
                viewModel.navigateToDetailCompleted()
            }
        })
        viewModel.recipeEditToDetail.observe(viewLifecycleOwner, Observer{
            if(it==true){
                if(allUIIsValid()){
                    selectedRecipe.recipeType = binding.recipeEditTypeSpinner.selectedItem.toString()
                    val recipeIngredientsList = mutableListOf<String>()
                    for(i in 0 until ingredientLayout.childCount){
                        val ingredientText = (ingredientLayout.getChildAt(i) as TextInputLayout).editText!!.text.toString()
                        if(ingredientText.isNotBlank()){
                            recipeIngredientsList.add(ingredientText)
                        }
                    }
                    selectedRecipe.recipeIngredient = recipeIngredientsList
                    val recipeStepsList = mutableListOf<String>()
                    for (i in 0 until stepsLayout.childCount){
                        val stepText = (stepsLayout.getChildAt(i) as TextInputLayout).editText!!.text.toString()
                        if(stepText.isNotBlank()){
                            recipeStepsList.add(stepText)
                        }
                    }
                    selectedRecipe.recipeSteps = recipeStepsList
                    viewModel.updateRecipe(selectedRecipe)
                    viewModel.updateClickedCompleted()
                }
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initializeUI(){
        val recipeTypeList = resources.getStringArray(R.array.recipe_type).toMutableList()
        recipeTypeList.remove("All")
        val typeAdapter = ArrayAdapter<String>(this.requireActivity().applicationContext, android.R.layout.simple_spinner_item, recipeTypeList)
        binding.recipeEditTypeSpinner.adapter = typeAdapter
        var spinnerSelectedPosition = 0
        for(i in 0 until recipeTypeList.size){
            if(recipeTypeList[i] == selectedRecipe.recipeType){
                spinnerSelectedPosition = i
            }
        }
        binding.recipeEditTypeSpinner.setSelection(spinnerSelectedPosition)

        binding.recipeEditIngredientTextLayout.setEndIconOnClickListener {
            addNewTextField(binding.recipeEditIngredientTextLayout.id, binding)
        }
        binding.recipeEditStepsTextLayout.setEndIconOnClickListener {
            addNewTextField(binding.recipeEditStepsTextLayout.id, binding)
        }

        var viewCounter = 0
        selectedRecipe.recipeIngredient.forEach {
            addNewTextField(binding.recipeEditIngredientTextLayout.id, binding)
            val textLayout = ingredientLayout.getChildAt(viewCounter) as TextInputLayout
            val textInputEditText = textLayout.editText
            textInputEditText!!.setText(selectedRecipe.recipeIngredient[viewCounter])
            viewCounter++
        }
        viewCounter = 0
        selectedRecipe.recipeSteps.forEach {
            addNewTextField(binding.recipeEditStepsTextLayout.id, binding)
            val textLayout = stepsLayout.getChildAt(viewCounter) as TextInputLayout
            val textInputEditText = textLayout.editText
            textInputEditText!!.setText(selectedRecipe.recipeSteps[viewCounter])
            viewCounter++
        }

    }

    private fun addNewTextField(id: Int, binding: FragmentRecipeEditBinding) {
        val layoutInflater = layoutInflater.inflate(R.layout.edit_text_layout, null, false)
        val subTextInputLayout = layoutInflater.findViewById(R.id.recipeNewTextInputLayout) as TextInputLayout

        //remove text field if user click remove icon
        subTextInputLayout.setEndIconOnClickListener {
            val layout = (subTextInputLayout.parent as ViewGroup)
            layout.removeView(subTextInputLayout)
        }

        when(id){
            R.id.recipeEditIngredientTextLayout->{
                subTextInputLayout.hint = getString(R.string.recipe_add_ingredient_label)
                val recipeEditIngredientTextInputEditText = binding.recipeEditIngredientTextLayout.editText

                //reassign text
                subTextInputLayout.editText!!.text = recipeEditIngredientTextInputEditText!!.text
                recipeEditIngredientTextInputEditText.setText("")

                //add the view to second last row
                ingredientLayout.addView(layoutInflater,ingredientLayout.childCount-1)
            }
            R.id.recipeEditStepsTextLayout->{
                subTextInputLayout.hint = getString(R.string.recipe_add_steps_label)
                val recipeEditStepsTextInputEditText = binding.recipeEditStepsTextLayout.editText

                //reassign text
                subTextInputLayout.editText!!.text = recipeEditStepsTextInputEditText!!.text
                recipeEditStepsTextInputEditText.setText("")

                stepsLayout.addView(layoutInflater, stepsLayout.childCount-1)
            }
        }
    }

    private fun allUIIsValid(): Boolean{
        var isNameValid = false
        var isIngredientValid = false
        var isStepValid = false

        //check recipe name is empty
        if(binding.recipeEditName.editText?.text.isNullOrBlank()){
            binding.recipeEditName.isErrorEnabled = true
            binding.recipeEditName.error = getString(R.string.recipe_add_name_is_empty)
            isNameValid = false
        } else {
            binding.recipeEditName.error = null
            binding.recipeEditName.isErrorEnabled = false
            isNameValid = true
        }

        //check ingredient is empty
        var ingredientEmptyFieldCount = 0
        for (i in 0 until ingredientLayout.childCount){
            val textInputLayout = ingredientLayout.getChildAt(i) as TextInputLayout
            if(textInputLayout.editText?.text.isNullOrBlank()){
                ingredientEmptyFieldCount++
            }
        }
        if(ingredientEmptyFieldCount==ingredientLayout.childCount){
            binding.recipeEditIngredientTextLayout.isErrorEnabled = true
            binding.recipeEditIngredientTextLayout.error = getString(R.string.recipe_add_ingredient_is_empty)
            isIngredientValid = false
        } else {
            binding.recipeEditIngredientTextLayout.error = null
            binding.recipeEditIngredientTextLayout.isErrorEnabled = false
            isIngredientValid = true
        }

        //check step is empty
        var stepEmptyFieldCount = 0
        for (i in 0 until stepsLayout.childCount){
            val textInputLayout = stepsLayout.getChildAt(i) as TextInputLayout
            if(textInputLayout.editText?.text.isNullOrBlank()){
                stepEmptyFieldCount++
            }
        }
        if(stepEmptyFieldCount==stepsLayout.childCount){
            binding.recipeEditStepsTextLayout.isErrorEnabled = true
            binding.recipeEditStepsTextLayout.error = getString(R.string.recipe_add_step_is_empty)
            isStepValid = false
        } else {
            binding.recipeEditStepsTextLayout.error = null
            binding.recipeEditStepsTextLayout.isErrorEnabled = false
            isStepValid = true
        }

        return (isNameValid && isIngredientValid && isStepValid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                IMAGE_PICKER_REQ->{
                    viewModel.imageUri = data!!.data
                    //var imageUriString = getRealPathFromUri(imageUri, this)
                    //imageUriString = "file://$imageUriString"
                    Log.i("test", "imageURI = ${viewModel.imageUri}")
                    //val imageUriNew = File(imageUriString.)

                    this.activity?.contentResolver?.takePersistableUriPermission(viewModel.imageUri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    Glide.with(this)
                        .load(viewModel.imageUri)
                        .into(binding.recipeEditImage)
                }
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