package com.vahan.mitra_playstore.view.ratecard.ui

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentRateCardNewBinding
import com.vahan.mitra_playstore.databinding.FragmentTermsAndCondBinding
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter


class TermsAndCondFragment : Fragment() {
    private lateinit var binding: FragmentTermsAndCondBinding
    var label: String = ""
    var description: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_terms_and_cond,
            container,
            false
        )

        initView()
        return binding.root
    }

    private fun initView() {
        label = requireArguments().getString("label")!!
        description = requireArguments().getString("description")!!
        Log.d("qwe", "initView: $description")
        binding.tvTAndC.text = Html.fromHtml(label)
        binding.tvComment1.text = description
        binding.ivBackButton.setOnClickListener { requireActivity().onBackPressed() }

    }
}