package com.example.recipecreator.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipecreator.R
import com.example.recipecreator.databinding.ItemListViewBinding
import com.example.recipecreator.model.Recipe


class RecipeAdapter(private val list: ArrayList<Recipe>) :
    RecyclerView.Adapter<MyViewHolder>() {

    var onItemClickListener: ((Recipe) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipe = list[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(recipe)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class MyViewHolder(private val binding: ItemListViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(recipe: Recipe) {
        Glide
            .with(binding.ivRecipeImage)
            .load(recipe.image)
            .centerCrop()
            .placeholder(R.drawable.ic_downloading)
            .into(binding.ivRecipeImage)

        binding.tvRecipeTitle.text = recipe.title


    }
}

