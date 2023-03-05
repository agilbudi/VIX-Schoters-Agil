package com.agilbudiprasetyo.newsapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.data.local.entity.RemoteKeys
import com.agilbudiprasetyo.newsapp.data.local.room.NewsDatabase
import com.agilbudiprasetyo.newsapp.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val language: String,
    private val sortBy: String,
    private val database: NewsDatabase,
    private val apiService: ApiService
): RemoteMediator<Int, NewsEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>
    ): MediatorResult {
        val page = when(loadType){
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1)?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND ->{
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND ->{
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.news(language, sortBy, page, state.config.pageSize).articles
            var endOfPaginationReached = false
                database.withTransaction {
                val data = responseData.map {
                    val isBookmark = database.bookmarkDao().isBookmarked(it.title)
                    NewsEntity(
                        title = it.title,
                        author = it.author,
                        description = it.description,
                        url = it.url,
                        urlToImage = it.urlToImage,
                        publishedAt = it.publishedAt,
                        isBookmarked = isBookmark
                    )
                }
                endOfPaginationReached = data.isEmpty()

                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeyDao().deleteRemoteKeys()
                        database.newsDao().deleteAll()
                        Log.e("DATABASE", "Database on REFRESH!!!!")
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys =
                        data.map { RemoteKeys(id = it.title, prevKey = prevKey, nextKey = nextKey) }
                    database.remoteKeyDao().insertAll(keys)
                    database.newsDao().insertNews(data)
                }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (exception: Exception){
            Log.e("MEDIATOR-ERROR", exception.toString())
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, NewsEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeyDao().getRemoteKeysId(data.title)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, NewsEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeyDao().getRemoteKeysId(data.title)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, NewsEntity>): RemoteKeys?{
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.title?.let { title ->
                database.remoteKeyDao().getRemoteKeysId(title)
            }
        }
    }

    companion object{
        const val INITIAL_PAGE_INDEX = 1
    }
}