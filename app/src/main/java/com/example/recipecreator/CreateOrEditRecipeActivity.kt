package com.example.recipecreator

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.recipecreator.databinding.ActivityCreateOrEditRecipeBinding
import com.example.recipecreator.databinding.DialogProgressBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Suppress("DEPRECATION")
class CreateOrEditRecipeActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
    }

    private lateinit var mProgressDialog: Dialog
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var binding: ActivityCreateOrEditRecipeBinding
    private lateinit var imageUri: Uri
    private var recipeImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        setUpActionBar()

        binding.ivRecipeImage.setOnClickListener {
            pickImage()
        }

        binding.btnAddDish.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            uploadRecipe()
        }

    }

    private fun pickImage(){
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_error_24)
                .into(binding.ivRecipeImage)
        }

    }

    private fun saveRecipeToFirestore(
        imageUri: String
    ) {

        val title = binding.etTitle.text.toString()
        val category = binding.etCategory.text.toString()
        val recipes = binding.etRecipe.text.toString()

        val recipe = HashMap<String, Any>()
        recipe["title"] = title
        recipe["category"] = category
        recipe["recipes"] = recipes
        recipe["image"] = imageUri

        firebaseFirestore.collection("recipes")
            .add(recipe)
            .addOnSuccessListener {
                hideProgressDialog()
                Toast.makeText(this,"Recipe Added Successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ViewRecipesActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "FAILED to add the recipe", Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun uploadRecipe() {
        val sRef: StorageReference = FirebaseStorage.getInstance()
            .reference.child(
                "RECIPE_IMAGE" + System.currentTimeMillis()
                        + "." + getExtension(imageUri)
            )

        sRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                recipeImageUri = uri.toString()
                saveRecipeToFirestore(recipeImageUri)

            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    it.message, Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.tbCreateOrEditRecipe)

        supportActionBar?.title = getString(R.string.create_recipe)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        binding.tbCreateOrEditRecipe.setNavigationOnClickListener { onBackPressed() }
    }

    private fun showProgressDialog(text:String){
        mProgressDialog = Dialog(this)
        val mBinding: DialogProgressBinding = DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(mBinding.root)
        mBinding.tvProgressText.text = text
        mProgressDialog.show()

    }

    private fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}