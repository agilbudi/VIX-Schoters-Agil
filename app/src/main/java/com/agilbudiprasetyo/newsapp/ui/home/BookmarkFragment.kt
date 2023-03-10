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
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.agilbudiprasetyo.newsapp.adapter.BookmarkAdapter
import com.agilbudiprasetyo.newsapp.adapter.NewsPagingAdapter
import com.agilbudiprasetyo.newsapp.data.local.entity.BookmarkEntity
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.databinding.FragmentBookmarkBinding
import com.agilbudiprasetyo.newsapp.ui.DetailActivity
import com.agilbudiprasetyo.newsapp.ui.viewmodel.MainViewModel
import com.agilbudiprasetyo.newsapp.ui.viewmodel.MainViewModelFactory

class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding
    private lateinit var viewFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels{viewFactory}
    private lateinit var bookmarkAdapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFactory = MainViewModelFactory.getInstance(view.context)
        bookmarkAdapter = BookmarkAdapter{ news ->
            val data = NewsEntity(
                title = news.title,
                author = news.author,
                description = news.description,
                url = news.url,
                urlToImage = news.urlToImage,
                publishedAt = news.publishedAt,
                isBookmarked = news.isBookmarked
            )
            if (news.isBookmarked){
                viewModel.deleteNews(data)
            }else{
                viewModel.saveNews(data)
            }
        }

        binding?.rvBookmarks?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = bookmarkAdapter
        }

        updateBookmarkNews()
        bookmarkAdapter.setOnItemClickCallback(object : BookmarkAdapter.OnItemClickCallback{
            override fun onItemClicked(data: BookmarkEntity) {
                selectedItem(view.context, data)
            }
        })
    }

    private fun selectedItem(context: Context, data: BookmarkEntity) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_NEWS, data)
        startActivity(intent)
    }

    private fun updateBookmarkNews() {
        try {
            showLoading(true)
            viewModel.bookmark().observe(viewLifecycleOwner){ bookmarkNews ->
                bookmarkAdapter.submitList(bookmarkNews)
            }
            showLoading(false)
        }catch (e: Exception){
            Log.e("FRAGMENT-BOOKMARK", e.message.toString())
            showLoading(false)
        }
    }

    private fun showLoading(status: Boolean) {
        if (status){
            binding?.progressBar?.visibility = View.VISIBLE
        }else{
            binding?.progressBar?.visibility = View.GONE
        }
    }
}