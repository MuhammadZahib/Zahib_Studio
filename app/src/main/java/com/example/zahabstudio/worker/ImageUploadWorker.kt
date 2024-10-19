package com.example.zahabstudio.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.zahabstudio.networking.ApiServiceBuilder
import com.example.zahabstudio.repository.ImageRepository
import kotlinx.coroutines.runBlocking

class ImageUploadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val imageData = inputData.getByteArray("image_data") ?: return Result.failure()

        return try {

            val apiService = ApiServiceBuilder.buildApiService()
            val repository = ImageRepository(apiService)


            runBlocking {
                repository.uploadImage(imageData)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ImageUploadWorker", "Error uploading image: ${e.message}")
            Result.retry()
        }
    }
}
