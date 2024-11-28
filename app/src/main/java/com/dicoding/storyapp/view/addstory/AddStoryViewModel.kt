//package com.dicoding.storyapp.view.addstory
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.dicoding.storyapp.data.local.entity.Story
//import com.dicoding.storyapp.data.repository.StoryRepository
//import kotlinx.coroutines.launch
//
//class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
//    fun addStory(title: String, content: String) {
//        viewModelScope.launch {
//            val story = Story(title = title, content = content)
//            repository.insertStory(story)
//        }
//    }
//}