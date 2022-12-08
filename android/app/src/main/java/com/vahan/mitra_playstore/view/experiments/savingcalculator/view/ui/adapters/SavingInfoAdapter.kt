package com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.PaymentHistoryItemBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.dateConversionToString
import com.vahan.mitra_playstore.utils.roundOff2digit
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.FetchingUserInfoDTO
import java.util.*

/**
 * Created By Prakhar
 * DATE : Mon 7 NOV 2022
 *  This adapter contains the fragment CONTEXT for UI operation
 *  and calling Extension Function and dataList for old transaction records
 */

class SavingInfoAdapter(
    private val activity: Context,
    private val dataList: ArrayList<FetchingUserInfoDTO.SavingsTransaction>,
    ) : RecyclerView.Adapter<SavingInfoAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: PaymentHistoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: PaymentHistoryItemBinding = itemBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemBinding: PaymentHistoryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.payment_history_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transDetails = dataList[position]
        holder.binding.apply {
                if(transDetails.status.equals(Constants.SUCCESS) && transDetails.type.equals(Constants.CREDITCAPS)) {
                    transcName.text = "Money Saved"
                }else if(transDetails.status.equals(Constants.SUCCESS) && transDetails.type.equals(Constants.DEBITCAPS)){
                    if(transDetails.event.equals(Constants.WITHDRAW)) {
                        transcName.text = "Money Withdraw"
                    }
                    else {
                        transcName.text = "Interest Paid"
                    }

                }
                else if (transDetails.status.equals(Constants.FAILEDCAPS)){
                    transcName.text = "Transaction Failed"
                }else{
                    transcName.text = "Transaction Pending"
                }

            transcNameSubHeading.text = activity.dateConversionToString(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                transDetails.createdAt.toString(),
                "dd LLLL yyyy"
                )
            salaryIncome.text = "₹"+activity.roundOff2digit(transDetails.amount!!.toDouble())

        }
    }

    override fun getItemCount(): Int {
       return dataList.size
    }




}