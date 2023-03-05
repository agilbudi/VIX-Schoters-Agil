package com.agilbudiprasetyo.newsapp.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.agilbudiprasetyo.newsapp.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        val url = intent.getStringExtra(EXTRA_URL) as String
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl(url)
    }

    companion object{
        const val EXTRA_URL = "extra_url"
    }
}