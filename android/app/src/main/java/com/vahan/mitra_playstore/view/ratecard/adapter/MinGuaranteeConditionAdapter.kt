package com.vahan.mitra_playstore.view.ratecard.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ConditionItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class MinGuaranteeConditionAdapter(private val context: Context,
                                   private val minimumGuarantee: List<RateCardDetailsDTO.Incentive.MinimumGuarantee.Condition?>?
): RecyclerView.Adapter<MinGuaranteeConditionAdapter.MyViewHolder>() {

    class MyViewHolder(itembinding: ConditionItemBinding) :
        RecyclerView.ViewHolder(itembinding.root) {
        val binding: ConditionItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: ConditionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.condition_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvKey.text = minimumGuarantee?.get(position)?.header
            tvHtmlContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(minimumGuarantee!![position]?.value, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(minimumGuarantee!![position]?.value)
            }
        }
    }

    override fun getItemCount(): Int {
       return minimumGuarantee?.size!!
    }

}