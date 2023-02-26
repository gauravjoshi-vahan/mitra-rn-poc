package com.vahan.mitra_playstore.view.profile.view.ui


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentProfileBinding
import com.vahan.mitra_playstore.models.*
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.LocaleManager
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.BaseApplication
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet
import com.vahan.mitra_playstore.view.profile.adapter.HelpAndSettingAdapter


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var modelOtherSetting: OtherSettingModel
    private var data = ArrayList<OtherSettingModel.OtherSetting>()
    private var viewSharedViewModel: SharedViewModel? = null
    private var isViewUpload = false
    private var isViewBank = false
    private var isAadhaarDocumentUpload = false
    private var isPanCardDocumentUpload = false
    private var dialogLoader: Dialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private var language = ""
    private var languageHeader = ""
    private var mContext : Context? = null
    private lateinit var fa: FirebaseAnalytics
    lateinit var alertDialog: AlertDialog
    private val listOfAllSMS: MutableList<LocaInfoSMSModel> = java.util.ArrayList()
    private lateinit var freschatModel: FreshchatEnableModel
    private var trigger: FeedbackTriggersModel? = null
    private var cashOutDetails: EarnDataModel.CashOutDetails? = null
    private var cashoutAdditionalData: EarnDataModel.CashoutAdditionalData? = null
    lateinit var user: EarnDataModel.User
    var key:String = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        if(requireArguments()!=null) {
            cashOutDetails = requireArguments().getParcelable("cashoutDetails")!!
            cashoutAdditionalData = requireArguments().getParcelable("cashoutAdditionalData")
        }
        user = requireArguments().getParcelable("userData")!!
        key = requireArguments().getString("key")!!
        setVersionName()
        fa = FirebaseAnalytics.getInstance(mContext!!)
        viewSharedViewModel = ViewModelProvider.NewInstanceFactory().create(SharedViewModel::class.java)
        initLoader(mContext)
        setSettingData()
        updateUi()
        checkForSessions()
        setDataBasedOnApi()
        onClickListener()
        dataFromRemoteConfig()
    }

    private fun setSettingData() {
        data = ArrayList()
        data.clear()

        val otherSetting = OtherSettingModel.OtherSetting()
        otherSetting.url = ""
        otherSetting.hiKey = "मदद समर्थन"
        otherSetting.key = "Help & Support"
        otherSetting.teKey = "సహాయం & మద్దతు"
        otherSetting.icon = R.drawable.ic_support
        otherSetting.status = ""
        data.add(otherSetting)

        val otherSettingLang = OtherSettingModel.OtherSetting()
        otherSettingLang.url = ""
        otherSettingLang.hiKey = "भाषा संपादित करें"
        otherSettingLang.key = "Edit Language"
        otherSettingLang.teKey = "భాషను సవరించండి"
        otherSettingLang.icon = R.drawable.ic_lang
        if (PrefrenceUtils.retriveLangData(mContext, Constants.LANGUAGE)
                .equals("en", ignoreCase = true)
        ) {
            otherSettingLang.status = "ENGLISH"
        } else if(PrefrenceUtils.retriveLangData(mContext, Constants.LANGUAGE)
                .equals("hi", ignoreCase = true)) {
            otherSettingLang.status = "हिंदी"
        }else{
            otherSettingLang.status = "తెలుగు"
        }
        data.add(otherSettingLang)

        val otherSettingOne = OtherSettingModel.OtherSetting()
        otherSettingOne.url = "https://mitra.vahan.co/privacy-policy"
        otherSettingOne.hiKey = "गोपनीयता नीति"
        otherSettingOne.key = "Privacy Policy"
        otherSettingOne.teKey = "గోప్యతా విధానం"
        otherSettingOne.icon = R.drawable.ic_privacy_policy
        otherSettingOne.status = ""
        data.add(otherSettingOne)

        val otherSettingTwo = OtherSettingModel.OtherSetting()
        otherSettingTwo.url = "https://mitra.vahan.co/terms-of-use"
        otherSettingTwo.hiKey = "नियम एवं शर्तें"
        otherSettingTwo.key = "Terms & Conditions"
        otherSettingTwo.teKey = "నిబంధనలు & షరతులు"
        otherSettingTwo.icon = R.drawable.ic_terms_and_conditions
        otherSettingTwo.status = ""
        data.add(otherSettingTwo)

        val otherSettingThree = OtherSettingModel.OtherSetting()
        otherSettingThree.url = ""
        otherSettingThree.hiKey = "लॉग आउट"
        otherSettingThree.key = "Logout"
        otherSettingThree.teKey = "లాగ్అవుట్"
        otherSettingThree.icon = R.drawable.ic_logout
        otherSettingThree.status = ""
        data.add(otherSettingThree)
    }

    @SuppressLint("SetTextI18n")
    private fun setVersionName() {
        if(BuildConfig.DEBUG) {
            if (Constants.BASE_URL.contains("stg")) {
                binding.appVersion.text =
                    getString(R.string.app_version) + " " + BuildConfig.VERSION_NAME + " STG"
            }else
                binding.appVersion.text =
                    getString(R.string.app_version) + " " + BuildConfig.VERSION_NAME + " PROD"
        }else{
            if (Constants.BASE_URL.contains("stg")) {
                binding.appVersion.text =
                    getString(R.string.app_version) + " " + BuildConfig.VERSION_NAME + " STG"
            }else
                binding.appVersion.text =
                    getString(R.string.app_version) + " " + BuildConfig.VERSION_NAME
        }
    }

    private fun dataFromRemoteConfig() {
        trigger = Gson().fromJson(
            PrefrenceUtils.retriveData(context, Constants.FEEDBACK_TRIGGERS_REMOTE),
            FeedbackTriggersModel::class.java
        )
        freschatModel = Gson()
            .fromJson(
                PrefrenceUtils.retriveData(
                    activity,
                    Constants.FRESHCHAT_ENABLE_CONDITION_REMOTE_CONFIG
                ),
                FreshchatEnableModel::class.java
            )
        if (freschatModel!=null){
            if (BuildConfig.VERSION_NAME >= freschatModel.version.toString()){
                if (freschatModel.enabled == 1){
                    binding.faqChat.visibility = ViewPager.VISIBLE
                }else{
                    binding.faqChat.visibility = ViewPager.GONE
                    data.removeAt(0)
                    binding.rvHelpSetting.adapter?.notifyDataSetChanged()
                }
            }else{
                binding.faqChat.visibility = ViewPager.GONE
                data.removeAt(0)
                binding.rvHelpSetting.adapter?.notifyDataSetChanged()
            }

        }
    }

    private fun showView() {
        if(isViewUpload){
            binding.documentPendingText.visibility = View.GONE
        }
        else{
            binding.documentPendingText.visibility = View.VISIBLE
        }
        binding.helpAndSettingsTv.visibility = View.VISIBLE
        binding.rvHelpSetting.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun setDataBasedOnApi() {
        binding.tvProfileName.text = PrefrenceUtils.retriveData(requireContext(),Constants.USERNAME)
        binding.tvProfileMobile.text = PrefrenceUtils.retriveData(requireContext(),Constants.PHONENUMBER)
        binding.tvMitraBalanceAmt.text = PrefrenceUtils.retriveData(requireContext(),Constants.MITRA_BALANCE)
        binding.tvWeeklyBalanceAmt.text = PrefrenceUtils.retriveData(requireContext(),Constants.WEEKLY_EARNINGS)
        isViewUpload =  PrefrenceUtils.retriveDataInBoolean(requireActivity(), Constants.CHECK_UPLOAD_STATUS)
        if (PrefrenceUtils.retriveDataInBoolean(requireActivity(), Constants.CHECK_BANK_STATUS)) {
            binding.bankdetailsPendingText.visibility = View.GONE
            binding.bankdetailsPendingText.text = getString(R.string.documents_missing)
            isViewBank = true
        } else {
            binding.bankdetailsPendingText.visibility = View.VISIBLE
            isViewBank = false
        }
        showView()
        showPopFeedaback()
    }

    private fun showPopFeedaback() {
        handler.postDelayed(Runnable {
            if (trigger != null && context !== null) {
                for (i in trigger!!.trigger!!.indices) {
                    if (!PrefrenceUtils.retriveDataInBoolean(
                            context,
                            Constants.PROFILE_PAGE_VIEWED
                        )
                    ) {
                        if (PrefrenceUtils.retriveDataInBoolean(
                                context,
                                Constants.ISFEEDBACKSESSION
                            )
                        ) {
                            if (trigger!!.trigger!![i].trigger_event.equals(
                                    Constants.PROFILE_PAGE_VIEWED,
                                    ignoreCase = true
                                )
                            ) {
                                FeedbackBottomsheet(requireContext(), Constants.PROFILE).show()
                                PrefrenceUtils.insertDataInBoolean(
                                    context,
                                    Constants.ISFEEDBACKSESSION,
                                    false
                                )
                            }
                        } else {
                            return@Runnable
                        }
                    } else {
                        return@Runnable
                    }
                }
            }
        }, 3000)
    }

    private fun updateUi() {
        if(PrefrenceUtils.retriveDataInBoolean(requireContext(),Constants.ISCROSSUTILSLOTAVAILABLE)){
            binding.crossUtilContainer.visibility = View.VISIBLE
        }else{
            binding.crossUtilContainer.visibility = View.GONE
        }
        binding.rvHelpSetting.adapter = HelpAndSettingAdapter(requireActivity(), data)
    }

    private fun onClickListener() {
        binding.ivBackButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.nav_home_fragment)
        }
        binding.rvMitraBalanceContainer.setOnClickListener {
            val properties = Properties()
            val data = HashMap<String, String>()
            data[Constants.REDIRECTION_URL] = mContext!!.getString(R.string.history_page)
            data[Constants.POSITION] = "0"
            properties.addAttribute(Constants.REDIRECTION_URL, mContext!!.getString(R.string.history_page))
            properties.addAttribute(Constants.POSITION, "0")
            MoEHelper.getInstance(mContext!!)
                    .trackEvent(Constants.BANNER_TAPPED, properties)
            UXCam.logEvent(Constants.BANNER_TAPPED, data)
            BlitzLlamaSDK.getSdkManager(mContext!!).triggerEvent(Constants.BANNER_TAPPED)

            val bundle = Bundle()
            bundle.putParcelable("cashoutDetails", cashOutDetails)
            bundle.putParcelable("cashoutAdditionalData", cashoutAdditionalData)
            bundle.putParcelable("userData", user)
            bundle.putString("key",key)

            findNavController().navigate(R.id.nav_fragment_history,bundle)
        }
        binding.crossUtilContainer.setOnClickListener {
            findNavController().navigate(R.id.nav_cross_util_fragment)
        }


        binding.weeklyEarningsContainer.setOnClickListener {
            val properties = Properties()
            val attribute = java.util.HashMap<String, Any>()
            captureAllEvents(
                requireContext(),
                Constants.WEEKLY_PAGE_PAYOUT_CLICKED,
                attribute,
                properties
            )
            findNavController().navigate(R.id.weekly_earnings_fragment)
        }
        binding.faqChat.setOnClickListener(View.OnClickListener {
            val tags: MutableList<String> = java.util.ArrayList()
            tags.add("newFaq")
            val faqOptions = FaqOptions()
                .showFaqCategoriesAsGrid(false)
                .showContactUsOnAppBar(false)
                .showContactUsOnFaqScreens(true)
                .showContactUsOnFaqNotHelpful(true)
                .filterContactUsByTags(tags, "Test 2") //tags, filtered screen title
            Freshchat.showFAQs(mContext!!, faqOptions)

        })
        binding.ivContainerUploadDocument.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.nav_upload_fragment)
        }
        binding.ivContainerAddBank.setOnClickListener {
            //setFragment(R.id.child_container, getChildFragmentManager(), new UploadDocumentFragment(), false);
            if (isViewBank) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.nav_fragment_addBank_view)
            } else Navigation.findNavController(binding.root).navigate(R.id.nav_fragment_add_bank)
        }
        binding.transactionContainer.setOnClickListener { view: View? ->
        //    if (isViewBank && isViewUpload) {
            val bundle = Bundle()
            bundle.putParcelable("cashoutDetails", cashOutDetails)
            bundle.putParcelable("cashoutAdditionalData", cashoutAdditionalData)
            bundle.putParcelable("userData", user)
            bundle.putString("key",key)
            findNavController().navigate(R.id.nav_fragment_history, bundle)
        }
        binding.viewPayslipContainer.setOnClickListener { view: View? ->
            Navigation.findNavController(
                binding.root
            ).navigate(R.id.nav_payslip)
        }
