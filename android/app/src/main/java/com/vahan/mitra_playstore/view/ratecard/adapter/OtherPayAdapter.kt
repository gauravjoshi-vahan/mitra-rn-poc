package com.vahan.mitra_playstore.view.ratecard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.OtherPayItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class OtherPayAdapter(
    private val context: Context,
    private val additionalPayList: List<RateCardDetailsDTO.Incentive.Additional?>?,
    private val earningsList: List<RateCardDetailsDTO.Incentive.CalculatorArray.Earnings?>?,
    private val selectedEarning: String?
) : RecyclerView.Adapter<OtherPayAdapter.MyViewholder>() {


    class MyViewholder(itembinding: OtherPayItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: OtherPayItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val itemBinding: OtherPayItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.other_pay_item, parent, false
        )
        return MyViewholder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.binding.apply {
            tvAdditionalTitle.text = additionalPayList?.get(position)?.title
            tvAmountGuarantee.text = "${additionalPayList?.get(position)?.amountPrefix} " +
                    "${additionalPayList?.get(position)?.amountLabel}" +
                    "${additionalPayList?.get(position)?.amount}"
            if(earningsList!=null) {
                for (i in earningsList!!.indices) {
                    if (selectedEarning?.isNotEmpty() == true) {
                        if (selectedEarning == earningsList[i]!!.key) {
                            if (additionalPayList!![position]!!.key == selectedEarning) {
                                tvAdditionalTitle.setTextColor(context.getColor(R.color.black_v2))
                                tvAmountGuarantee.setTextColor(context.getColor(R.color.black_v2))
                                ivTick.setImageDrawable(context.getDrawable(R.drawable.ic_dark_green_tick))
                                break
                            } else {
                                tvAdditionalTitle.setTextColor(context.getColor(R.color.grey))
                                tvAmountGuarantee.setTextColor(context.getColor(R.color.grey))
                                ivTick.setImageDrawable(context.getDrawable(R.drawable.ic_grey_tick))
                            }

                        } else {
                            tvAdditionalTitle.setTextColor(context.getColor(R.color.grey))
                            tvAmountGuarantee.setTextColor(context.getColor(R.color.grey))
                            ivTick.setImageDrawable(context.getDrawable(R.drawable.ic_grey_tick))
                        }

                    } else {
                        tvAdditionalTitle.setTextColor(context.getColor(R.color.grey))
                        tvAmountGuarantee.setTextColor(context.getColor(R.color.grey))
                        ivTick.setImageDrawable(context.getDrawable(R.drawable.ic_grey_tick))
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return additionalPayList?.size?:0
    }

}