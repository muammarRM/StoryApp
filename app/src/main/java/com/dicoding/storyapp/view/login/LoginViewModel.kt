package com.dicoding.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.repository.AuthRepository
import com.dicoding.storyapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginStatus = MutableLiveData<String>()
    val loginStatus: LiveData<String> = _loginStatus

    private val _loginErrorMessage = MutableLiveData<String?>()
    val loginErrorMessage: MutableLiveData<String?> = _loginErrorMessage

    // Fungsi untuk login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginUser(email, password)

                val user = UserModel(email, response.loginResult.token, isLogin = true)
                userRepository.saveSession(user)

                withContext(Dispatchers.Main) {
                    _loginStatus.value = "success"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _loginStatus.value = "error"
                }
            }
        }
    }
}