package com.agilbudiprasetyo.newsapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "bookmark")
@Parcelize
data class BookmarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "author")
    var author: String?,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "image")
    var urlToImage: String?,

    @ColumnInfo(name = "publishedAt")
    var publishedAt: String,

    @ColumnInfo(name = "isBookmarked")
    var isBookmarked: Boolean
): Parcelable