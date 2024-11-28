package com.dicoding.storyapp.view.storydetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository

class StoryDetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryDetail(storyId: String): LiveData<DetailStoryResponse> = liveData {
        try {
            val response = storyRepository.getStoryDetail(storyId)
            emit(response)
        } catch (e: Exception) {
            Log.e("StoryDetailViewModel", "Error fetching story details", e)
        }
    }
}
