package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.inapp.MoEInAppHelper
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.view.earn.view.ui.EarnFragmentV2
import com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment
import com.vahan.mitra_playstore.view.history.HistoryFragment

class BottomSheetTier(
    context: Context,
    private val userLevel: Int?,
    val price: String,
    val fragment: HomeFragment?,
    val historyFragment: HistoryFragment?,
    val eleAmount: Double,
    val cashoutFixedFee: Int,
    val cashoutEnabled: Boolean?,
    val cashoutFeePercentage: Double,
    val orderReachToNextLevel: Int?,
    val tripsReachToNextLevel: Int?,
    val orderReachToNextLevel1: Boolean?,
    val daysReachToNextLevel: Boolean?,
    val cashoutNextLevelPercentage: Int?
) : BottomSheetDialog(
    context, R.style.CustomBottomSheetDialogTheme
) {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_cashout_sheet_tier)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        initView()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        setInstrumentation(userLevel)
        if (userLevel?.equals(1) == true) {
            //dialog
            findViewById<TextView>(R.id.tv_cashout_tier_one)?.text = context.getString(R.string.selected_tier_one)
            findViewById<RelativeLayout>(R.id.level_one)?.setBackgroundResource(R.drawable.card_selected_bg)
            findViewById<TextView>(R.id.tv_text_one)?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vespa_white, 0, 0, 0);

            // findViewById<ImageView>(R.id.iv_first_container)?.setImageDrawable(context.getDrawable(R.drawable.ic_green_tier_one))
            findViewById<ImageView>(R.id.iv_tick1_completed)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick2_completed)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick2_locked)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_tick3_locked)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_seekbar1_line1)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_seekbar1_line2)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_seekbar2_line1)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_seekbar2_line2)?.visibility  = View.GONE
            findViewById<LottieAnimationView>(R.id.iv_tick1_animated)?.visibility  = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.iv_tick2_animated)?.visibility  = View.GONE
            findViewById<LottieAnimationView>(R.id.iv_tick3_animated)?.visibility  = View.GONE

            findViewById<TextView>(R.id.tv_text_one)?.visibility  = View.VISIBLE
            findViewById<RelativeLayout>(R.id.rl2)?.visibility  = View.VISIBLE

        }
        else if (userLevel?.equals(2) == true) {
            findViewById<TextView>(R.id.tv_cashout_tier_one)?.text = context.getString(R.string.selected_tier_one)
            findViewById<TextView>(R.id.tv_cashout_tier_two)?.text = context.getString(R.string.selected_tier_two)
            findViewById<TextView>(R.id.tv_text_three)?.text = context.getString(R.string.cashout_tv2)
            findViewById<TextView>(R.id.tv_text_three)?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vespa_white, 0, 0, 0);
            findViewById<RelativeLayout>(R.id.level_two)?.setBackgroundResource(R.drawable.card_selected_bg)
         //   findViewById<ImageView>(R.id.iv_first_container)?.setImageDrawable(context.getDrawable(R.drawable.ic_green_tier_two))
            findViewById<ImageView>(R.id.iv_tick1_completed)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_tick2_completed)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick2_locked)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick3_locked)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_seekbar1_line1)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_seekbar1_line2)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_seekbar2_line1)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_seekbar2_line2)?.visibility  = View.VISIBLE
            findViewById<TextView>(R.id.tv_text_four)?.visibility  = View.GONE
            findViewById<TextView>(R.id.text_three_plus)?.visibility  = View.GONE
            findViewById<TextView>(R.id.tv_text2_3)?.visibility  = View.GONE
            findViewById<LottieAnimationView>(R.id.iv_tick1_animated)?.visibility  = View.GONE
            findViewById<LottieAnimationView>(R.id.iv_tick2_animated)?.visibility  = View.VISIBLE
            findViewById<LottieAnimationView>(R.id.iv_tick3_animated)?.visibility  = View.GONE

            findViewById<TextView>(R.id.tv_text_one)?.visibility  = View.GONE
            findViewById<RelativeLayout>(R.id.rl2)?.visibility  = View.VISIBLE
        }
        else {
            findViewById<TextView>(R.id.tv_cashout_tier_one)?.text = context.getString(R.string.selected_tier_one)
            findViewById<TextView>(R.id.tv_cashout_tier_two)?.text = context.getString(R.string.selected_tier_two)
            findViewById<TextView>(R.id.tv_cashout_tier_three)?.text = context.getString(R.string.selected_tier_three)
            findViewById<TextView>(R.id.tv_text_three2)?.text = context.getString(R.string.cashout_tv3)
            findViewById<TextView>(R.id.tv_text_three2)?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vespa_white, 0, 0, 0);
            findViewById<RelativeLayout>(R.id.level_three)?.setBackgroundResource(R.drawable.card_selected_bg)
         //   findViewById<ImageView>(R.id.iv_first_container)?.setImageDrawable(context.getDrawable(R.drawable.ic_green_tier_three))

            findViewById<ImageView>(R.id.iv_tick1_completed)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_tick2_completed)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_tick2_locked)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick3_locked)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick1_animated)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick2_animated)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_tick3_animated)?.visibility  = View.VISIBLE
            findViewById<ImageView>(R.id.iv_seekbar2_line1)?.visibility  = View.GONE
            findViewById<ImageView>(R.id.iv_seekbar2_line2)?.visibility  = View.VISIBLE
            findViewById<TextView>(R.id.tv_text_four2)?.visibility  = View.GONE
            findViewById<TextView>(R.id.text_three_plus2)?.visibility  = View.GONE
            findViewById<TextView>(R.id.tv_text_three4)?.visibility  = View.GONE

            findViewById<TextView>(R.id.tv_text_one)?.visibility  = View.GONE
            findViewById<RelativeLayout>(R.id.rl2)?.visibility  = View.GONE
        }

        findViewById<CardView>(R.id.cv_continue)?.setOnClickListener {
            dismiss()

            if(fragment!=null){
                BottomSheetV2(
                    context,
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
            }else{
                BottomSheetV2(
                    context,
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

    }

    private fun setInstrumentation(userLevel: Int?) {
        MoEInAppHelper.getInstance().showInApp(context)
        val properties = Properties()
        val attribute: HashMap<String, Any> = HashMap()
        properties.addAttribute("level",userLevel)
        properties.addAttribute("percentage",cashoutNextLevelPercentage)
        attribute["level"] = userLevel!!
        attribute["percentage"] = cashoutNextLevelPercentage?:"null"
        Freshchat.trackEvent(context, "cashout_levels_viewed", attribute)
        MoEHelper.getInstance(context).trackEvent("cashout_levels_viewed", properties)

    }
}