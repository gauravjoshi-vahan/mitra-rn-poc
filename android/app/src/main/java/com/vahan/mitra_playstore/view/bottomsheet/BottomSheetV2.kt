package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.earn.view.ui.EarnFragmentV2
import com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment
import com.vahan.mitra_playstore.view.history.HistoryFragment
import kotlinx.android.synthetic.main.bottom_sheet_v2.*


class BottomSheetV2(
    context: Context,
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
    val cashoutNextLevelPercentage: Int?,

    ) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    var isEnable = true


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_v2)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val trnCndText = findViewById<TextView>(R.id.trn_cnd_text)
        findViewById<TextView>(R.id.set_price)?.apply {
            if (!price.contains(".")) {
                this.text = price
            } else {
                this.text = price.subSequence(0, price.indexOf("."))
            }

        }

        findViewById<TextView>(R.id.text_price_cashout)?.apply {
            if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                if (!price.contains(".")) {
                    this.text = "Cashout $price"
                } else {
                    this.text = "Cashout ${price.subSequence(0, price.indexOf("."))}"
                }

            } else {
                if (!price.contains(".")) {
                    this.text = "कैशआउट $price"
                } else {
                    this.text = "कैशआउट ${price.subSequence(0, price.indexOf("."))}"
                }
            }
        }
        findViewById<RelativeLayout>(R.id.goto_transation)?.apply {
            this.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(Constants.PRICE, price)
                bundle.putString(Constants.EPRICE, eleAmount.toString())
                bundle.putInt("cashoutFixedFeeLabel", cashoutFixedFee)
                bundle.putString("cashoutFeePercentage", cashoutFeePercentage.toString())
                bundle.putInt("orderReachToNextLevel", orderReachToNextLevel ?: 0)
                bundle.putInt("tripsReachToNextLevel", tripsReachToNextLevel ?: 0)
                bundle.putBoolean("orderReachToNextLevel1", orderReachToNextLevel1 ?: false)
                bundle.putBoolean("daysReachToNextLevel", daysReachToNextLevel ?: false)
                bundle.putInt("cashoutNextLevelPercentage", cashoutNextLevelPercentage ?: 0)
                if (cashoutEnabled != null) {
                    bundle.putBoolean("cashoutFeePercentageEnabled", cashoutEnabled)
                }
                if(fragment!=null){
                    fragment?.view?.let { it1 ->
                        Navigation.findNavController(it1)
                            .navigate(R.id.cashout_purpose_fragment, bundle)
                    }
                }else{
                    historyFragment?.view?.let { it1 ->
                        Navigation.findNavController(it1)
                            .navigate(R.id.cashout_purpose_fragment, bundle)
                    }
                }

                dismiss()
            }
        }

        findViewById<ImageView>(R.id.cross_bottom_sheet)?.setOnClickListener {
            PrefrenceUtils.insertDataInBoolean(context, Constants.IS_BOTTOM_FIRST_TIME, true)
            dismiss()
        }
        findViewById<TextView>(R.id.trm_cnd)?.apply {
            this.setOnClickListener {
                if (isEnable) {
                    trm_card_detail?.visibility = View.VISIBLE
                    isEnable = false
                    if (cashoutEnabled == true) {
                        if (cashoutFeePercentage > 0) {
                            if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                    .equals("en")
                            ) {
                                trnCndText?.text =
                                    "Cashout amount is nothing but a portion of your earnings available before scheduled payout date. If you opt to recieve this amount, the same will be deducted from your Mitra balance.\n" +
                                            "\n" +
                                            "You will be charged " + cashoutFeePercentage + "% extra for availing Cashout!"
                            } else {
                                trnCndText?.text =
                                    "कैशआउट राशि और कुछ नहीं बल्कि निर्धारित भुगतान तिथि से पहले उपलब्ध आपकी आय का एक हिस्सा है। यदि आप इस राशि को प्राप्त करने का विकल्प चुनते हैं, तो यह राशि आपके मित्र शेष से काट ली जाएगी।\n" +
                                            "\n" +
                                            "कैशआउट का लाभ उठाने के लिए आपसे " + cashoutFeePercentage + "% अतिरिक्त शुल्क लिया जाएगा!"
                            }
                        } else {
                            if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE)
                                    .equals("en")
                            ) {
                                trnCndText?.text =
                                    "Cashout amount is nothing but a portion of your earnings available before scheduled payout date. If you opt to recieve this amount, the same will be deducted from your Mitra balance.\n" +
                                            "\n" +
                                            "You will be charged $cashoutFixedFee extra for availing Cashout!"
                            } else {
                                trnCndText?.text =
                                    "कैशआउट राशि और कुछ नहीं बल्कि निर्धारित भुगतान तिथि से पहले उपलब्ध आपकी आय का एक हिस्सा है। यदि आप इस राशि को प्राप्त करने का विकल्प चुनते हैं, तो यह राशि आपके मित्र शेष से काट ली जाएगी।\n" +
                                            "\n" +
                                            "कैशआउट का लाभ उठाने के लिए आपसे " + cashoutFixedFee + "% अतिरिक्त शुल्क लिया जाएगा!"
                            }
                        }

                    }
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_float, 0)
                } else {
                    trm_card_detail?.visibility = View.GONE
                    isEnable = true
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_float, 0)
                }
            }

        }

        findViewById<TextView>(R.id.transc_id_charges).apply {
            if (cashoutEnabled == true) {
                if (cashoutFeePercentage > 0) {
                    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                        transc_id_charges.text = "$cashoutFeePercentage% FEE APPLICABLE"
                    } else {
                        transc_id_charges.text = "$cashoutFeePercentage% शुल्क लागू"
                    }
                } else {
                    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                        transc_id_charges.text = "₹$cashoutFixedFee FEE APPLICABLE"
                    } else {
                        transc_id_charges.text = "₹$cashoutFixedFee शुल्क लागू"
                    }
                }
            }
        }
    }
}