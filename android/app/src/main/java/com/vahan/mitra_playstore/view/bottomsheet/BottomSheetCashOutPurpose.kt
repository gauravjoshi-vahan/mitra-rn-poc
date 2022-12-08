package com.vahan.mitra_playstore.view.bottomsheet

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.inapp.MoEInAppHelper
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.earn.view.dialog.CashoutAvaialbleDialog
import com.vahan.mitra_playstore.view.earn.view.ui.EarnFragmentV2
import com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment
import com.vahan.mitra_playstore.view.history.HistoryFragment


class BottomSheetCashOutPurpose(
    context: FragmentActivity,
    private val userLevel: Int?,
    private val price: String,
    val fragment: HomeFragment?,
    val historyFragment: HistoryFragment?,
    private val eleAmount: Double,
    private val cashoutFixedFee: Int,
    private val cashoutEnabled: Boolean?,
    private val cashoutFeePercentage: Double,
    private val orderReachToNextLevel: Int?,
    private val tripsReachToNextLevel: Int?,
    private val cashoutEligibilityStatus: String?,
    private val orderReachToNextLevel1: Boolean?,
    private val daysReachToNextLevel: Boolean?,
    private val cashoutNextLevelPercentage: Int?
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_cashout_purpose)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setInstrumentation()
        findViewById<CardView>(R.id.cv_got_it)?.setOnClickListener {
            dismiss()

            if (cashoutEligibilityStatus.equals("E")) {
                if (fragment != null) {
                    BottomSheetTier(
                        context,
                        userLevel,
                        price,
                        fragment,
                        null,
                        eleAmount,
                        cashoutFixedFee,
                        cashoutEnabled,
                        cashoutFeePercentage,
                        orderReachToNextLevel,
                        tripsReachToNextLevel,
                        orderReachToNextLevel1,
                        daysReachToNextLevel,
                        cashoutNextLevelPercentage
                    ).show()
                }
                else{
                    BottomSheetTier(
                        context,
                        userLevel,
                        price,
                        null,
                        historyFragment,
                        eleAmount,
                        cashoutFixedFee,
                        cashoutEnabled,
                        cashoutFeePercentage,
                        orderReachToNextLevel,
                        tripsReachToNextLevel,
                        orderReachToNextLevel1,
                        daysReachToNextLevel,
                        cashoutNextLevelPercentage
                    ).show()
                }
            }
            val tv_percentage_cashout =  findViewById<TextView>(R.id.tv_cashout_percentage)
            if(PrefrenceUtils.retriveData(context, Constants.user_prev_state) !=userLevel.toString()){
                CashoutAvaialbleDialog(
                    context,
                    userLevel
                ).show()
                PrefrenceUtils.insertData(context, Constants.user_prev_state, userLevel.toString())
            }

        }
    }

    private fun setInstrumentation() {
        MoEInAppHelper.getInstance().showInApp(context)
        val properties2 = Properties()
        val attribute: HashMap<String, Any> = HashMap()
        Freshchat.trackEvent(context, "cashout_explanation_viewed", attribute)
        MoEHelper.getInstance(context).trackEvent("cashout_explanation_viewed", properties2)

    }
}