package com.vahan.mitra_playstore.view.history.adapter

import androidx.fragment.app.FragmentActivity
import com.vahan.mitra_playstore.models.TransactionDetailsFilterConstraintsModel
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.interfaces.SortAndFilterCallback
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.vahan.mitra_playstore.R
import android.widget.RadioButton
import com.vahan.mitra_playstore.view.history.HistoryFragment
import java.util.ArrayList

class SortListAdapter(
    private val activity: FragmentActivity,
    private val sortedArrayList: ArrayList<TransactionDetailsFilterConstraintsModel.Sort>,
    historyFragment: HistoryFragment
) : RecyclerView.Adapter<SortListAdapter.MyViewHolder>() {
    private val callback: SortAndFilterCallback
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sort_item_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == HistoryFragment.position) {
            holder.rbSort.setTextColor(activity.getColor(R.color.black_v2))
            holder.rbSort.isChecked = true
        } else {
            holder.rbSort.setTextColor(activity.getColor(R.color.light_color_heading))
            holder.rbSort.isChecked = false
        }
        holder.rbSort.text = "  " + sortedArrayList[position].name
    }

    override fun getItemCount(): Int {
        return sortedArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val rbSort: RadioButton = itemView.findViewById(R.id.rbSort)
        override fun onClick(v: View) {
            when (v.id) {
                R.id.rbSort -> {
                    callback.sortSelectedData(
                        sortedArrayList[adapterPosition].key,
                        sortedArrayList[adapterPosition].name
                    )
                    HistoryFragment.position = adapterPosition
                    val position = adapterPosition
                    var i = 0
                    while (i < sortedArrayList.size) {
                        sortedArrayList[i].isSelected = false
                        i++
                    }
                    sortedArrayList[position].isSelected = true
                    notifyDataSetChanged()
                }
            }
        }

        init {
            rbSort.setOnClickListener(this)
        }
    }

    init {
        callback = historyFragment
    }
}