package com.vahan.mitra_playstore.view.ratecard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyOrderCalculatorItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class WeeklyOrderAdapter(
    private val context: Context,
    private val weeklyBonusCondition: List<RateCardDetailsDTO.Incentive.WeeklyBonus.Condition?>?,
    private val positionToBeHighlighted: Int?
): RecyclerView.Adapter<WeeklyOrderAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: WeeklyOrderCalculatorItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: WeeklyOrderCalculatorItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: WeeklyOrderCalculatorItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_order_calculator_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView
        holder.binding.apply {
            tvOrders.text = "${weeklyBonusCondition?.get(position)?.orderCount}" + "${weeklyBonusCondition?.get(position)?.orderCountSuffix}"
            tvAmt.text = "${weeklyBonusCondition?.get(position)?.amountPrefix}" + " ${weeklyBonusCondition?.get(position)?.amountLabel}" + "${weeklyBonusCondition?.get(position)?.amount}"


            for (i in 0 until (weeklyBonusCondition?.size!!)){
                if(positionToBeHighlighted!=null) {
                    if (position <= positionToBeHighlighted) {
                        tvOrders.setTextColor(context.getColor(R.color.black_v2))
                        tvAmt.setTextColor(context.getColor(R.color.black_v2))
                        ivTick.setImageDrawable(context.getDrawable(R.drawable.ic_dark_green_tick))
                    } else {
                        tvOrders.setTextColor(context.getColor(R.color.grey))
                        tvAmt.setTextColor(context.getColor(R.color.grey))
                        ivTick.setImageDrawable(context.getDrawable(R.drawable.ic_grey_tick))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return weeklyBonusCondition?.size!!
    }
}