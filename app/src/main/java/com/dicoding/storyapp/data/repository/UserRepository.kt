package com.dicoding.storyapp.data.repository

import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiConfig: ApiConfig
) {

    private var currentToken: String? = null

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
        currentToken = user.token
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
    fun getToken(): String {
        return runBlocking {
            userPreference.getSession().first().token
        }
    }

    suspend fun logout() {
        userPreference.logout()
        currentToken = null
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiConfig: ApiConfig
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiConfig)
            }.also { instance = it }
    }
}