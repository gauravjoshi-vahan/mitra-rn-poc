package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.DailyOrderRvNested1ItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel

class DailyOrderNestedRV1Adapter(
    private val context: Context,
    private val data: DailyOrderModel,
    private val pos: Int
): RecyclerView.Adapter<DailyOrderNestedRV1Adapter.MyViewHolder>() {

    class MyViewHolder(itembinding: DailyOrderRvNested1ItemBinding):
        RecyclerView.ViewHolder(itembinding.root){
        val binding: DailyOrderRvNested1ItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: DailyOrderRvNested1ItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.daily_order_rv_nested1_item, parent, false
        )
        return DailyOrderNestedRV1Adapter.MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvMile.text = data.orderLevelDetails[pos].orderHeader.distanceCovered[position].label + " - "+
                    data.orderLevelDetails[pos].orderHeader.distanceCovered[position].value

            if(position==data.orderLevelDetails[pos].orderHeader.distanceCovered.size-1){
                llDivider.visibility = View.GONE
            }else{
                llDivider.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return data.orderLevelDetails[pos].orderHeader.distanceCovered.size ?: 0
    }
}