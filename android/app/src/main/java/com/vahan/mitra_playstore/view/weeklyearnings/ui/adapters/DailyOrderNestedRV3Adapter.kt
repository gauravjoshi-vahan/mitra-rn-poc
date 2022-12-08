package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.DailyOrderNestedRv3ItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel

class DailyOrderNestedRV3Adapter(
    private val context: Context,
    private val data: DailyOrderModel,
    private val pos: Int
) : RecyclerView.Adapter<DailyOrderNestedRV3Adapter.MyViewHolder>() {

    class MyViewHolder(itembinding: DailyOrderNestedRv3ItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: DailyOrderNestedRv3ItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: DailyOrderNestedRv3ItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.daily_order_nested_rv3_item, parent, false
        )
        return DailyOrderNestedRV3Adapter.MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvType.text = data.orderLevelDetails[pos].status[position].label
            tvTypeValue.text = data.orderLevelDetails[pos].status[position].value
        }
    }

    override fun getItemCount(): Int {
        return data.orderLevelDetails[pos].status.size ?: 0
    }
}