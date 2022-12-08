package com.vahan.mitra_playstore.view.earn.view.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.CashoutAvailableDialogBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils

class CashoutAvaialbleDialog (
        context: Context,
        private val userLevel: Int?
        ): AlertDialog(context, com.vahan.mitra_playstore.R.style.DialogTheme){

        private lateinit var binding: CashoutAvailableDialogBinding
        lateinit var textView: TextView

        @SuppressLint("SetTextI18n")
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = CashoutAvailableDialogBinding.inflate(LayoutInflater.from(context))
                setContentView(binding.root)

                window?.setLayout(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                initViews()

        }

        private fun initViews() {
                clickListener()
                setData()
        }

        private fun setData() {
                if(userLevel==1){
                        binding.tvCashoutPercentage.text = context.getString(R.string._50_cashout)

                        if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                        .equals("en", ignoreCase = true)
                        ) {
                                binding.tvCashoutGoal.text = context.getString(R.string.take_more_trips_to_unlock)+" " +context.getString(R.string._70_cashout)
                        } else {
                                binding.tvCashoutGoal.text = context.getString(R.string._70_cashout) + " अनलॉक करने के लिए और यात्राएं करें"
                        }


                }else if(userLevel==2){
                        binding.tvCashoutPercentage.text = context.getString(R.string._70_cashout)

                        if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                        .equals("en", ignoreCase = true)
                        ) {
                                binding.tvCashoutGoal.text = context.getString(R.string.take_more_trips_to_unlock)+" " + context.getString(R.string._90_cashout)
                        } else {
                                binding.tvCashoutGoal.text = context.getString(R.string._90_cashout) + " अनलॉक करने के लिए और यात्राएं करें"
                        }
                }else{
                        binding.tvCashoutPercentage.text = context.getString(R.string._90_cashout)
                        binding.tvCashoutGoal.text = context.getString(R.string.cashout_tv3)

                }
        }

        private fun clickListener() {
                binding.ivCross.setOnClickListener {
                        dismiss()
                }
        }
}