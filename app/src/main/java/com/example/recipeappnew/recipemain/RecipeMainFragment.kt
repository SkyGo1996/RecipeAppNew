package com.example.recipeappnew.recipemain

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeappnew.DatabaseInstance
import com.example.recipeappnew.R
import com.example.recipeappnew.database.RecipeDatabase
import com.example.recipeappnew.databinding.FragmentRecipeMainBinding

class RecipeMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRecipeMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_main, container, false)

        val application = requireNotNull(this.activity).application

        //val dataSource = RecipeDatabase.getInstance(application).recipeDatabaseDao
        val dataSource = DatabaseInstance.instance

        val viewModelFactory = RecipeMainViewModelFactory(dataSource)

        val viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeMainViewModel::class.java)

        binding.recipeMainViewModel = viewModel

        binding.lifecycleOwner = this

        val adapter = RecipeAdapter(RecipeListener {
            recipe -> viewModel.onRecipeItemClicked(recipe)
        })
        binding.recipeMainRecyclerView.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.submitList(it)
                Log.i("test", "updated list size = ${it.size}")
            }
        })

        viewModel.navigateToRecipeAdd.observe(viewLifecycleOwner, Observer {
            if(it == true){
                this.findNavController().navigate(RecipeMainFragmentDirections.actionRecipeMainFragmentToRecipeAddFragment())
                viewModel.addNewRecipeLayoutTriggered()
            }
        })
        viewModel.navigateToRecipeDetail.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                this.findNavController().navigate(RecipeMainFragmentDirections.actionRecipeMainFragmentToRecipeDetailFragment(it))
                viewModel.onRecipeItemClickedCompleted()
            }
        })

        binding.recipeMainSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                if(binding.recipeMainSpinner.selectedItem.toString() != "All"){
                    val filteredRecipes = viewModel.recipes.value!!.filter { recipe ->  recipe.recipeType == binding.recipeMainSpinner.selectedItem.toString()}
                    adapter.submitList(filteredRecipes)
                } else {
                    adapter.submitList(viewModel.recipes.value)
                }
            }

        }

        return binding.root
    }
}