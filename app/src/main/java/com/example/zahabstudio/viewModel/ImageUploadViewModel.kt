package com.example.zahabstudio.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.zahabstudio.global.UploadStatus
import com.example.zahabstudio.repository.ImageRepository
import com.example.zahabstudio.worker.ImageUploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ImageUploadViewModel(
    private val repository: ImageRepository,
    application: Application
) : AndroidViewModel(application){

    private val _imageCaptureState = MutableLiveData<ByteArray>()
    val imageCaptureState: LiveData<ByteArray> = _imageCaptureState

    private val _uploadState = MutableLiveData<UploadStatus>()
    val uploadState: LiveData<UploadStatus> = _uploadState

    var imageCapture: ImageCapture? = null

    fun startCamera(activity: AppCompatActivity, lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun captureImage(context: Context) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                imageCapture?.let {
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(createTempFile()).build()
                    it.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = imageProxyToBitmap(image)
                            val byteArray = bitmapToByteArray(bitmap)
                            _imageCaptureState.postValue(byteArray)
                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("ImageCapture", "Error capturing image", exception)
                        }
                    })
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    fun uploadImage(byteArray: ByteArray) {
        val data = Data.Builder()
            .putByteArray("image_data", byteArray)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setInputData(data)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10_000,
                TimeUnit.MILLISECONDS
            )
            .build()


        WorkManager.getInstance(getApplication()).enqueue(uploadWorkRequest)


        WorkManager.getInstance(getApplication()).getWorkInfoByIdLiveData(uploadWorkRequest.id)
            .observeForever { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            _uploadState.postValue(UploadStatus.Success)
                        }
                        WorkInfo.State.FAILED -> {
                            _uploadState.postValue(UploadStatus.Failure(Exception("Upload failed")))
                        }
                        else -> {
                        }
                    }
                }
            }
    }

    private fun createTempFile(): File {
        return File.createTempFile("temp_image", ".jpg")
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}