//        binding.ivContainerLanguage.setOnClickListener { changeLanguage() }
        binding.viewReferralContainer.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.nav_referral_home_fragment)
        }
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }



    private fun setInstrumentation() {
        val bundleOne = Bundle()
        val propertiesOne = Properties()
        val data = HashMap<String?, String?>()
        data[Constants.SELECTED_LANGUAGE] = language
        bundleOne.putString(Constants.SELECTED_LANGUAGE, language)
        propertiesOne.addAttribute(Constants.SELECTED_LANGUAGE, language)
        MoEHelper.getInstance(mContext!!).trackEvent(Constants.LANGUAGE_CHANGED, propertiesOne)
        fa.logEvent(Constants.LANGUAGE_CHANGED, bundleOne)
        UXCam.logEvent(Constants.LANGUAGE_CHANGED, data)
        val attribute = HashMap<String, Any>()
        attribute[Constants.SELECTED_LANGUAGE] = language
        Freshchat.trackEvent(requireContext(), Constants.LANGUAGE_CHANGED, attribute)
    }

    private fun setLocaleLanguage(lang: String) {
        LocaleManager.setNewLocale(BaseApplication.context, lang)
    }


    fun initLoader(context: Context?) {
        dialogLoader = Dialog(requireContext())
        dialogLoader!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader!!.setCancelable(false)
        dialogLoader!!.setContentView(R.layout.layout_loader)
        val imageViewAnimation = dialogLoader!!.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }

    override fun onPause() {
        super.onPause()
        try {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        } catch (e: Exception) {

        }

    }

    override fun onResume() {
        super.onResume()
        val properties = Properties()
        val bundle = Bundle()
        MoEHelper.getInstance(mContext!!).trackEvent(Constants.PROFILE_PAGE_VIEWED, properties)
        fa.logEvent(Constants.PROFILE_PAGE_VIEWED, bundle)
        UXCam.logEvent(Constants.PROFILE_PAGE_VIEWED)
        BlitzLlamaSDK.getSdkManager(context).triggerEvent(Constants.PROFILE_PAGE_VIEWED)
        val attribute = HashMap<String, Any>()
        attribute[Constants.SELECTED_LANGUAGE] = language
        Freshchat.trackEvent(requireContext(), Constants.PROFILE_PAGE_VIEWED, attribute)
    }

    private fun checkForSessions() {
        if (PrefrenceUtils.retriveDataInBoolean(context, Constants.CHECKFORFIRSTTIMESLOTSCREEN)) {
            PrefrenceUtils.insertDataInBoolean(context, Constants.CHECKFORFIRSTTIMESLOTSCREEN, false)
            startCoachMarks()
        } else {
            if (!PrefrenceUtils.retriveData(context, Constants.SHOWN_VERSION).equals(BuildConfig.VERSION_NAME, ignoreCase = true)
            ) {
                startCoachMarks()
            }
        }
    }

    private fun startCoachMarks() {
        // We have a sequence of targets, so lets build it!
        val sequence: TapTargetSequence = TapTargetSequence(activity)
                .targets(
                        TapTarget.forView(binding.tvMitraBalanceAmt,
                                resources.getString(R.string.mitra_balance_coach_mark_desc))
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
                                .id(1)
                )
                .listener(object : TapTargetSequence.Listener {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    override fun onSequenceFinish() {
                    }

                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id())
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {}
                })

        val spannedDesc = SpannableString("This is the sample app for TapTargetView")
        spannedDesc.setSpan(
                UnderlineSpan(),
                spannedDesc.length - "TapTargetView".length,
                spannedDesc.length,
                0)

        sequence.start()
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

}