package com.vahan.mitra_playstore.view.bankinfo.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentBankDetailsViewBinding
import com.vahan.mitra_playstore.models.GetBankDetailsModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.HomeActivity
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankDetailsViewFragment : Fragment() {
    private lateinit var binding: FragmentBankDetailsViewBinding
    private var viewEarnViewModel: EarnViewModel? = null
    private var fa: FirebaseAnalytics? = null
    private var is_verify_bank_for_non_payroll: String? = null
    private var statusMessage: String? = null
    private var statusColor: String? = "#04A7BD"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank_details_view, container, false)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            is_verify_bank_for_non_payroll = getString("is_non_payroll")
            PrefrenceUtils.insertData(requireContext(), "is_non_payroll", is_verify_bank_for_non_payroll)
            statusMessage = getString(Constants.MESSAGE)
            statusColor = getString("color")
        }
        if (is_verify_bank_for_non_payroll == "Navigation") {
            binding.proceedBtn.visibility = View.VISIBLE
            binding.statusParent.visibility = View.VISIBLE
            binding.accountInfoNameTitle.visibility = View.VISIBLE
            binding.accountInfoNumberTitle.visibility = View.VISIBLE
            binding.status.text = statusMessage
            val shape = GradientDrawable()
            shape.cornerRadius = 8f
            shape.setColor(Color.parseColor(statusColor))
            binding.statusParent.background = shape
        }
    }

    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireContext())
        binding.ivBackButton.setOnClickListener { view: View? ->
            if (is_verify_bank_for_non_payroll == "Navigation") {
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        HomeActivity::class.java
                    ).putExtra("link", Constants.REDIRECTION_URL)
                )
            } else
                requireActivity().onBackPressed()
        }

        binding.proceedBtn.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    requireContext(),
                    HomeActivity::class.java
                ).putExtra("link", Constants.REDIRECTION_URL)
            )
        }
        viewEarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("ViewBank Fragment")
        binding.updateDetails.setOnClickListener { v: View? ->
            setInstrumentation()
            Navigation.findNavController(binding.root).navigate(R.id.nav_fragment_add_bank)
        }
        bankDetail()
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        fa!!.logEvent(Constants.BANK_EDIT_TAPPED, bundle)
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.BANK_EDIT_TAPPED, properties)
        UXCam.logEvent(Constants.BANK_EDIT_TAPPED)
    }


    @SuppressLint("SetTextI18n")
    private fun bankDetail() {
        viewEarnViewModel!!.getBankDetail()
            .observe(viewLifecycleOwner) { getBankDetailsModel: GetBankDetailsModel ->
                if (getBankDetailsModel.statusCode == 500) {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.nav_error_fragment)
                } else {
                    if (!getBankDetailsModel.bankAccountDetails.bankName.equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        Log.d(
                            "Values",
                            "getBankDetail: " + getBankDetailsModel.bankAccountDetails.accountType
                        )
                        binding.tvBankName.text =
                            (getBankDetailsModel.bankAccountDetails.bankName + "  " +
                                    getBankDetailsModel.bankAccountDetails.accountNumber.substring(
                                        getBankDetailsModel.bankAccountDetails.accountNumber.length - 6
                                    )) ?: getBankDetailsModel.bankAccountDetails.accountNumber
                        binding.tvAccountTypeType.text =
                            getBankDetailsModel.bankAccountDetails.accountType
                        binding.accountInfoName.text =
                            getBankDetailsModel.bankAccountDetails.accountName
                        binding.accountInfoNumber.text =
                            getBankDetailsModel.bankAccountDetails.accountNumber
                    }
                    if (PrefrenceUtils.retriveDataInBoolean(
                            requireContext(),
                            Constants.ISBANKPRIMARYACCOUNT
                        )
                    ) {
                        if (getBankDetailsModel.isBankAccountVerificationInProgress) {
                            binding.updateDetails.backgroundTintList =
                                requireContext().getColorStateList(R.color.border_text_color)
                            binding.updateDetails.isClickable = false
                            binding.updateBank.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                            binding.updateBank.setText(R.string.verification_pending_msg)
                        } else {
                            // can add new a/c
                            binding.updateDetails.isClickable = true
                            binding.updateDetails.visibility = View.VISIBLE
                            binding.updateBank.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.white
                                )
                            )
                            binding.updateDetails.backgroundTintList =
                                requireContext().getColorStateList(R.color.default_200)
                        }
                    } else {
                        binding.updateDetails.visibility = View.GONE
                    }
                }
            }
    }
}