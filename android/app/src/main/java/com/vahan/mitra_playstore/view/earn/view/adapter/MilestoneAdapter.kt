package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.RateCardMilestoneItemBinding
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO

class MilestoneAdapter(
    private val context: Context,
    private val data: RateCardDetailsDTO,
    private var pos: Int
    // var listA: List<String> = listOf<String>()

) : RecyclerView.Adapter<MilestoneAdapter.MyViewHolder>() {

    //    var list: ArrayList<RateCardModel.IncentiveDetails.Milestone> =
//        ArrayList<RateCardModel.IncentiveDetails.Milestone>()
    private var cnt = 0

    class MyViewHolder(itemBinding: RateCardMilestoneItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RateCardMilestoneItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: RateCardMilestoneItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rate_card_milestone_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply {
            if(data.incentiveList?.get(pos)?.milestones?.get(position)?.achieved=="COMPLETED"){
                cnt++
                //   viewColor3.visibility = View.VISIBLE
               // seekbar1.visibility = View.GONE
                seekbar2.visibility = View.VISIBLE
                viewColor3.visibility = View.VISIBLE
                //   icVespa3.visibility = View.GONE
                viewColor2.visibility = View.VISIBLE
                seekbar4.visibility = View.VISIBLE
                icVespa2.setImageResource(R.drawable.ic_ellipse_orange)
                if (  position< data.incentiveList?.get(pos)?.milestones?.size!!-1 && data.incentiveList[pos]!!.milestones?.get(position+1)!!.achieved=="IN_PROGRESS") {
                    icVespaGreentick.visibility = View.VISIBLE
                    seekbar3.visibility = View.VISIBLE
                    viewColor.visibility = View.INVISIBLE
                }
                if (position == data.incentiveList.get(pos)!!.milestones!!.size - 1) {
                    seekbar4.visibility = View.VISIBLE
                    seekbar5.visibility = View.GONE
                    viewColor3.visibility = View.GONE
                    viewColor4.visibility = View.VISIBLE
                }

            } else if (data.incentiveList?.get(pos)?.milestones?.get(position)?.achieved == "IN_PROGRESS") {

                cnt++
                seekbar1.visibility = View.VISIBLE
                // seekbar3.visibility = View.VISIBLE
                seekbar4.visibility = View.GONE
                //   seekbar2.visibility = View.GONE
                //    icVespa3.visibility = View.GONE
                viewColor3.visibility = View.INVISIBLE
                viewColor2.visibility = View.INVISIBLE
                icVespaGreentick.visibility = View.GONE

                if (position == 0) {
                    seekbar2.visibility = View.GONE
                    seekbar3.visibility = View.VISIBLE
                    icVespa7.visibility = View.VISIBLE
                }

                if (position == data.incentiveList[pos]?.milestones?.size!! - 1) {
                    seekbar4.visibility = View.VISIBLE
                    seekbar5.visibility = View.GONE
                    viewColor3.visibility = View.GONE
                    viewColor4.visibility = View.VISIBLE
                }

            } else {
                cnt++
                seekbar1.visibility = View.GONE
                viewColor2.visibility = View.INVISIBLE
                seekbar2.visibility = View.VISIBLE
                icVespa2.visibility = View.VISIBLE
                viewColor3.visibility = View.GONE
                icVespa2.setImageResource(R.drawable.ic_ellipse_grey)
                icVespaGreentick.visibility = View.GONE

                if (cnt == itemCount) {
                    viewColor4.visibility = View.VISIBLE
                }

                if (position == (data.incentiveList?.get(pos)?.milestones?.size ?: 0) - 1) {
                    seekbar4.visibility = View.VISIBLE
                    seekbar5.visibility = View.GONE
                    viewColor3.visibility = View.GONE
                    viewColor4.visibility = View.VISIBLE
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return data.incentiveList?.get(pos)?.milestones?.size ?:0
    }
}
