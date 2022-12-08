package com.vahan.mitra_playstore.view.earn.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.moengage.core.Properties
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentHomeBinding
import com.vahan.mitra_playstore.interfaces.CoachmarkListener
import com.vahan.mitra_playstore.models.kotlin.BannerListDataModelNew
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.models.kotlin.Transaction
import com.vahan.mitra_playstore.models.kotlin.TransactionDetailModel
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.BaseApplication
import com.vahan.mitra_playstore.view.MainActivity
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetCashOutPurpose
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetCashoutHold
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetV2
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet
import com.vahan.mitra_playstore.view.earn.view.adapter.*
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import com.vahan.mitra_playstore.view.ratecard.ui.RateCardDetailViewOnClick
import com.vahan.mitra_playstore.workmanager.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_weekly_earnings.*
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment(), CoachmarkListener, RateCardDetailViewOnClick {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var fa: FirebaseAnalytics
    private lateinit var viewEarnViewModel: EarnViewModel
    private var eligiblityCheck: Boolean = false
    private var cashoutHoldCheck: Boolean = false
    private var token: String? = null
    private var dataModel: EarnDataModel? = null
    val handler = Handler(Looper.getMainLooper())
    private var notificationAlert: MoengageNotification? = null
    var transactionDetailAdapter: TransactionDetailAdapter? = null
    private var milestoneModel: EarnDataModel.IncentiveStructures? = null
    private var showLearnMoreTxt = true
    private var slotAvailableCompanyA = ""
    private var slotAvailableCompanyB = ""
    private var sourceStringOne = ""
    private var sourceStringTwo = ""
    private var isPanCardDocumentUpload = false
    private var isBankDetailsUpload = false
    private var isUploadFinalStatus = false
    private var isAadhaarDocumentUpload = false
    private var isViewUpload = false
    private var isViewBank = false
    private var cashoutFixedFeeLabel: String? = null
    var location = 0
    private val transactionDetailsModels = ArrayList<TransactionDetailModel.Transaction>()
    private val dynamicBannerList = ArrayList<BannerListDataModelNew.DynamicBanner>()
    var key: String = ""
    lateinit var dynamicEntryPointAdapter: DynamicEntryPointAdapter
    lateinit var entryPointAdapter: EntryPointAdapter
    private val entryPointList = ArrayList<BannerListDataModelNew.DynamicEntryPoint>()
    private var cashoutStatus = "hold"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        viewEarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]
        binding.cashLayout.visibility = View.VISIBLE
        getRemoteConfigDataForUpdate()
        setupScreenName()
        alertNotification()
        getTransactionDetails(1, 5, "")
        clickListener()
    }

    private fun clickListener() {
        binding.llHold.setOnClickListener {
            if (dataModel != null) {
                BottomSheetCashoutHold(
                    requireActivity(),
                    dataModel?.cashoutDetails!!.holdDetails!!.companyName,
                    dataModel!!.cashoutDetails!!.holdDetails!!.companyIcon,
                    dataModel!!.cashoutDetails!!.holdDetails!!.holdMessage,
                    dataModel!!.cashoutDetails!!.amountEligibleLabel
                ).show()
            }
        }



        binding.cashoutTxt.setOnClickListener {

            if (eligiblityCheck) {
                setUpInstrumentation()
                dataModel!!.cashoutDetails!!.amountEligibleLabel?.let { it1 ->
                    BottomSheetCashOutPurpose(
                        requireActivity(),
                        dataModel?.cashoutAdditionalData?.userLevel,
                        it1,
                        this@HomeFragment,
                        null,
                        dataModel!!.cashoutDetails?.amountEligible!!,
                        dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0,
                        dataModel!!.cashoutDetails?.isCashoutFeeEnabled!!,
                        dataModel!!.cashoutDetails?.cashoutFeePercentage!!,
                        dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel,
                        dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel,
                        dataModel?.cashoutAdditionalData?.cashoutEligibilityStatus,
                        dataModel?.cashoutAdditionalData?.orderReachToNextLevel,
                        dataModel?.cashoutAdditionalData?.daysReachToNextLevel,
                        dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage
                    ).show()
                }
            }

        }
        binding.chat.setOnClickListener {
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
//            findNavController().navigate(
//                R.id.nav_fragment_graph
//            )

        }
        binding.tvViewAll.setOnClickListener {
//            if(recylerViewItemCheck){
            val bundle = Bundle()
            bundle.putParcelable("cashoutDetails", dataModel?.cashoutDetails)
            bundle.putParcelable("cashoutAdditionalData", dataModel?.cashoutAdditionalData)
            bundle.putParcelable("userData", dataModel?.user)
            bundle.putString("key", key)
            findNavController().navigate(
                R.id.action_nav_home_fragment_to_nav_fragment_history,
                bundle
            )

            // findNavController().navigate(R.id.action_nav_home_fragment_to_nav_fragment_history)
            //          }
        }
        binding.profile.setOnClickListener {
            val items = HashMap<String, Any>()
            val properties = Properties()
            captureAllEvents(requireContext(), Constants.PROFILE_PAGE_VIEWED, items, properties)

            val bundle = Bundle()
            bundle.putParcelable("cashoutDetails", dataModel?.cashoutDetails)
            bundle.putParcelable("cashoutAdditionalData", dataModel?.cashoutAdditionalData)
            bundle.putParcelable("userData", dataModel?.user)
            bundle.putString("key", key)
            findNavController().navigate(
                R.id.action_nav_home_fragment_to_nav_profile_fragment,
                bundle
            )
            //findNavController().navigate(R.id.action_nav_home_fragment_to_nav_profile_fragment)
        }
        binding.rlPlaceHolderDocument.setOnClickListener {
            if (!isViewBank) {
                findNavController().navigate(R.id.action_nav_home_fragment_to_nav_add_fragment)
            } else {
                findNavController().navigate(R.id.action_nav_home_fragment_to_nav_upload_document)
            }

        }
        binding.tvAttendanceMark.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_nav_earn_fragment_to_nav_cross_util_fragment)
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
                requireContext().startBlitzSurvey(requireContext(), Constants.LEARN_MORE_EVENT)
            } else {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_nav_earn_fragment_to_nav_cross_util_fragment)
            }
        }
        binding.profile.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("cashoutDetails", dataModel?.cashoutDetails)
            bundle.putParcelable("cashoutAdditionalData", dataModel?.cashoutAdditionalData)
            bundle.putParcelable("userData", dataModel?.user)
            bundle.putString("key", key)
            findNavController().navigate(
                R.id.action_nav_home_fragment_to_nav_profile_fragment,
                bundle
            )
            // findNavController().navigate(R.id.action_nav_home_fragment_to_nav_profile_fragment)
        }


    }

    private fun getDeviceInfoS() {
        viewEarnViewModel.getDeviceInfoS()
    }

    private fun setUpInstrumentation() {
        requireContext().captureEvents(
            requireContext(),
            Constants.CASHOUT_DISABLED_TAPPED,
            HashMap()
        )
    }

    private fun getRemoteConfigDataForUpdate() {
        viewEarnViewModel.getRemoteConfigDataForUpdate()
        if (BuildConfig.VERSION_NAME >= viewEarnViewModel.freshchatmodel.value?.version.toString()) {
            if (viewEarnViewModel.freshchatmodel.value?.enabled == 1) {
                binding.chat.visibility = ViewPager.VISIBLE
            } else {
                binding.chat.visibility = ViewPager.INVISIBLE
            }
        } else {
            binding.chat.visibility = ViewPager.INVISIBLE
        }
    }

    private fun setupScreenName() {
        requireContext().setupScreen(false, "Earn Fragment")
    }

    private fun alertNotification() {
        notificationAlert = MoengageNotification()
        val intentFilter = IntentFilter()
        intentFilter.addAction(requireActivity().packageName)
        intentFilter.addAction(Freshchat.FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED)
        requireActivity().registerReceiver(notificationAlert, intentFilter)
    }

    private fun getTransactionDetails(
        startPage: Int,
        perPage: Int,
        type: String,
    ) {
        val transaction = Transaction(startPage, perPage, type, listOf(), listOf())
        binding.profile.isEnabled = false
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
                                binding.rvHistory.visibility = View.VISIBLE
                                binding.rlIncentiveContainer.visibility = View.GONE
                                transactionDetailAdapter =
                                    activity?.let { it1 ->
                                        TransactionDetailAdapter(
                                            it1,
                                            transactionDetailsModels
                                        )
                                    }
                            } else {
                                binding.rlIncentiveContainer.visibility = View.VISIBLE
                                binding.rvHistory.visibility = View.GONE
                                transactionDetailAdapter =
                                    activity?.let { it1 ->
                                        TransactionDetailAdapter(
                                            it1,
                                            ArrayList()
                                        )
                                    }
                            }
                        } else {
                            binding.rlIncentiveContainer.visibility = View.VISIBLE
                            binding.rvHistory.visibility = View.GONE
                            transactionDetailAdapter =
                                activity?.let { it1 ->
                                    TransactionDetailAdapter(
                                        it1,
                                        ArrayList()
                                    )
                                }
                        }
                        binding.rvHistory.adapter = transactionDetailAdapter
                        apiEarnInfo()
                    }
                    is ApiState.Failure -> {
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }

                }
            }
        }

    }

    private fun showLoading() {
        binding.shimmerViewValue.visibility = View.VISIBLE
        binding.shimmerViewValue.startShimmerAnimation()
        binding.rlParentContainer.visibility = View.GONE
    }

    private fun setNudgeIconText() {
        binding.tvEarningAmountNudge.text =
            PrefrenceUtils.retriveData(requireContext(), Constants.NUDGE_ICON_TEXT_REMOTE_CONFIG)
    }

    @SuppressLint("SetTextI18n")
    private fun apiEarnInfo() {
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getEarnList.collect {
                when (it) {
                    is ApiState.Success -> {
                        hideLoading()
                        showLearnMoreNudge(true)
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
                        it.data.crossUtilSlots?.let { it1 ->
                            setSlotAvailableData(it1)
                        }
                        if (dataModel?.cashoutAdditionalData != null &&
                            dataModel?.cashoutAdditionalData?.CashoutExpUser == true
                        ) {
                            key = "experimentalUser"
                            Log.d(
                                "peo",
                                "apiEarnInfo: " + dataModel!!.cashoutDetails!!.holdDetails!!.isHold
                            )
                            experimentFlow()
                        } else {
                            key = "oldUser"
                            Log.d(
                                "peo",
                                "apiEarnInfo: " + dataModel!!.cashoutDetails!!.holdDetails!!.isHold
                            )
                            updateUI()
                        }
                        documentStatus(dataModel!!.documents, dataModel!!.bankAccountDetails)
                        setDetailsForMilestone()
                        if (dataModel?.attendanceDetails !== null)
                            checkAttendanceIsMarked(dataModel?.attendanceDetails?.data)
                        setInnerViewPager(dataModel)
                        setInstrumentation()
                        setInstrumentationHold()
                    }
                    is ApiState.Failure -> {
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_error_fragment)
                    }
                    ApiState.Loading -> {
                    }
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.cashoutCardRoot.visibility = View.GONE
        binding.cashLayoutExp.visibility = View.GONE
        binding.cashoutCardHoldRoot.visibility = View.GONE
        binding.llHold.visibility = View.GONE
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
                binding.cashoutCardHoldRoot.visibility = View.GONE
                binding.llHold.visibility = View.GONE
                binding.cashoutCardRoot.visibility = View.VISIBLE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                )
                    binding.cashOutConfigTxt.text =
                        viewEarnViewModel.remoteCashOutData.value?.eC?.msg
                else binding.cashOutConfigTxt.text =
                    viewEarnViewModel.remoteCashOutData.value?.eC?.hiMsg
            }
            "E" -> {

                Log.d("lklk", "updateUI: " + dataModel!!.cashoutDetails!!.holdDetails!!.isHold)
                if (dataModel!!.cashoutDetails!!.holdDetails!!.isHold == true) {
                    binding.setPriceExpHold.text = dataModel!!.cashoutDetails!!.amountEligibleLabel
                    binding.cashoutCardRoot.visibility = View.GONE
                    binding.ivIconHold.setImageResource(R.drawable.ic_cashout_hold)
                    binding.cashoutCardHoldRoot.visibility = View.VISIBLE
                    binding.llHold.visibility = View.VISIBLE
                    binding.cashOutHoldTxt.text = context?.getString(R.string.cashout_on_hold)
                    binding.cashOutHoldTxtDesc.text =
                        dataModel!!.cashoutDetails!!.holdDetails!!.companyName + " " + dataModel!!.cashoutDetails!!.holdDetails!!.holdMessage
                } else {
                    cashoutHoldCheck = false
                    binding.cashoutCardHoldRoot.visibility = View.GONE
                    binding.llHold.visibility = View.GONE
                    binding.cashoutCardRoot.visibility = View.VISIBLE
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
                        properties.addAttribute(
                            Constants.ELIGIBLE_AMOUNT,
                            dataModel!!.cashoutDetails?.amountEligible ?: 0.0
                        )
                        properties.addAttribute(
                            Constants.CASHED_FEE_PERCENT,
                            dataModel!!.cashoutDetails?.cashoutFeePercentage ?: 0.0
                        )
                        properties.addAttribute(
                            Constants.CASHED_FIXED_FEE,
                            dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0
                        )
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
                        binding.cashLayout.setOnClickListener {
                            setInstrumentationOnCLick(dataModel!!, "regular_flow")
                            dataModel!!.cashoutDetails!!.amountEligibleLabel?.let { it1 ->
                                BottomSheetV2(
                                    requireActivity(),
                                    it1,
                                    this@HomeFragment,
                                    null,
                                    dataModel!!.cashoutDetails?.amountEligible!!,
                                    dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0,
                                    dataModel!!.cashoutDetails?.isCashoutFeeEnabled!!,
                                    dataModel!!.cashoutDetails?.cashoutFeePercentage!!,
                                    0,
                                    0,
                                    orderReachToNextLevel1 = false,
                                    daysReachToNextLevel = false,
                                    0,
                                ).show()
                            }
                        }
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
                        requireContext().captureEvents(
                            requireContext(),
                            Constants.CASHOUT_INELIGIBLE_VIEWED,
                            HashMap()
                        )
                        binding.cashLayout.setOnClickListener {
                            val properties = Properties()
                            val attribute = HashMap<String, Any>()
                            captureAllEvents(
                                requireContext(),
                                Constants.CASHOUT_INELIGIBLE_TAPPED,
                                attribute,
                                properties
                            )
                            if (PrefrenceUtils.retriveLangData(
                                    requireActivity(),
                                    Constants.LANGUAGE
                                )
                                    .equals("en", ignoreCase = true)
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "Minimum amount for cashout is Rs. "
                                            + dataModel!!.cashoutDetails!!.minAmountEligible,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "कैशआउट के लिए न्यूनतम राशि रु. "
                                            + dataModel!!.cashoutDetails!!.minAmountEligible,
                                    Toast.LENGTH_SHORT
                                ).show()
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
                binding.cashoutCardHoldRoot.visibility = View.GONE
                binding.llHold.visibility = View.GONE
                binding.cashoutCardRoot.visibility = View.VISIBLE
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
                binding.cashoutCardHoldRoot.visibility = View.GONE
                binding.llHold.visibility = View.GONE
                binding.cashoutCardRoot.visibility = View.VISIBLE
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
            binding.setPrice.text = dataModel!!.cashoutDetails!!.amountEligibleLabel!!.substring(
                0,
                dataModel!!.cashoutDetails!!.amountEligibleLabel!!.indexOf(".")
            )
        } else {
            binding.setPrice.text = dataModel!!.cashoutDetails!!.amountEligibleLabel
        }


        /*  if(dataModel!!.cashoutDetails!!.holdDetails!!.isHold == true){
              binding.tvCashoutDesc.text = "Cashout on hold"
              binding.tvCashoutDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,16F)
              binding.cashOutConfigTxtDetailExp.text = dataModel!!.cashoutDetails!!.holdDetails!!.companyName +
                      dataModel!!.cashoutDetails!!.holdDetails!!.holdMessage
              binding.tvOne.text = "Available Cashout"

              binding.tvOne.setTextColor(resources.getColor(R.color.faded_grey))
              // binding.tvOne.setTextColor(context?.let { ContextCompat.getColor(it,R.color.faded_grey) })
              binding.setPriceExp.setTextColor(resources.getColor(R.color.faded_grey))
          }*/
    }

    private fun hideLoading() {
        binding.shimmerViewValue.visibility = View.GONE
        binding.shimmerViewValue.stopShimmerAnimation()
        binding.rlParentContainer.visibility = View.VISIBLE
    }

    private fun documentStatus(
        documents: EarnDataModel.Documents?,
        bankAccountDetails: EarnDataModel.BankAccountDetails?
    ) {
        var imageUrlPan: String?
        var imageUrlBackAadhaar: String?
        var imageUrlFrontAadhaar: String?
        var imageUrlFrontDL: String?
        var imageUrlBackDL: String?
        if (documents?.documents != null) {
            if (documents.documents.isNotEmpty()) {
                for (i in documents.documents.indices) {
                    if (documents.documents[i].type.equals(
                            "Aadhaar Image",
                            ignoreCase = true
                        )
                    ) {
                        if (documents.documents[i].otherSideImageUrl != null) {
                            imageUrlBackAadhaar =
                                documents.documents[i].otherSideImageUrl
                            PrefrenceUtils.insertData(
                                requireActivity(),
                                Constants.AADHARCARDBACK,
                                imageUrlBackAadhaar
                            )
                        }
                        imageUrlFrontAadhaar =
                            documents.documents[i].imageUrl
                        PrefrenceUtils.insertData(
                            requireActivity(),
                            Constants.AADHARCARDFRONT,
                            imageUrlFrontAadhaar
                        )
                        isAadhaarDocumentUpload = true
                    }
                    if (documents.documents[i].type.equals(
                            "PAN Image",
                            ignoreCase = true
                        )
                    ) {
                        imageUrlPan = documents.documents[i].imageUrl
                        PrefrenceUtils.insertData(
                            requireActivity(),
                            Constants.PANCARDIMAGE,
                            imageUrlPan
                        )
                        isPanCardDocumentUpload = true
                    }
                    if (documents.documents[i].type.equals(
                            "DL Image",
                            ignoreCase = true
                        )
                    ) {
                        if (documents.documents[i].otherSideImageUrl != null) {
                            imageUrlBackDL = documents.documents[i].otherSideImageUrl
                            PrefrenceUtils.insertData(
                                requireActivity(),
                                Constants.DL_BACK_IMG,
                                imageUrlBackDL
                            )
                        }
                        imageUrlFrontDL = documents.documents[i].imageUrl
                        PrefrenceUtils.insertData(
                            requireActivity(),
                            Constants.DL_FRONT_IMG,
                            imageUrlFrontDL
                        )
                    }
                }
            } else {
                PrefrenceUtils.insertData(
                    requireActivity(),
                    Constants.AADHARCARDBACK,
                    ""
                )
                PrefrenceUtils.insertData(
                    requireActivity(),
                    Constants.AADHARCARDFRONT,
                    ""
                )
                PrefrenceUtils.insertData(requireActivity(), Constants.PANCARDIMAGE, "")
            }
        } else {
            PrefrenceUtils.insertData(requireActivity(), Constants.AADHARCARDBACK, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.AADHARCARDFRONT, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.PANCARDIMAGE, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.DL_FRONT_IMG, "")
            PrefrenceUtils.insertData(requireActivity(), Constants.DL_BACK_IMG, "")
        }
        isViewUpload = documents?.uploaded!!
        isViewBank = bankAccountDetails?.available!!

        showDocumentNude(
            isViewUpload,
            isViewBank,
            isAadhaarDocumentUpload,
            isPanCardDocumentUpload,
            isBankDetailsUpload
        )

    }

    private fun showDocumentNude(
        isDocument: Boolean,
        isViewBank: Boolean,
        aadhaarDocumentUpload: Boolean,
        panCardDocumentUpload: Boolean,
        bankDetailsUpload: Boolean
    ) {
        if (!isDocument || !isViewBank) {
            recylerViewItemCheck = false
            //  binding.tvViewAll.visibility = View.INVISIBLE
            binding.tvViewAll.visibility = View.VISIBLE
            if (aadhaarDocumentUpload && panCardDocumentUpload && bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.GONE
            } else if (!aadhaarDocumentUpload && panCardDocumentUpload && bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text = getString(R.string.aadhaar_card_missing)
            } else if (aadhaarDocumentUpload && !panCardDocumentUpload && bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text = getString(R.string.pan_card_missing)
            } else if (aadhaarDocumentUpload && panCardDocumentUpload && !bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text = getString(R.string.bank_details_missing2)
            } else if (!aadhaarDocumentUpload && panCardDocumentUpload && !bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text = getString(R.string.aadhar_and_bank_card_missing)
            } else if (aadhaarDocumentUpload && !panCardDocumentUpload && !bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text = getString(R.string.pan_and_bank_card_missing)
            } else if (!aadhaarDocumentUpload && !panCardDocumentUpload && bankDetailsUpload) {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text = getString(R.string.documents_missing)
            } else {
                binding.rlPlaceHolderDocument.visibility = View.VISIBLE
                binding.documentPendingText.text =
                    getString(R.string.documents_bank_details_missing)
            }
        } else {
            recylerViewItemCheck = true
            binding.tvViewAll.visibility = View.VISIBLE
            binding.rlPlaceHolderDocument.visibility = View.GONE
        }
    }

    private fun setDetailsForMilestone() {
        if ((milestoneModel != null || (milestoneModel?.incentiveList!!.isNotEmpty())) &&
            transactionDetailsModels.isNotEmpty()
        ) {
            if (milestoneModel == null || (milestoneModel?.incentiveList!!.isEmpty())) {
                binding.rvEarning.visibility = View.GONE
                binding.rlHistoryContainer.visibility = View.VISIBLE
                binding.rlIncentiveContainer.visibility = View.GONE
                binding.tvNoDocument.visibility = View.GONE
            } else {
                binding.rvEarning.visibility = View.VISIBLE
                binding.rlHistoryContainer.visibility = View.GONE
                binding.rlIncentiveContainer.visibility = View.VISIBLE
                binding.tvNoDocument.visibility = View.GONE
                milestoneModel?.let { it1 -> setMilestoneTrackerAdapterData(it1) }
            }
        } else {
            binding.rlIncentiveContainer.visibility = View.GONE
            binding.rlHistoryContainer.visibility = View.GONE
            binding.tvNoDocument.visibility = View.VISIBLE
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun checkAttendanceIsMarked(attendanceDetails: EarnDataModel.AttendanceDetails.Data?) {
        val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
        if (attendanceDetails!!.isAttendanceApplicable!!) {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val currentDateTime = cal.time
            val checkInMessageReminderTime =
                timeFormat.parse(attendanceDetails.checkInMessageReminderTime!!)
            val checkOutMessageReminderTime =
                timeFormat.parse(attendanceDetails.checkOutMessageReminderTime!!)
            if (attendanceDetails.checkInRequired == true) {
                if (currentDateTime > checkInMessageReminderTime) {
                    binding.tvAttendanceMark.visibility = View.VISIBLE
                    binding.tvSlot.visibility = View.GONE
                    binding.tvAttendanceMark.text = getString(R.string.generic_text_check_in)
                } else {
                    binding.tvSlot.visibility = View.VISIBLE
                    binding.tvAttendanceMark.visibility = View.GONE
                }
            } else if (attendanceDetails.checkInRequired == false) {
                binding.tvSlot.visibility = View.VISIBLE
                binding.tvAttendanceMark.visibility = View.GONE
            }
            if (attendanceDetails.checkOutRequired == true) {
                if (attendanceDetails.checkInRequired == true) {

                } else {
                    if (currentDateTime != null) {
                        if (currentDateTime < checkOutMessageReminderTime) {
                            binding.tvSlot.visibility = View.VISIBLE
                            binding.tvAttendanceMark.visibility = View.GONE
                        } else {
                            binding.tvSlot.visibility = View.GONE
                            binding.tvAttendanceMark.visibility = View.VISIBLE
                            binding.tvAttendanceMark.text =
                                getString(R.string.generic_text_check_out)
                        }

                    }
                }
            } else {
                binding.tvSlot.visibility = View.VISIBLE
                binding.tvAttendanceMark.visibility = View.GONE
            }
        } else {
            binding.tvSlot.visibility = View.VISIBLE
            binding.tvAttendanceMark.visibility = View.GONE
            binding.tvAttendanceMark.visibility = View.GONE
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setSlotAvailableData(crossUtilSlots: List<EarnDataModel.CrossUtilSlots>) {
        val currentTime: String =
            SimpleDateFormat("hh:mm a").format(Date())
        val format = SimpleDateFormat("hh:mm a")
        if (crossUtilSlots.isNotEmpty()) {
            binding.rlSlotContainer.visibility = View.VISIBLE
            PrefrenceUtils.insertDataInBoolean(
                requireContext(),
                Constants.ISCROSSUTILSLOTAVAILABLE,
                true
            )
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
                                    if (PrefrenceUtils.retriveLangData(
                                            requireContext(),
                                            Constants.LANGUAGE
                                        ).equals("en")
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.tvSlot.text =
                            Html.fromHtml(sourceString, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        binding.tvSlot.text = Html.fromHtml(sourceString)
                    }
                } else {
                    if (sourceStringOne.isNotEmpty()) {
                        val sourceString =
                            if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                    .equals("en")
                            )
                                "<b>${"Hey!!"}</b> Your upcoming $sourceStringOne. <b><u>View All</u></b>"
                            else
                                "<b>${"नमस्ते!!"}</b> आपका आगामी ${"$sourceStringOne <b><u>View All</u></b>"}"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.tvSlot.text =
                                Html.fromHtml(sourceString, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        } else {
                            binding.tvSlot.text = Html.fromHtml(sourceString)
                        }
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
                                    if (PrefrenceUtils.retriveLangData(
                                            requireContext(),
                                            Constants.LANGUAGE
                                        ).equals("en")
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
                            if (currentDateTime!! >= startingSlotTimeOne && currentDateTime <= endingSlotTimeOne) {
                                showLearnMoreNudge(false)
                                sourceStringTwo =
                                    if (PrefrenceUtils.retriveLangData(
                                            requireContext(),
                                            Constants.LANGUAGE
                                        ).equals("en")
                                    )
                                        "${crossUtilSlots[1].companyName} " + "slots is at <b>$timeA</b>"
                                    else
                                        "${crossUtilSlots[1].companyName} के " + "स्लॉट <b>$timeA</b> पर उपलब्ध है "
                                break
                            } else if (currentDateTime <= startingSlotTimeOne || currentDateTime <= endingSlotTimeOne) {
                                showLearnMoreNudge(false)
                                sourceStringTwo =
                                    if (PrefrenceUtils.retriveLangData(
                                            requireContext(),
                                            Constants.LANGUAGE
                                        ).equals("en")
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
            PrefrenceUtils.insertDataInBoolean(
                requireContext(),
                Constants.ISCROSSUTILSLOTAVAILABLE,
                false
            )
//            showLearnMoreNudge(true)
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

    private fun showLearnMoreNudge(isEmptySlot: Boolean) {
        if (isEmptySlot) {
            showLearnMoreTxt = true
            binding.rlSlotContainer.visibility = View.VISIBLE
            val sourceString =
                if (PrefrenceUtils.retriveLangData(
                        requireContext(),
                        Constants.LANGUAGE
                    )
                        .equals("en")
                )
                    "<b>Hey, Work with multiple companies together and earn extra! <u> Learn more </u></b>"
                else
                    "<b>नमस्ते! एक साथ कई कंपनियों के साथ काम करें और अतिरिक्त कमाई करें! <u> और अधिक जानें </u></b>"
            binding.tvSlot.text = Html.fromHtml(sourceString)
        } else {
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
        binding.cashLayout.visibility = ViewPager.GONE
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
                binding.cashoutTxt.visibility = ViewPager.VISIBLE
                binding.cashLayoutExp.visibility = ViewPager.GONE
                binding.cashoutCardHoldRoot.visibility = View.GONE
                binding.llHold.visibility = View.GONE
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
                if (dataModel!!.cashoutDetails!!.holdDetails!!.isHold == true) {
                    binding.setPriceExpHold.text = dataModel!!.cashoutDetails!!.amountEligibleLabel
                    binding.cashoutCardRoot.visibility = View.GONE
                    binding.cashoutCardHoldRoot.visibility = View.VISIBLE
                    binding.llHold.visibility = View.VISIBLE
                    binding.ivIconHold.setImageResource(R.drawable.ic_cashout_hold)
                    binding.cashOutHoldTxt.text = context?.getString(R.string.cashout_on_hold)
                    binding.cashOutHoldTxtDesc.text =
                        dataModel!!.cashoutDetails!!.holdDetails!!.companyName + " " + dataModel!!.cashoutDetails!!.holdDetails!!.holdMessage
                } else {
                    binding.cashoutCardHoldRoot.visibility = View.GONE
                    binding.llHold.visibility = View.GONE
                    eligiblityCheck = true
                    binding.cashoutTxt.visibility = View.GONE

                    Thread {
                        var progress = 1
                        while (progress <= (dataModel!!.cashoutAdditionalData?.cashoutPercentage
                                ?: 1)
                        ) {
                            try {
                                Thread.sleep(10) // 5 seconds
                                binding.cashoutProgressBar.progress = progress
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            progress += 1
                        }
                    }.start()
                    binding.cashoutProgressBar.progressDrawable =
                        AppCompatResources.getDrawable(requireContext(), R.drawable.item_progress)
                    if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                            .equals("en", ignoreCase = true)
                    ) {
                        binding.tvCashoutDesc.text =
                            "Your Cashout Limit is ${dataModel!!.cashoutAdditionalData?.cashoutPercentage}%"
                    } else {
                        binding.tvCashoutDesc.text =
                            "आपकी कैशआउट सीमा ${dataModel!!.cashoutAdditionalData?.cashoutPercentage}% है"

                    }


                    if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                            .equals("en", ignoreCase = true)
                    ) {
                        if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                            dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                        ) {
                            binding.cashOutConfigTxtDetailExp.text =
                                "Complete ${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} " +
                                        " trips in ${
                                            dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel
                                        } days to increase limit to ${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}%"
                        } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                            dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                        ) {
                            binding.cashOutConfigTxtDetailExp.text =
                                "Complete ${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} " +
                                        "trips with Mitra to unlock " +
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
                            binding.cashOutConfigTxtDetailExp.text =
                                "You are now eligible for maximum cashout!"
                        }
                    } else {
                        if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                            dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                        ) {
                            binding.cashOutConfigTxtDetailExp.text =
                                "${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} ट्रिप ${dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel} दिन में पूरा करके ${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% की लिमिट बढ़ा सकते हैं "
                        } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == true &&
                            dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                        ) {
                            binding.cashOutConfigTxtDetailExp.text =
                                "${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% कैशआउट अनलॉक करने के लिए मित्रा में ${dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel} " + "और यात्राएं पूरी करे"
                        } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == false &&
                            dataModel?.cashoutAdditionalData?.daysReachToNextLevel == false
                        ) {
                            binding.cashOutConfigTxtDetailExp.text =
                                "अब आप अधिकतम कैशआउट के पात्र हैं!"
                        } else if (dataModel?.cashoutAdditionalData?.orderReachToNextLevel == false &&
                            dataModel?.cashoutAdditionalData?.daysReachToNextLevel == true
                        ) {
                            binding.cashOutConfigTxtDetailExp.text =
                                " ${dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage}% कैशआउट अनलॉक करने के लिए मित्रा में ${dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel} और दिन काम करना जारी रखें "
                        }
                    }

                    if (dataModel!!.cashoutDetails?.enabled!! &&
                        dataModel!!.cashoutDetails?.amountEligible!! >=
                        dataModel!!.cashoutDetails?.minAmountEligible!!
                    ) {
                        binding.cashLayoutExp.visibility = View.VISIBLE
                        binding.cashLayoutExp.setOnClickListener {
                            setInstrumentationOnCLick(dataModel!!, "Cashout Levels Flow")
                            dataModel!!.cashoutDetails!!.amountEligibleLabel?.let { it1 ->
                                BottomSheetCashOutPurpose(
                                    requireActivity(),
                                    dataModel?.cashoutAdditionalData?.userLevel,
                                    it1,
                                    this@HomeFragment,
                                    null,
                                    dataModel!!.cashoutDetails?.amountEligible!!,
                                    dataModel!!.cashoutDetails?.cashoutFixedFee ?: 0,
                                    dataModel!!.cashoutDetails?.isCashoutFeeEnabled!!,
                                    dataModel!!.cashoutDetails?.cashoutFeePercentage!!,
                                    dataModel?.cashoutAdditionalData?.orderRequiredToReachToNextLevel,
                                    dataModel?.cashoutAdditionalData?.daysRequiredTOReachToNextLevel,
                                    dataModel?.cashoutAdditionalData?.cashoutEligibilityStatus,
                                    dataModel?.cashoutAdditionalData?.orderReachToNextLevel,
                                    dataModel?.cashoutAdditionalData?.daysReachToNextLevel,
                                    dataModel?.cashoutAdditionalData?.cashoutNextLevelPercentage
                                ).show()
                            }
                        }
                    } else if (!dataModel!!.cashoutDetails?.enabled!! &&
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
                    } else if (dataModel!!.cashoutDetails?.enabled!! &&
                        dataModel!!.cashoutDetails?.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                        dataModel!!.cashoutDetails!!.amountEligible != 0.0
                    ) {
                        val property = Properties()
                        val attribute = HashMap<String, Any>()
                        requireContext().captureEvents(
                            requireContext(),
                            Constants.CASHOUT_INELIGIBLE_VIEWED,
                            HashMap()
                        )
                        binding.cashLayoutExp.setOnClickListener {
                            captureAllEvents(
                                requireContext(),
                                Constants.CASHOUT_INELIGIBLE_TAPPED,
                                attribute,
                                property
                            )
                            if (PrefrenceUtils.retriveLangData(
                                    requireActivity(),
                                    Constants.LANGUAGE
                                )
                                    .equals("en", ignoreCase = true)
                            ) Toast.makeText(
                                requireContext(),
                                "Minimum amount for cashout is Rs. " +
                                        dataModel!!.cashoutDetails!!.minAmountEligible,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            else
                                Toast.makeText(
                                    requireContext(),
                                    "कैशआउट के लिए न्यूनतम राशि रु. " +
                                            dataModel!!.cashoutDetails!!.minAmountEligible,
                                    Toast.LENGTH_SHORT
                                ).show()

                        }
                        binding.cashLayoutExp.visibility = View.VISIBLE
                        binding.imgGreyExp.visibility = View.VISIBLE
                    } else if (dataModel!!.cashoutDetails!!.enabled!! &&
                        dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                        dataModel!!.cashoutDetails!!.amountEligible == 0.0
                    ) {
                        binding.cashLayoutExp.visibility = View.GONE
                    } else if (!dataModel!!.cashoutDetails!!.enabled!! &&
                        dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                        dataModel!!.cashoutDetails!!.amountEligible == 0.0
                    ) {
                        binding.cashLayoutExp.visibility = View.GONE
                    } else if (dataModel!!.cashoutDetails!!.enabled!! &&
                        dataModel!!.cashoutDetails!!.amountEligible!! < dataModel!!.cashoutDetails!!.minAmountEligible!! &&
                        dataModel!!.cashoutDetails!!.minAmountEligible!! < 0.0
                    ) {
                        binding.cashLayoutExp.visibility = View.GONE
                    }
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
                binding.cashoutCardHoldRoot.visibility = View.GONE
                binding.llHold.visibility = View.GONE
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
                binding.cashoutCardHoldRoot.visibility = View.GONE
                binding.llHold.visibility = View.GONE
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
        properties.addAttribute(
            Constants.ELIGIBLE_AMOUNT,
            dataModel.cashoutDetails?.amountEligible
        )
        properties.addAttribute(
            Constants.CASHED_FEE_PERCENT,
            dataModel.cashoutDetails?.cashoutFeePercentage ?: 0.0
        )
        properties.addAttribute(
            Constants.CASHED_FIXED_FEE,
            dataModel.cashoutDetails?.cashoutFixedFee ?: 0
        )
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
                        binding.profile.isEnabled = true
                        //Toast.makeText(requireContext(), "DSUCCESS", Toast.LENGTH_SHORT).show()

                        PrefrenceUtils.insertData(
                            requireContext(),
                            Constants.USERID,
                            dataModel.user.id
                        )
                        // hideInternalLoading()
                        binding.viewpageInner.visibility = View.VISIBLE
                        dynamicBannerList.clear()
                        entryPointList.clear()

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
                            getString(R.string.this_week_s_earnings),
                            dataModel.currentEarnings!!.amount,
                            dataModel.jobDetails.companyIcon
                        )
                        if (it.data.dynamicBanner != null) {
                            for (item in it.data.dynamicBanner) {
                                dynamicBannerList.add(item!!)
                            }
                        }
                        if (it.data.dynamicEntryPoint != null) {
                            for (item in it.data.dynamicEntryPoint) {
                                entryPointList.add(item!!)
                            }
                        }
                        dynamicBannerList.add(0, banner)
                        setViewPaggerInsertData(dynamicBannerList)

                        if (entryPointList.size > 0) {
                            if (entryPointList.size <= 4) {
                                binding.dynamicBannerViewpager2.visibility = View.VISIBLE
                                dynamicEntryPointAdapter =
                                    context?.let { it1 ->
                                        DynamicEntryPointAdapter(
                                            it1,
                                            entryPointList
                                        )
                                    }!!

                                binding.viewpageInner2.adapter = dynamicEntryPointAdapter
                            } else {
                                binding.dynamicBannerViewpager2.visibility = View.GONE
                            }
                        } else {
                            binding.dynamicBannerViewpager2.visibility = View.GONE
                        }
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
        binding.viewpageInner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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

    private fun setMilestoneTrackerAdapterData(listData: EarnDataModel.IncentiveStructures) {
        binding.rvEarning.adapter =
            listData?.let {
                MilestoneNewTrackerAdapter(requireActivity(), this, it)
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
            properties.addAttribute(
                Constants.MITRA_BALANCE,
                dataModel?.walletDetails?.walletBalance.toString()
            )
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
                        if (PrefrenceUtils.retriveDataInBoolean(
                                context,
                                Constants.ISFEEDBACKSESSION
                            )
                        ) {
                            if (viewEarnViewModel.trigger.value?.trigger?.get(i)?.trigger_event.equals(
                                    "earn_page_viewed"
                                )
                            ) {
                                FeedbackBottomsheet(requireContext(), "earn_page_viewed").show()
                                PrefrenceUtils.insertDataInBoolean(
                                    context,
                                    Constants.ISFEEDBACKSESSION,
                                    false
                                )
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

    private fun setInstrumentationHold() {
        val properties = Properties()
        val attribute = HashMap<String, Any>()

        properties.addAttribute(
            Constants.STATUS,
            dataModel?.cashoutDetails!!.holdDetails!!.isHold
        )
        attribute[Constants.STATUS] = dataModel?.cashoutDetails!!.holdDetails!!.isHold.toString()
        captureAllEvents(
            requireContext(),
            Constants.COD_PAYMENT_DONE,
            attribute,
            properties
        )
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
                if (earnDataModel.documents.documents[i].type.equals(
                        "Aadhaar Image",
                        ignoreCase = true
                    )
                ) {
                    if (earnDataModel.documents.documents[i].otherSideImageUrl != null) {
                        imageUrlBackAadhar = earnDataModel.documents.documents[i].otherSideImageUrl
                        PrefrenceUtils.insertData(
                            requireActivity(),
                            Constants.AADHARCARDBACK,
                            imageUrlBackAadhar
                        )
                    }
                    imageUrlFrontAadhar = earnDataModel.documents.documents[i].imageUrl
                    PrefrenceUtils.insertData(
                        requireActivity(),
                        Constants.AADHARCARDFRONT,
                        imageUrlFrontAadhar
                    )
                    isAadhaarDocumentUpload = true
                }
                if (earnDataModel.documents.documents[i].type.equals(
                        "PAN Image",
                        ignoreCase = true
                    )
                ) {
                    imageUrlPan = earnDataModel.documents.documents[i].imageUrl
                    PrefrenceUtils.insertData(
                        requireActivity(), Constants.PANCARDIMAGE, imageUrlPan
                    )
                    isPanCardDocumentUpload = true
                }

                if (earnDataModel.documents.documents[i].type.equals(
                        "DL Image",
                        ignoreCase = true
                    )
                ) {
                    if (earnDataModel.documents.documents[i].otherSideImageUrl != null) {
                        imageUrlBackDL = earnDataModel.documents.documents[i].otherSideImageUrl
                        PrefrenceUtils.insertData(
                            requireActivity(),
                            Constants.DL_BACK_IMG,
                            imageUrlBackDL
                        )
                    }
                    imageUrlFrontDL = earnDataModel.documents.documents[i].imageUrl
                    PrefrenceUtils.insertData(
                        requireActivity(),
                        Constants.DL_FRONT_IMG,
                        imageUrlFrontDL
                    )
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
                                binding.chatCount.visibility = ViewPager.GONE
                            } else {
                                binding.chatCount.visibility = ViewPager.VISIBLE
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

    override fun coachmarkCallback() {
        startCoachMarks()
    }

    private fun startCoachMarks() {
        // We have a sequence of targets, so lets build it!
        val sequence: TapTargetSequence = TapTargetSequence(activity)
            .targets(
                TapTarget.forView(
                    binding.ivScooterContainer,
                    resources.getString(R.string.work_for_mutliple_companies_together_with_mitra_earn_extra_money)
                )
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
                TapTarget.forView(
                    binding.profile,
                    resources.getString(R.string.profile_coach_mark_desc)
                )
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
        spannedDesc.setSpan(
            UnderlineSpan(),
            spannedDesc.length - "TapTargetView".length,
            spannedDesc.length,
            0
        )

        sequence.start()
    }

    override fun onClick(position: Int) {
        val bundle = Bundle()
        bundle.putInt("position", position)
        Navigation.findNavController(binding.root)
            .navigate(R.id.nav_rate_card_new, bundle)
    }

}