package com.example.submissionpertamaai.Paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionpertamaai.Api.ApiService
import com.example.submissionpertamaai.ListStoryItem

class StoryRepository (private val apiService: ApiService){
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)

            }
        ).liveData
    }

}