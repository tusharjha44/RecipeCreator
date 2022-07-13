package com.example.recipecreator

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipecreator.databinding.ItemListViewBinding

class RecipeAdapter(private val context: Context,private val list: ArrayList<Recipe>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                Glide
                    .with(context)
                    .load(this.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_error_24)
                    .into(binding.ivRecipeImage)

                binding.tvRecipeTitle.text = this.title
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class MyViewHolder(val binding: ItemListViewBinding): RecyclerView.ViewHolder(binding.root)
