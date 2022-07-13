package com.example.recipecreator.view.activities

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.recipecreator.R
import com.example.recipecreator.databinding.ActivityCreateRecipeBinding
import com.example.recipecreator.databinding.DialogProgressBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

@Suppress("DEPRECATION")
class CreateRecipeActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
        private const val READ_STORAGE_PERMISSION_CODE = 101
    }

    private lateinit var mProgressDialog: Dialog
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var binding: ActivityCreateRecipeBinding
    private var imageUri: Uri? = null
    private var recipeImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        setUpActionBar()

        binding.ivRecipeImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnAddDish.setOnClickListener {
            if(imageUri!=null){
                uploadRecipeImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                saveRecipeToFirestore()
            }
        }

    }

    private fun showImageChooser() {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data?.data!!

            try {
                Glide.with(this).load(imageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_downloading)
                    .into(binding.ivRecipeImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    private fun saveRecipeToFirestore() {

        val title = binding.etTitle.text.toString()
        val category = binding.etCategory.text.toString()
        val recipeDetail = binding.etRecipe.text.toString()

        val recipe: HashMap<String,Any> = hashMapOf(
            "title" to title,
            "category" to category,
            "recipeDetail" to recipeDetail,
            "image" to recipeImageUri
        )

        firebaseFirestore.collection("recipes")
            .document(title)
            .set(recipe)
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

    private fun uploadRecipeImage() {

        showProgressDialog(resources.getString(R.string.please_wait))
        if(imageUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child(
                    "RECIPE_IMAGE" + System.currentTimeMillis()
                            + "." + getExtension(imageUri)
                )

            sRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    recipeImageUri = uri.toString()
                    saveRecipeToFirestore()

                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message, Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
            }

        }



    }

    private fun getExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.tbCreateRecipe)

        supportActionBar?.title = getString(R.string.create_recipe)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        binding.tbCreateRecipe.setNavigationOnClickListener { onBackPressed() }
    }

    private fun showProgressDialog(text:String){
        mProgressDialog = Dialog(this)
        val mBinding: DialogProgressBinding = DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(mBinding.root)
        mBinding.tvProgressText.text = text
        mProgressDialog.show()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
            else{
                Toast.makeText(
                    this,
                    "Oops, you just denied Storage permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}