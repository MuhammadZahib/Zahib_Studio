package com.example.zahabstudio.global

sealed class UploadStatus {
    data class InProgress(val progress: Int) : UploadStatus()
    object Success : UploadStatus()
    data class Failure(val exception: Exception) : UploadStatus()
}
