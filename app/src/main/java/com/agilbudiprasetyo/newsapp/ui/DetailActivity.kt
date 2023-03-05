package com.agilbudiprasetyo.newsapp.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.agilbudiprasetyo.newsapp.R
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.databinding.ActivityDetailBinding
import com.agilbudiprasetyo.newsapp.ui.viewmodel.DetailViewModel
import com.agilbudiprasetyo.newsapp.ui.viewmodel.DetailViewModelFactory
import com.agilbudiprasetyo.newsapp.utils.AppExecutors
import com.agilbudiprasetyo.newsapp.utils.DateFormatter
import com.agilbudiprasetyo.newsapp.utils.showToast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewFactory: DetailViewModelFactory
    private val viewModel: DetailViewModel by viewModels { viewFactory }
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        supportActionBar?.hide()
        viewFactory = DetailViewModelFactory.getInstance(this)

        updateDetail(this)
        binding.fabOpen.setOnClickListener {
            if (url.isEmpty()) {
                showToast(this, "No Url to Open", 0)
            }else{
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra(WebActivity.EXTRA_URL, url)
                startActivity(intent)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateDetail(detailActivity: DetailActivity) {
        val news: NewsEntity = intent.getParcelableExtra<NewsEntity>(EXTRA_NEWS) as NewsEntity
        val image: Any? = if(news.urlToImage.isNullOrEmpty()){
            ContextCompat.getDrawable(detailActivity, R.mipmap.ic_no_image)
        }else{
            news.urlToImage
        }
        val author = if (news.author.isNullOrEmpty()) "Anonymous" else news.author
        val description =  if (news.description.isNullOrEmpty()) "No Description" else news.description
        val toolbarTextColor = ContextCompat.getColor(this, R.color.purple_500)
        url = news.url

        with(binding) {
            when (news.isBookmarked) {
                true -> ivDetailCardBookmark.setImageDrawable(
                    ContextCompat.getDrawable(detailActivity, R.drawable.ic_baseline_bookmarked)
                )
                false -> ivDetailCardBookmark.setImageDrawable(
                    ContextCompat.getDrawable(detailActivity, R.drawable.ic_baseline_bookmark)
                )
            }
            ivDetailImage.setImage(detailActivity, image)
            tvDetailTitle.text = news.title
            tvDetailDate.text = DateFormatter.formatDate(news.publishedAt)
            tvDetailAuthor.text = author
            tvDetailDescription.text = description

            collapsingToolbar.title = author
            toolbar.setTitleTextColor(toolbarTextColor)
            ivDetailCardBookmark.setOnClickListener {
                setBookmark( news.isBookmarked,news, this@DetailActivity)
            }
        }
    }

    private fun setBookmark(
        bookmarkState: Boolean,
        news: NewsEntity,
        context: Context
    ){
        AppExecutors().diskIO.execute {
            when (bookmarkState) {
                true -> {
                    viewModel.deleteNews(news)
                    binding.ivDetailCardBookmark.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_baseline_bookmark)
                    )
                }
                false -> {
                    viewModel.saveNews(news)
                    binding.ivDetailCardBookmark.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_baseline_bookmarked)
                    )
                }
            }
        }
    }

    private fun View.setImage(detailActivity: DetailActivity, image: Any?) {
        Glide.with(detailActivity)
            .load(image)
            .apply(RequestOptions().centerCrop())
            .into(this as ImageView)
    }

    companion object{
        const val EXTRA_NEWS = "extra_news"
    }
}