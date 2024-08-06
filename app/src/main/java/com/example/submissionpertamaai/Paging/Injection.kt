package com.example.submissionpertamaai.Paging

import android.content.Context
import com.example.submissionpertamaai.Api.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {

        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}