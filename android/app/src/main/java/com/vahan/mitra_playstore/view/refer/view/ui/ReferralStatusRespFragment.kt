package com.vahan.mitra_playstore.view.refer.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentReferralStatusRespBinding
import com.vahan.mitra_playstore.utils.Constants
import java.util.*
import kotlin.concurrent.schedule


class ReferralStatusRespFragment : Fragment() {

    private var type: String? = null
    private var duplicateCount: Int? = null
    private var validCount: Int? = null
    lateinit var binding: FragmentReferralStatusRespBinding
    private var message: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                Log.d("backbutton", "handleOnBackPressed: ")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }


    @SuppressLint("StringFormatInvalid")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        arguments.apply {
            message = this?.getString(Constants.MESSAGE) ?: ""
            duplicateCount = this?.getInt(Constants.DUPLICATE_COUNT) ?: 0
            validCount = this?.getInt(Constants.VALID_COUNT) ?: 0
            type = this?.getString(Constants.TYPE) ?: ""
        }
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_referral_status_resp,
                container,
                false
            )
        initView()
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (type?.equals(Constants.LANDINGURL_REFERRAL) == true) {
            if (validCount!! == 0 && duplicateCount!! == 0) {
                binding.imgLogo.setBackgroundResource(R.drawable.mitra_icon)
                binding.txtTitle.text = resources.getString(R.string.friend)
                binding.tvCount.visibility = View.GONE
            } else if (validCount!! > 0 && duplicateCount!! > 0) {
                binding.imgLogo.setBackgroundResource(R.drawable.ic_paymenysuccess)
                if (validCount == 1)
                    binding.txtTitle.text =
                        context?.getString(R.string.i_sent) + validCount + getString(R.string.tv_friend)
                else
                    binding.txtTitle.text =
                        context?.getString(R.string.i_sent)+context?.getString(R.string.space) + validCount +context?.getString(R.string.space)+ getString(R.string.tv_friends)
                binding.tvCount.text =
                    resources.getString(R.string.friend) + ": $duplicateCount"
                binding.tvCount.visibility = View.VISIBLE
            } else if (validCount!! > 0 && duplicateCount!! == 0) {
                binding.imgLogo.setBackgroundResource(R.drawable.ic_paymenysuccess)
                if (validCount == 1)
                    binding.txtTitle.text =
                        context?.getString(R.string.i_sent) +context?.getString(R.string.space)+ validCount+context?.getString(R.string.space) + getString(R.string.tv_friend)
                else
                    binding.txtTitle.text =
                        context?.getString(R.string.i_sent)+context?.getString(R.string.space) + validCount +context?.getString(R.string.space)+ getString(R.string.tv_friends)
                binding.tvCount.visibility = View.GONE
            } else if (validCount!! == 0 && duplicateCount!! > 0) {
                binding.imgLogo.setBackgroundResource(R.drawable.mitra_icon)
                binding.txtTitle.text = resources.getString(R.string.friend)
                binding.tvCount.text =
                    resources.getString(R.string.friend) + " :$duplicateCount"
                binding.tvCount.visibility = View.VISIBLE
            }
        }
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.PAYMENT_SUCCESSFUL_FRAGMENT)
        Timer().schedule(2000) {
            lifecycleScope.launchWhenResumed {
                if (type.equals(Constants.LANDINGURL_REFERRAL)) {
                    findNavController().popBackStack(R.id.nav_referral_rsp_status_fragment, true)
                    findNavController().navigate(R.id.nav_referral_home_fragment)
                } else {
                    findNavController().navigate(R.id.nav_earn_fragment)
                }
            }
        }

    }


}