package com.vahan.mitra_playstore.view.loan.view.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.RequestBuilder
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentLoanInfoBinding
import com.vahan.mitra_playstore.models.FeedbackTriggersModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.ExperimentActivity
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet


class LoanInfoFragment : Fragment() {

    private var siplyUrl: String? = ""
    private var amount: Int? = 0
    private var purpose: String? = ""
    private var interest: Double? = 0.0
    private var emi: Double? = 0.0
    private var period: String? = ""
    private var duration: Double? = 0.0
    private lateinit var binding: FragmentLoanInfoBinding
    private lateinit var dialogLoader: Dialog
    private var fa: FirebaseAnalytics? = null
    private var trigger: FeedbackTriggersModel? = null
    val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_loan_info,
            container, false
        )
        initLoader()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.arguments?.apply {
            amount = this.getInt(Constants.AMOUNT)
            purpose = this.getString(Constants.PURPOSE)
            siplyUrl = this.getString(Constants.SIPLY)
            interest = this.getDouble(Constants.INTEREST)
            emi = this.getDouble(Constants.EMI)
            period = this.getString(Constants.PERIOD)
            duration = this.getDouble(Constants.DURATION)
        }
        initView()
    }

    private fun initView() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.LOAN_INFO_FRAGMENT)
        fa = FirebaseAnalytics.getInstance(requireActivity())
        postLoanSummary()
        clickListener()
        getDataFromRemoteConfig()
    }

    private fun getDataFromRemoteConfig() {
        trigger = Gson()
            .fromJson(
                PrefrenceUtils.retriveData(
                    activity,
                    Constants.FEEDBACK_TRIGGERS_REMOTE
                ),
                FeedbackTriggersModel::class.java
            )
    }


    private fun clickListener() {
        binding.cbAcceptCondition.setOnClickListener {
            if (binding.cbAcceptCondition.isChecked) {
                binding.applyButton.isEnabled = true
                binding.applyButton.isClickable = true
                binding.applyButton.setBackgroundResource(R.drawable.curver_corner_12)
            } else {
                binding.applyButton.isEnabled = false
                binding.applyButton.isClickable = false
                binding.applyButton.setBackgroundResource(R.drawable.curver_corner_12_disable)
            }
        }



        binding.tvAcceptCondition.setOnClickListener {
            requireActivity().startActivity(
                Intent(activity, ExperimentActivity::class.java)
                    .putExtra(Constants._TYPE, "Term & Conditions")
                    .putExtra(
                        Constants.LINK, siplyUrl
                    )
            )

        }

        binding.applyButton.setOnClickListener {
            applyLoan()
        }

        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


    private fun applyLoan() {
        dialogLoader.show()
        val viewSharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.AMOUNT, amount)
        jsonObject.addProperty(Constants.PURPOSE, purpose)
        jsonObject.addProperty(Constants.DURATION, duration)
        jsonObject.addProperty(Constants.INTEREST, interest)
        jsonObject.addProperty(Constants.PERIOD, period)
        viewSharedViewModel.postApplyLoan(jsonObject).observe(viewLifecycleOwner) {
            when (it.statusCode) {
                500 -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.nav_error_fragment)
                    dialogLoader.dismiss()
                }
                in 400..499 -> {
                    Toast.makeText(
                        requireActivity(),
                        it.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    dialogLoader.dismiss()
                }
                else -> {
                    val bundleFirebase = Bundle()
                    bundleFirebase.putString(Constants.AMOUNT, amount.toString())
                    bundleFirebase.putString(Constants.PURPOSE, purpose)
                    bundleFirebase.putString(Constants.DURATION, duration.toString())
                    fa?.logEvent(Constants.LOAN_APPLIED, bundleFirebase)
                    val properties = Properties()
                    properties.addAttribute(Constants.AMOUNT, amount.toString())
                    properties.addAttribute(Constants.PURPOSE, purpose)
                    properties.addAttribute(Constants.DURATION, duration.toString())
                    val attribute = java.util.HashMap<String, Any>()
                    attribute[Constants.AMOUNT] = amount.toString()
                    attribute[Constants.PURPOSE] = purpose!!
                    attribute[Constants.DURATION] = duration.toString()
                    Freshchat.trackEvent(requireContext(), Constants.LOAN_APPLIED, attribute)
                    MoEHelper.getInstance(requireActivity())
                        .trackEvent(Constants.LOAN_APPLIED, properties)
                    var message: String? = ""

                    message = getString(R.string.loan_applied_successfully)

                    val bundle = bundleOf(Constants.MESSAGE to message)
                    if (it.success == true) {
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_fragment_payment_sucessful, bundle)
                        dialogLoader.dismiss()
                    } else {
                        dialogLoader.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
        handler.postDelayed({
            if (trigger != null) {
                for (i in 0 until trigger?.trigger?.size!!) {
                    if (!PrefrenceUtils.retriveDataInBoolean(context, Constants.LOAN_APPLIED)) {
                        if (PrefrenceUtils.retriveDataInBoolean(
                                context,
                                Constants.ISFEEDBACKSESSION
                            )
                        ) {
                            if (trigger?.trigger?.get(i)?.trigger_event.equals(Constants.LOAN_APPLIED)) {
                                FeedbackBottomsheet(requireContext(), Constants.LOAN_APPLIED).show()
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

    @SuppressLint("SetTextI18n")
    private fun postLoanSummary() {
        val viewSharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.AMOUNT, amount)
        jsonObject.addProperty(Constants.PURPOSE, purpose)
        jsonObject.addProperty(Constants.DURATION, duration)
        jsonObject.addProperty(Constants.EMI, emi)
        jsonObject.addProperty(Constants.PERIOD, period)
        jsonObject.addProperty(Constants.EMI_PERIOD, period)
        viewSharedViewModel.postLoanApiInfo(jsonObject).observe(viewLifecycleOwner) {
            when (it.statusCode) {
                500 -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.nav_error_fragment)
                }
                in 400..499 -> {
                    Toast.makeText(
                        requireActivity(),
                        R.string.something_went_wrong,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                else -> {
                    val bundle = Bundle()
                    bundle.putString(Constants.TENURE, duration.toString())
                    bundle.putString(Constants.INTEREST_RATE, "2")
                    bundle.putString(Constants.AMOUNT, amount.toString())
                    bundle.putString(Constants.EMI, emi.toString())
                    fa?.logEvent(Constants.LOAN_SUMMARY_VIEWED, bundle)
                    BlitzLlamaSDK.getSdkManager(requireContext())
                        .triggerEvent(Constants.LOAN_SUMMARY_VIEWED)
                    val properties = Properties()
                    properties.addAttribute(Constants.TENURE, duration.toString())
                    properties.addAttribute(Constants.INTEREST_RATE, "2")
                    properties.addAttribute(Constants.AMOUNT, amount.toString())
                    properties.addAttribute(Constants.EMI, emi.toString())

                    MoEHelper.getInstance(requireActivity())
                        .trackEvent(Constants.LOAN_SUMMARY_VIEWED, properties)

                    val attribute = java.util.HashMap<String, Any>()
                    attribute["tenure"] = duration.toString()
                    attribute["interest_rate"] = "2"
                    attribute["amount"] = amount.toString()
                    attribute["emi"] = emi.toString()
                    Freshchat.trackEvent(requireContext(), Constants.LOAN_SUMMARY_VIEWED, attribute)
                    binding.apply {
                        if (it.name != null) {
                            tvNameValue.text = it.name.toString()
                        } else {
                            tvNameValue.text = ""
                        }
                        insurancePremiumSubHeading.text = it.loanProviderName
                        tvSumAssuredValue.text =
                            getString(R.string.rupee) + getString(R.string.space) + it.amount.toString()
                        if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                .equals("en")
                        )
                            tvDueFromValue.text =
                                it.duration.toString() + " " + it.emiPeriodValue
                        else
                            tvDueFromValue.text =
                                it.duration.toString() + " " + it.emiPeriodValue
                        tvPremiumValue.text = "₹ " + it.emi.toString()
                        tvInterestValue.text = "2 %"
                        if (it.emi.toString().contains(".")) {
                            if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                    .equals("en")
                            )
                                insurancePremiumAmount.text =
                                    "₹ " + it.emi.toString()
                                        .subSequence(0, it.emi.toString().indexOf("."))
                                        .toString() + "/mo"
                            else {
                                insurancePremiumAmount.text =
                                    "₹ " + it.emi.toString()
                                        .subSequence(0, it.emi.toString().indexOf("."))
                                        .toString() + "/महीना"
                            }
                        } else {
                            if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                    .equals("en")
                            )
                                insurancePremiumAmount.text = "₹" + it.emi + "months"
                            else
                                insurancePremiumAmount.text =
                                    "₹ " + it.emi.toString()
                                        .subSequence(0, it.emi.toString().indexOf("."))
                                        .toString() + "/महीना"
                        }
                        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(
                            requireActivity()
                        ).`as`(PictureDrawable::class.java)
                            .listener(SvgSoftwareLayerSetter())
                        val uri = Uri.parse(it.loanProviderLogo)
                        requestBuilder.load(uri).into(ivLogoInsurance)
                    }
                }
            }
        }
    }


    fun initLoader() {
        dialogLoader = Dialog(requireContext())
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView =
            dialogLoader.findViewById(R.id.animate_icon)
        val rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }


}