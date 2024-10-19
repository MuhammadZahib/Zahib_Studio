package com.example.zahabstudio.global

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import java.util.concurrent.Executors

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val workManagerConfig = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setExecutor(Executors.newFixedThreadPool(3))
            .build()

        WorkManager.initialize(this, workManagerConfig)
    }
}
