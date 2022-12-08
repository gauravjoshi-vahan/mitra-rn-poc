package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vahan.mitra_playstore.view.earn.view.ui.CashOutTransactionFragment
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.BankItemBinding
import com.vahan.mitra_playstore.models.GetBankDetailsModel


class BankBottomSheet(
    context: Context,
    val price: String,
    val bankAccountDetails: GetBankDetailsModel.BankAccountDetails,
    val amount: String,
    val callback: CashOutTransactionFragment
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bank_bottom_sheet)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        findViewById<RecyclerView>(R.id.bank_list_bottom_sheet)?.apply {
            adapter = BankAdapter(bankAccountDetails)
        }
        findViewById<TextView>(R.id.transfer_money)?.apply {
            this?.text = resources.getString(R.string.transfer)+amount
            this?.setOnClickListener {
                dismiss()

            }
        }
    }

    class BankAdapter(val bankAccountDetails: GetBankDetailsModel.BankAccountDetails) :
        RecyclerView.Adapter<BankAdapter.ViewHolder>() {
        private lateinit var context: Context
        private lateinit var listItem: View
        private var qnt = 0


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemBinding: BankItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.bank_item, parent, false
            )
            listItem = itemBinding.root
            context = itemBinding.root.context
            return ViewHolder(itemBinding)
        }


        override fun getItemCount(): Int {
            return 1
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.apply {
                accountType.text=bankAccountDetails.accountType
                if (bankAccountDetails.accountNumber.length > 4)
                    accountName.text = "${bankAccountDetails.bankName} " + bankAccountDetails.accountNumber.substring(bankAccountDetails.accountNumber.length - 4, bankAccountDetails.accountNumber.length)
            }
        }


        class ViewHolder(itemBinding: BankItemBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            val binding: BankItemBinding = itemBinding

        }
    }
}

interface payment{
    fun paymeMoney(amount:String,position: Int)
}