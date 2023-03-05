package com.agilbudiprasetyo.newsapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agilbudiprasetyo.newsapp.data.local.di.Injection
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.data.repository.NewsRepository

class DetailViewModel(private val newsRepository: NewsRepository): ViewModel() {

    fun saveNews(news: NewsEntity){
        newsRepository.setBookmark(news, true)
    }

    fun deleteNews(news: NewsEntity){
        newsRepository.setBookmark(news, false)
    }
}
class DetailViewModelFactory private constructor(
    private val newsRepository: NewsRepository
): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(newsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: "+ modelClass.name)
    }

    companion object{
        @Volatile
        private var instance: DetailViewModelFactory? = null
        fun getInstance(context: Context): DetailViewModelFactory =
            instance?: synchronized(this){
                instance?: DetailViewModelFactory(Injection.provideNewsRepository(context))
            }.also { instance = it }
    }
}