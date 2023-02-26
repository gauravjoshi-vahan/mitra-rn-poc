package com.vahan.mitra_playstore.view.bottomsheet

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vahan.mitra_playstore.R

class BottomSheetCashoutHold(
    context: FragmentActivity,
    private val companyName: String?,
    private val companyIcon: String?,
    private val holdMsg: String?,
    private val eligibleAmt: String?,
    private val liabilitySum: Double?,
    private val amountLiabilityLabel: String?,
    private val holdPayoutSinceWeek: String?,
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    lateinit var iv_companyIcon :ImageView
    lateinit var tvDesc :TextView
    lateinit var amt :TextView
    lateinit var tvCashoutAmount :TextView
    lateinit var tvSinceDate :TextView
    lateinit var tvPendingData :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_bottom_sheet_cashout_hold)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
         iv_companyIcon = findViewById<ImageView>(R.id.iv_company_icon)!!
         tvDesc = findViewById<TextView>(R.id.tv_desc)!!
         amt = findViewById<TextView>(R.id.tv_amt)!!
         tvCashoutAmount = findViewById<TextView>(R.id.tv_cashout_amount)!!
        tvSinceDate = findViewById<TextView>(R.id.tv_cashout_date)!!
        tvPendingData = findViewById<TextView>(R.id.tv_cashout_desc2)!!
        clickListener()
        setData()
        findViewById<CardView>(R.id.cv_got_it)?.setOnClickListener {
            dismiss()
        }
        findViewById<ImageView>(R.id.iv_cross)?.setOnClickListener {
            dismiss()
        }

    }

    private fun setData() {
        Glide
            .with(context)
            .load(companyIcon)
            .placeholder(R.drawable.ic_app_icon)
            .into(iv_companyIcon)

        tvDesc.text =" "+ companyName + " " + holdMsg
        amt.text = eligibleAmt
        tvCashoutAmount.text = "₹ "+liabilitySum.toString()
        tvSinceDate.text = holdPayoutSinceWeek
        tvPendingData.text = amountLiabilityLabel

    }

    private fun clickListener() {

    }


}