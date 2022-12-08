package com.vahan.mitra_playstore.view.ratecard.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.RateCardRv1ItemBinding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel

class VariablePayAdapter(
    private val context: Context,
    private val data: EarnDataModel.IncentiveStructures,
    private var pos: Int
): RecyclerView.Adapter<VariablePayAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding:RateCardRv1ItemBinding):
            RecyclerView.ViewHolder(itembinding.root){
                val binding:RateCardRv1ItemBinding = itembinding
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: RateCardRv1ItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rate_card_rv1_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            if(data.incentiveList[pos].spinnerKey!="Mitra"){
                tvBasePay.text = data.incentiveList[pos].payoutStructure?.get(position)?.name
                Log.d("geet", "onBindViewHolder: "+data.incentiveList[pos].payoutStructure?.get(position)?.name)
                basePayLabel.text = data.incentiveList[pos].payoutStructure?.get(position)?.label
                perOrderValue.text = data.incentiveList[pos].payoutStructure?.get(position)?.value
                perOrder.text = data.incentiveList[pos].payoutStructure?.get(position)?.unitLabel
            }
        }
    }

    override fun getItemCount(): Int {
        return data.incentiveList[pos].payoutStructure!!.size
    }
}