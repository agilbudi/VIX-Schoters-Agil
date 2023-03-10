package com.agilbudiprasetyo.newsapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import com.agilbudiprasetyo.newsapp.data.NewsRemoteMediator
import com.agilbudiprasetyo.newsapp.data.local.entity.BookmarkEntity
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.data.local.room.NewsDatabase
import com.agilbudiprasetyo.newsapp.data.remote.retrofit.ApiService
import com.agilbudiprasetyo.newsapp.utils.AppExecutors
import com.agilbudiprasetyo.newsapp.utils.Result

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val database: NewsDatabase,
    private val appExecutors: AppExecutors
){

    @OptIn(ExperimentalPagingApi::class)
    fun getNews(language: String, sortBy: String): LiveData<Result<PagingData<NewsEntity>>>{
        val result = MediatorLiveData<Result<PagingData<NewsEntity>>>()
        result.value = Result.Loading
        try {
            val data = Pager(
                config = PagingConfig(pageSize = 8),
                remoteMediator = NewsRemoteMediator(language,sortBy, database, apiService),
                pagingSourceFactory = {database.newsDao().news()}
            ).liveData
            result.addSource(data){ newData ->
                result.value = Result.Success(newData)
            }
        }catch (t:Throwable){
            result.value = Result.Error(t.message.toString())
            Log.e("REPOSITORY-ERROR", t.message.toString())
        }
        return result
    }

    fun setBookmark(news: NewsEntity, bookmarkState: Boolean){
        appExecutors.diskIO.execute {
            news.isBookmarked = bookmarkState
            if (bookmarkState){
                database.bookmarkDao().insertBookmark(
                    BookmarkEntity(
                        title = news.title,
                        author = news.author,
                        description = news.description,
                        url = news.url,
                        urlToImage = news.urlToImage,
                        publishedAt = news.publishedAt,
                        isBookmarked = bookmarkState
                    )
                )
            }else{
                database.bookmarkDao().deleteBookmark(news.title)
            }
            database.newsDao().update(news)
        }
    }

    fun getBookmark(): LiveData<List<NewsEntity>> = database.newsDao().bookmarked()

    companion object{
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(
            apiService: ApiService,
            database: NewsDatabase,
            appExecutors: AppExecutors
        ): NewsRepository = instance?: synchronized(this){
            instance?: NewsRepository(apiService, database, appExecutors)
        }.also { instance = it }
    }
}