/**
 * Created by Prakhar Pandey
 * Date :- 04 Nov 2022
 * This fragment is an entry point for why user choose a saving calculator
 * This fragment contains two layouts one is Amount and other is Reason
 */

package com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.fragments
import android.annotation.SuppressLint
import android.app.Dialog
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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentWhySavingBinding
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.ApplySavingRequestModel
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.ReasonDTO
import com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.adapters.GetSavingDetailsAdapter
import com.vahan.mitra_playstore.view.experiments.savingcalculator.viewmodel.SavingCalculatorViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WhySavingFragment : Fragment() {
    private lateinit var viewSavingCalculatorViewModel: SavingCalculatorViewModel
    lateinit var binding: FragmentWhySavingBinding
    lateinit var dialogLoader : Dialog
    private var reasonList: ArrayList<ReasonDTO> = ArrayList()
    private var amountList: ArrayList<Int> = ArrayList()
    private var purpose = ""
    private var finalSelectedAmount : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_why_saving, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    // this method is used for initialized the view
    private fun initView() {
        initialized()
        setupInstrumentation("select plan")
        viewSavingCalculatorViewModel =
            ViewModelProvider(this)[SavingCalculatorViewModel::class.java]
        getDataForAmountAndReason()
        clickListener()
    }

    private fun setupInstrumentation(type : String) {
        val attribute : HashMap<String,Any> = HashMap()
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

    private fun clickListener() {
        // Apply Saving Button Click action
        binding.layoutTwoPurpose.saveButton.setOnClickListener {
            applySavings()
        }
        // change the layout from two to one
        binding.layoutTwoPurpose.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    // this function calls the saving Api and after apply successfully app navigate Home Page
    private fun applySavings() {
        if (finalSelectedAmount>0) {
            val resp = ApplySavingRequestModel(finalSelectedAmount,"WEEKLY")
            lifecycleScope.launchWhenStarted {
                viewSavingCalculatorViewModel.applySaving(resp).collect {
                    when (it) {
                        ApiState.Loading -> {
                            dialogLoader.show()
                        }
                        is ApiState.Success -> {
                            hideLoading()
                            if(it.data.success!!) {
                                setupInstrumentation("plan selected")
                                findNavController().navigate(R.id.nav_applied_fragment)
                            }
                            else
                                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                        is ApiState.Failure -> {
                            hideLoading()
                            findNavController()
                                .navigate(R.id.nav_error_fragment)
                        }
                    }
                }
            }

        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.please_enter_amount_greater_than_0),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // initialized loader
    private fun initialized() {
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

    // hide loader
    private fun hideLoading(){
        dialogLoader.dismiss()
    }

    // this function statically add an Amount and Reason in a list using custom data class
    @SuppressLint("SuspiciousIndentation")
    private fun getDataForAmountAndReason() {
        val rNine = ReasonDTO(
            "",
            "other",
            "Other",
            "अन्य"
        )
        reasonList.add(rNine)
        amountList.add(50)
        amountList.add(100)
        amountList.add(150)
        amountList.add(200)
        setReason()
    }

    // this method is used for setting the Adapter with reasonList and amountList
    private fun setReason() {
        val getSavingAdapter = GetSavingDetailsAdapter(
            requireActivity(),
            this@WhySavingFragment,
            reasonList, amountList, 0
        )
        binding.layoutOnePurpose.rvContainerSavings.addItemDecoration(
            ItemOffsetDecoration(
                2,
                HelperMethod.dpToPx(10f, activity),
                true
            )
        )
        binding.layoutOnePurpose.rvContainerSavings.adapter = getSavingAdapter
        binding.layoutOnePurpose.rvContainerSavings.layoutManager =
            GridLayoutManager(activity, 2)
        val getLoanAmountAdapter = GetSavingDetailsAdapter(
            requireActivity(),
            this@WhySavingFragment,
            reasonList,
            amountList,
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

    /*
     This function work as a callback from adapter
     pick a selected item return a reason in fragment
    */
    fun getPurposeSelectedData(key: String) {
        if (key != "") {
            binding.layoutOnePurpose.saveButtonOne.isEnabled = true
            binding.layoutOnePurpose.saveButtonOne.isClickable = true
            binding.layoutOnePurpose.saveButtonOne.setBackgroundResource(R.drawable.curver_corner_gold)
            purpose = key
        } else {
            binding.layoutOnePurpose.saveButtonOne.isEnabled = false
            binding.layoutOnePurpose.saveButtonOne.isClickable = false
            binding.layoutOnePurpose.saveButtonOne.setBackgroundResource(R.drawable.curve_corner_gold_disable)
        }
    }

    /*
     This function work as a callback from adapter
     pick a selected item return an amount in fragment
    */
    fun getAmountSelectedData(amount: Int) {
        if (amount > 0) {
            binding.layoutTwoPurpose.apply {
                saveButton.isEnabled = true
                saveButton.isClickable = true
                saveButton.setBackgroundResource(R.drawable.curver_corner_gold)
                edtAmount.requestFocus()
                edtAmount.hint = "$amount"
                edtAmount.setText("$amount")
                tvAmount.text = "$amount"
                finalSelectedAmount = amount
                edtAmount.setSelection(edtAmount.length())
               // setInstrumentationForSelectedData()
            }
        } else {
            binding.layoutTwoPurpose.saveButton.isEnabled = false
            binding.layoutTwoPurpose.saveButton.isClickable = false
            binding.layoutTwoPurpose.saveButton.setBackgroundResource(R.drawable.curve_corner_gold_disable)

        }
    }


}