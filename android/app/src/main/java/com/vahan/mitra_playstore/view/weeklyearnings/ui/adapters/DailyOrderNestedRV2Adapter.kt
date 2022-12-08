package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.DailyOrderNestedRv2ItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel

class DailyOrderNestedRV2Adapter(
    private val context: Context,
    private val data: DailyOrderModel,
    private val pos: Int
): RecyclerView.Adapter<DailyOrderNestedRV2Adapter.MyViewHolder>() {

    class MyViewHolder(itembinding: DailyOrderNestedRv2ItemBinding):
        RecyclerView.ViewHolder(itembinding.root){
        val binding: DailyOrderNestedRv2ItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: DailyOrderNestedRv2ItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.daily_order_nested_rv2_item, parent, false
        )
        return DailyOrderNestedRV2Adapter.MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvOrderEarning.text = data.orderLevelDetails[pos].details[position].label
            tvAmt.text =  data.orderLevelDetails[pos].details[position].value
        }
    }

    override fun getItemCount(): Int {
        return data.orderLevelDetails[pos].details.size ?: 0
    }
}