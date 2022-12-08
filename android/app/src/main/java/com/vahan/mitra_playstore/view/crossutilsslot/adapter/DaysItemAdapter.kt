package com.vahan.mitra_playstore.view.crossutilsslot.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.SlotAvailabilityBinding
import com.vahan.mitra_playstore.databinding.WeeklyDateItemBinding
import com.vahan.mitra_playstore.interfaces.SlotTimeClickListener
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.view.crossutilsslot.fragment.CrossUtilFragment
import com.vahan.mitra_playstore.view.crossutilsslot.models.DateValuesDTO

class DaysItemAdapter(
    private val listener: SlotTimeClickListener,
    val requireActivity: FragmentActivity,
    private val dateList: List<DateValuesDTO>,
) :
    RecyclerView.Adapter<DaysItemAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: WeeklyDateItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: WeeklyDateItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: WeeklyDateItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_date_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if(position == CrossUtilFragment.lastCheckedPosition){
            holder.binding.rlContainerClicked.setBackgroundResource(R.drawable.dateselector_highlight)
            holder.binding.weeklyDay.setTextColor(requireActivity.getColor(R.color.white))
            holder.binding.weekDate.setTextColor(requireActivity.getColor(R.color.white))
        }else{
            holder.binding.rlContainerClicked.setBackgroundResource(R.color.white)
            holder.binding.weeklyDay.setTextColor(requireActivity.getColor(R.color.black))
            holder.binding.weekDate.setTextColor(requireActivity.getColor(R.color.black))
        }
        holder.binding.weeklyDay.text = dateList[position].day
        holder.binding.weekDate.text = dateList[position].date.toString()
        holder.binding.rlContainerClicked.setOnClickListener {
            val copyOfLastCheckedPosition = CrossUtilFragment.lastCheckedPosition
            CrossUtilFragment.lastCheckedPosition = position
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(CrossUtilFragment.lastCheckedPosition)
            listener.slotTimeClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }
}