package com.dicoding.storyapp.view.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.adapter.StoryAdapter
import com.dicoding.storyapp.view.addstory.AddStoryActivity
import com.dicoding.storyapp.view.storydetail.StoryDetailActivity
import com.dicoding.storyapp.view.welcome.WelcomeActivity

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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                // User is logged in, do nothing
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        setupView()
        setupRecyclerView()
        playAnimation()
        viewModel.refreshStories()
        observeStoryList()
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

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
    }

    private fun setupRecyclerView() {
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.storyRecyclerView.setHasFixedSize(true)
    }

    private fun observeStoryList() {
        viewModel.stories.observe(this) { stories ->
            if (stories != null && stories.isNotEmpty()) {
                adapter = StoryAdapter(stories) { story ->
                    val detailIntent = Intent(this, StoryDetailActivity::class.java).apply {
                        putExtra("STORY_ID", story.id)
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

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showErrorToast(errorMessage)
            }
        }
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshStories()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun playAnimation() {
        binding.storyRecyclerView.alpha = 0f
        binding.storyRecyclerView.animate().alpha(1f).duration = 500

        val fabAnim = ObjectAnimator.ofFloat(binding.fabAddStory, View.TRANSLATION_Y, 300f, 0f).apply {
            duration = 300
        }
        fabAnim.start()

    }
}
