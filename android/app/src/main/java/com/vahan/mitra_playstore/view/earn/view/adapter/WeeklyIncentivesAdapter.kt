package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.RateCardTripsDoneItemBinding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter

class WeeklyIncentivesAdapter (
    private val context: Context,
    private val data: EarnDataModel.IncentiveStructures,
    private var pos: Int
  //  var listA: List<String> = listOf<String>()

    ) : RecyclerView.Adapter<WeeklyIncentivesAdapter.MyViewHolder>() {

 //   var list: ArrayList<RateCardModel.IncentiveDetails.Milestone> = ArrayList<RateCardModel.IncentiveDetails.Milestone>()

    class MyViewHolder(itemBinding: RateCardTripsDoneItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RateCardTripsDoneItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: RateCardTripsDoneItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rate_card_trips_done_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ){
        holder.binding.apply {
            tvXTripsToEarnY.text = data.incentiveList[pos].messageLabel
            if(data.incentiveList[pos].spinnerKey=="Mitra"){

                ivAdd.visibility=View.VISIBLE
                ivCompanyLogo2.visibility = View.VISIBLE
                tvTripsCompleted2.visibility = View.VISIBLE

                data.incentiveList[pos].companies?.get(1)?.let { it.companyIcon?.let { it1 ->
                    setImage(it1,ivCompanyLogo2)
                } }

                data.incentiveList[pos].companies?.get(0)?.let { it.companyIcon?.let { it1 ->
                    setImage(it1,ivCompanyLogo)
                } }
                tvTripsCompleted.text = data.incentiveList[pos].milestones[position].companyTargets?.get(0)?.target.toString() + " "+
                        data.incentiveList[pos].milestones[position].companyTargets?.get(0)?.unit.toString()
                tvAmtEarned.text =context.resources.getString(R.string.rupee)+" "+ data.incentiveList[pos].milestones[position].amount.toString()

                tvTripsCompleted2.text = data.incentiveList[pos].milestones[position].companyTargets?.get(1)?.target.toString() + " "+
                        data.incentiveList[pos].milestones[position].companyTargets?.get(1)?.unit.toString()

                if(data.incentiveList[pos].milestones[position].achieved=="COMPLETED"){
                    ivCash1.visibility = View.VISIBLE
                }
                if(data.incentiveList[pos].milestones[position].achieved=="PENDING"){
                    ivCash1.visibility = View.GONE
                }
                if(data.incentiveList[pos].milestones[position].achieved=="IN_PROGRESS"){

                    ll1.visibility = View.VISIBLE
                    ivCash1.visibility = View.GONE
                    ivCash1.visibility = View.INVISIBLE
                    rl1.setBackgroundResource(R.drawable.card_background_yellow_outlined)

                }
            }
            else{
                ivAdd.visibility=View.GONE
                ivCompanyLogo2.visibility = View.GONE
                tvTripsCompleted2.visibility = View.GONE

                data.incentiveList[pos].companies?.get(0)?.let { it.companyIcon?.let { it1 ->
                    setImage(it1,ivCompanyLogo)
                } }

                tvTripsCompleted.text = data.incentiveList[pos].milestones[position].companyTargets?.get(0)?.target.toString() + " "+
                        data.incentiveList[pos].milestones[position].companyTargets?.get(0)?.unit.toString()
                tvAmtEarned.text =context.resources.getString(R.string.rupee)+" "+ data.incentiveList[pos].milestones[position].amount.toString()

                if(data.incentiveList[pos].milestones[position].achieved=="IN_PROGRESS"){

                    ll1.visibility = View.VISIBLE
                    ivCash1.visibility = View.INVISIBLE
                    rl1.setBackgroundResource(R.drawable.card_background_yellow_outlined)

                }
            }



//            tvAmtEarned.text = rateCardModel.incentiveDetailsList[position]
//              list= rateCardModel.incentiveDetailsList?.get(0)?.milestones as ArrayList<RateCardModel.IncentiveDetails.Milestone>
//            tvAmtEarned.text= listA[position].toString()
//            if(listA[position] =="In Progress"){
//                ll1.visibility = View.VISIBLE
//                ivCash1.visibility = View.INVISIBLE
//                rl1.setBackgroundResource(R.drawable.card_background_yellow_outlined)
//            }
        }
    }

    private fun setImage(url: String, imageView: ImageView) {
        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        val uri = Uri.parse(url)
        requestBuilder.load(uri).into(imageView)
    }


    override fun getItemCount(): Int {
        return data.incentiveList[pos].milestones.size
    }
}