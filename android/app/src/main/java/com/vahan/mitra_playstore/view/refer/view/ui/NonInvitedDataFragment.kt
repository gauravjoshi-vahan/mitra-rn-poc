package com.vahan.mitra_playstore.view.refer.view.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentNonInvitedDataBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeRequestModel
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeRespModel
import com.vahan.mitra_playstore.view.refer.viewmodel.ReferralStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NonInvitedDataFragment : Fragment() {

    lateinit var binding: FragmentNonInvitedDataBinding
    private lateinit var viewReferralStatusModel: ReferralStatusViewModel
    private lateinit var dialogLoader: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_non_invited_data,
            container,
            false
        )
        initView()
        return binding.root
    }

    private fun initView() {
        viewReferralStatusModel = ViewModelProvider(this)[ReferralStatusViewModel::class.java]
        val homeReferralData = ReferralHomeRequestModel("3")
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.getHomeReferralHomeData(homeReferralData).collect {
                when (it) {
                    ApiState.Loading -> {
                        dialog.show()
                    }
                    is ApiState.Success -> {
                        dialog.dismiss()
                        setData(it.data)
                    }
                    is ApiState.Failure -> {
                        dialog.dismiss()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
        clickListener()

    }

    private fun clickListener() {
        binding.btInviteNow.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.nav_referral_home_fragment)
        }
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setData(data: ReferralHomeRespModel) {
        setUpEarnableAmount(data.earnableAmountForReferrals)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpEarnableAmount(earnableAmountForReferrals: List<ReferralHomeRespModel.EarnableAmountForReferral?>?) {
        binding.tvCompletingXReferrals.text =
            earnableAmountForReferrals?.get(0)?.numberOfReferrals.toString()
        binding.tvCompletingYReferrals.text =
            earnableAmountForReferrals?.get(1)?.numberOfReferrals.toString()
        binding.tvCompletingZReferrals.text =
            earnableAmountForReferrals?.get(2)?.numberOfReferrals.toString()
        binding.tvEarningXReferrals.text =
            "₹" + earnableAmountForReferrals?.get(0)?.earnableAmount.toString()
        binding.tvEarningYReferrals.text =
            "₹" + earnableAmountForReferrals?.get(1)?.earnableAmount.toString()
        binding.tvEarningZReferrals.text =
            "₹" + earnableAmountForReferrals?.get(2)?.earnableAmount.toString()
    }


    private fun createProgressDialog(): Dialog {
        dialogLoader = Dialog(requireContext())
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView =
            dialogLoader.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
        return dialogLoader
    }
}