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
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentReferralStatusBinding
import com.vahan.mitra_playstore.models.kotlin.CustomDataClass
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.interfaces.ReferralMilestoneOnClick
import com.vahan.mitra_playstore.utils.startBlitzSurvey
import com.vahan.mitra_playstore.view.refer.view.adapter.ReferContactListNewAdapter
import com.vahan.mitra_playstore.view.refer.view.customdialog.RecentReferralDialog
import com.vahan.mitra_playstore.view.refer.models.*
import com.vahan.mitra_playstore.view.refer.viewmodel.ReferralStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap


@AndroidEntryPoint
class ReferralStatusFragment : Fragment(), ReferralMilestoneOnClick {
    lateinit var binding: FragmentReferralStatusBinding
    private var listOf = ArrayList<ReferStatusModel>()
    private lateinit var viewReferralStatusModel : ReferralStatusViewModel
    private lateinit var dialogLoader: Dialog
    private lateinit var fa: FirebaseAnalytics
    private var customDataClass : ArrayList<CustomDataClass> = ArrayList()
    private var responseModelNew: ReferralStatusNewModel? = null
    private var responseModel: ReferralHomeNewRespModel? = null
    var arrTripsDoneReferralStatus = ArrayList<ReferralStatusNewModel.BonusAmountObject>()
    var arrTripsDoneReferralHome = ArrayList<ReferralHomeNewRespModel.BonusAmountObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_status, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        viewReferralStatusModel = ViewModelProvider(this)[ReferralStatusViewModel::class.java]
        getReferralStatusData("all")
        getReferralStatusNewData("all")
        clickListener()
    }

    private fun clickListener() {
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun  getReferralStatusData(limit: String)
    {
        val homeReferralData = ReferralHomeRequestModel(limit)
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.getReferralStatusNewData(homeReferralData).collect{
                when(it){
                    ApiState.Loading->{
                        dialog.show()
                    }
                    is ApiState.Success ->{
                        dialog.dismiss()
                        setData(it.data)
                    }
                    is ApiState.Failure ->{
                        dialog.dismiss()
                        Toast.makeText(requireContext(), ""+it.msg, Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
    }

    private fun  getReferralStatusNewData(limit: String)
    {
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
                        setDataNew(it.data)

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

    private fun setData(data: ReferralStatusNewModel) {
        arrTripsDoneReferralStatus.addAll(data.bonusAmountObject!!)
        responseModelNew = data
        setStatusScreenInfo(data)
        //setUpEarnableAmount(data.earnableAmountForReferrals)
    }
 private fun setDataNew(data: ReferralHomeNewRespModel) {
        responseModel = data
        setRecylerview(data)
        //setUpEarnableAmount(data.earnableAmountForReferrals)
    }

    @SuppressLint("SetTextI18n")
    private fun setStatusScreenInfo(data: ReferralStatusNewModel) {
        binding.tvRupeeEarnedAmt.text = resources.getString(R.string.rupee) +data.totalAmountEarnedForReferrals.toString()
        binding.tvCompletedAmt.text = data.referralsCompleted.toString()+resources.getString(R.string._friends)
        binding.tvInProgressAmt.text = data.referralsInProgress.toString()+resources.getString(R.string._friends)

        //need to confirm this
        PrefrenceUtils.insertData(requireContext(), Constants.REFERRAL_AMOUNT,data.totalAmountEarnedForReferrals.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun setUpEarnableAmount(earnableAmountForReferrals: List<ReferralHomeRespModel.EarnableAmountForReferral?>?) {
//        binding.tvCompletingXReferrals.text = earnableAmountForReferrals?.get(0)?.numberOfReferrals.toString()
//        binding.tvCompletingYReferrals.text = earnableAmountForReferrals?.get(1)?.numberOfReferrals.toString()
//        binding.tvCompletingZReferrals.text = earnableAmountForReferrals?.get(2)?.numberOfReferrals.toString()
//        binding.tvEarningXReferrals.text = resources.getString(R.string.rupee)+earnableAmountForReferrals?.get(0)?.earnableAmount.toString()
//        binding.tvEarningYReferrals.text = resources.getString(R.string.rupee)+earnableAmountForReferrals?.get(1)?.earnableAmount.toString()
//        binding.tvEarningZReferrals.text = resources.getString(R.string.rupee)+earnableAmountForReferrals?.get(2)?.earnableAmount.toString()
    }

    private fun setRecylerview(referralStatus: ReferralHomeNewRespModel) {

        binding.rvReferralsContact.layoutManager = LinearLayoutManager(requireContext())
        val adapter =
            responseModel?.let {
                context?.let { it1 -> ReferContactListNewAdapter(it1,this, it, fa) }
            }
        binding.rvReferralsContact.adapter = adapter

//        customDataClass.clear()
//        for(items in referralStatus.referralStatus!!){
//            for(item in referralStatus.referralStagesAndStatusMapping?.referralStages!!){
//                if(items?.latestReferralStage == item?.stage){
//                    customDataClass.add(
//                        CustomDataClass(
//                            items?.earnedReferralAmount,
//                            item?.info,
//                            item?.label,
//                            items?.latestReferralStage,
//                            items?.name,
//                            items?.referredPhoneNumber
//                        )
//                    )
//                }
//            }
//        }
//        binding.rvReferralsContact.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvReferralsContact.adapter = ReferContactListAdapter(
//            requireActivity(),
//            customDataClass,
//            fa
//        )
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

    @SuppressLint("InvalidAnalyticsName")
    private fun setInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        val data = HashMap<String, String>()
        val attribute = HashMap<String, Any>()
        fa.logEvent(Constants.REFERRAL_STATUS_VIEWED, bundle)
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.REFERRAL_STATUS_VIEWED, properties)
        UXCam.logEvent(Constants.REFERRAL_STATUS_VIEWED, data)
        Freshchat.trackEvent(requireContext(), Constants.REFERRAL_STATUS_VIEWED, attribute)

        requireContext().startBlitzSurvey(
            requireContext(),
            Constants.REFERRAL_STATUS_VIEWED
        )
    }

    override fun onResume() {
        super.onResume()
        setInstrumentation()
    }
    private fun getReferralMilestoneData(phNo: String,name:String) {
        val homeReferralData = ReferralMilestoneRequestModel(phNo)
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.getReferralMilestoneData(homeReferralData).collect {
                when (it) {
                    ApiState.Loading -> {
                        dialog.show()
                    }
                    is ApiState.Success -> {
                        dialog.dismiss()
                        RecentReferralDialog(
                            name,
                            requireContext(),
                            it.data,
                            arrTripsDoneReferralStatus,
                            arrTripsDoneReferralHome
                        ).show()

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

    override fun onClick(phoneNo: String, name: String) {
        getReferralMilestoneData(phoneNo,name)
    }

}