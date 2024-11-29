package com.dicoding.storyapp.data.repository

import com.dicoding.storyapp.data.remote.response.AddNewStoryResponse
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val userRepository: UserRepository,
    private val apiConfig: ApiConfig
) {
    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            userRepository: UserRepository,
            apiConfig: ApiConfig
        ): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(userRepository, apiConfig)
                INSTANCE = instance
                instance
            }
        }
    }

    private val apiService: ApiService
        get() {
            val token = userRepository.getToken()
            if (token.isEmpty()) {
                throw IllegalStateException("Token not found. Please login first.")
            }
            return apiConfig.getApiService(token)
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
