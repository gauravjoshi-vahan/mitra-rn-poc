package com.vahan.mitra_playstore.view.ratecard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.MinumumGuaranteeItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class MinGuaranteeZeptoAdapter(
    private val context: Context,
    private val minimumGuarantee: List<RateCardDetailsDTO.Incentive.MinimumGuarantee?>?,
    private var pos: Int
): RecyclerView.Adapter<MinGuaranteeZeptoAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: MinumumGuaranteeItemBinding):
        RecyclerView.ViewHolder(itembinding.root){
        val binding: MinumumGuaranteeItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: MinumumGuaranteeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.minumum_guarantee_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.binding.apply {
           tvMinimumGuarantee.text = minimumGuarantee?.get(position)?.title
           tvAmountGuarantee.text = minimumGuarantee?.get(position)?.amountLabel + minimumGuarantee?.get(position)?.amount
           rvContentCondition.adapter = MinGuaranteeConditionAdapter(context,
               minimumGuarantee?.get(position)?.condition)
       }
    }

    override fun getItemCount(): Int {
        return minimumGuarantee?.size?:0
    }


}
