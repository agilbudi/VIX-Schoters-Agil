package com.agilbudiprasetyo.newsapp.data.local.di

import android.content.Context
import com.agilbudiprasetyo.newsapp.data.local.room.NewsDatabase
import com.agilbudiprasetyo.newsapp.data.remote.retrofit.ApiService
import com.agilbudiprasetyo.newsapp.data.repository.NewsRepository
import com.agilbudiprasetyo.newsapp.utils.AppExecutors

object Injection {
    fun provideNewsRepository(context: Context): NewsRepository{
        val apiService = ApiService
        val database = NewsDatabase.getInstance(context)
        val appExecutors = AppExecutors()
        return NewsRepository.getInstance(apiService, database, appExecutors)
    }
}