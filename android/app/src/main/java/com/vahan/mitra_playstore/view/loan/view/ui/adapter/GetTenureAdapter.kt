package com.vahan.mitra_playstore.view.loan.view.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import com.vahan.mitra_playstore.view.loan.view.ui.fragment.TenureSelectionFragment
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.TenureRowItemBinding
import com.vahan.mitra_playstore.models.PostLoanApiModel

class GetTenureAdapter(
    val requireActivity: FragmentActivity,
    private val tenureSelectionFragment: TenureSelectionFragment,
    private val tenuresSelection: ArrayList<PostLoanApiModel>,
    val i: Int
) : RecyclerView.Adapter<GetTenureAdapter.MyViewHolder>() {

    private var mPosition: Int? = -1

    class MyViewHolder(itemBinding: TenureRowItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: TenureRowItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: TenureRowItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tenure_row_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply {
            tvMonthCount.text = tenuresSelection[position].duration.toString()
            tvTitle.text = tenuresSelection[position].emiPeriodValue.toString()
            if (mPosition == holder.adapterPosition) {
                //set selected here
                tenureContainer.setBackgroundResource(R.drawable.upload_text_color)
                tvMonthCount.setTextColor(ContextCompat.getColor(requireActivity,R.color.black_v2))
                tvTitle.setTextColor(ContextCompat.getColor(requireActivity,R.color.black_v2))
            } else {
                tenureContainer.setBackgroundResource(R.drawable.tenure_background)
                tvMonthCount.setTextColor(ContextCompat.getColor(requireActivity,R.color.light_color_heading))
                tvTitle.setTextColor(ContextCompat.getColor(requireActivity,R.color.light_color_heading))
            }
            tenureEvaluation.setOnClickListener {
                mPosition = holder.adapterPosition
                notifyDataSetChanged()
                tenureSelectionFragment.tenureItemValuesSet(
                    tenuresSelection[position].emi,
                    tenuresSelection[position].emiPeriodKey,
                    1, tenuresSelection[position].duration!!,
                    tenuresSelection[position].weeklyDebit
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return tenuresSelection.size
    }


}