package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyEarningsOtherItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel

class OtherEarningsAdapter(
    private val context: Context,
    private val weeklyEarningsData: WeeklyEarningsModel,
) : RecyclerView.Adapter<OtherEarningsAdapter.MyViewHolder>() {
    class MyViewHolder(otherEarningsBinding: WeeklyEarningsOtherItemBinding) :
        RecyclerView.ViewHolder(otherEarningsBinding.root) {
        val binding: WeeklyEarningsOtherItemBinding = otherEarningsBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val otherEarningsBinding: WeeklyEarningsOtherItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_earnings_other_item, parent, false
        )
        return MyViewHolder(otherEarningsBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {

        val item = weeklyEarningsData.otherEarnings?.otherEarningsBreakdown?.get(position)
        holder.binding.apply {
            if(item !== null){
                otherItemName.text = item.key
                amountTxt.text = item.amountInRupees?.toInt().toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return weeklyEarningsData.otherEarnings?.otherEarningsBreakdown?.size ?: 0
    }
}