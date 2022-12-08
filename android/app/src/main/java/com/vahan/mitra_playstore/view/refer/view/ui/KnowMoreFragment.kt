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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentKnowMoreBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeNewRespModel
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeRequestModel
import com.vahan.mitra_playstore.view.refer.viewmodel.ReferralStatusViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class KnowMoreFragment : Fragment() {
    private lateinit var binding: FragmentKnowMoreBinding
    private lateinit var viewReferralStatusModel: ReferralStatusViewModel
    private var responseModelNew: ReferralHomeNewRespModel? = null
    private lateinit var dialogLoader: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_know_more,
            container,
            false
        )

        initViews()
        return binding.root
    }

    private fun initViews() {
        viewReferralStatusModel = ViewModelProvider(this)[ReferralStatusViewModel::class.java]
        getReferralNewHomeData("3")
        clickListener()
    }

    private fun clickListener() {
        binding.ivCross.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.nav_referral_home_fragment)
        }
    }
    private fun getReferralNewHomeData(limit: String) {
        val homeReferralData = ReferralHomeRequestModel(limit)
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.getHomeReferralNewHomeData(homeReferralData).collect {
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
                        Toast.makeText(requireContext(), "" + it.msg, Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
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
    private fun setData(it: ReferralHomeNewRespModel) {
        responseModelNew = it
        setReferredNewHomeData(it)
    }
    @SuppressLint("SetTextI18n")
    private fun setReferredNewHomeData(data: ReferralHomeNewRespModel) {

        binding.tvR1C1.text =   data.bonusAmountObject?.get(0)?.numberOfTrips.toString()+" Trips"
        binding.tvR2C1.text =   data.bonusAmountObject?.get(1)?.numberOfTrips.toString()+" Trips"
        binding.tvR3C1.text =   data.bonusAmountObject?.get(2)?.numberOfTrips.toString()+" Trips"
        binding.tvR4C1.text =   data.bonusAmountObject?.get(3)?.numberOfTrips.toString()+" Trips"
        binding.tvR5C1.text =   data.bonusAmountObject?.get(4)?.numberOfTrips.toString()+" Trips"

        binding.tvR1C2.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(0)?.bonusEarned.toString()
        binding.tvR2C2.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(1)?.bonusEarned.toString()
        binding.tvR3C2.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(2)?.bonusEarned.toString()
        binding.tvR4C2.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(3)?.bonusEarned.toString()
        binding.tvR5C2.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(4)?.bonusEarned.toString()


        binding.tvR1C3.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(0)?.totalEarned.toString()
        binding.tvR2C3.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(1)?.totalEarned.toString()
        binding.tvR3C3.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(2)?.totalEarned.toString()
        binding.tvR4C3.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(3)?.totalEarned.toString()
        binding.tvR5C3.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(4)?.totalEarned.toString()

        if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                .equals("en", ignoreCase = true)){
            binding.tvDesc1.text = "Earn money upto ₹" + data.totalEarnableAmountPerSuccessfulReferral.toString() + " for every milestone that your friend will be completing in the first 15 days"
        } else {
            binding.tvDesc1.text = "आपके मित्र द्वारा पहले 15 दिनों में पूरे किए जाने वाले प्रत्येक उपलब्धि के लिए ₹"+ data.totalEarnableAmountPerSuccessfulReferral.toString() +" तक कमाएं"
        }



    }
}


