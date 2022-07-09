package com.example.recipecreator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipecreator.databinding.ActivityViewRecipesBinding

class ViewRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRecipesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbViewRecipe)
        supportActionBar?.title = getString(R.string.recipe)

        binding.fabAddRecipe.setOnClickListener {
            startActivity(Intent(this,CreateOrEditRecipeActivity::class.java))
        }

    }

}