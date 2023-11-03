package com.dicoding.storyapplintang.loginApp.view.Detail


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.dicoding.storyapplintang.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameDetail = intent.getStringExtra(NameStory)
        val descriptionDetail = intent.getStringExtra(DescStory)
        val imgUrl = intent.getStringExtra(ImageStory)
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(NameStory, nameDetail)
        intent.putExtra(DescStory, descriptionDetail)
        intent.putExtra(ImageStory, imgUrl)

        with(binding) {
            ivName.text = nameDetail
            ivDesc.text = descriptionDetail
            Glide.with(this@DetailActivity)
                .load(imgUrl)
                .into(ivStory)
        }

        // Set up shared element transition
        setSharedElementTransitions()
    }

    private fun setSharedElementTransitions() {
        val transition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)

        window.sharedElementEnterTransition = transition

    }

    companion object {
        const val NameStory = "name_story"
        const val DescStory = "desc_story"
        const val ImageStory = "image_story"
    }
}


