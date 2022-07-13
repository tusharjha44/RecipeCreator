package com.example.recipecreator.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.recipecreator.R
import com.example.recipecreator.databinding.ActivityRecipeDetailBinding
import com.example.recipecreator.model.Recipe

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        loadRecipeData()

    }

    private fun loadRecipeData() {
        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        if(recipe != null){
            binding.tvRecipeTitle.text = recipe.title
            binding.tvRecipeCategoryText.text = recipe.category
            binding.tvRecipesText.text = recipe.recipeDetail
            Glide
                .with(this)
                .load(recipe.image)
                .centerCrop()
                .placeholder(R.drawable.ic_downloading)
                .into(binding.ivRecipeImage)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.tbRecipeDetail)

        supportActionBar?.title = getString(R.string.recipe_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        binding.tbRecipeDetail.setNavigationOnClickListener { onBackPressed() }
    }
}