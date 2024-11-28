package com.dicoding.storyapp.view.addstory

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.dicoding.storyapp.data.remote.response.AddNewStoryResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.utils.compressImage
import com.dicoding.storyapp.utils.isFileSizeValid
import com.dicoding.storyapp.utils.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<Resource<AddNewStoryResponse>>()
    val uploadResult: LiveData<Resource<AddNewStoryResponse>> = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _postStatus = MutableLiveData<String>()
    val postStatus: LiveData<String> get() = _postStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun addNewStory(description: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)

                var file = withContext(Dispatchers.IO) { uriToFile(imageUri, context) }

                if (!isFileSizeValid(file)) {
                    file = withContext(Dispatchers.IO) { compressImage(file, context) }
                }

                if (!isFileSizeValid(file)) {
                    _isLoading.postValue(false)
                    _errorMessage.postValue("File size exceeds 1 MB even after compression.")
                    return@launch
                }

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

                repository.addNewStory(descriptionBody, body)

                withContext(Dispatchers.Main) {
                    _postStatus.value = "success"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _postStatus.postValue("error")
                _errorMessage.postValue("Error uploading story: ${e.message}")
            }
        }
    }

}