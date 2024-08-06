package com.example.submissionpertamaai.Api


import com.example.submissionpertamaai.Response.ResponseDetail
import com.example.submissionpertamaai.Response.ResponseLogin
import com.example.submissionpertamaai.Response.ResponseRegister
import com.example.submissionpertamaai.Response.ResponseUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @GET("stories")
    fun getListStories(
        @Header("Authorization") Authorization: String
    ): Call<com.example.submissionpertamaai.ResponseStory>

    @GET("stories")
    suspend fun getListStoriesPage(
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): com.example.submissionpertamaai.ResponseStory

    @GET("stories?location=1")
    fun getListStoriesWithLocation(
        @Header("Authorization") Authorization: String
    ): Call<com.example.submissionpertamaai.ResponseStory>

    @FormUrlEncoded
    @POST("login")
    fun Forlogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseLogin>

    @FormUrlEncoded
    @POST("register")
    fun Forregister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseRegister>

    @GET("stories/{id}")
    fun DetailStory(
        @Path("id") id: String,
        @Header("Authorization") Authorization: String
    ): Call<ResponseDetail>

    @Multipart
    @POST("stories")
    fun Forupload(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") Authorization: String,
        @Part ("lat") lat: Float?,
        @Part ("lon") lon: Float?,

        ): Call<ResponseUpload>

}