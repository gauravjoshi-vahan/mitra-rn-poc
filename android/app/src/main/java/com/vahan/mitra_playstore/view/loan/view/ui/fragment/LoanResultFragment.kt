package com.vahan.mitra_playstore.view.loan.view.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.RequestBuilder
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam


import com.vahan.mitra_playstore.view.loan.view.ui.adapter.LoanResultAdapter
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.LoanListBinding
import com.vahan.mitra_playstore.interfaces.Listener
import com.vahan.mitra_playstore.models.LoanList
import com.vahan.mitra_playstore.model.LoanModelClass
import com.vahan.mitra_playstore.models.FeedbackTriggersModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.NotificationViewActivity
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet

class LoanResultFragment : Fragment(), Listener {
    private lateinit var viewSharedViewModel: SharedViewModel
    private lateinit var binding: LoanListBinding
    private lateinit var loanAdapter: LoanResultAdapter
    private var model: LoanModelClass? = null
    private lateinit var ctDialog: Dialog
    private var dataList: ArrayList<LoanList.LoanApplication> = ArrayList()
    private var fa: FirebaseAnalytics? = null
    private var trigger: FeedbackTriggersModel? = null
    val handler = Handler(Looper.getMainLooper())
    var name : String?  = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,


        ): View {
        binding = LoanListBinding.inflate(layoutInflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        viewSharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        fa = FirebaseAnalytics.getInstance(requireActivity())
        getValueFromJson()
        getLoan()
        clickListener()
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.LOAN_RESULT_FRAGMENT2)
        setInstrumentation()

    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        fa?.logEvent(Constants.LOANS_LIST_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.LOANS_LIST_VIEWED, properties)
        UXCam.logEvent(Constants.LOANS_LIST_VIEWED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.LOANS_LIST_VIEWED)
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(requireContext(), Constants.LOANS_LIST_VIEWED, attribute)
    }

    private fun getLoan() {
        viewSharedViewModel.loanList.observe(requireActivity()) {
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
                    if (it.loans?.isNotEmpty()!!) {
                        name = it.name
                        dataList = ArrayList()
                        dataList.addAll(it.loans)
                        loanAdapter = LoanResultAdapter(this, requireActivity())
                        binding.layout2.recyclerview.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = loanAdapter
                            loanAdapter.submitList(it.loans)
                        }

                    } else {
                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun clickListener() {
        binding.layout1.ivBackButton.setOnClickListener {
            binding.layout2.root.visibility = View.VISIBLE
            binding.layout1.root.visibility = View.GONE
        }
        binding.layout2.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE).equals("en")) {
            binding.layout1.helpIcon.setOnClickListener {
                showHelpDialogue(
                    model?.LA?.buttonOneTextEn.toString(),
                    model?.LA?.buttonOnePhone.toString(),
                    model?.LA?.buttonOneDesc.toString()
                )
            }
            binding.layout1.claimIcon.setOnClickListener {
                showHelpDialogue(
                    model?.LA?.buttonTwoTextEn.toString(),
                    model?.LA?.buttonTwoPhone.toString(),
                    model?.LA?.buttonTwoDesc.toString()
                )
            }
        } else {
            binding.layout1.helpIcon.setOnClickListener {
                showHelpDialogue(
                    model?.LA?.buttonOneTextHi.toString(),
                    model?.LA?.buttonOnePhone.toString(),
                    model?.LA?.buttonOneDescHi.toString()
                )
            }
            binding.layout1.claimIcon.setOnClickListener {
                showHelpDialogue(
                    model?.LA?.buttonTwoTextHi.toString(),
                    model?.LA?.buttonTwoPhone.toString(),
                    model?.LA?.buttonTwoDescHi.toString()
                )
            }
        }

        binding.layout1.viewDetails.setOnClickListener {
            if (model?.LA?.docUrl!!.contains(".pdf")) {
                val bundle = Bundle()
                bundle.putString(Constants.REDIRECTION_URL, model!!.LA?.docUrl)
                bundle.putString(Constants.HEADING, Constants.SIPLY)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.nav_payslip_view, bundle)
            } else {
                startActivity(
                    Intent(requireActivity(), NotificationViewActivity::class.java)
                        .putExtra(Constants.TYPE, Constants.SIPLY)
                        .putExtra(Constants.TYPE, model!!.LA?.docUrl)
                )
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onClick(position: Int) {
        binding.apply {
            setValueFromRemoteConfig()
            binding.layout2.root.isVisible = false
            layout1.root.isVisible = true
            layout1.tvNameValue.text = name
            layout1.tvPremiumValue.text =
                dataList[position].emi + "/" + dataList[position].monthLang
            layout1.tvSumAssuredValue.text = dataList[position].amount
            layout1.tvDueFromValue.text =
                dataList[position].duration.toString() + " " + dataList[position].emiPeriodValue
            layout1.tvLoanStatusValue.text = dataList[position].loanStatus
            layout1.insurancePremiumSubHeading.text = dataList[position].loanProviderName
            layout1.insurancePremiumAmount.text =
                dataList[position].emi + "/" + dataList[position].monthLang
            layout1.tvExpiresOnValue.text = dataList[position].interestRate.toString() + getString(R.string.percentage)
            val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(
                requireActivity()
            ).`as`(PictureDrawable::class.java)
                .listener(SvgSoftwareLayerSetter())
            val uri = Uri.parse(dataList[position].loanProviderLogo)
            requestBuilder.load(uri).into(layout1.ivLogoInsurance)

            setDataFromRemoteConfig(dataList, position)
        }

    }

    private fun setDataFromRemoteConfig(
        dataList: ArrayList<LoanList.LoanApplication>,
        position: Int,
    ) {
        setInstrumentationRemoteData(dataList, position)
    }

    private fun setInstrumentationRemoteData(
        dataList: ArrayList<LoanList.LoanApplication>,
        position: Int,
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.TENURE, dataList[position].duration.toString())
        bundle.putString(Constants.INTEREST_RATE, "2")
        bundle.putString(Constants.AMOUNT, dataList[position].amount.toString())
        bundle.putString(Constants.EMI, dataList[position].emi.toString())
        fa?.logEvent(Constants.LOAN_APPLICATION_VIEWED, bundle)
        val properties = Properties()
        properties.addAttribute(Constants.TENURE, dataList[position].duration.toString())
        properties.addAttribute(Constants.INTEREST_RATE, "2")
        properties.addAttribute(Constants.AMOUNT, dataList[position].amount.toString())
        properties.addAttribute(Constants.EMI, dataList[position].emi.toString())
        MoEHelper.getInstance(requireActivity())
            .trackEvent(Constants.LOAN_APPLICATION_VIEWED, properties)
        val data = HashMap<String, String>()
        data[Constants.TENURE] = dataList[position].duration.toString()
        data[Constants.INTEREST_RATE] = "2"
        data[Constants.AMOUNT] = dataList[position].amount.toString()
        data[Constants.EMI] = dataList[position].emi.toString()
        UXCam.logEvent(Constants.LOAN_APPLICATION_VIEWED, data)

        val attribute = HashMap<String, Any>()
        attribute[Constants.TENURE] = dataList[position].duration.toString()
        attribute[Constants.INTEREST_RATE] = "2"
        attribute[Constants.AMOUNT] = dataList[position].amount.toString()
        attribute[Constants.EMI] = dataList[position].emi.toString()
        Freshchat.trackEvent(requireContext(), Constants.LOAN_APPLICATION_VIEWED, attribute)

        handler.postDelayed({
            if (trigger!=null){
                for (i in 0 until trigger?.trigger?.size!!) {
                    if (!PrefrenceUtils.retriveDataInBoolean(context,Constants.LOAN_APPLICATION_VIEWED)){
                        if (PrefrenceUtils.retriveDataInBoolean(context,Constants.ISFEEDBACKSESSION)){
                            if (trigger?.trigger?.get(i)?.trigger_event.equals(Constants.LOAN_APPLICATION_VIEWED)) {
                                FeedbackBottomsheet(requireContext(),Constants.LOAN_APPLICATION_VIEWED).show()
                                PrefrenceUtils.insertDataInBoolean(context,Constants.ISFEEDBACKSESSION,false)
                            }
                        }else{
                            return@postDelayed
                        }
                    }else{
                        return@postDelayed
                    }
                }
            }
        },3000)
    }

    private fun getValueFromJson() {
        model = Gson().fromJson(
            PrefrenceUtils.retriveData(
                requireContext(),
                Constants.LOAN_TERM_REMOTE_CONFIG
            ), LoanModelClass::class.java
        )
        trigger = Gson()
            .fromJson(
                PrefrenceUtils.retriveData(
                    activity,
                    Constants.FEEDBACK_TRIGGERS_REMOTE
                ),
                FeedbackTriggersModel::class.java
            )
    }

    private fun setValueFromRemoteConfig() {
        if (model?.LA?.buttonOne == 1 && model?.LA?.buttonTwo == 1) {
            binding.layout1.bottomContainer.visibility = View.VISIBLE
        } else if (model?.LA?.buttonOne == 1 && model?.LA?.buttonTwo == 0) {
            binding.layout1.helpIcon.visibility = View.VISIBLE
            binding.layout1.claimIcon.visibility = View.GONE
        } else if (model?.LA?.buttonOne == 0 && model?.LA?.buttonTwo == 1) {
            binding.layout1.helpIcon.visibility = View.GONE
            binding.layout1.claimIcon.visibility = View.VISIBLE
        } else {
            binding.layout1.helpIcon.visibility = View.GONE
            binding.layout1.claimIcon.visibility = View.GONE
        }
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

}
