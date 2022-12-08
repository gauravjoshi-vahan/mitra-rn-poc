package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uxcam.internals.ca
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils


class BottomSheetV1(
    context: Context,
    val cashoutFixedFeeLabel: String?,
    val cashoutEnabled: Boolean?,
    val cashoutFeePercentage: Double
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_v1)
        findViewById<TextView>(R.id.cashout_got_it)?.setOnClickListener {
            dismiss()
        }
        if (cashoutEnabled == true) {
            if(cashoutFeePercentage>0){
                if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                    findViewById<TextView>(R.id.cashout_heading)?.text =
                        "You will be charged $cashoutFeePercentage% extra fees when you cashout this amount. It is a part of your payout received early"
                } else {
                    findViewById<TextView>(R.id.cashout_heading)?.text =
                        "इस राशि को भुनाने पर आपसे $cashoutFeePercentage% अतिरिक्त शुल्क लिया जाएगा। यह आपके जल्दी प्राप्त भुगतान का एक भाग है"
                }
            }else{
                if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                    findViewById<TextView>(R.id.cashout_heading)?.text =
                        "You will be charged $cashoutFixedFeeLabel extra fees when you cashout this amount. It is a part of your payout received early"
                } else {
                    findViewById<TextView>(R.id.cashout_heading)?.text =
                        "इस राशि को भुनाने पर आपसे $cashoutFixedFeeLabel अतिरिक्त शुल्क लिया जाएगा। यह आपके जल्दी प्राप्त भुगतान का एक भाग है"
                }
            }
        }
    }
}