package com.agilbudiprasetyo.newsapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.agilbudiprasetyo.newsapp.adapter.NewsPagingAdapter
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.databinding.FragmentNewsBinding
import com.agilbudiprasetyo.newsapp.ui.DetailActivity
import com.agilbudiprasetyo.newsapp.ui.viewmodel.MainViewModel
import com.agilbudiprasetyo.newsapp.ui.viewmodel.MainViewModelFactory
import com.agilbudiprasetyo.newsapp.utils.Result
import com.agilbudiprasetyo.newsapp.utils.showToast

class NewsFragment : Fragment() {
    private var  _binding: FragmentNewsBinding? = null
    private val binding get() = _binding
    private lateinit var viewFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewFactory }
    private lateinit var newsPagingAdapter: NewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFactory = MainViewModelFactory.getInstance(view.context)
        newsPagingAdapter = NewsPagingAdapter{ news ->
            if (news.isBookmarked){
                viewModel.deleteNews(news)
            }else{
                viewModel.saveNews(news)
            }
        }

        binding?.rvNews?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = newsPagingAdapter
        }

        updateListNews(view.context)
        newsPagingAdapter.setOnItemClickCallback(object : NewsPagingAdapter.OnItemClickCallback{
            override fun onItemClicked(data: NewsEntity) {
                selectedItem(view.context, data)
            }
        })
    }

    private fun selectedItem(context: Context, data: NewsEntity) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_NEWS, data)
        startActivity(intent)
    }

    private fun updateListNews(context: Context) {
        try {
            viewModel.news(language[0], sortBy[2]).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        newsPagingAdapter.submitData(lifecycle, result.data)
                        Log.e("MAIN-DATA", result.data.map { it.title }.toString())
                        showLoading(false)
                    }
                    is Result.Error -> {
                        showToast(context, result.error, 1)
                        showLoading(false)
                    }
                }
            }
        }catch (e: Exception){
            Log.e("FRAGMENT-NEWS", e.message.toString())
        }
    }
    private fun showLoading(status: Boolean) {
        if (status){
            binding?.progressBar?.visibility = View.VISIBLE
        }else{
            binding?.progressBar?.visibility = View.GONE
        }
    }

    companion object{
        private val language = arrayListOf("id","us","en","es")
        private val sortBy = arrayListOf("relevancy", "popularity", "publishedAt")
    }
}