package com.vahan.mitra_playstore.view.helpandsupport

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentHelpAndSupportBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils


class HelpAndSupportFragment : Fragment() {

    private lateinit var binding: FragmentHelpAndSupportBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help_and_support, container, false)
        initView()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("HelpAndSupport Fragment")
        if(PrefrenceUtils.retriveLangData(activity,Constants.LANGUAGE).equals("en")){
            binding.callUsOnTv.text = "Call us on " + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG) + " any time between 10 AM - 6 PM, Monday to Friday."
            binding.callBtnTv.text = "Call Mitra at "   + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG)
        }else if(PrefrenceUtils.retriveLangData(activity,Constants.LANGUAGE).equals("hi")){
            binding.callUsOnTv.text = "हमें कॉल करें " + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG) + " सोमवार से शुक्रवार सुबह 10 बजे से शाम 6 बजे के बीच कभी भी।"
            binding.callBtnTv.text = "मित्रा को कॉल करें " + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG)
        }else{
            binding.callUsOnTv.text = "సోమవారం నుండి శుక్రవారం వరకు 10 AM - 6 PM మధ్య ఎప్పుడైనా"  + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG) + "లో మాకు కాల్ చేయండి."
            binding.callBtnTv.text = "మిత్రకు కాల్ చేయండి " + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG)
        }
        binding.ivBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.callBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + PrefrenceUtils.retriveData(requireContext(), Constants.SUPPORT_NUMBER_REMOTE_CONFIG))
            startActivity(intent)
        }
    }


}