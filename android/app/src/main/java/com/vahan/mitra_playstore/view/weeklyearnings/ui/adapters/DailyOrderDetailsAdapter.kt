package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.DailyOrderOrderDetailItemBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat


class DailyOrderDetailsAdapter(
    private val context: Context,
    private val data: DailyOrderModel
): RecyclerView.Adapter<DailyOrderDetailsAdapter.MyViewHolder>() {

    private var nestedRV1Adapter: DailyOrderNestedRV1Adapter? = null
    private var nestedRV2Adapter: DailyOrderNestedRV2Adapter? = null
    private var nestedRV3Adapter: DailyOrderNestedRV3Adapter? = null
    private var isEnabled = false

    class MyViewHolder(itembinding: DailyOrderOrderDetailItemBinding):
        RecyclerView.ViewHolder(itembinding.root){
        val binding: DailyOrderOrderDetailItemBinding = itembinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: DailyOrderOrderDetailItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.daily_order_order_detail_item, parent, false
        )
        return DailyOrderDetailsAdapter.MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvOrderId.text = data.orderLevelDetails[position].orderHeader.orderId[0].label +" : "+
                    data.orderLevelDetails[position].orderHeader.orderId[0].value


            val dtStart = "2022-08-29T13:30Z"
            val dateFormat : DateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            try {
                val date = dateFormat.parse(dtStart)
                Log.d("DATE", "onBindViewHolder: $date")
                val formatter: DateFormat = SimpleDateFormat("hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
                val dateStr = formatter.format(date)
                Log.d("DATE_VALUES", "onBindViewHolder: $dateStr")
                tvTime.text = data.orderLevelDetails[position].orderHeader.tripTimeAndAmount[0].label+ " " + dateStr
            }catch (e : ParseException){

            }
            //You will get date object relative to server/client timezone wherever it is parsed







            tvAmtValue.text = data.orderLevelDetails[position].orderHeader.tripTimeAndAmount[1].value

            // Calling Nested_RV1 (firstMile,lastMile)
            nestedRV1Adapter = DailyOrderNestedRV1Adapter(context,data,position)
            rvNested1.adapter = nestedRV1Adapter

            // Calling Nested_RV2
            nestedRV2Adapter = DailyOrderNestedRV2Adapter(context,data,position)
            rvNested2.adapter = nestedRV2Adapter

            // Calling Nested_RV3
            nestedRV3Adapter = DailyOrderNestedRV3Adapter(context,data,position)
            rvNested3.adapter = nestedRV3Adapter

            rlContainer.setOnClickListener {
                if(!isEnabled){
                    rvNested2.visibility = View.VISIBLE
                    rvNested3.visibility = View.VISIBLE
                    ivDropdown.setImageResource(R.drawable.ic_arrow_up)
                    isEnabled = true
                }else{
                    rvNested2.visibility = View.GONE
                    rvNested3.visibility = View.GONE
                    ivDropdown.setImageResource(R.drawable.ic_arrow_down)
                    isEnabled = false
                }
            }
            if(position==data.orderLevelDetails.size-1){
                rlDivider.visibility = View.GONE
            }else{
                rlDivider.visibility = View.VISIBLE
            }


            if(position==data.orderLevelDetails.size-1){
                rlDivider.visibility=View.GONE
            }

        }


    }

    override fun getItemCount(): Int {
        return data.orderLevelDetails.size ?: 0
    }
}