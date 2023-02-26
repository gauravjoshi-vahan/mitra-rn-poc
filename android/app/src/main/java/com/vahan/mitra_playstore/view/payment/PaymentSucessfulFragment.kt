package com.vahan.mitra_playstore.view.payment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentPaymentSucessfulBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import java.util.*
import kotlin.concurrent.schedule

class PaymentSucessfulFragment : Fragment() {

    lateinit var binding: FragmentPaymentSucessfulBinding
    private var message: String? = null
    private var type: String? = null
    private var orderReachToNextLevel: Int = 0
    private var orderReachToNextLevel1: Boolean = false
    private var daysReachToNextLevel: Boolean = false
    private var tripsReachToNextLevel: Int = 0
    private var percentage: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        arguments.apply {
            message = this?.getString(Constants.MESSAGE) ?: ""
            type = this?.getString("type") ?: ""
            orderReachToNextLevel = this?.getInt("orderReachToNextLevel") ?: 0
            tripsReachToNextLevel = this?.getInt("tripsReachToNextLevel") ?: 0
            orderReachToNextLevel1 = this?.getBoolean("orderReachToNextLevel1") ?: false
            daysReachToNextLevel = this?.getBoolean("daysReachToNextLevel") ?: false
            percentage = this?.getInt("percentage") ?: 0
        }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_payment_sucessful, container, false)
        @Suppress("DEPRECATION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        initView()
        return binding.root
    }

    private fun initView() {
        binding.txtTitle.text = message
        if (type.equals("cashout")) {
            binding.cashoutExpFlow.visibility = View.VISIBLE
            showCashoutExpInfo()
        } else {
            binding.cashoutExpFlow.visibility = View.GONE
        }
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.PAYMENT_SUCCESSFUL_FRAGMENT)
        Timer().schedule(4000) {
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(R.id.nav_home_fragment)
            }
        }

    }

    private fun showCashoutExpInfo() {
        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE).equals("en")) {
            if (orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.VISIBLE
                binding.textOnePlus.visibility = View.VISIBLE
                binding.tvActiveDays.visibility = View.VISIBLE
                binding.tvTextOne.text = "Complete $orderReachToNextLevel more Trips"
                binding.tvActiveDays.text = "Stay Active for $tripsReachToNextLevel more days"
                binding.tvPercentage.text = "to unlock $percentage% percent"
            } else if (orderReachToNextLevel1 && !daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.VISIBLE
                binding.tvActiveDays.visibility = View.GONE
                binding.textOnePlus.visibility = View.GONE
                binding.tvActiveDays.text = "Stay Active for $tripsReachToNextLevel more days"
                binding.tvPercentage.text = "to unlock $percentage% percent"
            } else if (!orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.GONE
                binding.textOnePlus.visibility = View.GONE
                binding.tvActiveDays.visibility = View.VISIBLE
                binding.tvTextOne.text = "Complete $orderReachToNextLevel more Trips"
                binding.tvPercentage.text = "to unlock $percentage% percent"
            } else {
                binding.cashoutExpFlow.visibility = View.GONE
            }
        } else if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE).equals("hi")) {
            if (orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.VISIBLE
                binding.textOnePlus.visibility = View.VISIBLE
                binding.tvActiveDays.visibility = View.VISIBLE
                binding.tvTextOne.text = "$orderReachToNextLevel और यात्राएं पूरी करें"
                binding.tvActiveDays.text = "$tripsReachToNextLevel और दिनों तक सक्रिय रहें"
                if (percentage != 0)
                    binding.tvPercentage.text = "$percentage% प्रतिशत अनलॉक करने के लिए"
            } else if (orderReachToNextLevel1 && !daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.VISIBLE
                binding.tvActiveDays.visibility = View.GONE
                binding.textOnePlus.visibility = View.GONE
                binding.tvTextOne.text = "$orderReachToNextLevel और यात्राएं पूरी करें"
                if (percentage != 0)
                    binding.tvPercentage.text = "$percentage% प्रतिशत अनलॉक करने के लिए"
            } else if (!orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.GONE
                binding.textOnePlus.visibility = View.GONE
                binding.tvActiveDays.visibility = View.VISIBLE
                binding.tvActiveDays.text = "$tripsReachToNextLevel और दिनों तक सक्रिय रहें"
                if (percentage != 0)
                    binding.tvPercentage.text = "$percentage% प्रतिशत अनलॉक करने के लिए"
            } else {
                binding.cashoutExpFlow.visibility = View.GONE
            }
        }else {
            if (orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.VISIBLE
                binding.textOnePlus.visibility = View.VISIBLE
                binding.tvActiveDays.visibility = View.VISIBLE
                binding.tvTextOne.text = "మరో $orderReachToNextLevel ట్రిప్ లను పూర్తి చేయండి"
                binding.tvActiveDays.text = "మరో $tripsReachToNextLevel రోజులు యాక్టివ్\u200Cగా ఉండండి"
                if (percentage != 0)
                    binding.tvPercentage.text = "$percentage% प्रतिशत अनलॉक करने के लिए"
            } else if (orderReachToNextLevel1 && !daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.VISIBLE
                binding.tvActiveDays.visibility = View.GONE
                binding.textOnePlus.visibility = View.GONE
                binding.tvTextOne.text = "$orderReachToNextLevel और यात्राएं पूरी करें"
                if (percentage != 0)
                    binding.tvPercentage.text = "మరో $tripsReachToNextLevel రోజులు యాక్టివ్\u200Cగా ఉండండి"
            } else if (!orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = View.GONE
                binding.textOnePlus.visibility = View.GONE
                binding.tvActiveDays.visibility = View.VISIBLE
                binding.tvActiveDays.text = "మరో $tripsReachToNextLevel రోజులు యాక్టివ్\u200Cగా ఉండండి"
                if (percentage != 0)
                    binding.tvPercentage.text = "క్యాష్అవుట్ $percentage% అన్\u200Cలాక్ చేయండి"
            } else {
                binding.cashoutExpFlow.visibility = View.GONE
            }
        }

    }
}