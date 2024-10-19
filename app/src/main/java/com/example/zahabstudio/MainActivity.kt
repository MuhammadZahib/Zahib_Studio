package com.example.zahabstudio

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zahabstudio.global.UploadStatus
import com.example.zahabstudio.networking.ApiServiceBuilder
import com.example.zahabstudio.repository.ImageRepository
import com.example.zahabstudio.viewModel.ImageUploadViewModel
import com.example.zahabstudio.viewModelFactory.ImageUploadViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ImageUploadViewModel
    private lateinit var previewView: PreviewView
    private lateinit var uploadProgress: ProgressBar
    private lateinit var uploadStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        previewView = findViewById(R.id.previewView)
        uploadProgress = findViewById(R.id.uploadProgress)
        uploadStatusText = findViewById(R.id.uploadStatusText)


        val apiService = ApiServiceBuilder.buildApiService()
        val repository = ImageRepository(apiService)

        viewModel = ViewModelProvider(this, ImageUploadViewModelFactory(repository, application))
            .get(ImageUploadViewModel::class.java)


        viewModel.startCamera(this, this, previewView)


        viewModel.imageCaptureState.observe(this, { imageData ->
            viewModel.uploadImage(imageData)
            showUploadInProgress()
        })


        viewModel.uploadState.observe(this, { status ->
            when (status) {
                is UploadStatus.InProgress -> updateUploadProgress(status.progress)
                is UploadStatus.Success -> showUploadSuccess()
                is UploadStatus.Failure -> showUploadFailure()
            }
        })


        viewModel.captureImage(this)
    }

    private fun showUploadInProgress() {
        uploadProgress.visibility = View.VISIBLE
        uploadStatusText.text = "Uploading..."
    }

    private fun updateUploadProgress(progress: Int) {
        uploadProgress.progress = progress
    }

    private fun showUploadSuccess() {
        uploadProgress.visibility = View.GONE
        uploadStatusText.text = "Upload Success!"
    }

    private fun showUploadFailure() {
        uploadProgress.visibility = View.GONE
        uploadStatusText.text = "Upload Failed. Retrying..."
    }
}


