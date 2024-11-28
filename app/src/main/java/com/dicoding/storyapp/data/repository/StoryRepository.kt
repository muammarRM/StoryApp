package com.dicoding.storyapp.data.repository

import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.remote.response.AddNewStoryResponse
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getStoryDetail(storyId: String): DetailStoryResponse {
        return apiService.getStoryDetail(storyId)
    }

    suspend fun addNewStory(
        description: RequestBody,
        photo: MultipartBody.Part
    ): AddNewStoryResponse {
        return apiService.addNewStory(description, photo)
    }

    suspend fun getAllStories(): StoryResponse {
        return apiService.getStories()
    }
}
