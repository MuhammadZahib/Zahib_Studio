package com.example.zahabstudio.viewModelFactory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zahabstudio.repository.ImageRepository
import com.example.zahabstudio.viewModel.ImageUploadViewModel

class ImageUploadViewModelFactory(
    private val repository: ImageRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageUploadViewModel::class.java)) {
            return ImageUploadViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
