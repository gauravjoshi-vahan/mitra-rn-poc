package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.earn.view.ui.EarnFragmentV2
import com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment.Companion.isFeedbackUpdateCheck
import kotlinx.android.synthetic.main.fragment_rate_card.*
import kotlinx.android.synthetic.main.layout_bottom_earn_extend.view.*


class BottomSheetEarn(
    context: Context,
    val milestoneData: List<EarnDataModel.Milestone>,
    val position: Int,
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    private lateinit var fa: FirebaseAnalytics

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_rate_card)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        fa = FirebaseAnalytics.getInstance(context)
        val requestBuilder: RequestBuilder<PictureDrawable> = Glide.with(context)
            .`as`(PictureDrawable::class.java)
            .listener(SvgSoftwareLayerSetter())
        val uri = Uri.parse(milestoneData[position].companyIcon)
        requestBuilder.load(uri).into(company_image)

        tv_rate_card.text = milestoneData[position].payoutStructure.title
        company_name.text = milestoneData[position].companyName
        tv_order_pay.text = milestoneData[position].payoutStructure.basicPay!!.title
        tv_earning.text = milestoneData[position].payoutStructure.basicPay!!.label
        tv_base_pay.text = milestoneData[position].payoutStructure.basicPay!!.items?.get(0)?.name
        per_order_value.text =
            milestoneData[position].payoutStructure.basicPay!!.items?.get(0)?.value.toString()
        base_pay_label.text = milestoneData[position].payoutStructure.basicPay?.items!![0].label
        per_order.text = milestoneData[position].payoutStructure.basicPay?.items!![0].unitLabel
        per_km.text = milestoneData[position].payoutStructure.basicPay!!.items?.get(1)?.unitLabel
        distance_pay_label.text = milestoneData[position].payoutStructure.basicPay?.items!![1].label
        tv_distance_pay.text = milestoneData[position].payoutStructure.basicPay?.items!![1].name
        distance_pay_value.text =
            milestoneData[position].payoutStructure.basicPay?.items!![1].value.toString()
        try {
            if (milestoneData[position].payoutStructure.basicPay?.items!![2] != null) {
                tv_peek_hours_incentive.text =
                    milestoneData[position].payoutStructure.basicPay?.items!![2].name
                peek_hours_pay_label.text =
                    milestoneData[position].payoutStructure.basicPay?.items!![2].label
                peek_hours_incentive_value.text =
                    milestoneData[position].payoutStructure.basicPay?.items!![2].value.toString()
                per_hrs.text =
                    milestoneData[position].payoutStructure.basicPay?.items!![2].unitLabel

                if (milestoneData[position].payoutStructure.basicPay?.items!![2].value!!.toInt() > 0) {
                    peek_container.visibility = View.VISIBLE
                } else {
                    peek_container.visibility = View.GONE
                }
                if (milestoneData[position].payoutStructure.basicPay?.items!![1].value!!.toInt() > 0) {
                    forth.visibility = View.VISIBLE
                } else {
                    forth.visibility = View.GONE
                }
                if (milestoneData[position].payoutStructure.basicPay?.items!![0].value!!.toInt() > 0) {
                    third.visibility = View.VISIBLE
                } else {
                    third.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            peek_container.visibility = View.GONE
            forth.visibility = View.GONE
            third.visibility = View.GONE
        }


        min_earning.text = milestoneData[position].payoutStructure.incentivePay?.title
        tv_reach.text = milestoneData[position].payoutStructure.incentivePay?.label
        tv_weekly.text = milestoneData[position].payoutStructure.incentivePay?.structure
        tv_week.text = milestoneData[position].payoutStructure.incentivePay?.structureLabel
        tv_current_earning.text = milestoneData[position].payoutStructure.currentEarnings?.title
        tv_current_earning_message.text =
            milestoneData[position].payoutStructure.currentEarnings?.items!![0].extraEarningsLabel
        tv_current_earn_value.text =
            milestoneData[position].payoutStructure.currentEarnings?.items!![0].value.toString()

        // Commited Changes
        val length = milestoneData[position].payoutStructure.incentivePay?.items!![0].values?.size

        val unitValue =
            milestoneData[position].payoutStructure.currentEarnings?.items!![0].unitValue?.toDouble()
        if (length != null) {
            renderMileStoneLogic(unitValue!!, position, milestoneData, length)
        }

        tv_upto2.text =
            context.getString(R.string.rupee) + context.getString(R.string.space) + milestoneData[position].payoutStructure.incentivePay?.items!![0].maxValue.toString()
        val array = arrayOf(
            context.getString(R.string.week),
            context.getString(R.string.month),
            context.getString(R.string.year)
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            context,
            R.layout.spinner_item, array
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tv_month.adapter = adapter
        tv_month.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (position == 0) {
                    setInstrumentation(context.getString(R.string.week))
                    tv_current_earn_value.text =
                        milestoneData[0].payoutStructure.currentEarnings?.items!![0].value.toString()

                }
                if (position == 1) {
                    setInstrumentation(context.getString(R.string.month))
                    tv_current_earn_value.text =
                        milestoneData[0].payoutStructure.currentEarnings?.items!![1].value.toString()
                }
                if (position == 2) {
                    setInstrumentation(context.getString(R.string.year))
                    tv_current_earn_value.text =
                        milestoneData[0].payoutStructure.currentEarnings?.items!![2].value.toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun setInstrumentation(time_frame: String) {
        val bundleOne = Bundle()
        bundleOne.putString(Constants.BUNDLE_TIMEFRAME, time_frame)
        val propertiesOne = Properties()
        propertiesOne.addAttribute(Constants.BUNDLE_TIMEFRAME, time_frame)
        MoEHelper.getInstance(context)
            .trackEvent(Constants.MILESTONE_PROJECTION_CHANGED, propertiesOne)
        fa.logEvent(Constants.MILESTONE_PROJECTION_CHANGED, bundleOne)
        val data = HashMap<String, String>()
        data[Constants.BUNDLE_TIMEFRAME] = time_frame
        UXCam.logEvent(Constants.MILESTONE_PROJECTION_CHANGED, data)
        BlitzLlamaSDK.getSdkManager(context).triggerEvent(Constants.MILESTONE_PROJECTION_CHANGED)
    }

    @SuppressLint("SetTextI18n")
    private fun renderMileStoneLogic(
        unitValue: Double,
        position: Int,
        milestone: List<EarnDataModel.Milestone>,
        length: Int,
    ) {
        if (milestone[position].payoutStructure.incentivePay?.items!![0].values?.size!! > 0) {
            for (i in 0..length) {
                if (unitValue < milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i
                    )!!.min!!.toDouble()
                    && milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 1)!!.min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(length - 1)!!.min
                ) {
                    step_view_ui.max_1.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i)!!.min?.toInt()
                            .toString() + "+"
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 1)!!.min?.toInt()
                            .toString() + "+"
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 2)!!.min?.toInt()
                            .toString() + "+"

                    step_view_ui.value_1.text = "₹" +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i
                            )!!.value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = "₹" +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 1
                            )!!.value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = "₹" +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 2
                            )!!.value?.toInt()
                                .toString()
                    if (unitValue == 0.0) {
                        step_view_ui.seekbar_0.visibility = View.VISIBLE
                        step_view_ui.text_0.text = ""
                    } else {
                        step_view_ui.seekbar_40.visibility = View.VISIBLE
                        step_view_ui.text_40.text =
                            milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    }
                    break
                } else if (milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i + 1
                    )?.min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(length - 1)?.min &&
                    unitValue > milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i
                    )?.min!!.toDouble()
                    && unitValue < milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i + 1
                    )?.min!!.toDouble()
                ) {
                    step_view_ui.max_1.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 1)?.min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 2)?.min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 3)?.min?.toInt()
                            .toString() + context.getString(R.string.plus)

                    step_view_ui.value_1.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 1
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 2
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 3
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.seekbar_40.visibility = View.VISIBLE
                    step_view_ui.text_40.text =
                        milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    break
                } else if (unitValue > milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i
                    )?.min!!.toDouble()
                    && unitValue == milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toDouble() &&
                    milestone[position].payoutStructure.incentivePay?.items?.get(0)?.values!![i + 1].min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min
                ) {
                    step_view_ui.max_1.text = ""
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![2].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![3].min?.toInt()
                            .toString() + context.getString(R.string.plus)

                    step_view_ui.value_1.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![1].value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![2].value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![3].value?.toInt()
                                .toString()
                    step_view_ui.seekbar_55.visibility = View.VISIBLE
                    step_view_ui.text_55.text =
                        milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    break
                } else if (unitValue > milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min!!.toDouble()
                    && unitValue < milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min!!.toDouble()
                    && milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min
                ) {
                    step_view_ui.max_1.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min?.toInt()
                            .toString() + context.getString(R.string.rupee)

                    step_view_ui.value_1.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].value?.toInt()
                                .toString()
                    step_view_ui.seekbar_70.visibility = View.VISIBLE
                    step_view_ui.text_70.text =
                        milestone[position].payoutStructure.currentEarnings?.items?.get(0)?.unitValue.toString()
                    break
                } else if (unitValue == milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toDouble()
                    && milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min
                ) {
                    step_view_ui.max_1.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min?.toInt()
                            .toString() + context.getString(R.string.plus)

                    step_view_ui.value_1.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].value?.toInt()
                                .toString()
                    step_view_ui.seekbar_80.visibility = View.VISIBLE
                    step_view_ui.text_85.text =
                        milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    break
                } else if (milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min &&
                    unitValue > milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min!!.toDouble()
                    && unitValue < milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min!!.toDouble()
                ) {
                    step_view_ui.max_1.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min?.toInt()
                            .toString() + context.getString(R.string.plus)

                    step_view_ui.value_1.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 3
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.seekbar_92.visibility = View.VISIBLE
                    step_view_ui.text_92.text =
                        milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    break
                } else if (milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i + 3
                    )?.min !=
                    milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(length - 1)?.min &&
                    unitValue == milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        i + 3
                    )?.min?.toDouble()
                ) {
                    step_view_ui.max_1.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 2)?.min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_2.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(i + 3)?.min?.toInt()
                            .toString() + context.getString(R.string.plus)
                    step_view_ui.max_3.text =
                        milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                            length - 1
                        )?.min?.toInt()
                            .toString() + context.getString(R.string.plus)

                    step_view_ui.value_1.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 2
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.value_2.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                i + 3
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.value_3.text = context.getString(R.string.rupee) +
                            milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                length - 1
                            )?.value?.toInt()
                                .toString()
                    step_view_ui.seekbar_92.visibility = View.VISIBLE
                    step_view_ui.text_92.text =
                        milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    break
                } else if (unitValue == milestone[position].payoutStructure.incentivePay?.items!![0].values?.get(
                        length - 1
                    )?.min?.toDouble()
                ) {
                    step_view_ui.max_1.text = context.getString(R.string.space)
                    step_view_ui.max_2.text = context.getString(R.string.space)
                    step_view_ui.max_3.text = context.getString(R.string.space)

                    step_view_ui.value_1.text = context.getString(R.string.space)
                    step_view_ui.value_2.text = context.getString(R.string.space)
                    step_view_ui.value_3.text = context.getString(R.string.space)
                    step_view_ui.seekbar_100.visibility = View.VISIBLE
                    step_view_ui.text_100.text =
                        milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                    break
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isFeedbackUpdateCheck) {
            FeedbackBottomsheet(context, "").show()
            EarnFragmentV2.isFeedbackUpdateCheck = false
        }

    }


}
