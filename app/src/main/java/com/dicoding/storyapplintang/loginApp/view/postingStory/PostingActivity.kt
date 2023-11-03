package com.dicoding.storyapplintang.loginApp.view.postingStory

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapplintang.databinding.ActivityPostBinding
import com.dicoding.storyapplintang.loginApp.data.LoginPreference
import com.dicoding.storyapplintang.loginApp.utils.Result
import com.dicoding.storyapplintang.loginApp.utils.reduceFileImage
import com.dicoding.storyapplintang.loginApp.utils.uriToFile
import com.dicoding.storyapplintang.loginApp.view.CameraStory.CameraActivity
import com.dicoding.storyapplintang.loginApp.view.login.LoginActivity
import com.dicoding.storyapplintang.loginApp.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")


class PostingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostBinding

    private val postingViewModel: PostingViewModel by viewModels {
        PostingViewModelFactory.getInstance(
            this,
            LoginPreference.getInstance(dataStore)
        )
    }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { startGallery() }
            buttonUpload.setOnClickListener { setupButton() }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSessionValid()
    }

    private fun checkSessionValid() {
        postingViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERA_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPhotoPreview.setImageURI(it)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherGallery.launch(chooser)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            if (selectedImage != null) {
                currentImageUri = selectedImage
                showImage()
            }
        }
    }

    private fun setupButton() {
        binding.buttonUpload.setOnClickListener {
            postingViewModel.checkIfTokenAvailable().observe(this) {
                if (it == "null") {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    uploadImage("Bearer $it")
                }
            }
        }
    }

    private fun uploadStory(token: String, description: String, imageUri: Uri) {
        binding.progressBar.visibility = View.VISIBLE

        val imageFile = uriToFile(imageUri, this).reduceFileImage()
        val result = postingViewModel.postStory(token, imageFile, description)
        result.observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val error = it.error
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, "Story uploaded", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else -> {
                    // Handle other cases as needed
                }
            }
        }
    }

    // Pindahkan logika uploadImage yang lama ke sini
    private fun uploadImage(token: String) {
        if (binding.tvDescriptionPost.text.isNullOrEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            currentImageUri?.let { uri ->
                uploadStory(token, binding.tvDescriptionPost.text.toString(), uri)
            }
        }
    }
    }

