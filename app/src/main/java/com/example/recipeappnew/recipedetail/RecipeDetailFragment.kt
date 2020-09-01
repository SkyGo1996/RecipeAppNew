package com.example.recipeappnew.recipedetail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeappnew.DatabaseInstance
import com.example.recipeappnew.R
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {
    private lateinit var viewModel: RecipeDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentRecipeDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false)
        binding.recipeDetailNameText.isSelected = true

        val dataSource = DatabaseInstance.instance

        val selectedRecipe: Recipe = RecipeDetailFragmentArgs.fromBundle(requireArguments()).selectedRecipe

        val viewModelFactory = RecipeDetailViewModelFactory(dataSource, selectedRecipe)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeDetailViewModel::class.java)

        viewModel.navigateDetailToMain.observe(viewLifecycleOwner, Observer {
            if(it==true){
                Toast.makeText(this.context, getString(R.string.recipe_detail_delete_successful_message), Toast.LENGTH_LONG).show()
                this.findNavController().navigateUp()
                viewModel.recipeDetailToMainCompleted()
            }
        })
        viewModel.navigateDetailToEdit.observe(viewLifecycleOwner, Observer {
            if(it==true){
                this.findNavController().navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToRecipeEditFragment(selectedRecipe))
                viewModel.recipeDEtailToEditCompleted()
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_detail_appbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.detailAppBarDelete->{
                viewModel.deleteRecipe(viewModel.selectedRecipe)
                viewModel.onRecipeDetailToMain()
            }
            R.id.detailAppBarEdit->{
                viewModel.onRecipeDetailToEdit()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}