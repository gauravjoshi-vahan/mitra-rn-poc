package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ItemWeekEarning2Binding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetEarn


class EarningCalculationAdapter(
    val requireActivity: FragmentActivity,
    private val milestone: List<EarnDataModel.Milestone>,
) :
    RecyclerView.Adapter<EarningCalculationAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: ItemWeekEarning2Binding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: ItemWeekEarning2Binding = itemBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val itemBinding: ItemWeekEarning2Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_week_earning2, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            try {
                val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(requireActivity)
                    .`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                val uri = Uri.parse(milestone[position].companyIcon)
                requestBuilder.load(uri).into(brandImage)
            } catch (e: NullPointerException) {
                Glide
                    .with(requireActivity)
                    .load(R.drawable.ic_app_icon)
                    .placeholder(R.drawable.ic_app_icon)
                    .into(brandImage)
            }
            cardMain.isEnabled = milestone[position].viewDetails!!
            if (PrefrenceUtils.retriveLangData(requireActivity, Constants.LANGUAGE).equals("en"))
                tvTimeStamp.text = "Updated till: " + milestone[position].lastUpdatedTimestamp
            else
                tvTimeStamp.text = milestone[position].lastUpdatedTimestamp + " तक अपडेट करें"
            tvHead.text = milestone[position].payoutStructure?.currentEarnings?.title
            tvAmount.text =
                milestone[position].payoutStructure.currentEarnings?.items!![0].value.toString()

            val length = milestone[position].payoutStructure.incentivePay?.items!![0].values?.size

            val unitValue =
                milestone[position].payoutStructure.currentEarnings?.items!![0].unitValue?.toDouble()

            cardMain.setOnClickListener {
                setInstrumentation()
                BottomSheetEarn(requireActivity, milestone, position).show()
            }
            renderMileStoneLogic(unitValue!!, position, milestone, holder.binding, length!!)
        }

    }

    private fun setInstrumentation() {
        val propertiesOne = Properties()
        MoEHelper.getInstance(requireActivity)
            .trackEvent("milestone_details_viewed", propertiesOne)
        UXCam.logEvent("milestone_details_viewed")
        BlitzLlamaSDK.getSdkManager(requireActivity).triggerEvent("milestone_details_viewed")

    }

    @SuppressLint("SetTextI18n")
    private fun renderMileStoneLogic(
        unitValue: Double,
        position: Int,
        milestone: List<EarnDataModel.Milestone>,
        binding: ItemWeekEarning2Binding,
        length: Int,
    ) {
        binding.apply {
            try {
                milestone.let {
                    if (it[position].payoutStructure.incentivePay?.items!![0].values?.size!! > 0) {
                        for (i in 0..length) {
                            if (unitValue < it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i
                                )!!.min!!.toDouble()
                                && it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i + 1
                                )!!.min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    length - 1
                                )!!.min
                            ) {
                                max1.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i
                                    )!!.min?.toInt()
                                        .toString() + requireActivity.getString(R.string.plus)
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 1
                                    )!!.min?.toInt()
                                        .toString() + requireActivity.getString(R.string.plus)
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 2
                                    )!!.min?.toInt()
                                        .toString() + requireActivity.getString(R.string.plus)

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i
                                        )!!.value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 1
                                        )!!.value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 2
                                        )!!.value?.toInt()
                                            .toString()
                                if (unitValue == 0.0) {
                                    seekbar0.visibility = View.VISIBLE
                                    text0.text = ""
                                } else {
                                    seekbar40.visibility = View.VISIBLE
                                    text40.text =
                                        it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                }
                                break
                            } else if (it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i + 1
                                )?.min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    length - 1
                                )?.min &&
                                unitValue > it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i
                                )?.min!!.toDouble()
                                && unitValue < it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i + 1
                                )?.min!!.toDouble()
                            ) {
                                max1.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 1
                                    )?.min?.toInt()
                                        .toString() + "+"
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 2
                                    )?.min?.toInt()
                                        .toString() + "+"
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 3
                                    )?.min?.toInt()
                                        .toString() + "+"

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 1
                                        )?.value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 2
                                        )?.value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 3
                                        )?.value?.toInt()
                                            .toString()
                                seekbar40.visibility = View.VISIBLE
                                text40.text =
                                    it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                break
                            } else if (unitValue > it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i
                                )?.min!!.toDouble()
                                && unitValue == it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toDouble() &&
                                it[position].payoutStructure.incentivePay?.items?.get(0)?.values!![i + 1].min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min
                            ) {
                                max1.text = ""
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![2].min?.toInt()
                                        .toString() + "+"
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![3].min?.toInt()
                                        .toString() + "+"

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![1].value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![2].value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![3].value?.toInt()
                                            .toString()
                                seekbar55.visibility = View.VISIBLE
                                text55.text =
                                    it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                break
                            } else if (unitValue > it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min!!.toDouble()
                                && unitValue < it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min!!.toDouble()
                                && it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min
                            ) {
                                max1.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toInt()
                                        .toString() + "+"
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toInt()
                                        .toString() + "+"
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min?.toInt()
                                        .toString() + "+"

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].value?.toInt()
                                            .toString()
                                seekbar70.visibility = View.VISIBLE
                                text70.text =
                                    it[position].payoutStructure.currentEarnings?.items?.get(0)?.unitValue.toString()
                                break
                            } else if (unitValue == it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toDouble()
                                && it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min
                            ) {
                                max1.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toInt()
                                        .toString() + "+"
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toInt()
                                        .toString() + "+"
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min?.toInt()
                                        .toString() + "+"

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].value?.toInt()
                                            .toString()
                                seekbar80.visibility = View.VISIBLE
                                text85.text =
                                    it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                break
                            } else if (it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values!![length - 1].min &&
                                unitValue > it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min!!.toDouble()
                                && unitValue < it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min!!.toDouble()
                            ) {
                                max1.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].min?.toInt()
                                        .toString() + "+"
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].min?.toInt()
                                        .toString() + "+"
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values!![i + 3].min?.toInt()
                                        .toString() + "+"

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 1].value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values!![i + 2].value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 3
                                        )?.value?.toInt()
                                            .toString()
                                seekbar92.visibility = View.VISIBLE
                                text92.text =
                                    it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                break
                            } else if (it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i + 3
                                )?.min !=
                                it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    length - 1
                                )?.min &&
                                unitValue == it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    i + 3
                                )?.min?.toDouble()
                            ) {
                                max1.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 2
                                    )?.min?.toInt()
                                        .toString() + "+"
                                max2.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        i + 3
                                    )?.min?.toInt()
                                        .toString() + "+"
                                max3.text =
                                    it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                        length - 1
                                    )?.min?.toInt()
                                        .toString() + "+"

                                value1.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 2
                                        )?.value?.toInt()
                                            .toString()
                                value2.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            i + 3
                                        )?.value?.toInt()
                                            .toString()
                                value3.text = "₹" +
                                        it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                            length - 1
                                        )?.value?.toInt()
                                            .toString()
                                seekbar92.visibility = View.VISIBLE
                                text92.text =
                                    it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                break
                            } else if (unitValue == it[position].payoutStructure.incentivePay?.items!![0].values?.get(
                                    length - 1
                                )?.min?.toDouble()
                            ) {
                                max1.text = ""
                                max2.text = ""
                                max3.text = ""
                                value1.text = ""
                                value2.text = ""
                                value3.text = ""
                                seekbar100.visibility = View.VISIBLE
                                text100.text =
                                    it[position].payoutStructure.currentEarnings?.items!![0].unitValue.toString()
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    override fun getItemCount(): Int {
        return milestone.size
    }
}