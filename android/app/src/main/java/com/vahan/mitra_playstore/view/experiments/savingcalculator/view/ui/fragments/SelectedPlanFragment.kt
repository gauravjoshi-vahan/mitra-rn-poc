/**
 * Created by Prakhar Pandey
 * Date :- 04 Nov 2022
 * This fragment is an entry point for viewing the Selected Plan for a saving calculator
 * Able to withdraw the money as well
 */

package com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentSelectedPlanBinding
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.FetchingUserInfoDTO
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.WithDrawRequestModel
import com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.adapters.SavingInfoAdapter
import com.vahan.mitra_playstore.view.experiments.savingcalculator.viewmodel.SavingCalculatorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectedPlanFragment : Fragment() {

    lateinit var binding: FragmentSelectedPlanBinding
    lateinit var dialogLoader: Dialog
    private var isWithDrawOptionEnable: Boolean = false
    private lateinit var viewSavingCalculatorViewModel: SavingCalculatorViewModel
    private var dataList: ArrayList<FetchingUserInfoDTO.SavingsTransaction> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selected_plan, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickListener()
    }

    private fun clickListener() {
        binding.tvNeedMoreMoneyWithdraw.setOnClickListener {
            if (isWithDrawOptionEnable) {
                applyForWithDraw()
            }
        }
        binding.saveButton.setOnClickListener {
            findNavController().navigate(R.id.nav_home_fragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.nav_home_fragment)
        }

    }

    private fun applyForWithDraw() {
        // Add Dialogue
        showDialogue()

    }

    private fun showDialogue() {
        setupInstrumentation("withdrawal")
        val data = WithDrawRequestModel("CANCELLED")
        val ctDialog = Dialog(requireContext())
        ctDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ctDialog.setContentView(R.layout.dialogue_withdraw)
        val button = ctDialog.findViewById<TextView>(R.id.tv_withdraw)
        val tvCancel = ctDialog.findViewById<TextView>(R.id.tv_cancel)
        tvCancel.setOnClickListener {
            ctDialog.dismiss()
        }
        button.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                viewSavingCalculatorViewModel.withDrawAmount(data).collect {
                    when (it) {
                        ApiState.Loading -> {
                            dialogLoader.show()
                        }
                        is ApiState.Success -> {
                            hideLoading()
                            ctDialog.dismiss()
                            if (it.data.success!!) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Your Amount is Withdraw it will reflect in your account within next 24 hrs",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchPlan()
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        is ApiState.Failure -> {
                            hideLoading()
                            findNavController()
                                .navigate(R.id.nav_error_fragment)
                        }
                    }

                }
            }
        }

        ctDialog.setCanceledOnTouchOutside(true)
        ctDialog.setCancelable(true)
        ctDialog.show()
    }

    // initialized the view
    private fun initView() {
        setupInstrumentation("plan active")
        initializedLoader()
        viewSavingCalculatorViewModel =
            ViewModelProvider(this)[SavingCalculatorViewModel::class.java]
        fetchPlan()

    }

    // This methods calls an api for getting the selected plan
    private fun fetchPlan() {
        dataList.clear()
        lifecycleScope.launchWhenStarted {
            viewSavingCalculatorViewModel.getFetchDetails().collect {
                when (it) {
                    ApiState.Loading -> {
                        dialogLoader.show()
                    }
                    is ApiState.Success -> {
                        hideLoading()
                        if (it.data.isSavingsPlanPresent == true) {
                            setData(it.data)
                        } else {
                            findNavController().navigate(R.id.nav_starter_fragment)
                        }
                    }
                    is ApiState.Failure -> {
                        hideLoading()
                        findNavController()
                            .navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    // This function set data for walletDetails, previous transaction,
    private fun setData(selectedPlanInfo: FetchingUserInfoDTO) {
        val selectedWalletDetails = selectedPlanInfo.savingsWalletBalances
        isWithDrawOptionEnable = selectedPlanInfo.savingsPlanDetails!!.status.equals("ACTIVE") &&
                (selectedWalletDetails!!.amount != null && selectedWalletDetails.amount!!.toDouble() > 0)
        if (isWithDrawOptionEnable)
            binding.rlWithdraw.visibility = View.VISIBLE
        else
            binding.rlWithdraw.visibility = View.GONE

        if (selectedPlanInfo.futureInstallmentDetails != null) {
            binding.noteMessage.visibility = View.VISIBLE
            if (selectedPlanInfo.savingsPlanDetails.status.equals("ACTIVE")) {
                binding.noteMessage.text = "Next deduction" +
                        " will be on ${
                            requireContext().dateConversionToString(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                selectedPlanInfo.futureInstallmentDetails.futureInstallmentDate,
                                "d LLLL yyyy"
                            )
                        }," +
                        " ${selectedPlanInfo.futureInstallmentDetails.installmentDayName}"
            } else {
                binding.noteMessage.text =
                    "Your savings plan stands cancelled, the emergency fund would be credited to your Mitra wallet within 24 hrs after your withdrawal request."
            }
        } else {
            binding.noteMessage.visibility = View.GONE
        }

        // LOGIC FOR WALLET DETAILS
        if (selectedWalletDetails?.amount != null) {
            binding.tvAmount.text =
                "₹" + requireContext().roundOff2digit(selectedWalletDetails.accruedAmount!!.toDouble())
            binding.amountOne.text =
                "₹" + requireContext().roundOff2digit(selectedWalletDetails.amount.toDouble())
            binding.amountTwo.text =
                "₹" + requireContext().roundOff2digit(selectedWalletDetails.amount.toDouble())
            binding.amountEarned.text =
                "₹" + requireContext().roundOff2digit(selectedWalletDetails.accruedAmount.toDouble() - selectedWalletDetails.amount.toDouble())
        }
        // LOGIC FOR SAVING TRANSC
        if (selectedPlanInfo.savingsTransactions!!.isNotEmpty()) {
            for (i in 0 until selectedPlanInfo.savingsTransactions.size) {
                if (selectedPlanInfo.savingsTransactions[i]!!.event.equals("SYSTEMATIC_INSTRUCTION_EXECUTION")
                    || selectedPlanInfo.savingsTransactions[i]!!.event.equals("WITHDRAWAL")
                    || selectedPlanInfo.savingsTransactions[i]!!.event.equals("INTEREST_PAID")
                ) {
                    dataList.add(selectedPlanInfo.savingsTransactions[i]!!)
                }
            }
            binding.rvPaymentHistory.visibility = View.VISIBLE
            binding.tvPaymentHistory.visibility = View.VISIBLE
            val getSavingAdapter = SavingInfoAdapter(requireActivity(), dataList)
            binding.rvPaymentHistory.adapter = getSavingAdapter
        } else {
            binding.rvPaymentHistory.visibility = View.GONE
            binding.tvPaymentHistory.visibility = View.GONE
        }
    }


    private fun initializedLoader() {
        dialogLoader = Dialog(requireActivity())
        dialogLoader.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation = dialogLoader.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }

    private fun hideLoading() {
        dialogLoader.dismiss()
    }

    private fun setupInstrumentation(type: String) {
        val attribute: HashMap<String, Any> = HashMap()
        val properties = Properties()
        properties.addAttribute("status", type)
        attribute["status"] = type
        captureAllEvents(
            requireContext(),
            Constants.SAVINGPAGEVIEWED,
            attribute,
            properties
        )
    }


}