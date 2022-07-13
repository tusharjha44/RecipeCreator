package com.example.recipecreator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipecreator.databinding.ActivityViewRecipesBinding
import com.google.firebase.firestore.*

class ViewRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRecipesBinding
    private lateinit var mAdapter: RecipeAdapter
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var db: FirebaseFirestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbViewRecipe)
        supportActionBar?.title = getString(R.string.recipe)

        binding.fabAddRecipe.setOnClickListener {
            startActivity(Intent(this,CreateOrEditRecipeActivity::class.java))
        }
        
        db = FirebaseFirestore.getInstance()
        eventChangeListener()
        recipeList = ArrayList()
        initRecyclerView()

    }

    private fun eventChangeListener() {

        db.collection("recipes").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        recipeList.add(dc.document.toObject(Recipe::class.java))
                    }
                }

                mAdapter.notifyDataSetChanged()
            }
        })

    }

    private fun initRecyclerView() {
        binding.rvDishesList.apply {
            setHasFixedSize(true)
            mAdapter = RecipeAdapter(this@ViewRecipesActivity,recipeList)
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@ViewRecipesActivity,2)
        }
    }

}