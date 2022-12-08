package com.vahan.mitra_playstore.view.bankinfo.view


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentAddBankBinding
import com.vahan.mitra_playstore.models.CheckCorrectIFSCModel
import com.vahan.mitra_playstore.models.VerifyModel
import com.vahan.mitra_playstore.models.kotlin.Account
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.HomeActivity
import com.vahan.mitra_playstore.view.bankinfo.viewmodel.BankViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddBankFragment : Fragment() {
    private var binding: FragmentAddBankBinding? = null
    private var dialogLoader: Dialog? = null
    private var fa: FirebaseAnalytics? = null
    private var is_verify_bank_for_non_payroll: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for getActivity() fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_bank,
            container,
            false
        )
//        initView()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            is_verify_bank_for_non_payroll = getString("nav")
            PrefrenceUtils.insertData(requireContext(), "nav", is_verify_bank_for_non_payroll)
        }
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding!!.enterName.mainRelative.setOnTouchListener { _: View?, event: MotionEvent? ->
            binding!!.enterName.edtNameLayout.clearFocus()
            hideKeyboard(activity, binding!!.enterName.edtNameLayout)
            true
        }
        initLoader(requireContext())
        fa = FirebaseAnalytics.getInstance(requireActivity())
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("AddBank Fragment")
        clickListener()
    }

    private fun clickListener() {
        binding!!.enterName.saveButton.setOnClickListener { checkValidationForName() }
        binding!!.enterNumber.saveButton.setOnClickListener { checkValidationForEnterNumber() }
        binding!!.enterReNumber.saveButton.setOnClickListener { checkValidationForReEnterNumber() }
        binding!!.enterIfsc.saveButton.setOnClickListener { createAccountApi() }
        binding!!.enterName.backButton.setOnClickListener {
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
        binding!!.enterNumber.backButton.setOnClickListener {
            binding!!.enterName.root.visibility = View.VISIBLE
            binding!!.enterReNumber.root.visibility = View.GONE
            binding!!.enterNumber.root.visibility = View.GONE
            binding!!.enterIfsc.root.visibility = View.GONE
        }
        binding!!.enterReNumber.backButton.setOnClickListener {
            binding!!.enterName.root.visibility = View.GONE
            binding!!.enterReNumber.root.visibility = View.GONE
            binding!!.enterNumber.root.visibility = View.VISIBLE
            binding!!.enterIfsc.root.visibility = View.GONE
        }
        binding!!.enterIfsc.backButton.setOnClickListener { view: View? ->
            binding!!.enterName.root.visibility = View.GONE
            binding!!.enterReNumber.root.visibility = View.VISIBLE
            binding!!.enterNumber.root.visibility = View.GONE
            binding!!.enterIfsc.root.visibility = View.GONE
        }
        binding!!.enterIfsc.edtIfscLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().length == 11) {
                    dialogLoader?.show()
                    checkCorrectIFSCCode(editable.toString())
                }
                var s: String = editable.toString()
                if (s != s.uppercase(Locale.getDefault())) {
                    s = s.uppercase(Locale.getDefault())
                    binding!!.enterIfsc.edtIfscLayout.setText(s)
                    binding!!.enterIfsc.edtIfscLayout.setSelection(binding!!.enterIfsc.edtIfscLayout.length()) //fix reverse texting
                }
            }
        })
        binding!!.enterName.edtNameLayout.filters = arrayOf<InputFilter>(
            object : InputFilter {
                override fun filter(
                    cs: CharSequence, start: Int,
                    end: Int, spanned: Spanned, dStart: Int, dEnd: Int
                ): CharSequence {
                    // TODO Auto-generated method stub
                    if (cs == "") { // for backspace
                        return cs
                    }
                    return if (cs.toString().matches(Regex("[a-zA-Z ]+"))) {
                        cs
                    } else ""
                }
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkValidationForReEnterNumber() {
        if (binding!!.enterReNumber.edtNumberLayout.text.toString().equals("", ignoreCase = true)) {
            binding!!.enterReNumber.tilAccountNumber.error =
                resources.getString(R.string.account_no_doent_match)
            binding!!.enterReNumber.edtNumberLayout.error = null
        } else if (!binding!!.enterReNumber.edtNumberLayout.text.toString()
                .equals(binding!!.enterNumber.edtNumberLayout.text.toString(), ignoreCase = true)
        ) {
            binding!!.enterReNumber.tilAccountNumber.error =
                resources.getString(R.string.account_no_doent_match)
            binding!!.enterReNumber.edtNumberLayout.error = null
        } else {
            binding!!.enterName.root.visibility = View.GONE
            binding!!.enterNumber.root.visibility = View.GONE
            binding!!.enterReNumber.root.visibility = View.GONE
            binding!!.enterIfsc.root.visibility = View.VISIBLE
            binding!!.enterIfsc.root.requestFocus()
            showKeyBoard(requireActivity(), binding!!.enterIfsc.edtIfscLayout)
            binding!!.enterIfsc.containerIfsc.setOnTouchListener { _: View?, _: MotionEvent? ->
                binding!!.enterIfsc.edtIfscLayout.clearFocus()
                hideKeyboard(activity, binding!!.enterIfsc.edtIfscLayout)
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkValidationForEnterNumber() {
        if (binding!!.enterNumber.edtNumberLayout.text.toString().equals("", ignoreCase = true)) {
            binding!!.enterNumber.tilAccountNumber.error =
                resources.getString(R.string.enter_account_number)
            binding!!.enterNumber.edtNumberLayout.error = null
        } else {
            binding!!.enterName.root.visibility = View.GONE
            binding!!.enterNumber.root.visibility = View.GONE
            binding!!.enterIfsc.root.visibility = View.GONE
            binding!!.enterReNumber.root.visibility = View.VISIBLE
            binding!!.enterReNumber.root.requestFocus()
            showKeyBoard(requireActivity(), binding!!.enterReNumber.edtNumberLayout)
            binding!!.enterReNumber.containerReEnter.setOnTouchListener { _: View?, _: MotionEvent? ->
                binding!!.enterReNumber.edtNumberLayout.clearFocus()
                hideKeyboard(activity, binding!!.enterReNumber.edtNumberLayout)
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkValidationForName() {
        if (binding!!.enterName.edtNameLayout.text.toString().equals("", ignoreCase = true)) {
            binding!!.enterName.tilAccountName.error =
                resources.getString(R.string.enter_account_name)
            binding!!.enterName.edtNameLayout.error = null
        } else {
            binding!!.enterName.root.visibility = View.GONE
            binding!!.enterNumber.root.visibility = View.VISIBLE
            binding!!.enterNumber.root.requestFocus()
            showKeyBoard(requireActivity(), binding!!.enterNumber.edtNumberLayout)
            binding!!.enterReNumber.root.visibility = View.GONE
            binding!!.enterIfsc.root.visibility = View.GONE
            binding!!.enterNumber.containerAccountNumber.setOnTouchListener { _: View?, _: MotionEvent? ->
                binding!!.enterNumber.edtNumberLayout.clearFocus()
                hideKeyboard(activity, binding!!.enterNumber.edtNumberLayout)
                true
            }
        }
    }

    fun initLoader(context: Context) {
        dialogLoader = Dialog(context)
        dialogLoader?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader?.setCancelable(false)
        dialogLoader?.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView? = dialogLoader?.findViewById(R.id.animate_icon)
        val rotate = RotateAnimation(
            0f, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation?.startAnimation(rotate)
    }

    private fun checkCorrectIFSCCode(ifscNumber: String) {
        val viewSharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]
        viewSharedViewModel.checkCorrectIfsc(ifscNumber).observe(
            this
        ) { correctIfscModel: CheckCorrectIFSCModel? ->
            if (correctIfscModel != null) {
                dialogLoader?.dismiss()
                if (!correctIfscModel.bank.equals("Not Found", ignoreCase = true)) {
                    setTextOfIFSC(correctIfscModel)
                } else {
                    resetData()
                }
            }
        }
    }

    private fun resetData() {
        binding!!.enterIfsc.tilAccountNumber.error = resources.getString(R.string.invalid_ifsc)
        binding!!.enterIfsc.ifscText.error = null
        binding!!.enterIfsc.ifscText.visibility = View.GONE
        requireActivity().runOnUiThread {
            Toast.makeText(
                activity,
                resources.getString(R.string.invalid_ifsc),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextOfIFSC(correctIfscModel: CheckCorrectIFSCModel) {
        binding!!.enterIfsc.ifscText.text =
            correctIfscModel.bank + resources.getString(R.string.coma_) + correctIfscModel.getBranch()
        binding!!.enterIfsc.tilAccountNumber.error = null
        binding!!.enterIfsc.edtIfscLayout.error = null
        binding!!.enterIfsc.ifscText.visibility = View.VISIBLE
    }

    private fun createAccountApi() {
        val account = Account(
            Objects.requireNonNull(binding!!.enterNumber.edtNumberLayout.text).toString(),
            Objects.requireNonNull(binding!!.enterIfsc.edtIfscLayout.text).toString(),
            Objects.requireNonNull(binding!!.enterName.edtNameLayout.text).toString()
        )
        val viewBankViewModel: BankViewModel =
            ViewModelProvider(requireActivity())[BankViewModel::class.java]
        lifecycleScope.launchWhenStarted {
            viewBankViewModel.createAccount(account)
                .collect {
                    when (it) {
                        ApiState.Loading -> {
                            dialogLoader?.show()
                        }
                        is ApiState.Success -> {
                            dialogLoader?.dismiss()
                            if (it.data.status.equals("validated", ignoreCase = true)) {
                                resetAllErrorValue()
                                validationSuccess(it.data)
                            } else if (it.data.status.equals("failed", ignoreCase = true)) {
                                resetAllErrorValue()
                                resetFailedData(it.data)
                            } else if (it.data.status.equals("Pending", ignoreCase = true) ||
                                it.data.status.equals("Requested", ignoreCase = true)
                            ) {
                                resetAllErrorValue()
                                verificationPendingData(it.data)
                            } else {
                                resetAllErrorValue()
                                resetFailedData(it.data)
                            }
                            // TODO() redirect back to web page for when user enters the add screen from non payroll account
                        }
                        is ApiState.Failure -> {
                            dialogLoader?.dismiss()
                        }
                    }

                }
        }

    }

    private fun verificationPendingData(verifyModel: VerifyModel) {
        if (verifyModel.status.equals("pending", ignoreCase = true)) {
            Toast.makeText(
                activity,
                verifyModel.message,
                Toast.LENGTH_LONG
            ).show()
            Navigation.findNavController(binding!!.root).navigate(R.id.nav_fragment_addBank_view)
        } else {
            Navigation.findNavController(binding!!.root).navigate(R.id.nav_bank_detail_saved)
        }
    }

    private fun resetFailedData(verifyModel: VerifyModel) {
        Toast.makeText(activity, verifyModel.message, Toast.LENGTH_LONG).show()
    }

    private fun validationSuccess(verifyModel: VerifyModel) {
        val message: String = verifyModel.message
        val bundle = Bundle()
        bundle.putString(Constants.MESSAGE, message)
        bundle.putString("type", "bank")
        bundle.putInt("orderReachToNextLevel", 0)
        bundle.putInt("tripsReachToNextLevel", 0)
        bundle.putBoolean("orderReachToNextLevel1", false)
        bundle.putBoolean("daysReachToNextLevel", false)
        bundle.putInt("percentage", 0)
        //Toast.makeText(requireContext(), "${it?.message}", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.nav_fragment_addBank_view, bundle)
    }

    private fun resetAllErrorValue() {
        binding!!.enterName.tilAccountName.error = null
        binding!!.enterReNumber.edtNumberLayout.error = null
        binding!!.enterNumber.edtNumberLayout.error = null
        binding!!.enterIfsc.tilAccountNumber.error = null
        binding!!.enterIfsc.edtIfscLayout.error = null
        binding!!.enterName.tilAccountName.error = null
        binding!!.enterName.edtNameLayout.error = null
    }

    private fun hideKeyboard(activity: Activity?, view: EditText) {
        val imm: InputMethodManager = activity?.applicationContext
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyBoard(activity: Activity, view: EditText) {
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            view.applicationWindowToken,
            InputMethodManager.SHOW_FORCED, 0
        )
    }
}
