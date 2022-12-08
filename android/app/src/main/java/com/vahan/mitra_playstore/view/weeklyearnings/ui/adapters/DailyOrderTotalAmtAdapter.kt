package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.DailyOrderTotalAmountItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel

class DailyOrderTotalAmtAdapter(
    private val context: Context,
    private val data: DailyOrderModel.OrderLevelSummary
): RecyclerView.Adapter<DailyOrderTotalAmtAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: DailyOrderTotalAmountItemBinding):
        RecyclerView.ViewHolder(itembinding.root){
        val binding: DailyOrderTotalAmountItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: DailyOrderTotalAmountItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.daily_order_total_amount_item, parent, false
        )
        return DailyOrderTotalAmtAdapter.MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvAmt.text = data.summaryBreakUp[position].label
            tvAmtValue.text = data.summaryBreakUp[position].value
        }
    }

    override fun getItemCount(): Int {
        return data.summaryBreakUp.size ?: 0
    }
}