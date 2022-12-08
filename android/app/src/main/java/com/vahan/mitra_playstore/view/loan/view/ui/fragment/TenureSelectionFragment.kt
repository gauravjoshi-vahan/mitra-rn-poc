package com.vahan.mitra_playstore.view.loan.view.ui.fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam

import com.vahan.mitra_playstore.view.loan.view.ui.adapter.GetTenureAdapter
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentTenureSelectionBinding
import com.vahan.mitra_playstore.models.PostLoanApiModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.HelperMethod.dpToPx
import com.vahan.mitra_playstore.utils.ItemOffsetDecoration
import com.vahan.mitra_playstore.utils.PrefrenceUtils


class TenureSelectionFragment : Fragment() {

    private var siplyURL: String?  = "";
    private lateinit var binding: FragmentTenureSelectionBinding
    private var amount: Int? = 0
    private var purpose: String? = ""
    private var interest: Double? = 0.0
    private var duration: Double? = 0.0
    private var emi: Double? = 0.0
    private var period: String? = ""
    private lateinit var tenuresSelection: ArrayList<PostLoanApiModel>
    private var fa: FirebaseAnalytics? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tenure_selection,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            amount = this.getInt(Constants.AMOUNT)
            purpose = this.getString(Constants.PURPOSE)
            siplyURL = this.getString(Constants.SIPLY)
            interest = this.getDouble(Constants.INTEREST)
        }
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.TENURE_SELECTION_FRAGMENT)
        binding.tvHeading.text = getString(R.string.tenure_of) + " ₹ $amount"
        postLoanApiCall()
        clickListener()
    }

    private fun clickListener() {

        binding.next.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.AMOUNT, amount!!)
            bundle.putString(Constants.PURPOSE, purpose)
            bundle.putDouble(Constants.INTEREST, interest!!)
            bundle.putDouble(Constants.EMI, emi!!)
            bundle.putString(Constants.PERIOD, period)
            bundle.putString(Constants.SIPLY, siplyURL)
            bundle.putDouble(Constants.DURATION, duration!!)
            Navigation.findNavController(binding.container)
                .navigate(R.id.nav_loan_info_fragment, bundle)
        }

        binding.ivBack.setOnClickListener {
            PrefrenceUtils.insertDataInBoolean(context, Constants.TENURE_BACK, true)
            requireActivity().onBackPressed()

        }
    }

    private fun postLoanApiCall() {
        val viewSharedViewModel: SharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.AMOUNT, amount)
        jsonObject.addProperty(Constants.PURPOSE, purpose)
        jsonObject.addProperty(Constants.INTEREST, interest)
        viewSharedViewModel.postLoanApiModel(jsonObject)
            .observe(viewLifecycleOwner) {
                when (it[0].statusCode) {
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
                        tenuresSelection = ArrayList()
                        tenuresSelection.addAll(it)
                        binding.rvTenure.addItemDecoration(
                            ItemOffsetDecoration(
                                2,
                                dpToPx(10f, activity),
                                true
                            )
                        )
                        val getTenureAdapter = GetTenureAdapter(
                            requireActivity(),
                            this@TenureSelectionFragment, tenuresSelection, 1
                        )
                        binding.rvTenure.adapter = getTenureAdapter
                        binding.rvTenure.layoutManager = GridLayoutManager(activity, 2)
                    }
                }
            }
    }


    @SuppressLint("SetTextI18n")
    fun tenureItemValuesSet(
        emi: Double?,
        emiPeriod: String?,
        selected: Int,
        timeDuration: Double?,
        weeklyTimeDebit: Double?,
    ) {
        if (selected == 1) {
            this.emi = emi
            this.period = emiPeriod
            this.duration = timeDuration
            binding.nextContainer.visibility = View.VISIBLE
            binding.tvEmiAmount.text = getString(R.string.rupee)+getString(R.string.space) + emi.toString()

            binding.weeklyDebited.text = getString(R.string.rupee)+weeklyTimeDebit + getString(R.string.to_be_deducted_weekly)
        } else {
            binding.nextContainer.visibility = View.GONE
        }

        setInstrumentation()
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        bundle.putString("tenure", duration.toString())
        fa?.logEvent(Constants.LOAN_TENURE_PICKED, bundle)
        val properties = Properties()
        properties.addAttribute(Constants.TENURE, duration)
        MoEHelper.getInstance(requireActivity()).setUserAttribute(Constants.LOAN_TENURE_PICKED, purpose!!)
        val data = HashMap<String, String>()
        data[Constants.TENURE] = duration.toString()
        UXCam.logEvent(Constants.LOAN_TENURE_PICKED, data)
        val attribute = HashMap<String, Any>()
        attribute[Constants.TENURE] = duration.toString()
        Freshchat.trackEvent(requireContext(), Constants.LOAN_TENURE_PICKED, attribute)
    }

    override fun onResume() {
        super.onResume()
        setInstrumentationLoanViewed()
    }

    private fun setInstrumentationLoanViewed() {
        val bundle = Bundle()
        fa?.logEvent(Constants.LOAN_TENURE_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.LOAN_TENURE_VIEWED, properties)
        UXCam.logEvent(Constants.LOAN_TENURE_VIEWED)
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(requireContext(), Constants.LOAN_TENURE_VIEWED, attribute)
    }


}