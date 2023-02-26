package com.vahan.mitra_playstore.view.ratecard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ZomatoOrderPayItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class ZomatoAdapter(
    private val context: Context,
    private val dataList: List<RateCardDetailsDTO.Incentive?>?,
    private val pos: Int,
    private val listCheck: String
): RecyclerView.Adapter<ZomatoAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding:ZomatoOrderPayItemBinding):
            RecyclerView.ViewHolder(itembinding.root){
                val binding:ZomatoOrderPayItemBinding = itembinding
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: ZomatoOrderPayItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.zomato_order_pay_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            if(listCheck=="order_pay"){
                val dataList = dataList?.get(pos)?.orderPay?.get(position)
                tvBasePay.text = dataList?.name
                basePayLabel.text = dataList?.label
                tvBasePayAmtPrefix.text = dataList?.amountLabel
                perOrderValue.text = dataList?.amount
                perOrder.text = dataList?.unitLabel
            }else{
                val milestoneIncentiveData = dataList?.get(pos)?.milestoneIncentives?.get(position)
                tvBasePay.text = milestoneIncentiveData?.name
                basePayLabel.text = milestoneIncentiveData?.label
                tvBasePayAmtPrefix.text = milestoneIncentiveData?.amountLabel
                perOrderValue.text = milestoneIncentiveData?.amount
                perOrder.text = milestoneIncentiveData?.unitLabel
            }

        }
    }

    override fun getItemCount(): Int {
        return dataList?.get(pos)!!.orderPay!!.size
    }
}