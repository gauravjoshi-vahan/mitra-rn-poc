package com.vahan.mitra_playstore.view.payslip.view.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentPayslipBinding
import com.vahan.mitra_playstore.models.PayslipDataModel
import com.vahan.mitra_playstore.models.FeedbackTriggersModel
import com.vahan.mitra_playstore.models.kotlin.PayslipDTO
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet
import com.vahan.mitra_playstore.view.payslip.view.ui.adapter.PayslipAdapter
import com.vahan.mitra_playstore.view.payslip.viewModel.PayslipModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayslipFragment : Fragment() {

    lateinit var binding: FragmentPayslipBinding
    private lateinit var viewPayslipModel: PayslipModel
    private lateinit var fb: FirebaseAnalytics
    private lateinit var payslipListItem: ArrayList<PayslipDTO.Payslip>
    private var trigger: FeedbackTriggersModel? = null
    val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payslip, container, false)
        initView()
        return binding.root
    }

    private fun showLoading(){
        binding.showLoaderContent.visibility = View.VISIBLE
        binding.rvViewPayslip.visibility = View.GONE
        binding.shimmerLayout1.startShimmerAnimation()
    }

    private fun hideLoading(){
        binding.showLoaderContent.visibility = View.GONE
        binding.rvViewPayslip.visibility = View.VISIBLE
        binding.shimmerLayout1.stopShimmerAnimation()
    }

    private fun initView() {
        //getApiCall

        fb = FirebaseAnalytics.getInstance(requireContext())
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.PAYMENT_SLIP_FRAGMENT)
        viewPayslipModel = ViewModelProvider(this)[PayslipModel::class.java]
        lifecycleScope.launchWhenStarted {
            viewPayslipModel.getPaySlipModel("1", "10").collect(){
                when(it){
                    ApiState.Loading ->{
                        showLoading()
                    }
                    is ApiState.Success -> {
                            hideLoading()
                            binding.payslipTvLabel.text = it.data.label
                            payslipListItem = ArrayList()
                            if (it.data.payslips!!.isNotEmpty())
                                payslipListItem.addAll(it.data.payslips)
                            if (payslipListItem.size > 0) {
                                binding.rvViewPayslip.visibility = View.VISIBLE
                                binding.tvNoRecords.visibility = View.GONE
                                binding.rvViewPayslip.adapter = PayslipAdapter(payslipListItem, it.data.icon)
                            } else {
                                binding.tvNoRecords.visibility = View.VISIBLE
                                binding.rvViewPayslip.visibility = View.GONE
                            }
                        }
                    is ApiState.Failure -> {
                        findNavController().navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
        getDataFromRemoteConfig()
        clickListener()
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
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setInstrumentation()
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        fb.logEvent(Constants.PAYSLIP_LIST_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.PAYSLIP_LIST_VIEWED, properties)
        UXCam.logEvent(Constants.PAYSLIP_LIST_VIEWED)
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(requireContext(), Constants.PAYSLIP_LIST_VIEWED, attribute)
        handler.postDelayed({
            if (trigger!=null){
                for (i in 0 until trigger?.trigger?.size!!) {
                    if (!PrefrenceUtils.retriveDataInBoolean(context,Constants.PAY_SLIP_VIEWED)){
                        if (PrefrenceUtils.retriveDataInBoolean(context,Constants.ISFEEDBACKSESSION)){
                            if (trigger?.trigger?.get(i)?.trigger_event.equals(Constants.PAYSLIP_LIST_VIEWED)) {
                                FeedbackBottomsheet(requireActivity(),Constants.PAYSLIP).show()
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


}