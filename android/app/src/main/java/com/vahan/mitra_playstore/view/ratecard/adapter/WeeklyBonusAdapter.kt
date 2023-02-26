package com.vahan.mitra_playstore.view.ratecard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyBonusItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class WeeklyBonusAdapter(private val context: Context,
                         private val weeklyBonusCondition: List<RateCardDetailsDTO.Incentive.WeeklyBonus.Condition?>?
): RecyclerView.Adapter<WeeklyBonusAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: WeeklyBonusItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: WeeklyBonusItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: WeeklyBonusItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_bonus_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvMilestone.text = "${weeklyBonusCondition?.get(position)?.orderCount}" +
                    "${weeklyBonusCondition?.get(position)?.orderCountSuffix}"
             tvMilestoneSlab.text = "${weeklyBonusCondition?.get(position)?.orderCountLabel}"
             tvMilestoneAmount.text = "${weeklyBonusCondition?.get(position)?.amountPrefix} " +
                     "${weeklyBonusCondition?.get(position)?.amountLabel}" + "${weeklyBonusCondition?.get(position)?.amount}"
        }
        if(position==weeklyBonusCondition?.size!!-1){
            holder.binding.divider.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return weeklyBonusCondition?.size?:0
    }
}