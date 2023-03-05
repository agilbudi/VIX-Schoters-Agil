package com.agilbudiprasetyo.newsapp.data.remote.response

import com.agilbudiprasetyo.newsapp.model.News

data class Articles(
    var status: String,
    var totalResults: Int,
    var articles: List<News>
)
