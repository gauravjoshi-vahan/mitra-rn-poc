package com.vahan.mitra_playstore.view.experiments.mitrapaylater.view.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentMPLBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import com.vahan.mitra_playstore.view.experiments.mitrapaylater.models.ReqModelMPLCredit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MPLFragment : Fragment() {
    private lateinit var binding: FragmentMPLBinding
    private var faqIsOpened = false
    private lateinit var viewEarnViewModel: EarnViewModel
    private var dialogLoader: Dialog? = null
    private var amount : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_m_p_l,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.ivDropdown.setBackgroundResource(R.drawable.ic_dropdown_light_grey)
        viewEarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]
        //Api call for getting bank details
        getBankDetail()
        clickListener()
        setupInstrumentation()
    }

    private fun setupInstrumentation() {
        val attribute : HashMap<String,Any> = HashMap()
        val properties = Properties()
        properties.addAttribute("status", "main page")
        attribute["status"] = "main page"
        captureAllEvents(
            requireContext(),
            Constants.MITRAPAYLATERPAGEVIEWED,
            attribute,
            properties
        )
    }

    @SuppressLint("SetTextI18n")
    private fun clickListener() {
        binding.ivBackButton.setOnClickListener { view -> requireActivity().onBackPressed() }

        // handling the faq dropdown
        binding.rlFAQContainer.setOnClickListener {
            if(!faqIsOpened) {
                binding.ivDropdown.setBackgroundResource(R.drawable.ic_dropup_grey)
                binding.tvMplFaqDesc.visibility = View.VISIBLE
                faqIsOpened = true
            }else{
                binding.ivDropdown.setBackgroundResource(R.drawable.ic_dropdown_light_grey)
                binding.tvMplFaqDesc.visibility = View.GONE
                faqIsOpened = false
            }
        }

        binding.btNext1stScreen.setOnClickListener {
           if(binding.edtTransferringAmt2.text!!.isNotEmpty()){
               setAmountData()
               binding.rl1stScreen.visibility = View.GONE
               binding.btNext1stScreen.visibility = View.GONE
               binding.btNext2ndScreen.visibility = View.VISIBLE
               binding.clCheckbox.visibility = View.VISIBLE
               binding.rl2ndScreen.visibility = View.VISIBLE
               binding.rl3rdScreen.visibility = View.GONE
           } else {
               Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
           }
        }

        binding.btNext2ndScreen.setOnClickListener {
            if (binding.rbAmt.isChecked) {
                if (binding.tcCheckbox.isChecked) {
                    binding.clCheckbox.visibility = View.GONE
                    binding.btNext2ndScreen.visibility = View.GONE
                    binding.rl3rdScreen.visibility = View.VISIBLE
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please accept term and conditions",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }else {
                Toast.makeText(requireContext(), "Please select the tenure plan", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btTransferMoney.setOnClickListener {
            setupInstrumentation(amount)
            // show loader
            if(amount!!.toDouble()<1000) {
                val requestModelMPLCredit = ReqModelMPLCredit(amount!!.toDouble())
                lifecycleScope.launchWhenStarted {
                    viewEarnViewModel.mplCredit(requestModelMPLCredit).collect {
                        when (it) {
                            is ApiState.Success -> {
                                if (it.data.success == true) {
                                    dialogLoader?.dismiss()
                                    findNavController().navigate(R.id.nav_mpl_fragment_applied)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        it.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is ApiState.Failure -> {
                                dialogLoader?.dismiss()
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.nav_error_fragment)
                            }
                            ApiState.Loading -> {
                                dialogLoader?.show()
                            }
                        }
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Amount should be less than or equal to ₹1000", Toast.LENGTH_SHORT).show()
            }

        }

        binding.tvSuggestion1.setOnClickListener {
            binding.edtTransferringAmt2.setText("400")
        }
        binding.tvSuggestion2.setOnClickListener {
            binding.edtTransferringAmt2.setText("600")
        }
        binding.tvSuggestion3.setOnClickListener {
            binding.edtTransferringAmt2.setText("800")
        }
    }

    private fun setupInstrumentation(amount: String?) {
        val attribute : HashMap<String,Any> = HashMap()
        val properties = Properties()
        properties.addAttribute("amount", amount)
        attribute["amount"] = amount?:""
        captureAllEvents(
            requireContext(),
            Constants.MITRAPAYLATERAVAILED,
            attribute,
            properties
        )
    }

    fun initLoader() {
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

    @SuppressLint("SetTextI18n")
    private fun setAmountData() {
        amount = binding.edtTransferringAmt2.text.toString()
        binding.btTransferMoney.text =getString(R.string.transfer) + " "+amount
        binding.edtTransferringAmt.setText(binding.edtTransferringAmt2.text.toString())
        val evaluatedSum = amount!!.toDouble()/4
        binding.rbAmt.text = ("₹$evaluatedSum")
        binding.tvAmtCalc.text = ("(₹"+evaluatedSum+" x 4 weeks = ${evaluatedSum*4})")
    }

    @SuppressLint("SetTextI18n")
    private fun getBankDetail() {
        viewEarnViewModel.getBankDetail().observe(viewLifecycleOwner) {
            if (it?.statusCode == 500) {
                findNavController().navigate(R.id.nav_error_fragment)
            } else {
                if(it?.bankAccountDetails?.accountNumber?.length!! >0) {
                    binding.accountName.text = it.bankAccountDetails?.bankName + "..." +
                            it.bankAccountDetails?.accountNumber?.length?.let { it1 ->
                                it.bankAccountDetails?.accountNumber!!.subSequence(
                                    it.bankAccountDetails?.accountNumber?.length!! - 4, it1
                                )
                            }
                }else{
                    binding.accountName.text = it.bankAccountDetails?.bankName
                }
            }
        }
    }

}