package com.vahan.mitra_playstore.view.ratecard.bottomSheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentBottomSheetCalculatorBinding
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.ratecard.adapter.OtherPayAdapter
import com.vahan.mitra_playstore.view.ratecard.adapter.WeeklyOrderAdapter
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO
import kotlinx.android.synthetic.main.fragment_bottom_sheet_calculator.*
import kotlinx.android.synthetic.main.fragment_rate_card.*
import kotlin.math.abs


class BottomSheetCalculatorFragment(
    context: Context,
    private val calculatorArrayDataList: ArrayList<RateCardDetailsDTO.Incentive.CalculatorArray>,
    private val calculatorLabelsData: RateCardDetailsDTO.Incentive.CalculatorLabels,
    private val weeklyBonus: RateCardDetailsDTO.Incentive.WeeklyBonus,
    private val additionInfoList: List<RateCardDetailsDTO.Incentive.Additional?>,
    private val incentiveMapping: List<RateCardDetailsDTO.Incentive.IncentiveMapping?>?
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    private lateinit var binding: FragmentBottomSheetCalculatorBinding
    // variable for checking if the list size is less than the CalculatorArray arraylist
    var isCalcArrayValidateLimit: Boolean = false
    var selectedCheckBoxSet = HashSet<String>()
    var selectedWeekDateMappingKey: String? = null
    private var selectedAdditionalPlanList: List<String?>? = null
    private  var selectedAdditionalPlanString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBottomSheetCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        setDefaultAdapter()
        // setting the by default data
        setDataForInitialStage()
        editTextTextChangeListener()
        binding.sbOrder.leftSeekBar.setIndicatorText("0 Orders")
        seekbarRangeChangeListener()
        setUpInstrumentation()
    }


    private fun setDefaultAdapter() {
        binding.rvWeeklyOrders.adapter = WeeklyOrderAdapter(
            context,
            weeklyBonus.condition,
            null
        )
        binding.rvAddition.adapter = OtherPayAdapter(
            context,
            additionInfoList,
            null,
            selectedAdditionalPlanString
        )
    }

    private fun setUpInstrumentation() {
        captureAllEvents(context, "zepto_calculator_viewed", HashMap(), Properties())
    }

    private fun seekbarRangeChangeListener() {
        binding.sbOrder.setOnRangeChangedListener(object : OnRangeChangedListener {
            @SuppressLint("SetTextI18n")
            override fun onRangeChanged(
                view: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                // setting the left value of seekbar
                if(leftValue.toInt()>0 && leftValue.toInt()<calculatorArrayDataList.size-1) {
                    view.leftSeekBar.setIndicatorText(
                        "${leftValue.toInt() - 1} ${
                            context.getString(
                                R.string.orders
                            )
                        } "
                    )
                    // setting the edit text value
                    etOrderValue.setText((leftValue.toInt()).toString())
                    setWeeklyBonusCondition(leftValue.toInt())
                    setLogicForShowingUpdateCalcValue(leftValue.toInt())
                    setLogicForSettingOtherPayData(leftValue)
                    setLogicForSettingColor(leftValue)
                }
            }


            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

        })
    }

    private fun setLogicForSettingOtherPayData(leftValue: Float) {
        selectedAdditionalPlanList = ArrayList()
        selectedAdditionalPlanString = ""
        if (leftValue.toInt() == 0) {
            binding.rvAddition.adapter = OtherPayAdapter(context, additionInfoList,calculatorArrayDataList[leftValue.toInt()].earnings,selectedAdditionalPlanString)
        } else {
            for (i in 0 until incentiveMapping!!.size) {
                if (selectedWeekDateMappingKey == incentiveMapping[i]!!.orderString) {
                    selectedAdditionalPlanList = incentiveMapping[i]!!.incentives
                }
            }
            if (selectedAdditionalPlanList?.isNotEmpty() == true) {
               first@ for (k in 0 until selectedAdditionalPlanList!!.size) {
                        for (j in 0 until calculatorArrayDataList[leftValue.toInt()].earnings!!.size) {
                            if (selectedAdditionalPlanList!![k]==calculatorArrayDataList[leftValue.toInt()].earnings!![j]!!.key) {
                                selectedAdditionalPlanString = selectedAdditionalPlanList!![k]
                                break@first
                            }
                        }
                    }
                }
                binding.rvAddition.adapter = OtherPayAdapter(
                    context,
                    additionInfoList,
                    calculatorArrayDataList[leftValue.toInt()].earnings,
                    selectedAdditionalPlanString
                )
            }
        }


    private fun setLogicForShowingUpdateCalcValue(leftValue: Int) {
        if (leftValue == 0) {
            tvTotalAmt.text = "0"
        } else {
            checkSelectedCheckBox()
            val listOfMapping = parseLogicForSettingValues()
            pickTheIncentiveMapping(listOfMapping, calculatorArrayDataList[leftValue - 1])
        }
    }

    private fun setLogicForSettingColor(leftValue: Float) {
        var isColorSelected = false
        for (i in 0 until incentiveMapping!!.size) {
            if (selectedWeekDateMappingKey == incentiveMapping[i]!!.orderString) {
                selectedAdditionalPlanList = incentiveMapping[i]!!.incentives
            }
        }
        if (selectedAdditionalPlanList?.isNotEmpty() == true) {
            for (i in 0 until selectedAdditionalPlanList!!.size) {
                for(j in 0 until calculatorArrayDataList[leftValue.toInt()-1].earnings!!.size){
                    if(calculatorArrayDataList[leftValue.toInt()-1].earnings?.get(j)?.key == "minimum_guarantee_weekly"){
                        if (selectedAdditionalPlanList!![i] == "minimum_guarantee_weekly") {
                            isColorSelected = true
                        }
                    }
                }

            }
            if (isColorSelected) {
                binding.rlOrderDetails.setBackgroundColor(context.getColor(R.color.light_green_F2FFF8))
                binding.tvCondition.visibility = View.VISIBLE
            } else {
                binding.rlOrderDetails.setBackgroundColor(context.getColor(R.color.light_pink2))
                binding.tvCondition.visibility = View.INVISIBLE
            }
        }else{
            binding.rlOrderDetails.setBackgroundColor(context.getColor(R.color.light_pink2))
            binding.tvCondition.visibility = View.INVISIBLE
        }

    }

    private fun setWeeklyBonusCondition(leftValue: Int) {
        // setting the weekly order list & highlighting the orders range completed
        for (i in 0 until weeklyBonus.condition?.size!! - 1) {
            when (leftValue) {
                in 0 until weeklyBonus.condition[0]?.orderCount!! ->
                    rvWeeklyOrders.adapter = WeeklyOrderAdapter(
                        context,
                        weeklyBonus.condition,
                        null
                    )
                in weeklyBonus.condition[i]?.orderCount!! until weeklyBonus.condition[i + 1]?.orderCount!! ->
                    rvWeeklyOrders.adapter = WeeklyOrderAdapter(
                        context,
                        weeklyBonus.condition,
                        i
                    )
                in weeklyBonus.condition[weeklyBonus.condition.size - 1]?.orderCount!! until calculatorArrayDataList.size ->
                    rvWeeklyOrders.adapter = WeeklyOrderAdapter(
                        context,
                        weeklyBonus.condition,
                        weeklyBonus.condition.size - 1
                    )

            }
        }
    }

    private fun pickTheIncentiveMapping(
        listOfMapping: List<String?>?,
        calculatorArray: RateCardDetailsDTO.Incentive.CalculatorArray
    ) {
        if (listOfMapping!!.isNotEmpty()) {
            first@  for (i in 0 until listOfMapping!!.size) {
                for (j in 0 until calculatorArray.earnings!!.size) {
                    if (listOfMapping[i] == calculatorArray.earnings[j]!!.key) {
                        Log.d("ITEMS", "pickTheIncentiveMapping: ${listOfMapping[i]}")
                        setBreakUpDetails(calculatorArray.earnings[j])
                        break@first
                    }
                }
            }
        } else {
            for (j in 0 until calculatorArray.earnings!!.size) {
                if (calculatorArray.earnings[j]!!.key == "base") {
                    Log.d("ITEMS_NOT", "pickTheIncentiveMapping: ${calculatorArray.earnings[j]}")
                    setBreakUpDetails(calculatorArray.earnings[j])
                }
            }
        }
    }

    private fun setBreakUpDetails(earnings: RateCardDetailsDTO.Incentive.CalculatorArray.Earnings?) {
        binding.apply {
            tvPerOrderPaymentAmt.text = earnings?.perOrder ?: ""
            tvPerOrder.text = "x " + earnings?.perOrder
            tvTotalAmt.text = earnings?.total ?: ""
            tvTotalAmtBreakup.text = earnings?.breakup ?: ""
        }
    }

    private fun parseLogicForSettingValues(): List<String?>? {
        // Compare key with incentiveMapping

        // NEED TO UPDATE LIST
        for (i in 0 until incentiveMapping?.size!!) {
            if (selectedWeekDateMappingKey == incentiveMapping[i]!!.orderString) {
                Log.d("ITEMS_SELECTED", "pickTheIncentiveMapping: ${selectedWeekDateMappingKey}")
                return incentiveMapping[i]!!.incentives
            }
        }
        return emptyList()
    }

    private fun editTextTextChangeListener() {
        etOrderValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty() || s.toString() > "0") {
                    // user has entered the number of orders greater than Calculator array size in edit text
                    isCalcArrayValidateLimit =
                        if (Integer.parseInt(s.toString()) > calculatorArrayDataList.size) {
                            Toast.makeText(
                                context,
                                "Please enter the orders value less than " + calculatorArrayDataList.size,
                                Toast.LENGTH_SHORT
                            ).show()
                            etOrderValue.setText("0")
                            etOrderValue.requestFocus()
                            binding.sbOrder.setProgress(0F)
                            true
                        } else {
                            false
                        }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                etOrderValue.setSelection(etOrderValue.text.length)
                // checking if user has manually edited the text (for orders) in the edit text
                val userChange = abs(count - before) == 1
                // if user has manually edited the no. of orders in edit text & orders lies within the range
                  if (userChange && !isCalcArrayValidateLimit) {
                      if (s.toString().isNotEmpty()) {
                          if (s.toString().toInt() < calculatorArrayDataList.size - 1 && s.toString()
                                  .toInt() > 1
                          ) {
                              setWeeklyBonusCondition(s.toString().toInt())
                              setLogicForShowingUpdateCalcValue(s.toString().toInt())
                              setLogicForSettingOtherPayData(s.toString().toFloat())
                              setLogicForSettingColor(s.toString().toFloat())
                              binding.sbOrder.setProgress(s.toString().toFloat())
                          } else {
                              binding.tvTotalAmt.text = "0"
                          }
                      } else {
                          binding.tvTotalAmt.text = "0"
                      }
                  }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setDataForInitialStage() {
        binding.apply {
            tvTotalAmt.text = "0"
            tvCalculator.text = calculatorLabelsData.title
            tvCalculatorDesc.text = calculatorLabelsData.label
            tvBasePay.text = calculatorLabelsData.basePayTitle
            tvWeekly.text = calculatorLabelsData.type
            tvBasePayAmt.text =
                calculatorLabelsData.basePayPrefix + calculatorLabelsData.basePayAmount
            tvShiftIncentiveAmt.text =
                calculatorLabelsData.shiftIncentivePrefix + calculatorLabelsData.shiftIncentiveAmount
            tvShiftIncentive.text = calculatorLabelsData.shiftIncentiveTitle
            tvWeeklyOrders.text = calculatorLabelsData.weeklyOrderLabel
            tvPerOrderPayment.text = calculatorLabelsData.perOrderPaymentTitle
            tvOrders.text = calculatorLabelsData.orderLabel
            tvOrderAmt.text = calculatorLabelsData.amountLabel
            tvEndOrders.text =
                calculatorArrayDataList.size.toString() + " " + context.getString(R.string.orders)
            sbOrder.setRange(0F, calculatorArrayDataList.size.toFloat())
        }
    }

    private fun checkSelectedCheckBox() {
        selectedWeekDateMappingKey = ""
        selectedCheckBoxSet = HashSet()
        binding.apply {
            if (cbMonday.isChecked) {
                selectedCheckBoxSet.add("1")
            }
            if (cbTuesday.isChecked) {
                selectedCheckBoxSet.add("2")
            }
            if (cbWed.isChecked) {
                selectedCheckBoxSet.add("3")
            }
            if (cbThursday.isChecked) {
                selectedCheckBoxSet.add("4")
            }
            if (cbFriday.isChecked) {
                selectedCheckBoxSet.add("5")
            }
            if (cbSat.isChecked) {
                selectedCheckBoxSet.add("6")
            }
            if (cbSun.isChecked) {
                selectedCheckBoxSet.add("7")
            }
        }
        selectedWeekDateMappingKey = selectedCheckBoxSet.sorted().joinToString(separator = "")
        Log.d("CHECKED", "onRangeChanged: $selectedWeekDateMappingKey")
    }
}