package com.example.recipeappnew.recipemain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeappnew.database.Recipe
import com.example.recipeappnew.databinding.RecipeMainRecyclerViewItemBinding

class RecipeAdapter(private val onClickListener: RecipeListener): ListAdapter<Recipe, RecipeAdapter.ViewHolder>(RecipeDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onClickListener)
    }

    class ViewHolder private constructor(private val binding: RecipeMainRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(recipe: Recipe, onClickListener: RecipeListener){
            binding.recipe = recipe
            binding.itemRecipeName.isSelected = true
            binding.clickListener = onClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipeMainRecyclerViewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class RecipeDiffCallback: DiffUtil.ItemCallback<Recipe>(){
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.recipeId == newItem.recipeId
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }

}

class RecipeListener(val clickListener: (recipe:Recipe)->Unit){
    fun onClick(recipe: Recipe) = clickListener(recipe)
}