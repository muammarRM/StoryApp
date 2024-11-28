package com.dicoding.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerStatus = MutableLiveData<String>()
    val registerStatus: LiveData<String> = _registerStatus

    private val _registerErrorMessage = MutableLiveData<String?>()
    val registerErrorMessage: MutableLiveData<String?> = _registerErrorMessage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                //get success message
                val message = repository.register(name, email, password).message

                _registerStatus.value = "success"
            } catch (e: HttpException) {
                //get error message
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message

                _registerErrorMessage.value = errorMessage
                _registerStatus.value = "error"
            } catch (e: Exception) {
                // Tangani error lain selain HttpException
                _registerErrorMessage.value = "An unexpected error occurred: ${e.message}"
                _registerStatus.value = "error"
            }
        }
    }
}