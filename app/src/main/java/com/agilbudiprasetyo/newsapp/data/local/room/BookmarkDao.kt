package com.agilbudiprasetyo.newsapp.data.local.room

import androidx.room.*
import com.agilbudiprasetyo.newsapp.data.local.entity.BookmarkEntity

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(news: BookmarkEntity)

    @Query("DELETE FROM bookmark WHERE title = :title ")
    fun deleteBookmark(title: String)

    @Query("SELECT EXISTS(SELECT * FROM bookmark WHERE title = :title)")
    fun isBookmarked(title: String): Boolean
}