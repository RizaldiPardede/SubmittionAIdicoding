package com.example.submissionpertamaai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.submissionpertamaai.Response.ResponseDetail
import com.example.submissionpertamaai.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(ID).toString()
        val Token = intent.getStringExtra(TOKEN).toString()

        detailsetup(id,Token)



    }

    private fun detailsetup(id:String,Token:String) {
        showLoading(true)
        val apiService = com.example.submissionpertamaai.Api.ApiConfig.getApiService()
        val getDetailStoryRequest = apiService.DetailStory(id,"Bearer $Token")
        getDetailStoryRequest.enqueue(object : Callback<ResponseDetail> {
            override fun onResponse(
                call: Call<ResponseDetail>,
                response: Response<ResponseDetail>
            ) {
                val responseBody = response.body()
                showLoading(false)
                if (responseBody != null) {
                    Glide.with(this@DetailActivity)
                        .load(responseBody.story?.photoUrl)
                        .into(binding.storyimg)
                    binding.tvUsername.text = responseBody.story?.name
                    binding.tvCreatedAdd.text = responseBody.story?.createdAt
                    binding.tvDesc.text = responseBody.story?.description
                }
            }

            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                showLoading(false)

            }

        })
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    companion object {
        const val ID = "extra_id"
        const val TOKEN = "extra_token"

    }
}