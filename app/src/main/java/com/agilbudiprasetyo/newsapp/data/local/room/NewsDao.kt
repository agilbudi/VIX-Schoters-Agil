package com.agilbudiprasetyo.newsapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Update
    fun update(news: NewsEntity)

    @Delete
    fun delete(news: NewsEntity)

    @Query("SELECT * FROM news")
    fun news(): PagingSource<Int,NewsEntity>

    @Query("DELETE FROM news")
    fun deleteAll()
}
