package com.agilbudiprasetyo.newsapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.agilbudiprasetyo.newsapp.R
import com.agilbudiprasetyo.newsapp.databinding.FragmentAboutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            Glide.with(view.context)
                .load(ContextCompat.getDrawable(view.context, R.mipmap.ic_photo))
                .apply(RequestOptions().centerCrop())
                .into(binding.ivAboutImage)
            binding.tvAboutName.text = "Agil Budi Prasetyo"
            binding.tvAboutGithub.text = "agilbudi"
    }

}