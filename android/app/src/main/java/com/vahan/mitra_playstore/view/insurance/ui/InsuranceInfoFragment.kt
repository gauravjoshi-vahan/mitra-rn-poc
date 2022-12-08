package com.vahan.mitra_playstore.view.insurance.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.Glide
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.inapp.MoEInAppHelper
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentInsuranceInfoBinding
import com.vahan.mitra_playstore.models.InsureModelClass
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.SalaryViewActivity

import java.util.HashMap


class InsuranceInfoFragment : Fragment() {

    private lateinit var binding: FragmentInsuranceInfoBinding
    private var model: InsureModelClass? = null
    private lateinit var ctDialog: Dialog
    private lateinit var fa: FirebaseAnalytics


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_insurance_info, container, false)
        initView()
        clickListner()
        return binding.root
    }

    private fun clickListner() {

            binding.helpIcon.setOnClickListener {
                showHelpDialogue(
                    getString(R.string.call_now),
                    model?.e?.helpPhone.toString(),
                    model?.e?.helpDesc.toString()
                )
            }
            binding.claimIcon.setOnClickListener {
                showHelpDialogue(
                    getString(R.string.claim_now),
                    model!!.e?.claimPhone.toString(),
                    model!!.e?.claimDesc.toString()
                )
            }
        binding.viewDetails.setOnClickListener { view ->
            startActivity(
                Intent(requireContext(), SalaryViewActivity::class.java)
                    .putExtra(Constants._TYPE, "View details")
                    .putExtra(Constants.LINK, model!!.e.viewDetailsUrl)
            )
        }
        binding.ivBackButton.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun showHelpDialogue(btnText: String, number: String, desc: String) {
        ctDialog = Dialog(requireContext())
        ctDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ctDialog.setContentView(R.layout.dialog_layout_help)
        val button = ctDialog.findViewById<RelativeLayout>(R.id.btn_viewEarning)
        val tv = ctDialog.findViewById<TextView>(R.id.btn_tv)
        tv.text = btnText
        button.setOnClickListener { view: View? ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        }
        val tvNameSubHeading = ctDialog.findViewById<TextView>(R.id.name_sub_heading)
        tvNameSubHeading.text = desc
        ctDialog.setCanceledOnTouchOutside(true)
        ctDialog.setCancelable(true)
        ctDialog.show()
    }

    private fun initView() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.INSURANCE_INFO_FRAGMENT)
        fa = FirebaseAnalytics.getInstance(requireContext())
        setValueFromIntent()
        getValueFromJson()
    }

    private fun getValueFromJson() {
        model = Gson().fromJson(
            PrefrenceUtils.retriveData(
                requireContext(),
                Constants.INSURANCE_REMOTE_CONFIG
            ), InsureModelClass::class.java
        )
    }

    private fun setValueFromIntent() {
        this.arguments?.apply {
            binding.tvBack.text = this.getString(Constants.BUNDLE_INSURANCE_NAME)
            binding.insurancePremiumSubHeading.text = this.getString(Constants.BUNDLE_INSURANCE_NAME_SUBHEADING)
            binding.insurancePremiumAmount.text = this.getString(Constants.BUNDLE_SUM_ASSURED)
            if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE).equals("en")) {
                binding.insurancePremiumAmountExpire.text =
                    "Expire :" + this.getString("expire_from")
            } else {
                binding.insurancePremiumAmountExpire.text =
                    "समय समाप्तई :" + this.getString("expire_from")
            }

            binding.tvNameValue.text = this.getString(Constants.BUNDLE_NAME)
            binding.tvPremiumValue.text = this.getString(Constants.BUNDLE_PREMIUM_RATE)
            binding.tvSumAssuredValue.text = this.getString(Constants.BUNDLE_SUM_ASSURED)
            binding.tvDueFromValue.text = this.getString(Constants.BUNDLE_DUE_FROM)
            binding.tvExpireValue.text = this.getString(Constants.BUNDLE_EXPIRE_FROM)
            binding.tvPolicyValue.text = this.getString(Constants.BUNDLE_POLICY_STATUS)
            Glide.with(requireActivity()).load(this.getString(Constants.BUNDLE_INSURANCE_LOGO))
                .override(240, 100)
                .fitCenter()
                .into(binding.ivLogoInsurance)
        }

        /*     if (getIntent() != null) {
                 binding.tvBack.setText(getIntent().getStringExtra("insurance_name"))
                 binding.insurancePremiumSubHeading.setText(getIntent().getStringExtra("insurance_name_subHeading"))
                 binding.insurancePremiumAmount.setText(getIntent().getStringExtra("sum_assured"))
                 binding.insurancePremiumAmountExpire.setText("Expire :" + getIntent().getStringExtra("expire_from"))
                 binding.tvNameValue.setText(getIntent().getStringExtra("name"))
                 binding.tvPremiumValue.setText(getIntent().getStringExtra("premium"))
                 binding.tvSumAssuredValue.setText(getIntent().getStringExtra("sum_assured"))
                 binding.tvDueFromValue.setText(getIntent().getStringExtra("due_from"))
                 binding.tvExpireValue.setText(getIntent().getStringExtra("expire_from"))
                 binding.tvPolicyValue.setText(getIntent().getStringExtra("policy_status"))
                 Glide.with(this).load(getIntent().getStringExtra("insurance_logo"))
                         .override(240, 100)
                         .fitCenter()
                         .into<Target<Drawable>>(binding.ivLogoInsurance)
             }*/
    }

    override fun onResume() {
        super.onResume()

        setInstrumentationInsurance()
    }

    private fun setInstrumentationInsurance() {
        val bundle = Bundle()
        MoEInAppHelper.getInstance().showInApp(requireContext())
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.INSURE_DETAILS_VIEWED, properties)
        fa.logEvent(Constants.INSURE_DETAILS_VIEWED, bundle)
        UXCam.logEvent(Constants.INSURE_DETAILS_VIEWED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.INSURE_DETAILS_VIEWED)
        val attribute = HashMap<String,Any>()
        attribute[Constants.INSURE_DETAILS_VIEWED] = ""
        Freshchat.trackEvent(requireContext(),Constants.INSURE_DETAILS_VIEWED,attribute)

    }

}