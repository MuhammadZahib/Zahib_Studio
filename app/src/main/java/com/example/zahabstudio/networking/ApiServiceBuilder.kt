package com.example.zahabstudio.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceBuilder {

    fun buildApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-api-endpoint.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
