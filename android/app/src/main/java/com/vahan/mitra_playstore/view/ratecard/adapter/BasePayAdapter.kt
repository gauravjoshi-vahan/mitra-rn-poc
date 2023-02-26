package com.vahan.mitra_playstore.view.ratecard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.BasePayItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class BasePayAdapter (private val context: Context,
                      private val basePayList: List<RateCardDetailsDTO.Incentive.BasePay?>?
): RecyclerView.Adapter<BasePayAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: BasePayItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: BasePayItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: BasePayItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.base_pay_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvAmount.text = basePayList?.get(position)?.amountLabel + basePayList?.get(position)?.amount
            tvEachOrder.text = basePayList?.get(position)?.label
            tvKey.text = basePayList?.get(position)?.title
            tvPerOrder.text = basePayList?.get(position)?.unitLabel
            tvCreditedBalance.text = basePayList?.get(position)?.frequency
        }
    }

    override fun getItemCount(): Int {
        return basePayList?.size?:0
    }
}