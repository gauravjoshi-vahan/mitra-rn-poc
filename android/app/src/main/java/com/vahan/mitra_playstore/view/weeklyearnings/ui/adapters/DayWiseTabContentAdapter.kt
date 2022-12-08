package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyEarningsDaywiseTabContentBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel


class DayWiseTabContentAdapter(
    private val context: Context,
    private val dailyEarningsInd: Int,
    private val dailyBreakdownInd: Int,
    private val weeklyEarningsData: WeeklyEarningsModel,
) : RecyclerView.Adapter<DayWiseTabContentAdapter.MyViewHolder>() {
    class MyViewHolder(dayWiseTabContentBinding: WeeklyEarningsDaywiseTabContentBinding) :
        RecyclerView.ViewHolder(dayWiseTabContentBinding.root) {
        val binding: WeeklyEarningsDaywiseTabContentBinding = dayWiseTabContentBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val dayWiseTabContentBinding: WeeklyEarningsDaywiseTabContentBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.weekly_earnings_daywise_tab_content, parent, false
            )
        return MyViewHolder(dayWiseTabContentBinding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val item =
            weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(dailyEarningsInd)?.dailyBreakdown?.get(
                dailyBreakdownInd)?.dailyBreakdownValue?.get(position)
        holder.binding.apply {
            if (item != null) {
                if (item.title?.lowercase() == "order count") {
                    orderCount.visibility = View.VISIBLE
                    orderCountTxt.text = item.title + " : "
                    orderCountValue.text = item.value?.toInt().toString()
                    normalTxt.visibility = View.GONE
                } else {
                    normalTxt.visibility = View.VISIBLE
                    orderCount.visibility = View.GONE
                    tabItemName.text = item.title
                    tabItemValue.text = " " + item.value?.toInt().toString()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(dailyEarningsInd)?.dailyBreakdown?.get(
            dailyBreakdownInd)?.dailyBreakdownValue?.size
            ?: 0
    }

}