package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.AuthRepository
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository

object Injection {
    fun provideApiConfig(): ApiConfig {
        return ApiConfig
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiConfig = provideApiConfig()
        return UserRepository.getInstance(pref, apiConfig)
    }

    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService("") // Empty token for auth endpoints
        return AuthRepository(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val userRepository = provideUserRepository(context)
        val apiConfig = provideApiConfig()

        return StoryRepository.getInstance(
            userRepository,
            apiConfig
        )
    }
}