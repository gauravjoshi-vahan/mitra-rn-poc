package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyEarningsDeductionItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel
import java.lang.Exception

class DeductionsItemAdapter(
    private val context: Context,
    private val ind: Int,
    private val weeklyEarningsData: WeeklyEarningsModel,
) : RecyclerView.Adapter<DeductionsItemAdapter.MyViewHolder>() {
    class MyViewHolder(deductionsItemBinding: WeeklyEarningsDeductionItemBinding) :
        RecyclerView.ViewHolder(deductionsItemBinding.root) {
        val binding: WeeklyEarningsDeductionItemBinding = deductionsItemBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DeductionsItemAdapter.MyViewHolder {
        val deductionsItemBinding: WeeklyEarningsDeductionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_earnings_deduction_item, parent, false
        )
        return DeductionsItemAdapter.MyViewHolder(deductionsItemBinding)
    }

    override fun onBindViewHolder(
        holder: DeductionsItemAdapter.MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val item =
            weeklyEarningsData.weeklyDeduction?.companyDeduction?.get(ind)?.deductionBreakdown?.get(
                position)
        holder.binding.apply {
            if (item != null) {
                if (item.key !== null)
                    deductionItemName.text = item.key
                else
                    deductionItemName.text = "--"
                if (item.amountInRupees !== null)
                    deductionAmountTxt.text = item.amountInRupees.toString()
                else
                    deductionAmountTxt.text = "--"
            }
        }
    }

    override fun getItemCount(): Int {
        try {
            return weeklyEarningsData.weeklyDeduction?.companyDeduction?.get(ind)?.deductionBreakdown?.size
                ?: 0
        }catch (e: Exception){
            return 0
        }

    }
}