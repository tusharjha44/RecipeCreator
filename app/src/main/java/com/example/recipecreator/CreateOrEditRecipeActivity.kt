package com.example.recipecreator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipecreator.databinding.ActivityCreateOrEditRecipeBinding

class CreateOrEditRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateOrEditRecipeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.tbCreateOrEditRecipe)

        supportActionBar?.title = getString(R.string.create_recipe)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        binding.tbCreateOrEditRecipe.setNavigationOnClickListener { onBackPressed() }

    }
}