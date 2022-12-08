package com.vahan.mitra_playstore.view.loan.view.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentLoanApplicationBinding
import com.vahan.mitra_playstore.model.LoanModelClass
import com.vahan.mitra_playstore.models.kotlin.GetAmountAndPurposeDTO
import com.vahan.mitra_playstore.models.kotlin.GetPurPosesAndAmountModel
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.loan.view.ui.adapter.GetLoanPurposeAdapter
import com.vahan.mitra_playstore.view.loan.viewmodel.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanApplicationFragment : Fragment() {

    private lateinit var binding: FragmentLoanApplicationBinding
    private lateinit var getPurposeList: MutableList<GetAmountAndPurposeDTO.Purpose>
    private lateinit var getAmountList: MutableList<Int>
    lateinit var valuesSelectedList: ArrayList<String>
    private var purpose: String? = ""
    private var siplyUrL: String? = ""
    private var fa: FirebaseAnalytics? = null
    private var model: LoanModelClass? = null
    private var defaultInterest : Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_loan_application,
                container,
                false
            )
        initView()
        return binding.root
    }

    private fun initView() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.LOAN_APPLICATION_FRAGMENT)
        valuesSelectedList = ArrayList()
        getValueFromJson()
        getPurposes()
        clickListener()
        setInstrumentation()
    }

    private fun setInstrumentation() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        val bundle = Bundle()
        fa?.logEvent(Constants.LOAN_PURPOSE_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.LOAN_PURPOSE_VIEWED, properties)
        UXCam.logEvent(Constants.LOAN_PURPOSE_VIEWED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.LOAN_PURPOSE_VIEWED)

    }

    private fun getValueFromJson() {
        model = Gson().fromJson(
            PrefrenceUtils.retriveData(
                requireContext(),
                Constants.LOAN_TERM_REMOTE_CONFIG
            ), LoanModelClass::class.java
        )
        siplyUrL = model?.E?.tncUrl

    }

    private fun clickListener() {
        binding.layoutTwoPurpose.setPrice.setOnClickListener {
            binding.layoutTwoPurpose.edtAmount.requestFocus()
        }
        binding.layoutOnePurpose.saveButtonOne.setOnClickListener {
            setInstrumentationForAmount()
            binding.layoutOnePurpose.container.visibility = View.GONE
            binding.layoutTwoPurpose.container.visibility = View.VISIBLE
        }
        binding.layoutTwoPurpose.saveButton.setOnClickListener {
            if (binding.layoutTwoPurpose.edtAmount.text.toString() != "") {
                Log.d("data_value", "clickListener: " + binding.layoutTwoPurpose.edtAmount.text)
                val bundle = Bundle()
                bundle.putInt(Constants.AMOUNT, binding.layoutTwoPurpose.edtAmount.text.toString().toInt())
                bundle.putString(Constants.PURPOSE, purpose)
                bundle.putString(Constants.SIPLY, siplyUrL)
                bundle.putDouble(Constants.INTEREST, defaultInterest!!)
                Navigation.findNavController(binding.containerRl)
                    .navigate(R.id.nav_loan_tenure_fragment, bundle)
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_amount_greater_than_0),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.layoutOnePurpose.backButtonContainer.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.layoutTwoPurpose.ivBack.setOnClickListener {
            binding.layoutOnePurpose.container.visibility = View.VISIBLE
            binding.layoutTwoPurpose.container.visibility = View.GONE
        }
    }

    private fun setInstrumentationForAmount() {
        val bundle = Bundle()
        fa?.logEvent(Constants.LOAN_AMOUNT_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.LOAN_AMOUNT_VIEWED, properties)
        UXCam.logEvent(Constants.LOAN_AMOUNT_VIEWED)
    }


    private fun getPurposes() {
        val loanViewModel : LoanViewModel = ViewModelProvider(this)[LoanViewModel::class.java]
        lifecycleScope.launchWhenStarted {
            loanViewModel.getPurpose.collect() {
                when (it) {
                    ApiState.Loading -> {

                    }
                   is ApiState.Failure -> {
                       findNavController().navigate(R.id.nav_error_fragment)
                   }
                    is ApiState.Success -> {
                        getPurposeList = ArrayList()
                        getAmountList = ArrayList()
                        if(it.data.purposes!=null)
                        getPurposeList.addAll(it.data.purposes)
                        getAmountList.addAll(it.data.amounts!!)
                        defaultInterest = it.data.defaultInterestRate
                        val getLoanPurposeAdapter = GetLoanPurposeAdapter(
                            requireActivity(),
                            this@LoanApplicationFragment,
                            getPurposeList, getAmountList, 0
                        )
                        binding.layoutOnePurpose.rvContainer.addItemDecoration(
                            ItemOffsetDecoration(
                                2,
                                HelperMethod.dpToPx(10f, activity),
                                true
                            )
                        )
                        binding.layoutOnePurpose.rvContainer.adapter = getLoanPurposeAdapter
                        binding.layoutOnePurpose.rvContainer.layoutManager =
                            GridLayoutManager(activity, 2)
                        val getLoanAmountAdapter = GetLoanPurposeAdapter(
                            requireActivity(),
                            this@LoanApplicationFragment,
                            getPurposeList,
                            getAmountList,
                            1
                        )
                        binding.layoutTwoPurpose.rvContainerAmount.addItemDecoration(
                            ItemOffsetDecoration(
                                2,
                                HelperMethod.dpToPx(10f, activity),
                                true
                            )
                        )
                        binding.layoutTwoPurpose.rvContainerAmount.adapter = getLoanAmountAdapter
                        binding.layoutTwoPurpose.rvContainerAmount.layoutManager =
                            GridLayoutManager(activity, 2)
                    }
                }

            }
        }

    }

    fun getPurposeSelectedData(selectedItem: String) {
        if (selectedItem != "") {
            binding.layoutOnePurpose.saveButtonOne.isEnabled = true
            binding.layoutOnePurpose.saveButtonOne.isClickable = true
            binding.layoutOnePurpose.saveButtonOne.setBackgroundResource(R.drawable.curver_corner_12)
            purpose = selectedItem
            setInstrumentationForSelectedData()

        } else {
            binding.layoutOnePurpose.saveButtonOne.isEnabled = false
            binding.layoutOnePurpose.saveButtonOne.isClickable = false
            binding.layoutOnePurpose.saveButtonOne.setBackgroundResource(R.drawable.curver_corner_12_disable)
        }
    }

    private fun setInstrumentationForSelectedData() {
        val bundle = Bundle()
        bundle.putString(Constants.REASON, purpose)
        fa?.logEvent(Constants.LOAN_REASON_PICKED, bundle)
        val properties = Properties()
        properties.addAttribute(Constants.REASON, purpose)
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.LOAN_REASON_PICKED, properties)
        val data = HashMap<String, String>()
        data[Constants.REASON] = purpose!!
        UXCam.logEvent(Constants.LOAN_REASON_PICKED, data)
        val attribute = java.util.HashMap<String, Any>()
        attribute[Constants.REASON] = purpose!!
        Freshchat.trackEvent(requireContext(),Constants.LOAN_REASON_PICKED,attribute)
    }

    fun getAmountSelectedData(amount: Int) {
        if (amount > 0) {
            binding.apply {
                layoutTwoPurpose.saveButton.isEnabled = true
                layoutTwoPurpose.saveButton.isClickable = true
                layoutTwoPurpose.saveButton.setBackgroundResource(R.drawable.curver_corner_12)
                layoutTwoPurpose.edtAmount.requestFocus()
                layoutTwoPurpose.edtAmount.hint = "$amount"
                layoutTwoPurpose.edtAmount.setText("$amount")
                layoutTwoPurpose.tvAmount.text = "$amount"
                if (model?.E?.customAmount == true) {
                    layoutTwoPurpose.edtAmount.visibility = View.VISIBLE
                    layoutTwoPurpose.tvAmount.visibility = View.GONE
                } else {
                    layoutTwoPurpose.edtAmount.visibility = View.GONE
                    layoutTwoPurpose.tvAmount.visibility = View.VISIBLE
                }
                layoutTwoPurpose.edtAmount.setSelection(layoutTwoPurpose.edtAmount.length())
                setInstrumentationForSelectedData()
            }
        } else {
            binding.layoutTwoPurpose.saveButton.isEnabled = false
            binding.layoutTwoPurpose.saveButton.isClickable = false
            binding.layoutTwoPurpose.saveButton.setBackgroundResource(R.drawable.curver_corner_12_disable)
            binding.layoutTwoPurpose.edtAmount.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (PrefrenceUtils.retriveDataInBoolean(context, Constants.TENURE_BACK)) {
            binding.layoutTwoPurpose.root.visibility = View.VISIBLE
            binding.layoutOnePurpose.root.visibility = View.GONE
            PrefrenceUtils.insertDataInBoolean(context, Constants.TENURE_BACK, false)
        }
    }

}