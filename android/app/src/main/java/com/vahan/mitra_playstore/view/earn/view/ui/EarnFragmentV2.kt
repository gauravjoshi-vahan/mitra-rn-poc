package com.vahan.mitra_playstore.view.earn.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.*
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.activityViewModel.ActivityViewModel
import com.vahan.mitra_playstore.databinding.FragmentEarnV2Binding
import com.vahan.mitra_playstore.interfaces.CoachmarkListener
import com.vahan.mitra_playstore.models.kotlin.BannerListDataModelNew
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.models.kotlin.Transaction
import com.vahan.mitra_playstore.models.kotlin.TransactionDetailModel
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.BaseApplication
import com.vahan.mitra_playstore.view.MainActivity
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet
import com.vahan.mitra_playstore.view.earn.view.adapter.DynamicBannerAdapter
import com.vahan.mitra_playstore.view.earn.view.adapter.MilestoneTrackerAdapter
import com.vahan.mitra_playstore.view.earn.view.adapter.SliderAdapter
import com.vahan.mitra_playstore.view.earn.view.adapter.TransactionDetailAdapter
import com.vahan.mitra_playstore.view.earn.model.TextModel
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import com.vahan.mitra_playstore.view.errorview.ErrorFragment
import com.vahan.mitra_playstore.view.ratecard.ui.RateCardDetailViewOnClick
import com.vahan.mitra_playstore.workmanager.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class EarnFragmentV2 : Fragment(), RateCardDetailViewOnClick, CoachmarkListener {
    private var eligiblityCheck: Boolean = false
    private var cashoutFixedFeeLabel: String? = null
    private lateinit var binding: FragmentEarnV2Binding
    var location = 0
    private lateinit var fa: FirebaseAnalytics
    private var notificationAlert: MoengageNotification? = null
    private var isAadhaarDocumentUpload = false
    private var isDLDocumentUpload = false
    private var isPanCardDocumentUpload = false
    private var isBankDetailsUpload = false
    private var isUploadFinalStatus = false
    private var token: String? = null
    val handler = Handler(Looper.getMainLooper())
    private var dataModel: EarnDataModel? = null
    private val transactionDetailsModels = ArrayList<TransactionDetailModel.Transaction>()
    private var isTransc = false
    private lateinit var dialogLoader: Dialog
    private lateinit var viewEarnViewModel: EarnViewModel
    var transactionDetailAdapter: TransactionDetailAdapter? = null
    private val fragmentArrayList = ArrayList<BannerListDataModelNew.DynamicBanner>()
    private lateinit var ivForwardHistoryFinger: ImageView
    private var milestoneModel: EarnDataModel.IncentiveStructures? = null
    private var slotAvailableCompanyA = ""
    private var slotAvailableCompanyB = ""
    private var sourceStringOne = ""
    private var sourceStringTwo = ""
    private var listOfSlotOne = ArrayList<String>()
    private var showLearnMoreTxt = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_earn_v2,
            container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        viewEarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]
        binding.cashLayout.visibility = View.VISIBLE
        showLearnMoreNudge(true);
        getRemoteConfigDataForUpdate()
        setupScreenName()
        alertNotification()
        getTransactionDetails(1, 5, "")
        clickListener()
    }

    private fun getDeviceInfoS() {
        viewEarnViewModel.getDeviceInfoS()
    }

    private fun getRemoteConfigDataForUpdate() {
        viewEarnViewModel.getRemoteConfigDataForUpdate()
        if (BuildConfig.VERSION_NAME >= viewEarnViewModel.freshchatmodel.value?.version.toString()) {
            if (viewEarnViewModel.freshchatmodel.value?.enabled == 1) {
                binding.chat.visibility = VISIBLE
            } else {
                binding.chat.visibility = INVISIBLE
            }
        } else {
            binding.chat.visibility = INVISIBLE
        }
    }

    private fun setupScreenName() {
        requireContext().setupScreen(false, "Earn Fragment")
    }

    private fun showLoading() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.rootEarning.visibility = View.GONE
        binding.shimmerViewContainer.startShimmerAnimation()
    }

    private fun alertNotification() {
        notificationAlert = MoengageNotification()
        val intentFilter = IntentFilter()
        intentFilter.addAction(requireActivity().packageName)
        intentFilter.addAction(Freshchat.FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED)
        requireActivity().registerReceiver(notificationAlert, intentFilter)
    }

    private fun clickListener() {
//        binding.cashoutTxt.setOnClickListener {
//            if (eligiblityCheck) {
//                setUpInstrumentation()
//                dataModel!!.cashoutDetails!!.amountEligibleLabel?.let { it1 ->
//                    BottomSheetCashOutPurpose(
//                        requireActivity(),
//                        dataModel?.cashoutAdditionalData?.userLevel,
//                        it1,
//                        this@EarnFragmentV2,
//                        dataModel!!.cashoutDetails?.amountEligible!!,
//                        dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0,
//                        dataModel!!.cashoutDetails?.isCashoutFeeEnabled!!,
//                        dataModel!!.cashoutDetails?.cashoutFeePercentage!!,
//                        dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel,
//                        dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel,
//                        dataModel?.cashoutAdditionalData?.cashoutEligibilityStatus,
//                        dataModel?.cashoutAdditionalData?.orderReachToNextLevel,
//                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel,
//                        dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage
//                    ).show()
//                }
//            }
//
//        }
        binding.chat.setOnClickListener {
            //Freshchat.showConversations(requireContext())
            val tags: MutableList<String> = ArrayList()
            tags.add("newFaq")
            val faqOptions = FaqOptions()
                .showFaqCategoriesAsGrid(false)
                .showContactUsOnAppBar(false)
                .showContactUsOnFaqScreens(true)
                .showContactUsOnFaqNotHelpful(true)
                .filterContactUsByTags(tags, "Test 2") //tags, filtered screen title
            Freshchat.showFAQs(requireActivity(), faqOptions)
        }
        binding.notification.setOnClickListener {
            binding.ivNotificationIconLight.visibility = View.VISIBLE
            binding.notificationContainerLight.visibility = View.GONE
            Navigation.findNavController(binding.root).navigate(R.id.nav_fragment_notification)
        }
        binding.viewAllContainerClick.setOnClickListener {
            Navigation.findNavController(
                binding.root
            ).navigate(R.id.action_nav_earn_fragment_to_nav_fragment_history)
        }
        binding.profile.setOnClickListener {
            (requireActivity() as MainActivity).setFragment(
                "profile_fragment",
                3
            )
        }
        binding.viewAllContainer2.setOnClickListener {
//            Navigation.findNavController(
//                binding.root
//            ).navigate(R.id.weekly_earnings_fragment)
            findNavController().navigate(R.id.weekly_earnings_fragment)
        }
        binding.rlSlotContainer.setOnClickListener {
            if (showLearnMoreTxt) {
                val items = HashMap<String, Any>()
                val properties = Properties()
                captureAllEvents(
                    requireContext(),
                    Constants.LEARN_MORE_EVENT,
                    items,
                    properties
                )
                requireContext().startBlitzSurvey(requireContext(),Constants.LEARN_MORE_EVENT)
            } else {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_nav_earn_fragment_to_nav_cross_util_fragment)
            }
        }
    }

    private fun setUpInstrumentation() {
        requireContext().captureEvents(requireContext(),
            Constants.CASHOUT_DISABLED_TAPPED,
            HashMap())
    }

    private fun updateUI() {
        binding.cashoutCardRoot.visibility = View.VISIBLE
        binding.cashLayoutExp.visibility = View.GONE
        when (dataModel!!.user!!.cashoutEligibilityStatus) {
            "EC" -> {
                captureAllEvents(
                    requireContext(),
                    Constants.CASHOUT_DISABLED_VIEWED,
                    HashMap(),
                    Properties()
                )
                binding.cashoutTxt.visibility = View.VISIBLE
                binding.cashLayout.visibility = View.GONE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                )
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eC?.msg
                else binding.cashOutConfigTxt.text =
                    viewEarnViewModel.remoteCashOutData.value?.eC?.hiMsg
            }
            "E" -> {
                if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                )
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.e?.msg
                else binding.cashOutConfigTxt.text =
                    viewEarnViewModel.remoteCashOutData.value?.e?.hiMsg
                binding.cashoutTxt.visibility = View.GONE
                if (dataModel!!.cashoutDetails?.enabled!! &&
                    dataModel!!.cashoutDetails?.amountEligible!! >= dataModel!!.cashoutDetails?.minAmountEligible!!
                ) {
                    val properties = Properties()
                    val attribute: HashMap<String, Any> = HashMap()
                    properties.addAttribute(Constants.ELIGIBLE_AMOUNT,
                        dataModel!!.cashoutDetails?.amountEligible ?: 0.0)
                    properties.addAttribute(Constants.CASHED_FEE_PERCENT,
                        dataModel!!.cashoutDetails?.cashoutFeePercentage ?: 0.0)
                    properties.addAttribute(Constants.CASHED_FIXED_FEE,
                        dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0)
                    // CHECK VALUE IS NOT EMPTY...
                    attribute[Constants.ELIGIBLE_AMOUNT] =
                        dataModel!!.cashoutDetails?.amountEligible ?: 0.0
                    attribute[Constants.CASHED_FEE_PERCENT] =
                        dataModel!!.cashoutDetails?.cashoutFeePercentage ?: 0.0
                    attribute[Constants.CASHED_FIXED_FEE] =
                        dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0
                    captureAllEvents(
                        requireContext(),
                        Constants.CASHOUT_ELIGIBLE_VIEWED,
                        attribute,
                        properties
                    )
                    binding.cashLayout.visibility = View.VISIBLE
//                    binding.cashLayout.setOnClickListener {
//                        setInstrumentationOnCLick(dataModel!!, "regular_flow")
//                        dataModel!!.cashoutDetails!!.amountEligibleLabel?.let { it1 ->
//                            BottomSheetV2(
//                                requireActivity(),
//                                it1,
//                                this@EarnFragmentV2,
//                                dataModel!!.cashoutDetails?.amountEligible!!,
//                                dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0,
//                                dataModel!!.cashoutDetails?.isCashoutFeeEnabled!!,
//                                dataModel!!.cashoutDetails?.cashoutFeePercentage!!,
//                                0,
//                                0,
//                                orderReachToNextLevel1 = false,
//                                daysReachToNextLevel = false,
//                                0,
//                            ).show()
//                        }
//                    }
                } else if (!dataModel!!.cashoutDetails?.enabled!! &&
                    dataModel!!.cashoutDetails?.amountEligible!! >=
                    dataModel!!.cashoutDetails?.minAmountEligible!!
                ) {
                    binding.imgGrey.visibility = View.VISIBLE
                    binding.cashLayout.visibility = View.VISIBLE
                    binding.cashLayout.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.you_have_already_availed_the_service),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (dataModel!!.cashoutDetails?.enabled!! &&
                    dataModel!!.cashoutDetails?.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.amountEligible != 0.0
                ) {
                    requireContext().captureEvents(requireContext(),
                        Constants.CASHOUT_INELIGIBLE_VIEWED,
                        HashMap())
                    binding.cashLayout.setOnClickListener {
                        val properties = Properties()
                        val attribute = HashMap<String, Any>()
                        captureAllEvents(
                            requireContext(),
                            Constants.CASHOUT_INELIGIBLE_TAPPED,
                            attribute,
                            properties
                        )
                        if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                                .equals("en", ignoreCase = true)
                        ) {
                            Toast.makeText(requireContext(),
                                "Minimum amount for cashout is Rs. "
                                        + dataModel!!.cashoutDetails!!.minAmountEligible,
                                Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(),
                                "कैशआउट के लिए न्यूनतम राशि रु. "
                                        + dataModel!!.cashoutDetails!!.minAmountEligible,
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.cashLayout.visibility = View.VISIBLE
                    binding.imgGrey.visibility = View.VISIBLE
                } else if (dataModel!!.cashoutDetails!!.enabled!! &&
                    dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.amountEligible == 0.0
                ) {
                    binding.cashLayout.visibility = View.GONE
                } else if (!dataModel!!.cashoutDetails!!.enabled!! &&
                    dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.amountEligible == 0.0
                ) {
                    binding.cashLayout.visibility = View.GONE
                } else if (dataModel!!.cashoutDetails!!.enabled!! &&
                    dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.minAmountEligible!! < 0.0
                ) {
                    binding.cashLayout.visibility = View.GONE
                }
            }
            "EW" -> {
                val properties = Properties()
                val attribute = HashMap<String, Any>()
                captureAllEvents(
                    requireContext(),
                    Constants.CASHOUT_DISABLED_VIEWED,
                    attribute,
                    properties
                )
                binding.cashoutTxt.visibility = View.VISIBLE
                binding.cashLayout.visibility = View.GONE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eW?.msg
                } else {
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eW?.hiMsg
                }
            }
            "NE" -> {
                val properties = Properties()
                val attribute = HashMap<String, Any>()
                captureAllEvents(
                    requireContext(),
                    Constants.CASHOUT_DISABLED_VIEWED,
                    attribute,
                    properties
                )
                binding.cashoutTxt.visibility = View.VISIBLE
                binding.cashLayout.visibility = View.GONE
                if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.nE?.msg
                } else {
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.nE?.hiMsg
                }
            }
        }
        if (dataModel!!.cashoutDetails!!.amountEligibleLabel!!.contains(".")) {
            binding.setPrice.text = dataModel!!.cashoutDetails!!.amountEligibleLabel!!.substring(0,
                dataModel!!.cashoutDetails!!.amountEligibleLabel!!.indexOf("."))
        } else {
            binding.setPrice.text = dataModel!!.cashoutDetails!!.amountEligibleLabel
        }
    }

    private fun internalShowLoading() {
        binding.shimmerRvContainer.visibility = View.VISIBLE
        binding.insideViewpagerShimmer.visibility = View.VISIBLE
        binding.insideViewpagerShimmer.visibility = View.VISIBLE
        binding.shimmerViewValue.visibility = View.VISIBLE
        // binding.rvEarning.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.SliderDots.visibility = View.GONE
        binding.viewpageInner.visibility = View.GONE
        binding.rlSlotContainer.visibility = View.GONE
        // binding.rlSlotContainer.visibility = View.GONE
        binding.SliderDotsInner.visibility = View.GONE
        binding.shimmerRvContainer.startShimmerAnimation()
        binding.insideViewpagerShimmer.startShimmerAnimation()
        binding.shimmerViewValue.startShimmerAnimation()
    }

    private fun hideInternalLoading() {
        binding.shimmerRvContainer.visibility = View.GONE
        binding.insideViewpagerShimmer.visibility = View.GONE
        binding.shimmerViewValue.visibility = View.GONE
        // binding.rvEarning.visibility = View.VISIBLE
        //  binding.rlSlotContainer.v
        //  isibility = View.VISIBLE
        binding.rlSlotContainer.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE
        binding.SliderDots.visibility = View.VISIBLE
        binding.viewpageInner.visibility = View.VISIBLE
        binding.SliderDotsInner.visibility = View.VISIBLE
        binding.shimmerRvContainer.stopShimmerAnimation()
        binding.insideViewpagerShimmer.stopShimmerAnimation()
        binding.shimmerViewValue.stopShimmerAnimation()
    }

    private fun hideLoading() {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
        binding.rootEarning.visibility = View.VISIBLE
    }

    private fun getTransactionDetails(
        startPage: Int,
        perPage: Int,
        type: String,
    ) {
        val transaction = Transaction(startPage, perPage, type, listOf(), listOf())
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getTransactionDetails(transaction).collect {
                when (it) {
                    ApiState.Loading -> {
                        showLoading()
                    }
                    is ApiState.Success -> {
                        transactionDetailsModels.clear()
                        if (it.data.transactions != null) {
                            for (item in it.data.transactions) {
                                transactionDetailsModels.add(item!!)
                            }
                            if (transactionDetailsModels.size > 0) {
                                binding.rvTranscHistory.visibility = View.VISIBLE
                                binding.tvInfoRvData.visibility = View.GONE
                                transactionDetailAdapter =
                                    activity?.let { it1 ->
                                        TransactionDetailAdapter(
                                            it1,
                                            transactionDetailsModels
                                        )
                                    }
                            } else {
                                binding.tvInfoRvData.visibility = View.VISIBLE
                                binding.rvTranscHistory.visibility = View.GONE
                                transactionDetailAdapter =
                                    activity?.let { it1 ->
                                        TransactionDetailAdapter(
                                            it1,
                                            ArrayList()
                                        )
                                    }
                            }
                        } else {
                            binding.tvInfoRvData.visibility = View.VISIBLE
                            binding.rvTranscHistory.visibility = View.GONE
                            transactionDetailAdapter =
                                activity?.let { it1 ->
                                    TransactionDetailAdapter(
                                        it1,
                                        ArrayList()
                                    )
                                }
                        }
                        binding.rvTranscHistory.adapter = transactionDetailAdapter
                        hideLoading()
                        apiEarnInfo()
                    }
                    is ApiState.Failure -> {
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }

                }
            }
        }

    }

    private fun checkForSessions() {
        if (PrefrenceUtils.retriveDataInBoolean(activity, Constants.CHECKFORFIRSTTIME)) {
            Constants.checkSessionSoftUpdate = false

        } else {
            // checkForAutoUpdate(dataModel)
            if (!PrefrenceUtils.retriveData(activity, Constants.SHOWN_VERSION)
                    .equals(BuildConfig.VERSION_NAME, ignoreCase = true)
            ) {
            }
            if (Constants.checkSessionSoftUpdate) {
                Constants.checkSessionSoftUpdate = false
                //               checkForSoftUpdate(dataModel);
            }
        }
        PrefrenceUtils.insertData(
            requireActivity(),
            Constants.SHOWN_VERSION,
            BuildConfig.VERSION_NAME
        )
    }

    private fun startCoachMarks() {
        // We have a sequence of targets, so lets build it!
        val sequence: TapTargetSequence = TapTargetSequence(activity)
            .targets(
                TapTarget.forView(binding.ivScooterContainer,
                    resources.getString(R.string.work_for_mutliple_companies_together_with_mitra_earn_extra_money))
                    .outerCircleColor(R.color.default_200)
                    .outerCircleAlpha(0.9f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(16)
                    .titleTextColor(R.color.black_v2)
                    .descriptionTextSize(16)
                    .descriptionTextColor(R.color.white)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.DEFAULT_BOLD)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(false)
                    .transparentTarget(true)
                    .targetRadius(50)
                    .id(1),
                TapTarget.forView(binding.profile,
                    resources.getString(R.string.profile_coach_mark_desc))
                    .outerCircleColor(R.color.default_200)
                    .outerCircleAlpha(0.9f)
                    .targetCircleColor(com.vahan.mitra_playstore.R.color.white)
                    .titleTextSize(16)
                    .titleTextColor(R.color.black_v2)
                    .descriptionTextSize(16)
                    .descriptionTextColor(R.color.white)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.DEFAULT_BOLD)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(false)
                    .transparentTarget(true)
                    .targetRadius(50)
                    .id(2)
            )
            .listener(object : TapTargetSequence.Listener {
                // This listener will tell us when interesting(tm) events happen in regards
                // to the sequence
                override fun onSequenceFinish() {
                    (requireActivity() as MainActivity).startCoachMarks()
                }

                override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                    Log.d("TapTargetView", "Clicked on " + lastTarget.id())
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {
//                    val dialog = AlertDialog.Builder(requireContext())
//                        .setTitle("Uh oh")
//                        .setMessage("You canceled the sequence")
//                        .setPositiveButton("Oops", null).show()
//                    TapTargetView.showFor(dialog,
//                        TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE),
//                            "Uh oh!",
//                            "You canceled the sequence at step " + lastTarget.id())
//                            .cancelable(false)
//                            .tintTarget(false), object : TapTargetView.Listener() {
//                            override fun onTargetClick(view: TapTargetView) {
//                                super.onTargetClick(view)
//                                dialog.dismiss()
//                            }
//                        })
                }
            })

        // You don't always need a sequence, and for that there's a single time tap target

        // You don't always need a sequence, and for that there's a single time tap target
        val spannedDesc = SpannableString("This is the sample app for TapTargetView")
        spannedDesc.setSpan(UnderlineSpan(),
            spannedDesc.length - "TapTargetView".length,
            spannedDesc.length,
            0)

        sequence.start()
    }

    @SuppressLint("SetTextI18n")
    private fun apiEarnInfo() {
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getEarnList.collect {
                when (it) {
                    is ApiState.Success -> {
                        dataModel = it.data
                        setNudgeIconText()
                        milestoneModel = it.data.incentiveStructures
                        (requireActivity() as MainActivity).loanStatusCheck =
                            dataModel!!.user?.loanEligibilityStatus ?: ""
                        setCustomMoengageGenericType(dataModel)
                        setFreshChatSession(dataModel!!)
                        insertPreferencesData()
                        setMoenageUserDetails()
                        setBlitzllama()
                        loadInitialApiData(dataModel!!)
                        checkTypeOfDataStorage()
                        checkForSessions()
                        setDetailsForWeeklyEarnings()
                        it.data.crossUtilSlots?.let { it1 ->
                            setSlotAvailableData(it1)
                        }
                        if (dataModel?.cashoutAdditionalData != null &&
                            dataModel?.cashoutAdditionalData?.CashoutExpUser == true
                        ) {
                            experimentFlow()
                        } else {
                            updateUI()
                        }
                        intialiseViewPager(dataModel!!.milestones.size)
                        setDetailsForMilestone()
                        setInnerViewPager(dataModel)
                        setInstrumentation()
                    }
                    is ApiState.Failure -> {
                        Log.d("asd", "apiEarnInfo: " + it.msg)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_error_fragment)
                    }
                    ApiState.Loading -> {
                        internalShowLoading()
                    }
                }
            }
        }

    }

    private fun setNudgeIconText() {
        binding.tvEarningAmountNudge.text = PrefrenceUtils.retriveData(requireContext(),Constants.NUDGE_ICON_TEXT_REMOTE_CONFIG)
    }

    private fun setDetailsForMilestone() {
        if (milestoneModel == null || (milestoneModel?.incentiveList!!.isEmpty())) {
            binding.rvEarning.visibility = View.GONE
            binding.rvContainerHistory.visibility = View.VISIBLE
        } else {
            binding.rvEarning.visibility = View.VISIBLE
            binding.rvContainerHistory.visibility = View.GONE
            milestoneModel?.let { it1 -> setMilestoneTrackerAdapterData(it1) }
        }
    }

    private fun setDetailsForWeeklyEarnings() {
        val weeklyEarningsUnderlined = SpannableString("₹ ${milestoneModel?.weeklyEarnings}")
        weeklyEarningsUnderlined.setSpan(UnderlineSpan(), 0, weeklyEarningsUnderlined.length, 0)
        binding.tvTransEarningViewAll.text = weeklyEarningsUnderlined
    }

    @SuppressLint("SimpleDateFormat")
    private fun setSlotAvailableData(crossUtilSlots: List<EarnDataModel.CrossUtilSlots>) {
        val currentTime: String =
            SimpleDateFormat("hh:mm a").format(Date())
        val format = SimpleDateFormat("hh:mm a")
        if (crossUtilSlots.isNotEmpty()) {
            binding.rlSlotContainer.visibility = View.VISIBLE
            PrefrenceUtils.insertDataInBoolean(requireContext(),
                Constants.ISCROSSUTILSLOTAVAILABLE,
                true)
            // For Single Company
            if (crossUtilSlots.size == 1) {
                val currentDateTime = format.parse(currentTime)
                for (i in 0 until crossUtilSlots[0].slots!!.size) {
                    val timeA =
                        crossUtilSlots[0].slots?.get(i)?.split(" - ")?.toTypedArray()?.get(0)
                            ?.uppercase(Locale.getDefault())?.format(Date())
                    val timeB =
                        crossUtilSlots[0].slots?.get(i)?.split(" - ")?.toTypedArray()?.get(1)
                            ?.uppercase(Locale.getDefault())?.format(Date())
                    val startingSlotTimeOne = format.parse(timeA!!)
                    val endingSlotTimeTwo = format.parse(timeB!!)
                    if (currentDateTime >= startingSlotTimeOne && currentDateTime!! <= endingSlotTimeTwo) {
                        showLearnMoreNudge(false)
                        binding.rlSlotContainer.visibility = View.VISIBLE
                        slotAvailableCompanyA =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "It's <b>$currentTime</b> your ${crossUtilSlots[0].companyName} slots is open!"
                            else {
                                "<b>$currentTime</b> होरे है | आपके " + "${crossUtilSlots[0].companyName} के स्लॉट खुले हैं!"
                            }
                        break
                    } else {
                        for (j in 0 until crossUtilSlots[0].slots!!.size) {
                            val timeA = crossUtilSlots[0].slots!![j].split(" - ").toTypedArray()[0]
                                .uppercase(Locale.getDefault()).format(Date())
                            val timeB = crossUtilSlots[0].slots!![j].split(" - ").toTypedArray()[1]
                                .uppercase(Locale.getDefault()).format(Date())

                            val startingSlotTimeOne = format.parse(timeA)
                            val endingSlotTimeOne = format.parse(timeB)
                            if (currentDateTime <= startingSlotTimeOne || currentDateTime <= endingSlotTimeOne) {
                                showLearnMoreNudge(false)
                                binding.rlSlotContainer.visibility = View.VISIBLE
                                sourceStringOne =
                                    if (PrefrenceUtils.retriveLangData(requireContext(),
                                            Constants.LANGUAGE).equals("en")
                                    )
                                        "${crossUtilSlots[0].companyName} " + "slots is at <b>$timeA</b>"
                                    else
                                        "${crossUtilSlots[0].companyName} के " + "स्लॉट <b>$timeA</b> पर उपलब्ध है "
                                break
                            } else {
//                              binding.rlSlotContainer.visibility = View.GONE
//                                showLearnMoreNudge(true)
                            }
                        }
                    }
                }

                if (slotAvailableCompanyA.isNotEmpty()) {
                    val sourceString =
                        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                .equals("en")
                        )
                            "<b>${"Hey,"}</b> ${
                                "Its <b>$currentTime</b> your " +
                                        "${crossUtilSlots[0].companyName}" + " slots is open! Get Ready for your ride. <b> <u>Login Now</u> </b>"
                            }"
                        else
                            "<b>${"नमस्ते!!"}</b> ${
                                " <b>$currentTime</b> होरे है| आपके " +
                                        "${crossUtilSlots[0].companyName} " + " के स्लॉट खुले हैं! अपनी सवारी के लिए तैयार हो जाओ <b> <u>प्रवेश करें</u> </b>"
                            }"
                    binding.tvSlot.text = Html.fromHtml(sourceString)
                } else {
                    if (sourceStringOne.isNotEmpty()) {
                        val sourceString =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "<b>${"Hey!!"}</b> Your upcoming $sourceStringOne. <b><u>View All</u></b>"
                            else
                                "<b>${"नमस्ते!!"}</b> आपका आगामी ${"$sourceStringOne <b><u>View All</u></b>"}"
                        binding.tvSlot.text = Html.fromHtml(sourceString)
                    }
                }

            }

            // For Multiple Company Recent Two
            else if (crossUtilSlots.size > 1) {
                val currentDateTime = format.parse(currentTime)
                val listOfSlotOne = crossUtilSlots[0].slots!!
                for (i in listOfSlotOne.indices) {
                    val timeA = listOfSlotOne[i].split(" - ").toTypedArray()[0]
                        .uppercase(Locale.getDefault()).format(Date())
                    val timeB = listOfSlotOne[i].split(" - ").toTypedArray()[1]
                        .uppercase(Locale.getDefault()).format(Date())
                    val startingSlotTimeOne = format.parse(timeA)
                    val endingSlotTimeTwo = format.parse(timeB)
                    if (currentDateTime!! >= startingSlotTimeOne && currentDateTime <= endingSlotTimeTwo) {
                        showLearnMoreNudge(false)
                        binding.rlSlotContainer.visibility = View.VISIBLE
                        slotAvailableCompanyA =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "${crossUtilSlots[0].companyName}" + " slots is open!"
                            else
                                "${crossUtilSlots[0].companyName} के स्लॉट खुले हैं!"
                        break
                    } else {
                        for (j in listOfSlotOne.indices) {
                            val timeA = listOfSlotOne[j].split(" - ").toTypedArray()[0]
                                .uppercase(Locale.getDefault()).format(Date())
                            val timeB = listOfSlotOne[j].split(" - ").toTypedArray()[1]
                                .uppercase(Locale.getDefault()).format(Date())

                            val startingSlotTimeOne = format.parse(timeA)
                            val endingSlotTimeOne = format.parse(timeB)
                            if (currentDateTime <= startingSlotTimeOne || currentDateTime <= endingSlotTimeOne) {
                                showLearnMoreNudge(false)
                                binding.rlSlotContainer.visibility = View.VISIBLE
                                sourceStringOne =
                                    if (PrefrenceUtils.retriveLangData(requireContext(),
                                            Constants.LANGUAGE).equals("en")
                                    )
                                        "${crossUtilSlots[0].companyName} " + " slots is at <b>$timeA</b>"
                                    else
                                        "${crossUtilSlots[0].companyName} के " + "स्लॉट <b>$timeA</b> पर उपलब्ध है "

                                break
                            } else {
//                                binding.rlSlotContainer.visibility = View.GONE
//                                showLearnMoreNudge(true)
                            }
                        }

                    }
                }
                val listOfSlotTwo = crossUtilSlots[1].slots!!
                for (i in listOfSlotTwo.indices) {
                    val timeA = listOfSlotTwo[i].split(" - ").toTypedArray()[0]
                        .uppercase(Locale.getDefault()).format(Date())
                    val timeB = listOfSlotTwo[i].split(" - ").toTypedArray()[1]
                        .uppercase(Locale.getDefault()).format(Date())
                    val startingSlotTimeOne = format.parse(timeA)
                    val endingSlotTimeTwo = format.parse(timeB)
                    if (currentDateTime in startingSlotTimeOne..endingSlotTimeTwo) {
                        showLearnMoreNudge(false)
                        slotAvailableCompanyB =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "${crossUtilSlots[1].companyName}" + " slots is open!"
                            else
                                "${crossUtilSlots[1].companyName} के स्लॉट खुले हैं!"
                        break
                    } else {
                        for (j in listOfSlotTwo.indices) {
                            val timeA = listOfSlotTwo[j].split(" - ").toTypedArray()[0]
                                .uppercase().format(Date())
                            val timeB = listOfSlotTwo[j].split(" - ").toTypedArray()[1]
                                .uppercase().format(Date())

                            val startingSlotTimeOne = format.parse(timeA)
                            val endingSlotTimeOne = format.parse(timeB)
                            if(currentDateTime!! >= startingSlotTimeOne && currentDateTime <= endingSlotTimeOne){
                                showLearnMoreNudge(false)
                                sourceStringTwo =
                                    if (PrefrenceUtils.retriveLangData(requireContext(),
                                            Constants.LANGUAGE).equals("en")
                                    )
                                        "${crossUtilSlots[1].companyName} " + "slots is at <b>$timeA</b>"
                                    else
                                        "${crossUtilSlots[1].companyName} के " + "स्लॉट <b>$timeA</b> पर उपलब्ध है "
                                break
                            }
                            else if (currentDateTime <= startingSlotTimeOne || currentDateTime <= endingSlotTimeOne) {
                                showLearnMoreNudge(false)
                                sourceStringTwo =
                                    if (PrefrenceUtils.retriveLangData(requireContext(),
                                            Constants.LANGUAGE).equals("en")
                                    )
                                        "${crossUtilSlots[1].companyName} " + "slots is at <b>$timeA</b>"
                                    else
                                        "${crossUtilSlots[1].companyName} के " + "स्लॉट <b>$timeA</b> पर उपलब्ध है "
                                break
                            } else {
//                                showLearnMoreNudge(true)
                            }
                        }

                    }
                }

                if (slotAvailableCompanyA.isNotEmpty() && slotAvailableCompanyB.isNotEmpty()) {
                    val sourceString =
                        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                .equals("en")
                        )
                            "<b>${"Hey!!"}</b>" + " Its <b>$currentTime</b> your $slotAvailableCompanyA & $slotAvailableCompanyB  Get Ready for your ride <b> <u>Login Now</u> </b>"
                        else
                            "<b>${"नमस्ते!!"}</b> ${
                                " <b>$currentTime</b> होरे है | " +
                                        "$slotAvailableCompanyA & $slotAvailableCompanyB " + " अपनी सवारी के लिए तैयार हो जाएं <b> <u>अभी लॉगिन करें</u> </b>"
                            }"
                    binding.tvSlot.text = Html.fromHtml(sourceString)
                } else if (slotAvailableCompanyA.isEmpty() && slotAvailableCompanyB.isNotEmpty()) {
                    val sourceString =
                        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                .equals("en")
                        )
                            "<b>${"Hey!!"}</b> ${
                                "Your Upcoming " +
                                        sourceStringOne + slotAvailableCompanyB + " <b><u>View All</u></b>"

                            }"
                        else
                            "<b>${"नमस्ते!!"}</b> ${
                                "आपका आगामी " +
                                        "$sourceStringOne $slotAvailableCompanyB" + " <b> <u> सभी को देखें </u></b>"
                            }"
                    binding.tvSlot.text = Html.fromHtml(sourceString)
                } else if (slotAvailableCompanyA.isNotEmpty() && slotAvailableCompanyB.isEmpty()) {
                    val sourceString =
                        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                .equals("en")
                        )
                            "<b>${"Hey!!"}</b> ${
                                "Your Upcoming " +
                                        sourceStringTwo + slotAvailableCompanyA + " <b><u>View All</u></b>"
                            }"
                        else
                            "<b>${"नमस्ते!!"}</b> ${
                                "आपका आगामी " +
                                        "$sourceStringTwo $slotAvailableCompanyA" + " <b> <u> सभी को देखें </u></b>"
                            }"
                    binding.tvSlot.text = Html.fromHtml(sourceString)
                } else {
                    if (sourceStringOne.isNotEmpty() && sourceStringTwo.isNotEmpty()) {
                        val sourceString =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "<b>${"Hey!!"}</b> ${"Your Upcoming $sourceStringOne & $sourceStringTwo"}" + " <b><u>View All</u></b>"
                            else
                                "<b>${"नमस्ते!!"}</b> ${"आपका आगामी "}$sourceStringOne  &  $sourceStringTwo" + " <b> <u> सभी को देखें </u></b>"
                        binding.tvSlot.text = Html.fromHtml(sourceString)
                    } else if (sourceStringOne.isEmpty() && sourceStringTwo.isNotEmpty()) {
                        val sourceString =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "<b>${"Hey!!"}</b> ${"Your Upcoming "}" + sourceStringTwo + " <b><u>View All</u></b>"
                            else
                                "<b>${"नमस्ते!!"}</b> ${"आपका आगामी $sourceStringTwo"} " + "<b> <u> सभी को देखें </u></b>"
                        binding.tvSlot.text = Html.fromHtml(sourceString)
                    } else if (sourceStringOne.isNotEmpty() && sourceStringTwo.isEmpty()) {
                        val sourceString =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "<b>${"Hey!!"}</b> ${"Your Upcoming "}" + sourceStringOne + " <b><u>View All</u></b>"
                            else
                                "<b>${"नमस्ते!!"}</b> ${"आपका आगामी"}" + sourceStringOne + "<b> <u> सभी को देखें </u></b>"
                        binding.tvSlot.text = Html.fromHtml(sourceString)
                    }
                }
            }
        } else {
            PrefrenceUtils.insertDataInBoolean(requireContext(),
                Constants.ISCROSSUTILSLOTAVAILABLE,
                false)
//            showLearnMoreNudge(true)
        }
    }

    private fun showLearnMoreNudge(isEmptySlot: Boolean){
        if(isEmptySlot){
            showLearnMoreTxt = true
            binding.rlSlotContainer.visibility = View.VISIBLE
            val sourceString =
                if (PrefrenceUtils.retriveLangData(requireContext(),
                        Constants.LANGUAGE)
                        .equals("en")
                )
                    "<b>Hey, Work with multiple companies together and earn extra! <u> Learn more </u></b>"
                else
                    "<b>नमस्ते! एक साथ कई कंपनियों के साथ काम करें और अतिरिक्त कमाई करें! <u> और अधिक जानें </u></b>"
            binding.tvSlot.text = Html.fromHtml(sourceString)
        }
        else{
            showLearnMoreTxt = false;
        }
    }


    private fun checkTypeOfDataStorage() {
        if (doesUserHavePermission()) {
            WorkerScheduler.updateUserSMS(
                requireActivity(),
                PrefrenceUtils.retriveData(requireContext(), Constants.PHONENUMBER),
                PrefrenceUtils.retriveData(requireContext(), Constants.API_TOKEN)
            )

        } else {
            // If userUpdate from previous version
            if (!PrefrenceUtils.retriveData(activity, Constants.SHOWN_VERSION)
                    .equals(BuildConfig.VERSION_NAME, ignoreCase = true)
            ) {
                WorkerScheduler.noUser(
                    requireActivity(),
                    PrefrenceUtils.retriveData(requireContext(), Constants.PHONENUMBER),
                    PrefrenceUtils.retriveData(requireContext(), Constants.API_TOKEN)
                )
                PrefrenceUtils.insertData(requireContext(), Constants.CHECKSMSONCES, "Two")
            }
            // Fresh Login
            if (PrefrenceUtils.retriveData(requireActivity(), Constants.CHECKSMSONCES)
                    .equals("ONCES")
            ) {
                WorkerScheduler.noUser(
                    requireActivity(),
                    PrefrenceUtils.retriveData(requireContext(), Constants.PHONENUMBER),
                    PrefrenceUtils.retriveData(requireContext(), Constants.API_TOKEN)
                )
                PrefrenceUtils.insertData(requireContext(), Constants.CHECKSMSONCES, "Two")
            }
        }
        getDeviceInfoS()
    }

    // cashout experimentFlow
    @SuppressLint("SetTextI18n")
    private fun experimentFlow() {
        binding.cashoutCardRoot.visibility = View.VISIBLE
        binding.cashLayout.visibility = GONE
        when (dataModel!!.cashoutAdditionalData!!.cashoutEligibilityStatus) {
            "EC" -> {
                eligiblityCheck = false
                val properties = Properties()
                val attribute: HashMap<String, Any> = HashMap()
                captureAllEvents(
                    requireContext(),
                    Constants.CASHOUT_DISABLED_VIEWED,
                    attribute,
                    properties
                )
                binding.cashoutTxt.visibility = VISIBLE
                binding.cashLayoutExp.visibility = GONE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eC?.msg
                } else {
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eC?.hiMsg
                }
            }
            "E" -> {
                eligiblityCheck = true
                if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                    ) {
                        binding.cashOutConfigTxtDetailExp.text =
                            "Complete ${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} " +
                                    " more trips and continue working for ${
                                        dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel
                                    } " +
                                    "more days with Mitra to unlock " +
                                    "${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% of Cashout"
                    } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                    ) {
                        binding.cashOutConfigTxtDetailExp.text =
                            "Complete ${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} " +
                                    "more trips with Mitra to unlock " +
                                    "${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% of Cashout"
                    } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == false &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                    ) {
                        binding.cashOutConfigTxtDetailExp.text =
                            "Continue working ${
                                dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel
                            } more days with Mitra to unlock " +
                                    "${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% of Cashout"
                    } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == false &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                    ) {
                        binding.cashOutConfigTxtDetailExp.visibility = GONE
                        binding.ivIconElectricExp.visibility = INVISIBLE
                    }
                }
                else {
                    if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                    ) {
                        binding.cashOutConfigTxtDetailExp.text =
                            "${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% कैशआउट अनलॉक करने के लिए मित्रा में " +
                                    "${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel}" +
                                    " यात्राएं और पूरी करें और साथ मे ${dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel} दिन और काम करना जारी रखें"
                    } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                    ) {
                        binding.cashOutConfigTxtDetailExp.text =
                            "${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% कैशआउट अनलॉक करने के लिए मित्रा में ${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} " + "और यात्राएं पूरी करे"
                    } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == false &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                    ) {
                        binding.cashOutConfigTxtDetailExp.visibility = GONE
                        binding.ivIconElectricExp.visibility = INVISIBLE
                    } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == false &&
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                    ) {
                        binding.cashOutConfigTxtDetailExp.text =
                            " ${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% कैशआउट अनलॉक करने के लिए मित्रा में ${dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel} और दिन काम करना जारी रखें "
                    }
                }
                binding.cashoutTxt.visibility = View.GONE
                if (dataModel!!.cashoutDetails?.enabled!! &&
                    dataModel!!.cashoutDetails?.amountEligible!! >=
                    dataModel!!.cashoutDetails?.minAmountEligible!!
                ) {
                    binding.cashLayoutExp.visibility = View.VISIBLE
//                    binding.cashLayoutExp.setOnClickListener {
//                        setInstrumentationOnCLick(dataModel!!, "Cashout Levels Flow")
//                        dataModel!!.cashoutDetails!!.amountEligibleLabel?.let { it1 ->
//                            BottomSheetCashOutPurpose(
//                                requireActivity(),
//                                dataModel?.cashoutAdditionalData?.userLevel,
//                                it1,
//                                this@EarnFragmentV2,
//                                dataModel!!.cashoutDetails?.amountEligible!!,
//                                dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0,
//                                dataModel!!.cashoutDetails?.isCashoutFeeEnabled!!,
//                                dataModel!!.cashoutDetails?.cashoutFeePercentage!!,
//                                dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel,
//                                dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel,
//                                dataModel?.cashoutAdditionalData?.cashoutEligibilityStatus,
//                                dataModel?.cashoutAdditionalData?.orderReachToNextLevel,
//                                dataModel?.cashoutAdditionalData?.daysReachToNextLevel,
//                                dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage
//                            ).show()
//                        }
//                    }
                }
                else if (!dataModel!!.cashoutDetails?.enabled!! &&
                    dataModel!!.cashoutDetails?.amountEligible!! >=
                    dataModel!!.cashoutDetails?.minAmountEligible!!
                ) {
                    binding.imgGreyExp.visibility = View.VISIBLE
                    binding.cashLayoutExp.visibility = View.VISIBLE
                    binding.cashLayoutExp.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.you_have_already_availed_the_service),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else if (dataModel!!.cashoutDetails?.enabled!! &&
                    dataModel!!.cashoutDetails?.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.amountEligible != 0.0
                ) {
                    val property = Properties()
                    val attribute = HashMap<String, Any>()
                    requireContext().captureEvents(requireContext(),
                        Constants.CASHOUT_INELIGIBLE_VIEWED,
                        HashMap())
                    binding.cashLayoutExp.setOnClickListener {
                        captureAllEvents(
                            requireContext(),
                            Constants.CASHOUT_INELIGIBLE_TAPPED,
                            attribute,
                            property
                        )
                        if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                                .equals("en", ignoreCase = true)
                        ) Toast.makeText(requireContext(), "Minimum amount for cashout is Rs. " +
                                dataModel!!.cashoutDetails!!.minAmountEligible, Toast.LENGTH_SHORT)
                            .show()
                        else
                            Toast.makeText(requireContext(),
                                "कैशआउट के लिए न्यूनतम राशि रु. " +
                                        dataModel!!.cashoutDetails!!.minAmountEligible,
                                Toast.LENGTH_SHORT).show()

                    }
                    binding.cashLayoutExp.visibility = View.VISIBLE
                    binding.imgGreyExp.visibility = View.VISIBLE
                }
                else if (dataModel!!.cashoutDetails!!.enabled!! &&
                    dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.amountEligible == 0.0
                ) {
                    binding.cashLayoutExp.visibility = View.GONE
                }
                else if (!dataModel!!.cashoutDetails!!.enabled!! &&
                    dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.amountEligible == 0.0
                ) {
                    binding.cashLayoutExp.visibility = View.GONE
                }
                else if (dataModel!!.cashoutDetails!!.enabled!! &&
                    dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                    dataModel!!.cashoutDetails!!.minAmountEligible!! < 0.0
                ) {
                    binding.cashLayoutExp.visibility = View.GONE
                }
            }
            "EW" -> {
                eligiblityCheck = false
                val properties = Properties()
                val attribute = HashMap<String, Any>()
                captureAllEvents(
                    requireContext(),
                    Constants.CASHOUT_DISABLED_VIEWED,
                    attribute,
                    properties
                )
                binding.cashoutTxt.visibility = View.VISIBLE
                binding.cashLayout.visibility = View.GONE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                )
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eW?.msg
                else binding.cashOutConfigTxt.text =
                    viewEarnViewModel.remoteCashOutData.value?.eW?.hiMsg
            }
            "NE" -> {
                eligiblityCheck = false
                val properties = Properties()
                val attribute = HashMap<String, Any>()
                captureAllEvents(
                    requireContext(),
                    Constants.CASHOUT_DISABLED_VIEWED,
                    attribute,
                    properties
                )
                binding.cashoutTxt.visibility = View.VISIBLE
                binding.cashLayoutExp.visibility = View.GONE
                if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) binding.cashOutConfigTxt.text = viewEarnViewModel.remoteCashOutData.value?.nE?.msg
                else
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.nE?.hiMsg
            }
        }
        if (dataModel!!.cashoutDetails!!.amountEligibleLabel!!.contains(".")) {
            binding.setPriceExp.text = dataModel!!.cashoutDetails!!.amountEligibleLabel!!.substring(
                0,
                dataModel!!.cashoutDetails!!.amountEligibleLabel!!.indexOf(".")
            )
        } else {
            binding.setPriceExp.text = dataModel!!.cashoutDetails!!.amountEligibleLabel
        }
    }

    private fun setInstrumentationOnCLick(dataModel: EarnDataModel, type: String) {
        val properties = Properties()
        val attribute: HashMap<String, Any> = HashMap()
        properties.addAttribute(Constants.ELIGIBLE_AMOUNT,
            dataModel.cashoutDetails?.amountEligible)
        properties.addAttribute(Constants.CASHED_FEE_PERCENT,
            dataModel.cashoutDetails?.cashoutFeePercentage ?: 0.0)
        properties.addAttribute(Constants.CASHED_FIXED_FEE,
            dataModel.cashoutDetails?.cashoutFixedFee ?: 0)
        properties.addAttribute("type", type)
        attribute[Constants.ELIGIBLE_AMOUNT] = dataModel.cashoutDetails?.amountEligible ?: ""
        attribute[Constants.CASHED_FEE_PERCENT] =
            dataModel.cashoutDetails?.cashoutFeePercentage ?: 0.0
        attribute[Constants.CASHED_FIXED_FEE] = dataModel.cashoutDetails?.cashoutFixedFee ?: 0
        captureAllEvents(
            requireContext(),
            Constants.CASHOUT_ELIGIBLE_TAPPED,
            attribute,
            properties
        )
    }

    private fun setFreshChatSession(dataModel: EarnDataModel) {
        token = requireContext().setFreshChatSessions(requireContext(), dataModel)
    }

    private fun setBlitzllama() {
        requireContext().setBlitzLamaSurvey(requireContext(), dataModel, Constants.HOME_PAGE_VIEWED)
    }

    private fun insertPreferencesData() {
        requireContext().insertPreferencesData(requireContext(), dataModel!!)
    }

    private fun setMoenageUserDetails() {
        requireContext().setMoengageUserDetails(dataModel!!, requireContext())
    }

    private fun setInnerViewPager(dataModel: EarnDataModel?) {
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getBanner(
                dataModel?.jobDetails?.companyKey!!,
                dataModel.user?.type!!,
                dataModel.user.city!!
            ).collect {
                when (it) {
                    is ApiState.Success -> {
                        //Toast.makeText(requireContext(), "DSUCCESS", Toast.LENGTH_SHORT).show()

                        PrefrenceUtils.insertData(
                            requireContext(),
                            Constants.USERID,
                            dataModel.user.id
                        )
                        hideInternalLoading()
                        binding.viewpageInner.visibility = View.VISIBLE
                        fragmentArrayList.clear()

                        val banner = BannerListDataModelNew.DynamicBanner(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            getString(R.string.mitra_balance),
                            dataModel.currentEarnings!!.amount,
                            dataModel.jobDetails.companyIcon
                        )
                        if (it.data.dynamicBanner != null) {
                            for (item in it.data.dynamicBanner) {
                                fragmentArrayList.add(item!!)
                            }
                        }
                        fragmentArrayList.add(0, banner)
                        setViewPaggerInsertData(fragmentArrayList)
                    }
                    is ApiState.Failure -> {
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_error_fragment)
                    }
                    ApiState.Loading -> {}
                }
            }
        }

    }

    private fun setViewPaggerInsertData(fragmentArrayList: ArrayList<BannerListDataModelNew.DynamicBanner>) {
        var dotscount = 0
        val viewPagerAdapter = DynamicBannerAdapter(this, requireActivity(), fragmentArrayList)
        binding.viewpageInner.adapter = viewPagerAdapter
        binding.viewpageInner.setAnimationEnabled(true)
        binding.viewpageInner.setFadeEnabled(true)
        binding.viewpageInner.setFadeFactor(0.6f)

//        val swipeTimer = Timer()
//        swipeTimer.schedule(object : TimerTask() {
//            override fun run() {
//                Handler(Looper.getMainLooper()).postDelayed({
//                    location++
//                    if (viewPagerAdapter.count == location) location = 0
//                    binding.viewpageInner.setCurrentItem(location + 1, true)
//                }, 0)
//            }
//        }, 6000, 6000)
        dotscount = viewPagerAdapter.count
        val dots: Array<ImageView?> = arrayOfNulls(dotscount)
        for (i in 0 until dotscount) {
            dots[i] = ImageView(requireActivity())
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.non_active_dot
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.SliderDotsInner.addView(dots[i], params)
        }
        dots[0]!!.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )
        val finalDotscount = dotscount
        binding.viewpageInner.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                location = position
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until finalDotscount) {
                    dots[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.non_active_dot
                        )
                    )
                }
                dots[position]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.active_dot
                    )
                )

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setMilestoneTrackerAdapterData(listData: EarnDataModel.IncentiveStructures? = milestoneModel) {
        binding.rvEarning.adapter =
            listData?.let {
                MilestoneTrackerAdapter(requireActivity(), this, it)
            }
    }

    private fun setCustomMoengageGenericType(dataModel: EarnDataModel?) {
        requireContext().setCustomMoengageGenericType(dataModel!!, requireContext())
    }

    override fun onResume() {
        super.onResume()
        showCashoutFeedbackPopup()
    }

    private fun showCashoutFeedbackPopup() {
        if (isCashoutFeedbackCheck) {
            FeedbackBottomsheet(requireContext(), Constants.CASHOUT).show()
            isCashoutFeedbackCheck = false
        }
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        if (dataModel?.walletDetails?.walletBalance.toString() != null) {
            properties.addAttribute(Constants.MITRA_BALANCE,
                dataModel?.walletDetails?.walletBalance.toString())
            attribute[Constants.MITRA_BALANCE] = dataModel?.walletDetails?.walletBalance.toString()
        } else {
            bundle.putString(Constants.MITRA_BALANCE, "")
            properties.addAttribute(Constants.MITRA_BALANCE, "")
        }
        captureAllEvents(
            requireContext(),
            Constants.HOME_PAGE_VIEWED,
            attribute,
            properties
        )
        handler.postDelayed({
            if (viewEarnViewModel.trigger.value != null) {
                for (i in 0 until viewEarnViewModel.trigger.value?.trigger?.size!!) {
                    if (!PrefrenceUtils.retriveDataInBoolean(context, Constants.HOME_PAGE_VIEWED)) {
                        if (PrefrenceUtils.retriveDataInBoolean(context,
                                Constants.ISFEEDBACKSESSION)
                        ) {
                            if (viewEarnViewModel.trigger.value?.trigger?.get(i)?.trigger_event.equals(
                                    "earn_page_viewed")
                            ) {
                                FeedbackBottomsheet(requireContext(), "earn_page_viewed").show()
                                PrefrenceUtils.insertDataInBoolean(context,
                                    Constants.ISFEEDBACKSESSION,
                                    false)
                            }
                        } else {
                            return@postDelayed
                        }
                    } else {
                        return@postDelayed
                    }


                }
            }
        }, 3000)
    }

    private fun loadInitialApiData(earnDataModel: EarnDataModel) {
        PrefrenceUtils.insertData(
            requireActivity(),
            Constants.USERNAME,
            earnDataModel.user!!.name
        )
        PrefrenceUtils.insertData(
            requireActivity(),
            Constants.PHONENUMBER,
            earnDataModel.user.phoneNumber
        )
        PrefrenceUtils.insertDataInBoolean(
            requireActivity(),
            Constants.CHECK_BANK_STATUS,
            earnDataModel.bankAccountDetails!!.available!!
        )
        PrefrenceUtils.insertDataInBoolean(
            requireActivity(),
            Constants.TEST_USER,
            earnDataModel.user.settings?.isTestUser!!
        )
        PrefrenceUtils.insertDataInBoolean(
            requireActivity(),
            Constants.CHECK_UPLOAD_STATUS,
            earnDataModel.documents!!.uploaded!!
        )
        // Flatfeelabel
        cashoutFixedFeeLabel = dataModel?.cashoutDetails?.cashoutFixedFeeLabel

        if (earnDataModel.bankAccountDetails.available!!) {
            isBankDetailsUpload = true
        }
        if (earnDataModel.documents.uploaded!!) {
            isUploadFinalStatus = true
        }
        var imageUrlPan: String?
        var imageUrlBackAadhar: String?
        var imageUrlFrontAadhar: String?
        var imageUrlFrontDL: String?
        var imageUrlBackDL: String?
        if (earnDataModel.documents.documents.isNotEmpty()) {
            for (i in earnDataModel.documents.documents.indices) {
                if (earnDataModel.documents.documents[i].type.equals("Aadhaar Image",
                        ignoreCase = true)
                ) {
                    if (earnDataModel.documents.documents[i].otherSideImageUrl != null) {
                        imageUrlBackAadhar = earnDataModel.documents.documents[i].otherSideImageUrl
                        PrefrenceUtils.insertData(requireActivity(),
                            Constants.AADHARCARDBACK,
                            imageUrlBackAadhar)
                    }
                    imageUrlFrontAadhar = earnDataModel.documents.documents[i].imageUrl
                    PrefrenceUtils.insertData(requireActivity(),
                        Constants.AADHARCARDFRONT,
                        imageUrlFrontAadhar)
                    isAadhaarDocumentUpload = true
                }
                if (earnDataModel.documents.documents[i].type.equals("PAN Image",
                        ignoreCase = true)
                ) {
                    imageUrlPan = earnDataModel.documents.documents[i].imageUrl
                    PrefrenceUtils.insertData(requireActivity(), Constants.PANCARDIMAGE, imageUrlPan
                    )
                    isPanCardDocumentUpload = true
                }

                if (earnDataModel.documents.documents[i].type.equals("DL Image",
                        ignoreCase = true)
                ) {
                    if (earnDataModel.documents.documents[i].otherSideImageUrl != null) {
                        imageUrlBackDL = earnDataModel.documents.documents[i].otherSideImageUrl
                        PrefrenceUtils.insertData(requireActivity(),
                            Constants.DL_BACK_IMG,
                            imageUrlBackDL)
                    }
                    imageUrlFrontDL = earnDataModel.documents.documents[i].imageUrl
                    PrefrenceUtils.insertData(requireActivity(),
                        Constants.DL_FRONT_IMG,
                        imageUrlFrontDL)
                }
            }
        } else {
            PrefrenceUtils.insertData(requireActivity(), Constants.AADHARCARDBACK, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.AADHARCARDFRONT, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.PANCARDIMAGE, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.DL_FRONT_IMG, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.DL_BACK_IMG, "")
        }
        binding.setPrice.text = "₹ 3600"
    }

    private fun intialiseViewPager(size: Int) {
        val urlTest = ArrayList<TextModel>()
        Log.d(
            "CheckForBankDetails",
            "intialiseViewPager: $isBankDetailsUpload isBankDetailsUpload isUploadFinalStatus $isUploadFinalStatus"
        )
        if (!isBankDetailsUpload && !isUploadFinalStatus) {
            binding.viewPager.visibility = View.VISIBLE
            isTransc = false
            binding.SliderDots.visibility = View.VISIBLE

            val modelValue = TextModel(
                getString(R.string.bank_details_missing),
                getString(R.string.add_bank_details_to_receive_payouts),
                getString(R.string.bank)
            )
            val model =
                TextModel(
                    getString(R.string.documents_missing_),
                    getString(R.string.upload_document_),
                    getString(R.string.document)
                )
            urlTest.add(modelValue)
            urlTest.add(model)
            setViewPager(urlTest)
            hideCashoutFlow()
            hideParentContainer()
        } else if (isPanCardDocumentUpload && isAadhaarDocumentUpload && !isBankDetailsUpload) {
            binding.viewPager.visibility = View.VISIBLE
            isTransc = false
            binding.SliderDots.visibility = View.VISIBLE

            val modelValue = TextModel(
                getString(R.string.bank_details_missing),
                getString(R.string.add_bank_details_to_receive_payouts),
                getString(R.string.bank)
            )
            urlTest.add(modelValue)

            setViewPager(urlTest)
            hideCashoutFlow()
            hideParentContainer()
        } else if (isBankDetailsUpload && !isAadhaarDocumentUpload && !isPanCardDocumentUpload) {
            binding.viewPager.visibility = View.VISIBLE
            isTransc = false
            binding.SliderDots.visibility = View.VISIBLE

            val model =
                TextModel(
                    getString(R.string.documents_missing_),
                    getString(R.string.upload_document_),
                    getString(R.string.document)
                )
            urlTest.add(model)

            setViewPager(urlTest)
            showCashoutFlow()
            hideParentContainer()
        } else if (isBankDetailsUpload && isAadhaarDocumentUpload && !isPanCardDocumentUpload) {
            binding.viewPager.visibility = View.VISIBLE
            isTransc = false
            binding.SliderDots.visibility = View.VISIBLE

            val model =
                TextModel(
                    getString(R.string.documents_missing_),
                    getString(R.string.upload_document_),
                    getString(R.string.document)
                )
            urlTest.add(model)

            setViewPager(urlTest)
            showCashoutFlow()
            hideParentContainer()
        } else if (isBankDetailsUpload && !isAadhaarDocumentUpload && isPanCardDocumentUpload) {
            binding.viewPager.visibility = View.VISIBLE
            binding.containerTwoViewpager.visibility = View.GONE
            isTransc = false
            binding.SliderDots.visibility = View.VISIBLE
            val model =
                TextModel(
                    getString(R.string.documents_missing_),
                    getString(R.string.upload_document_),
                    getString(R.string.document)
                )
            urlTest.add(model)

            setViewPager(urlTest)
            showCashoutFlow()
            hideParentContainer()
        } else if (!isBankDetailsUpload && !isAadhaarDocumentUpload && !isPanCardDocumentUpload) {
            binding.viewPager.visibility = View.VISIBLE
            binding.SliderDots.visibility = View.VISIBLE
            binding.containerTwoViewpager.visibility = View.GONE
            isTransc = false
            val model =
                TextModel(
                    getString(R.string.documents_missing_),
                    getString(R.string.upload_document_),
                    getString(R.string.document)
                )
            urlTest.add(model)
            val modelValue2 =
                TextModel(
                    getString(R.string.bank_details_missing),
                    getString(R.string.add_bank_details_to_receive_payouts),
                    getString(R.string.bank)
                )
            urlTest.add(modelValue2)
            setViewPager(urlTest)
            hideCashoutFlow()
            hideParentContainer()
        } else {
            binding.viewPager.visibility = View.GONE
            binding.containerOneViewpager.visibility = View.GONE
            //            binding.icNotEligible.setVisibility(View.GONE);
            binding.rvContainer.visibility = View.VISIBLE
            binding.viewAllContainer.visibility = View.VISIBLE
            binding.SliderDots.visibility = View.GONE
            binding.rvContainerHistory.visibility = View.VISIBLE
            isTransc = true
            showParentContainer()
        }
    }

    private fun showParentContainer() {
        if(dataModel!!.incentiveStructures !== null && dataModel!!.incentiveStructures?.incentiveList?.isNotEmpty() == true)
        if (dataModel!!.incentiveStructures?.incentiveList?.get(0)?.milestones?.isNotEmpty() == true) {
            captureAllEvents(
                requireContext(),
                Constants.MILESTONE_VIEWED,
                HashMap(),
                Properties()
            )
        } else {
            //  binding.rvContainerEarning.visibility = View.GONE
        }
        binding.viewAllContainerClick.visibility = View.VISIBLE
        recylerViewItemCheck = true
        if (transactionDetailAdapter != null) transactionDetailAdapter!!.notifyDataSetChanged()
    }

    private fun hideParentContainer() {
        recylerViewItemCheck = false
        if (transactionDetailAdapter != null)
            transactionDetailAdapter!!.notifyDataSetChanged()
    }

    private fun hideCashoutFlow() {
        binding.cashoutCardRoot.visibility = View.GONE
    }

    private fun showCashoutFlow() {
        binding.cashoutCardRoot.visibility = View.VISIBLE
    }

    private fun setViewPager(urlTest: ArrayList<TextModel>?) {
        var dotscount = 0
        val viewPagerAdapter = urlTest?.let { SliderAdapter(requireActivity(), it) }
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = 2
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).postDelayed({
                    location++
                    if (viewPagerAdapter != null) {
                        if (viewPagerAdapter.count == location) location = 0
                    }
                    binding.viewPager.setCurrentItem(location + 1, true)
                }, 5000)
            }
        }, 5000, 5000)
        if (viewPagerAdapter != null) {
            dotscount = viewPagerAdapter.count
        }
        val dots: Array<ImageView?> = arrayOfNulls(dotscount)
        for (i in 0 until dotscount) {
            dots[i] = ImageView(requireActivity())
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.non_active_dot
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.SliderDots.addView(dots[i], params)
        }
        dots[0]!!.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )
        val finalDotscount = dotscount
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                location = position
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until finalDotscount) {
                    dots[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.non_active_dot
                        )
                    )
                }
                dots[position]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.active_dot
                    )
                )
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    inner class MoengageNotification : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra(Constants.NEW_NOTIFICATION).equals(Constants.RECEIVED)) {
                val notificationUpdate = intent.getStringExtra(Constants.NEW_NOTIFICATION)
                notificationUpdate?.let { updateAlertIcon(it) }
            } else {
                BaseApplication.context?.let {
                    Freshchat.getInstance(it)
                        .getUnreadCountAsync { _, unreadCount -> //Assuming "badgeTextView" is a text view to show the count on
                            //badgeTextView.setText()
                            if (unreadCount.toString() == "0") {
                                binding.chatCount.visibility = GONE
                            } else {
                                binding.chatCount.visibility = VISIBLE
                                binding.chatCount.text = unreadCount.toString()
                            }
                        }
                }
            }

        }
    }

    private fun updateAlertIcon(notificationUpdate: String) {
        binding.notificationContainerLight.visibility = View.VISIBLE
        binding.ivNotificationIconLight.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (notificationAlert != null) requireActivity().unregisterReceiver(
            notificationAlert
        )
    }

    companion object {
        @JvmField
        var recylerViewItemCheck = false

        @JvmField
        var isFeedbackUpdateCheck = false

        @JvmField
        var isCashoutFeedbackCheck = false
    }

    private fun doesUserHavePermission(): Boolean {
        val result = requireActivity().checkCallingOrSelfPermission(Manifest.permission.READ_SMS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onClick(position: Int) {
        val bundle = Bundle()
        bundle.putInt("position", position)
        Navigation.findNavController(binding.root)
            .navigate(R.id.nav_rate_card_new, bundle)
    }

    override fun coachmarkCallback() {
       // startCoachMarks()
    }





}