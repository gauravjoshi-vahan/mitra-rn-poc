package com.vahan.mitra_playstore.view.earn.view.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.CashoutMoneyScreenBinding
import com.vahan.mitra_playstore.models.FeedbackTriggersModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.utils.startBlitzSurvey
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class CashOutTransactionFragment : Fragment() {
    private lateinit var defaultSeperator: String
    var price = ""
    var eprice = ""
    var cashoutEnabled = false
    var cashoutFeePercentage = ""
    var cashoutFixedFee = 0
    var purpose = ""
    var orderReachToNextLevel = 0
    var tripsReachToNextLevel = 0
    lateinit var binding: CashoutMoneyScreenBinding
    lateinit var fa: FirebaseAnalytics
    private lateinit var viewEarnViewModel: EarnViewModel
    private var trigger: FeedbackTriggersModel? = null
    val handler = Handler(Looper.getMainLooper())
    private var count: Int = 0
    var orderReachToNextLevel1: Boolean = false
    var daysReachToNextLevel: Boolean = false
    var cashoutNextLevelPercentage: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.cashout_money_screen, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            price = this.getString(Constants.PRICE).toString()
            cashoutEnabled = this.getBoolean(Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE_ENABLED)
            cashoutFeePercentage = this.getString(Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE)!!
            cashoutFixedFee = this.getInt(Constants.BUNDLE_CASHOUT_FEE_LABEL)
            purpose = this.getString(Constants.BUNDLE_CASHOUT_PURPOSE)!!
            orderReachToNextLevel = this.getInt("orderReachToNextLevel")
            tripsReachToNextLevel = this.getInt("tripsReachToNextLevel")
            orderReachToNextLevel1 = this.getBoolean("orderReachToNextLevel1")
            daysReachToNextLevel = this.getBoolean("daysReachToNextLevel")
            cashoutNextLevelPercentage = this.getInt("cashoutNextLevelPercentage")
            binding.edtAmount.requestFocus()
            if (!price.contains(".")) {
                if (price.contains("₹"))
                    binding.edtAmount.hint = price.subSequence(1, price.length)
                else {
                    binding.edtAmount.hint = price
                }
            } else {
                binding.edtAmount.hint = price.subSequence(1, price.indexOf("."))
            }
            UXCam.setAutomaticScreenNameTagging(false)
            UXCam.tagScreenName(context?.getString(R.string.cashout_transaction_fragment))

            eprice = this.getString(Constants.EPRICE).toString()
//            binding?.setPrice?.text = price
            if (cashoutEnabled) {
                if (cashoutFeePercentage.toDouble() > 0) {
                    if (eprice.contains(".")) {
                        binding.trnCndText.text = getString(R.string.tnc_card_text) +
                                eprice.subSequence(0, eprice.indexOf(".")) +
                                getString(R.string.tnc_card_text_two)
                    } else {
                        binding.trnCndText.text =
                            getString(R.string.tnc_card_text) + eprice + getString(R.string.tnc_card_text_two)
                    }
                } else {
                    binding.trnCndText.setTextColor(requireContext().getColor(R.color.default_200))
                }
            } else {
                if (eprice.contains(".")) {
                    binding.trnCndText.setTextColor(requireContext().getColor(R.color.black))
                    binding.trnCndText.text = getString(R.string.tnc_card_text) +
                            eprice.subSequence(0, eprice.indexOf(".")) +
                            getString(R.string.tnc_card_text_two)
                } else {
                    binding.trnCndText.text =
                        getString(R.string.tnc_card_text) + eprice + getString(R.string.tnc_card_text_two)
                }
            }


        }

        initViews()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initViews() {
        showCashoutExpInfo()
        fa = FirebaseAnalytics.getInstance(requireContext())
        binding.setPrice.setOnClickListener {
            binding.edtAmount.requestFocus()
        }
        viewEarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]

        val userName = PrefrenceUtils.retriveData(activity, Constants.USERNAME)
        val phoneNumber = PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER)
        binding.apply {
            tvTransHitstoryHeading.text = userName
            tvTransNumber.text = phoneNumber
        }
        binding.cashoutGotIt.setOnClickListener {
            try {
                if (binding.edtAmount.text.toString().isNotEmpty()) {

                    if (binding.edtAmount.text.toString().toDouble()!! >=
                        PrefrenceUtils.retriveData(
                            requireContext(),
                            Constants.MINIMUM_ELIGIBLE
                        ).toDouble()
                        && binding.edtAmount.text.toString().toDouble() <=
                        price.substring(2).toDouble()
                    ) {
                        count = 1;
                        binding.ctaDetails.visibility = VISIBLE
                        binding.cashoutGotIt.visibility =GONE
                        binding.transferMoney.isEnabled = true
                        binding.transferMoney.isClickable = true
                        binding.transferMoney.text =
                            "" + context?.getString(R.string.transfer_rupees) + binding.edtAmount.text.toString()
                    } else {
                        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                .equals("en")
                        ) {
                            Toast.makeText(
                                requireContext(), "Please enter a value between ₹" +
                                        PrefrenceUtils.retriveData(
                                            requireContext(),
                                            Constants.MINIMUM_ELIGIBLE
                                        )
                                        + " and " + price, Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "कृपया" +
                                        PrefrenceUtils.retriveData(
                                            requireContext(),
                                            Constants.MINIMUM_ELIGIBLE
                                        )
                                        + " से " + price + " के बीच का कोई मान डालें",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                            .equals("en")
                    ) {
                        Toast.makeText(
                            requireContext(), "Please enter a value between ₹" +
                                    PrefrenceUtils.retriveData(
                                        requireContext(),
                                        Constants.MINIMUM_ELIGIBLE
                                    )
                                    + " and " + price, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(), "कृपया " +
                                    PrefrenceUtils.retriveData(
                                        requireContext(),
                                        Constants.MINIMUM_ELIGIBLE
                                    )
                                    + " से " + price + " के बीच का कोई मान डालें", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                context.runCatching {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG)
                        .show();
                }
            }

        }
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.tvTransHitstoryHeading.text =
            PrefrenceUtils.retriveData(requireContext(), Constants.USERNAME)
                ?: ""
        binding.tvTransNumber.text =
            PrefrenceUtils.retriveData(requireContext(), Constants.PHONENUMBER) ?: ""
        binding.transferMoney.setOnClickListener {
            binding.transferMoney.isEnabled = false
            binding.transferMoney.isClickable = false
            payMeMoney(binding.edtAmount.text.toString())
        }


        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (count > 0) {
                    if (binding.edtAmount.text.isNotEmpty())
                        if (binding.edtAmount.text.toString().toInt() > 100) {
                            binding.ctaDetails.visibility = View.GONE
                            binding.cashoutGotIt.visibility = VISIBLE

                        }
                }

            }

            override fun afterTextChanged(editable: Editable) {
                if (binding.edtAmount.length() > 0) {
                    if (cashoutEnabled) {
                        binding.trnCndText.setTextColor(context!!.getColor(R.color.default_200))
                        val stringAmount = binding.edtAmount.text.toString()
                        var percentageAmount: BigDecimal? = null
                        try {
                            percentageAmount =
                                BigDecimal(stringAmount.toDouble() / 100) * BigDecimal(
                                    cashoutFeePercentage
                                )
                        } catch (e: Exception) {

                        }
                        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                                .equals("en")
                        ) {
                            if (cashoutFeePercentage.toDouble() > 0) {
                                binding.trnCndText.text =
                                    getString(R.string.trnCndText1) + " " + cashoutFeePercentage + getString(
                                        R.string.trnCndText2
                                    ) + "${
                                        percentageAmount?.setScale(
                                            2,
                                            RoundingMode.HALF_EVEN
                                        )
                                    } " + getString(R.string.trnCndText3)
                            } else {
                                binding.trnCndText.text =
                                    getString(R.string.trnCndText4) + " ₹" + cashoutFixedFee + " " + getString(
                                        R.string.trnCndText5
                                    )
                            }
                        } else {
                            if (cashoutFeePercentage.toDouble() > 0) {
                                binding.trnCndText.text = "" +
                                        getString(R.string.trnCndText6) + "${
                                    percentageAmount?.setScale(
                                        2,
                                        RoundingMode.HALF_EVEN
                                    )
                                } " + getString(R.string.trnCndText7)
                            } else {
                                binding.trnCndText.text =
                                    getString(R.string.trnCndText6) + cashoutFixedFee + " " + getString(
                                        R.string.trnCndText7
                                    )
                            }
                        }

                    }
                } else {
                    binding.trnCndText.setTextColor(context!!.getColor(R.color.black))
                    if (!eprice.contains(getString(R.string.dot))) {
                        binding.trnCndText.text = getString(R.string.tnc_card_text) +
                                eprice.subSequence(0, eprice.indexOf(getString(R.string.dot))) +
                                getString(R.string.tnc_card_text_two)
                    } else {
                        binding.trnCndText.text =
                            getString(R.string.tnc_card_text) + eprice + getString(R.string.tnc_card_text_two)
                    }
                }
            }
        })

        binding.parentContainer.setOnTouchListener { _, _ ->
            binding.edtAmount.clearFocus()
            hideKeyboard(activity, binding.edtAmount)
            true
        }
        //API.
        getBankDetail()
        getDataFromRemoteConfig()
    }

    private fun showCashoutExpInfo() {
        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE).equals("en")) {
            if (orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = VISIBLE
                binding.textOnePlus.visibility = VISIBLE
                binding.tvActiveDays.visibility = VISIBLE
                binding.tvTextOne.text = "Complete $orderReachToNextLevel more Trips"
                binding.tvActiveDays.text = "Stay Active for $tripsReachToNextLevel more days"
            } else if (orderReachToNextLevel1 && !daysReachToNextLevel) {
                binding.tvTextOne.visibility = VISIBLE
                binding.tvActiveDays.visibility = GONE
                binding.textOnePlus.visibility = GONE
                binding.tvActiveDays.text = "Stay Active for $tripsReachToNextLevel more days"
            } else if (!orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = GONE
                binding.textOnePlus.visibility = GONE
                binding.tvActiveDays.visibility = VISIBLE
                binding.tvTextOne.text = "Complete $orderReachToNextLevel more Trips"
            } else {
                binding.trmCardDetailUp.visibility = GONE
            }
        } else {
            if (orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = VISIBLE
                binding.textOnePlus.visibility = VISIBLE
                binding.tvActiveDays.visibility = VISIBLE
                binding.tvTextOne.text = "$orderReachToNextLevel और यात्राएं पूरी करें"
                binding.tvActiveDays.text = "$tripsReachToNextLevel और दिनों तक सक्रिय रहें"
            } else if (orderReachToNextLevel1 && !daysReachToNextLevel) {
                binding.tvTextOne.visibility = VISIBLE
                binding.tvActiveDays.visibility = GONE
                binding.textOnePlus.visibility = GONE
                binding.tvTextOne.text = "$orderReachToNextLevel और यात्राएं पूरी करें"
            } else if (!orderReachToNextLevel1 && daysReachToNextLevel) {
                binding.tvTextOne.visibility = GONE
                binding.textOnePlus.visibility = GONE
                binding.tvActiveDays.visibility = VISIBLE
                binding.tvActiveDays.text = "$tripsReachToNextLevel और दिनों तक सक्रिय रहें"
            } else {
                binding.trmCardDetailUp.visibility = GONE
            }
        }

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

    private fun hideKeyboard(activity: FragmentActivity?, edtAmount: EditText) {
        val inputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edtAmount.windowToken, 0)
    }

    @SuppressLint("SetTextI18n")
    private fun getBankDetail() {
        viewEarnViewModel.getBankDetail().observe(viewLifecycleOwner) {
            if (it?.statusCode == 500) {
                findNavController().navigate(R.id.nav_error_fragment)
            } else {
                binding.transferMoney.text =
                    getString(R.string.transfer_rupees) + { binding.edtAmount.text }

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

    private fun payMeMoney(amount: String) {
        viewEarnViewModel.paymentMoney(amount, purpose).observe(viewLifecycleOwner) {
            if (it != null) {
                binding.transferMoney.isEnabled = true
                binding.transferMoney.isClickable = true
                if (it.success) {
                    val bundle = Bundle()
                    setInstrumentation()
                    var message = ""
                    message = getString(R.string.cashout_request_is_processed)
                    bundle.putString(Constants.MESSAGE, message)
                    bundle.putString("type", "cashout")
                    bundle.putInt("orderReachToNextLevel", orderReachToNextLevel)
                    bundle.putInt("tripsReachToNextLevel", tripsReachToNextLevel)
                    bundle.putBoolean("orderReachToNextLevel1", orderReachToNextLevel1)
                    bundle.putBoolean("daysReachToNextLevel", daysReachToNextLevel)
                    bundle.putInt("percentage", cashoutNextLevelPercentage)
                    //Toast.makeText(requireContext(), "${it?.message}", Toast.LENGTH_SHORT).show()
                    val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.fade_in)
                        .setExitAnim(R.anim.fade_out)
                        .build()
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.nav_fragment_payment_sucessful, bundle, animationOptions)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cashout_failed_please_try_again_later),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        try {
            properties.addAttribute(
                Constants.CASHED_AMOUNT,
                binding.edtAmount.text.toString().toInt()
            )
            properties.addAttribute(Constants.CASHED_FEE_PERCENT, cashoutFeePercentage.toDouble())
            properties.addAttribute(Constants.CASHED_FIXED_FEE, cashoutFixedFee)
            properties.addAttribute(Constants.BUNDLE_CASHOUT_PURPOSE, purpose)
            bundle.putString(Constants.CASHED_AMOUNT, binding.edtAmount.text.toString())
            bundle.putString(Constants.CASHED_AMOUNT, binding.edtAmount.text.toString())
            val data = HashMap<String, Any>()
            data[Constants.CASHED_AMOUNT] = binding.edtAmount.text.toString()
            captureAllEvents(requireContext(), Constants.CASHOUT_AVAIL, data, properties)
            requireContext().startBlitzSurvey(requireContext(),Constants.CASHOUT_AVAIL)
//            MoEHelper.getInstance(requireContext()).trackEvent(Constants.CASHOUT_AVAIL, properties)
//            BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.CASHOUT_AVAIL)
//            fa.logEvent(Constants.CASHOUT_AVAIL, bundle)
//            UXCam.logEvent(Constants.CASHOUT_AVAIL, data)
        } catch (e: Exception) {
        }
        handler.postDelayed({
            if (trigger != null) {
                for (i in 0 until trigger?.trigger?.size!!) {
                    if (!PrefrenceUtils.retriveDataInBoolean(
                            context,
                            Constants.CASHOUT_AVAIL
                        )
                    ) {
                        if (PrefrenceUtils.retriveDataInBoolean(
                                context,
                                Constants.ISFEEDBACKSESSION
                            )
                        ) {
                            if (trigger?.trigger?.get(i)?.trigger_event.equals(Constants.CASHOUT_AVAIL)) {
                                FeedbackBottomsheet(requireContext(), Constants.CASHOUT).show()
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


    fun showKeyBoard(activity: Activity, view: EditText) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            view.applicationWindowToken,
            InputMethodManager.SHOW_FORCED, 0
        )
    }

    private fun hideKeyboard(activity: Activity?, view: EditText) {
        val imm: InputMethodManager = activity?.applicationContext
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}