package com.dicoding.storyapp.view.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.adapter.StoryAdapter
import com.dicoding.storyapp.view.addstory.AddStoryActivity
import com.dicoding.storyapp.view.storydetail.StoryDetailActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        playAnimation()
        observeStoryList()
        binding.fabAddStory.setOnClickListener {
            // Navigasi ke AddStoryActivity untuk menambah cerita baru
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        viewModel.refreshStories()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupRecyclerView() {
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.storyRecyclerView.setHasFixedSize(true)
    }

    private fun observeStoryList() {
        // Observasi data cerita
        viewModel.stories.observe(this) { stories ->
            if (stories != null && stories.isNotEmpty()) {
                adapter = StoryAdapter(stories) { story ->
                    val detailIntent = Intent(this, StoryDetailActivity::class.java).apply {
                        putExtra("STORY_ID", story.id) // Kirimkan ID cerita
                    }
                    startActivity(detailIntent)
                }
                binding.storyRecyclerView.adapter = adapter
                binding.storyRecyclerView.visibility = View.VISIBLE
                binding.emptyTextView.visibility = View.GONE
            } else {
                binding.storyRecyclerView.visibility = View.GONE
                binding.emptyTextView.visibility = View.VISIBLE
            }
        }

        // Observasi loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi error
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showErrorToast(errorMessage)
            }
        }
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun playAnimation() {

        // Menambahkan animasi fade-in untuk RecyclerView saat data dimuat
        binding.storyRecyclerView.alpha = 0f
        binding.storyRecyclerView.animate().alpha(1f).duration = 500

        // Animasi FAB (FloatingActionButton) muncul dari bawah
        val fabAnim = ObjectAnimator.ofFloat(binding.fabAddStory, View.TRANSLATION_Y, 300f, 0f).apply {
            duration = 300
        }
        fabAnim.start()

    }
}
