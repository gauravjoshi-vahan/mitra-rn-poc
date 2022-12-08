package com.vahan.mitra_playstore.view.refer.view.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentReferralHomeBinding
import com.vahan.mitra_playstore.models.kotlin.CustomDataClass
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.SalaryViewActivity
import com.vahan.mitra_playstore.interfaces.ReferralMilestoneOnClick
import com.vahan.mitra_playstore.view.refer.view.adapter.ReferContactListNewAdapter
import com.vahan.mitra_playstore.view.refer.view.customdialog.RecentReferralDialog
import com.vahan.mitra_playstore.view.refer.models.*
import com.vahan.mitra_playstore.view.refer.viewmodel.ReferralStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.regex.Pattern


@AndroidEntryPoint
class ReferralHomeFragment : Fragment(), ReferralMilestoneOnClick {
    private lateinit var binding: FragmentReferralHomeBinding
    var isEnable1 = true
    var isEnable2 = true
    var isEnable3 = true
    val statusValue = 1
    private lateinit var viewReferralStatusModel: ReferralStatusViewModel
    private lateinit var dialogLoader: Dialog
    private var responseModel: ReferralHomeRespModel? = null
    private var responseMilestoneModel: ReferralMilestoneModel? = null
    private var responseModelNew: ReferralHomeNewRespModel? = null
    private lateinit var fa: FirebaseAnalytics
    private var customDataClassList: ArrayList<CustomDataClass> = ArrayList()
    var arrTripsDoneReferralStatus = ArrayList<ReferralStatusNewModel.BonusAmountObject>()
    var arrTripsDoneReferralHome = ArrayList<ReferralHomeNewRespModel.BonusAmountObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_referral_home,
            container,
            false
        )
        // Inflate the layout for this fragment
        initViews()
        return binding.root
    }

    private fun initViews() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        viewReferralStatusModel = ViewModelProvider(this)[ReferralStatusViewModel::class.java]
        disableInviteButton()
        getReferralNewHomeData("3")
        getReferralHomeData("3")
        clickListener()
        queryListener()

    }

    private fun getReferralHomeData(limit: String) {
        val homeReferralData = ReferralHomeRequestModel(limit)
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
                        Toast.makeText(requireContext(), "" + it.msg, Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }
                }
            }
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
                        setDataNew(it.data)
                     //   RecentReferralDialog()
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

    private fun setDataNew(it: ReferralHomeNewRespModel) {
        arrTripsDoneReferralHome.addAll(it.bonusAmountObject!!)
        responseModelNew = it
        setReferredNewHomeData(it)
        setUpNewRecyclerView(it)
        //setUpEarnableAmount(it.earnableAmountForReferrals)
    }

    private fun setData(it: ReferralHomeRespModel) {
        responseModel = it
        setReferredHomeData(it)
       // setUpNewRecyclerView(it)
        //setUpEarnableAmount(it.earnableAmountForReferrals)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpEarnableAmount(earnableAmountForReferrals: List<ReferralHomeRespModel.EarnableAmountForReferral?>?) {
//        bindin g.tvCompletingXReferrals.text =
//            earnableAmountForReferrals?.get(0)?.numberOfReferrals.toString()
//        binding.tvCompletingYReferrals.text =
//            earnableAmountForReferrals?.get(1)?.numberOfReferrals.toString()
//        binding.tvCompletingZReferrals.text =
//            earnableAmountForReferrals?.get(2)?.numberOfReferrals.toString()
//        binding.tvEarningXReferrals.text =
//            "₹" + earnableAmountForReferrals?.get(0)?.earnableAmount.toString()
//        binding.tvEarningYReferrals.text =
//            "₹" + earnableAmountForReferrals?.get(1)?.earnableAmount.toString()
//        binding.tvEarningZReferrals.text =
//            "₹" + earnableAmountForReferrals?.get(2)?.earnableAmount.toString()
    }


    @SuppressLint("SetTextI18n")
    private fun setReferredHomeData(data: ReferralHomeRespModel) {

        PrefrenceUtils.insertData(
            requireContext(),
            Constants.REFERRAL_AMOUNT,
            data.totalEarnableAmountPerSuccessfulReferral.toString()
        )
        if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
            binding.tvThird.text ="Get bonus for the trips completed by your friend in "+ data.referralValidForDays+" days"
        } else {
            binding.tvThird.text =
                "अपने मित्र द्वारा "+data.referralValidForDays + " दिनों में पूरी की गई यात्राओं के लिए बोनस प्राप्त करें"
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setReferredNewHomeData(data: ReferralHomeNewRespModel) {


//        PrefrenceUtils.insertData(
//            requireContext(),
//            Constants.REFERRAL_AMOUNT,
//            data.earnableAmountPerSuccessfulReferral.toString()
//        )
        if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
            binding.tvThird.text =
                "Get bonus for the trips\ncompleted by your friend\nin " + data.referralValidForDays + " days"
        } else {
            binding.tvThird.text =
               "अपने मित्र द्वारा "+ data.referralValidForDays +" दिनों में पूरी की गई यात्राओं के लिए बोनस प्राप्त करें"
        }
        binding.tvEarnAmt.text = resources.getString(R.string.rupee) + data.totalEarnableAmountPerSuccessfulReferral.toString()
        binding.tvRupeeEarnedAmt.text = resources.getString(R.string.rupee) + data.totalAmountEarnedForReferrals.toString()


        binding.tvCompletedAmt.text =   data.referralsCompleted.toString()+resources.getString(R.string._friends)
        binding.tvInProgressAmt.text =   data.referralsInProgress.toString()+resources.getString(R.string._friends)

        binding.tvR1C1.text =   data.bonusAmountObject?.get(0)?.numberOfTrips.toString()
        binding.tvR1C2.text =   data.bonusAmountObject?.get(1)?.numberOfTrips.toString()
        binding.tvR1C3.text =   data.bonusAmountObject?.get(2)?.numberOfTrips.toString()
        binding.tvR1C4.text =   data.bonusAmountObject?.get(3)?.numberOfTrips.toString()
        binding.tvR1C5.text =   data.bonusAmountObject?.get(4)?.numberOfTrips.toString()

        binding.tvR2C1.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(0)?.totalEarned.toString()
        binding.tvR2C2.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(1)?.totalEarned.toString()
        binding.tvR2C3.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(2)?.totalEarned.toString()
        binding.tvR2C4.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(3)?.totalEarned.toString()
        binding.tvR2C5.text =   resources.getString(R.string.rupee) + data.bonusAmountObject?.get(4)?.totalEarned.toString()


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

    private fun setUpRecyclerView(referralStatus: ReferralHomeRespModel) {
//        customDataClassList.clear()
//        val stgMap: HashMap<String, ReferralHomeRespModel.ReferralStagesAndStatusMapping.ReferralStage> =
//            HashMap()
//        for (referralStg in referralStatus.referralStagesAndStatusMapping?.referralStages!!) {
//            val stg: String = referralStg?.stage!!
//            stgMap[stg] = referralStg
//        }
//        for (items in referralStatus.referralStatus!!) {
//            val data = items?.latestReferralStage!!
//            val referenceData = stgMap[data]
//            customDataClassList.add(
//                CustomDataClass(
//                    items.earnedReferralAmount,
//                    referenceData?.info,
//                    referenceData?.label,
//                    items.latestReferralStage,
//                    items.name,
//                    items.referredPhoneNumber
//                )
//            )
//        }
//
//        if (customDataClassList.isNotEmpty()) {
//            binding.rl2.visibility = View.VISIBLE
//            binding.rvRecentReferrals.layoutManager = LinearLayoutManager(requireContext())
//            val adapter = ReferContactListAdapter(requireActivity(), customDataClassList, fa)
//            binding.rvRecentReferrals.adapter = adapter
//        } else {
//            binding.rl2.visibility = View.GONE
//        }

    }

    private fun setUpNewRecyclerView(referralStatus: ReferralHomeNewRespModel) {

        binding.rl2.visibility = View.VISIBLE
        binding.rvRecentReferrals.layoutManager = LinearLayoutManager(requireContext())
            val adapter =
                responseModelNew?.let {
                    context?.let { it1 -> ReferContactListNewAdapter(it1,this, it, fa) }
                }
            binding.rvRecentReferrals.adapter = adapter

        //customDataClassList.clear()
    //       val stgMap: HashMap<String, ReferralHomeNewRespModel.ReferralStagesAndStatusMapping.ReferralStage> =
//            HashMap()
//        for (referralStg in referralStatus.referralStagesAndStatusMapping?.referralStages!!) {
//            val stg: String = referralStg?.stage!!
//            stgMap[stg] = referralStg
//        }
//        for (items in referralStatus.referralStatus!!) {
//            val data = items?.latestReferralStage!!
//            val referenceData = stgMap[data]
//            customDataClassList.add(
//                CustomDataClass(
//                    items.earnedReferralAmount,
//                    referenceData?.info,
//                    referenceData?.label,
//                    items.latestReferralStage,
//                    items.name,
//                    items.referredPhoneNumber
//                )
//            )
//        }
//
//        if (customDataClassList.isNotEmpty()) {
//            binding.rl2.visibility = View.VISIBLE
//            binding.rvRecentReferrals.layoutManager = LinearLayoutManager(requireContext())
//            val adapter = ReferContactListAdapter(requireActivity(), customDataClassList, fa)
//            binding.rvRecentReferrals.adapter = adapter
//        } else {
//            binding.rl2.visibility = View.GONE
//        }

    }

    private fun queryListener() {
        binding.etEnterPhNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                val flag = isValidMobile(s.toString())
                if (flag) {
                    binding.btInvite.isEnabled = true
                    binding.btInvite.background.alpha = 255

                } else {
                    binding.btInvite.isEnabled = false
                    binding.btInvite.background.alpha = 64
                }
            }
        })
    }

    private fun disableInviteButton() {
        binding.btInvite.isEnabled = false
        binding.btInvite.background.alpha = 64
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun clickListener() {
        binding.tvKnowMore.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.nav_know_more_fragment)
        }
        binding.ivPhonebook.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.nav_invite_friends_fragment)
        }
        binding.btInviteFriendsToMitra.setOnClickListener {
            binding.svContainer.fullScroll(ScrollView.FOCUS_UP)
        }
        binding.tvWtIsRef.apply {
            this.setOnClickListener {
                if (isEnable1) {
                    binding.tvWtIsRefTxt.visibility = View.VISIBLE
                    isEnable1 = false
                    binding.tvWtIsRefTxt.text =
                        resources.getString(R.string.invite_to_mitra)
                    binding.tvWtIsRef.setTextColor(ContextCompat.getColor(context,R.color.default_200))
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_minus, 0, 0, 0)
                } else {
                    binding.tvWtIsRefTxt.visibility = View.GONE
                    isEnable1 = true
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
                    binding.tvWtIsRef.setTextColor(ContextCompat.getColor(context,R.color.dark_grey))
                }
            }
        }
        binding.tvHwItWork.apply {
            this.setOnClickListener {
                if (isEnable2) {
                    binding.tvHwItWork.setTextColor(ContextCompat.getColor(context,R.color.default_200))
                    binding.tvHwItWorkTxt.visibility = View.VISIBLE
                    isEnable2 = false
                    val dataOne = responseModel?.totalEarnableAmountPerSuccessfulReferral
                    val dataTwo = responseModel?.referralValidForDays

                    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                        binding.tvHwItWorkTxt.text ="You can refer your friends and make some extra money. Earn upto ₹"+dataOne+
                                " per referral depending on the number of rides your friend is taking in "+dataTwo+" days time."
                    } else {
                        binding.tvHwItWorkTxt.text ="आप अपने दोस्तों को रेफर कर सकते हैं और कुछ अतिरिक्त पैसे कमा सकते हैं। आपके दोस्त द्वारा "+dataTwo+
                                " दिनों में ली जा रही राइड्स की संख्या के आधार पर प्रति रेफरल ₹"+dataOne+" तक कमाएं।"
                    }

                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_minus, 0, 0, 0)
                } else {
                    binding.tvHwItWork.setTextColor(ContextCompat.getColor(context,R.color.dark_grey))
                    binding.tvHwItWorkTxt.visibility = View.GONE
                    isEnable2 = true
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
                }
            }
        }
        // this click action showing and moving to next screen i.e ReferralCodeFragment
        binding.tvShowCode.setOnClickListener {
          //  findNavController().navigate(R.id.nav_referral_code_fragment)
            ReferralCodeFragment().show(childFragmentManager,null)
        }
        binding.tvHwItCanBenefit.apply {
            this.setOnClickListener {
                if (isEnable3) {
                    binding.tvHwItCanBenefit.setTextColor(ContextCompat.getColor(context,R.color.default_200))
                    binding.tvHwItCanBenefitTxt.visibility = View.VISIBLE
                    isEnable3 = false
                    val data = responseModel?.totalEarnableAmountPerSuccessfulReferral


                    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                        binding.tvHwItCanBenefitTxt.text =
                            "Invite your friend to join mitra, and earn upto ₹$data per referral."
                    } else {
                        binding.tvHwItCanBenefitTxt.text =
                            "अपने मित्र को मित्रा में शामिल होने के लिए आमंत्रित करें, और प्रति रेफरल ₹$data तक अर्जित करें।"
                    }


                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_minus, 0, 0, 0)
                } else {
                    binding.tvHwItCanBenefit.setTextColor(ContextCompat.getColor(context,R.color.dark_grey))
                    binding.tvHwItCanBenefitTxt.visibility = View.GONE
                    isEnable3 = true
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
                }
            }
        }
        binding.btInvite.setOnClickListener {
            sentInvite()
        }
        binding.tvViewAll.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.nav_referral_status_fragment)
        }
        binding.ivBackBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.tvViewStatus.setOnClickListener {
            if (responseModel?.referralStatus?.isNotEmpty() == true) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.nav_referral_status_fragment)
            } else {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.nav_referral_non_status_fragment)
            }

        }
        binding.tvTc.setOnClickListener {
            val jsonObject = JSONObject(
                PrefrenceUtils.retriveData(
                    requireContext(),
                    Constants.TERM_AND_CONDITION_REMOTE_CONFIG
                )
            )
            jsonObject.getString("tc_url")

            requireContext().startActivity(
                Intent(requireContext(), SalaryViewActivity::class.java)
                    .putExtra("Type", getString(R.string.term_and_conditions))
                    .putExtra(
                        "link", jsonObject.getString("tc_url")
                    )
            )
        }
    }

    private fun sentInvite() {
        val inviteReferralData =
            ReferralInviteRequestModel(listOf(binding.etEnterPhNo.text.toString()))
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.sentInviteReferral(inviteReferralData).collect {
                when (it) {
                    ApiState.Loading -> {
                        dialog.show()
                    }
                    is ApiState.Success -> {
                        dialog.dismiss()
                        val bundle = Bundle()
                        bundle.putString(Constants.MESSAGE, it.data.validReferralsMessage)
                        bundle.putInt(Constants.DUPLICATE_COUNT, it.data.duplicateReferrals!!)
                        bundle.putInt(Constants.VALID_COUNT, it.data.validReferrals!!)
                        bundle.putString(Constants.TYPE, Constants.LANDINGURL_REFERRAL)
                        Navigation.findNavController(binding.root)
                            .popBackStack(R.id.nav_referral_home_fragment, true)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_referral_rsp_status_fragment, bundle)

                        setReferTypeInstrumentation()
                    }
                    is ApiState.Failure -> {
                        dialog.dismiss()
                        val errormsg = resources.getString(R.string.error_msg)
                        Toast.makeText(requireContext(), "" + errormsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setReferTypeInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        val data = HashMap<String, String>()
        val attribute = HashMap<String, Any>()
        bundle.putString(Constants.TYPE, Constants.PHONENUMBER)
        properties.addAttribute(Constants.TYPE, Constants.PHONENUMBER)
        data[Constants.TYPE] = Constants.PHONENUMBER
        attribute[Constants.TYPE] = Constants.PHONENUMBER
        fa.logEvent(Constants.REFER_TYPE, bundle)
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.REFER_TYPE, properties)
        UXCam.logEvent(Constants.REFER_TYPE, data)
        Freshchat.trackEvent(requireContext(), Constants.REFER_TYPE, attribute)
    }

    private fun isValidMobile(phone: String): Boolean {
        val regexStr = resources.getString(R.string.regex)
        if (Pattern.matches(regexStr, phone)) {
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        setInstrumentation()
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        val data = HashMap<String, String>()
        val attribute = HashMap<String, Any>()
        fa.logEvent("referral_home_viewed", bundle)
        MoEHelper.getInstance(requireActivity())
            .trackEvent(Constants.REFERRAL_HOME_VIEWED, properties)
        UXCam.logEvent(Constants.REFERRAL_HOME_VIEWED, data)
        Freshchat.trackEvent(requireContext(), Constants.REFERRAL_HOME_VIEWED, attribute)
    }

    override fun onClick(phoneNo: String,name: String) {
        getReferralMilestoneData(phoneNo,name)
      // getReferralMilestoneData("9999999999")

    }
}


