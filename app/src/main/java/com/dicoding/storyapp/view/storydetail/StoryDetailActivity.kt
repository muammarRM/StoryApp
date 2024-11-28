package com.dicoding.storyapp.view.storydetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityStoryDetailBinding
import com.dicoding.storyapp.view.ViewModelFactory

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    private val viewModel by viewModels<StoryDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID") ?: return
        observeDetailStory(storyId)
    }

    private fun observeDetailStory(storyId: String) {
        viewModel.getStoryDetail(storyId).observe(this) { storyDetail ->
            if (storyDetail != null) {
                binding.tvDetailName.text = storyDetail.story?.name
                binding.tvDetailDescription.text = storyDetail.story?.description
                Glide.with(this)
                    .load(storyDetail.story?.photoUrl)
                    .into(binding.ivDetailPhoto)
            } else {
                Toast.makeText(this, "Failed to load story details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
