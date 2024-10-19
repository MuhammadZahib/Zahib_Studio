package com.example.zahabstudio.repository

import com.example.zahabstudio.networking.ApiService
import com.example.zahabstudio.networking.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ImageRepository(private val apiService: ApiService) {

    suspend fun uploadImage(imageData: ByteArray): Result<Unit> {
        return try {
            val requestBody = MultipartBody.Part.createFormData(
                "image", "image_${System.currentTimeMillis()}.jpg",
                imageData.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val response = apiService.uploadImage(requestBody)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable("Upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

