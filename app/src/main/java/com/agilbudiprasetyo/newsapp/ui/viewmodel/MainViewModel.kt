package com.agilbudiprasetyo.newsapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agilbudiprasetyo.newsapp.data.local.di.Injection
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.data.repository.NewsRepository

class MainViewModel(private val newsRepository: NewsRepository): ViewModel() {

    fun news(language: String, sortBy: String) = newsRepository.getNews(language, sortBy)

    fun bookmark() = newsRepository.getBookmark()

    fun saveNews(news: NewsEntity){
        newsRepository.setBookmark(news, true)
    }

    fun deleteNews(news: NewsEntity){
        newsRepository.setBookmark(news, false)
    }

}
class MainViewModelFactory private constructor(
    private val newsRepository: NewsRepository
): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(newsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: "+ modelClass.name)
    }

    companion object{
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(context: Context): MainViewModelFactory =
            instance?: synchronized(this){
                instance?: MainViewModelFactory(Injection.provideNewsRepository(context))
            }.also { instance = it }
    }
}