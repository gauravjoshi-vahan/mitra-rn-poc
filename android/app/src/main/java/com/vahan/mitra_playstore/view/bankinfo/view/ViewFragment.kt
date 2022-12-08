package com.vahan.mitra_playstore.view.bankinfo.view

import dagger.hilt.android.AndroidEntryPoint
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.vahan.mitra_playstore.R
import androidx.navigation.Navigation
import androidx.lifecycle.ViewModelProvider
import com.uxcam.UXCam
import com.moe.pushlibrary.MoEHelper
import android.annotation.SuppressLint
import android.util.Log
import com.vahan.mitra_playstore.models.GetBankDetailsModel
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.moengage.core.Properties
import com.vahan.mitra_playstore.databinding.FragmentViewBinding
import com.vahan.mitra_playstore.utils.Constants

@AndroidEntryPoint
class ViewFragment : Fragment() {
    private lateinit var binding: FragmentViewBinding
    private var viewEarnViewModel: EarnViewModel? = null
    private var fa: FirebaseAnalytics? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireContext())
        binding.ivBackButton.setOnClickListener { view: View? ->
           requireActivity().onBackPressed()
        }
        viewEarnViewModel = ViewModelProvider(this).get(EarnViewModel::class.java)
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("ViewBank Fragment")
        binding.updateDetails.setOnClickListener { v: View? ->
            setInstrumentation()
            Navigation.findNavController(binding.root).navigate(R.id.nav_fragment_add)
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
    private fun bankDetail(){
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
                                getBankDetailsModel.bankAccountDetails.bankName + "  " +
                                        getBankDetailsModel.bankAccountDetails.accountNumber.substring(
                                            9
                                        )
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