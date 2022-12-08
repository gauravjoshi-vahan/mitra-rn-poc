package com.vahan.mitra_playstore.view.history.adapter

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.vahan.mitra_playstore.R
import androidx.core.content.ContextCompat
import android.widget.ToggleButton
import com.google.gson.Gson
import com.vahan.mitra_playstore.models.TransactionDetailsFilterConstraintsModel
import com.vahan.mitra_playstore.view.history.HistoryFragment
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList

class FilterListAdapter(
    private val activity: FragmentActivity,
    private val filterArrayList: ArrayList<TransactionDetailsFilterConstraintsModel.Filter.Item>,
    companyName: String?,
    private val historyFragment: HistoryFragment
) : RecyclerView.Adapter<FilterListAdapter.MyViewHolder>() {
    private var mPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_sheet_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvInfoValue.textOff = filterArrayList[position].label
        holder.tvInfoValue.textOn = filterArrayList[position].label
        holder.tvInfoValue.text = filterArrayList[position].label
        if (historyFragment.singleSelectedFilter.contains(filterArrayList[position].key)) {
            holder.tvInfoValue.isChecked = true
            holder.tvInfoValue.setTextColor(ContextCompat.getColor(activity, R.color.default_200))
        } else {
            holder.tvInfoValue.isChecked = false
            holder.tvInfoValue.setTextColor(
                ContextCompat.getColor(
                    activity,
                    R.color.light_color_heading
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return filterArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvInfoValue: ToggleButton
        override fun onClick(view: View) {
            when (view.id) {
                R.id.tv_info_filter_Data -> {
                    //                    if (tvInfoValue.isChecked()) {
//                        tvInfoValue.setTextColor(ContextCompat.getColor(activity, R.color.purple_200));
//                        historyFragment.valuesSelectedList.add(filterArrayList.get(getAdapterPosition()).getKey());
//                    } else {
//                        tvInfoValue.setTextColor(ContextCompat.getColor(activity, R.color.light_color_heading));
//                        historyFragment.valuesSelectedList.remove(filterArrayList.get(getAdapterPosition()).getKey());
//                    }
//                    Log.d("JSONARRYClick", "getFilterdJsonArray: " + historyFragment.valuesSelectedList);
                    mPosition = adapterPosition
                    tvInfoValue.isChecked
                    tvInfoValue.setTextColor(ContextCompat.getColor(activity, R.color.default_200))
                    notifyDataSetChanged()
                    historyFragment.singleSelectedFilter = filterArrayList[mPosition].key
                }
            }
        }

        init {
            tvInfoValue = itemView.findViewById(R.id.tv_info_filter_Data)
            tvInfoValue.setOnClickListener(this)
        }
    }

    val jsonArray: JSONArray
        get() {
            try {
                Log.d(
                    "JSONARRYRETURN",
                    "getFilterdJsonArray: " + historyFragment.valuesSelectedList
                )
                return JSONArray(Gson().toJson(historyFragment.valuesSelectedList))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return JSONArray()
        }
}