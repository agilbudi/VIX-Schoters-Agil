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
    var title: String
): Parcelable