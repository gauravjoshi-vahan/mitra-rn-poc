package com.vahan.mitra_playstore.view.ratecard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.AdditionalPayItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class AdditionalPayAdapter(private val context: Context,
                           private val additionalPayList: List<RateCardDetailsDTO.Incentive.Additional?>?
): RecyclerView.Adapter<AdditionalPayAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: AdditionalPayItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: AdditionalPayItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: AdditionalPayItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.additional_pay_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvAdditionalTitle.text = additionalPayList?.get(position)?.title
            tvAmountGuarantee.text = "${additionalPayList?.get(position)?.amountPrefix} " +
                    "${additionalPayList?.get(position)?.amountLabel}" +
                    "${additionalPayList?.get(position)?.amount}"
            tvPerOrder.text = additionalPayList?.get(position)?.unitLabel
            tvKey.text = additionalPayList?.get(position)?.label
            tvHtmlContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(additionalPayList!![position]?.condition, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(additionalPayList!![position]?.condition)
            }
            tvFrequency.text = additionalPayList[position]?.frequency?:""
        }
    }

    override fun getItemCount(): Int {
        return additionalPayList?.size?:0
    }
}