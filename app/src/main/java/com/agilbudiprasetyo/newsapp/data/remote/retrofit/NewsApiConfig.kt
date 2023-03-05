package com.agilbudiprasetyo.newsapp.data.remote.retrofit

import androidx.lifecycle.LiveData
import com.agilbudiprasetyo.newsapp.data.remote.response.Articles
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiConfig {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int
    ): Call<LiveData<Articles>>
    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Articles
    @GET("top-headlines")
    suspend fun getSearch(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Articles
}