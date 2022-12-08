package com.vahan.mitra_playstore.view.earn.view.adapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.MilestoneTrackerNewAdapterBinding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.setImage
import com.vahan.mitra_playstore.view.ratecard.ui.RateCardDetailViewOnClick


/**
 * Created by Prakhar
 * Date : 28-Oct-22
This Adapter is Used for Incentive Summary in HOME PAGE
* In this Adapter we passed :-
* context--(For UI Operations),
* listener as a callback for click Listener --> Opening Rate Card
* milestoneData --> rendering the incentives information
 **/
class MilestoneNewTrackerAdapter (
    private val context: Context,
    private val listener: RateCardDetailViewOnClick,
    private val milestoneData: EarnDataModel.IncentiveStructures,
) : RecyclerView.Adapter<MilestoneNewTrackerAdapter.MyViewHolder>() {

    lateinit var companyAInfo : EarnDataModel.IncentiveStructures.IncentiveList.Company
    lateinit var companyBInfo : EarnDataModel.IncentiveStructures.IncentiveList.Company

    class MyViewHolder(milestoneStatusBinding: MilestoneTrackerNewAdapterBinding) :
        RecyclerView.ViewHolder(milestoneStatusBinding.root) {
        val binding: MilestoneTrackerNewAdapterBinding = milestoneStatusBinding
    }

    // Initialized the view using data binding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val milestoneStatusBinding: MilestoneTrackerNewAdapterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.milestone_tracker_new_adapter, parent, false
        )
        return MyViewHolder(milestoneStatusBinding)
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            val itemList = milestoneData.incentiveList
            val item = milestoneData.incentiveList[position]
            val allMilestones = item.milestones
            // variable Initialization for Position
            tvCompanyTitle.text = itemList[position].companyTitle
            tvLastUpdated.text = context.resources.getString(R.string.updated_till) + "${item.lastUpdatedTimeStamp}"
            label.text = itemList[position].messageLabel
            parentRootContainer.setOnClickListener {
                milestoneData.let { listener.onClick(position) }
            }

            //Mitra Incentive cards details rendering for 2 company
            // This setImage method is an Extension Function for setting Image using URL
            if(itemList[position].companies!!.size>1){
                companyAInfo = itemList[position].companies?.get(0)!!
                companyBInfo = itemList[position].companies?.get(1)!!
                context.setImage(context,companyAInfo.companyIcon!!,ivCompletedViewOne)
                context.setImage(context,companyBInfo.companyIcon!!,ivCompletedViewTwo)
                viewOne.text = companyAInfo.companyName + "+" + companyBInfo.companyName
                viewTwo.text = companyAInfo.companyName + "+" + companyBInfo.companyName
                context.setImage(context, companyAInfo.companyIcon!!,ivOne)
                context.setImage(context, companyBInfo.companyIcon!!,ivTwo)
                context.setImage(context, companyAInfo.companyIcon!!,ivOneEnd)
                context.setImage(context, companyBInfo.companyIcon!!,ivTwoEnd)
                companyTargetOne.text = ""+companyAInfo.targetAchieved!! + "\n" + companyAInfo.unit!!
                companyTargetTwo.text = ""+companyBInfo.targetAchieved!! + "\n" + companyBInfo.unit!!
            }
            // Other than Mitra Incentive Cards
            else{
                companyAInfo = itemList[position].companies?.get(0)!!
                item.companies?.get(0)?.companyIcon?.let { context.setImage(context,it, icAppIcon) }
                viewOne.text = companyAInfo.companyName
                viewTwo.text = companyAInfo.companyName
                companyTargetOne.text = ""+companyAInfo.targetAchieved!! + " " + companyAInfo.unit!!
                context.setImage(context,companyAInfo.companyIcon?:"",ivCompletedViewOne)
                context.setImage(context,companyAInfo.companyIcon?:"",ivOneEnd)
                context.setImage(context,companyAInfo.companyIcon?:"",ivOne)
                context.setImage(context,companyAInfo.companyIcon?:"",ivCompletedViewOne)
            }

            // Setting Seekbar Components Based on API DATA
            if(!itemList[position].spinnerKey?.contains("Mitra")!!)
            setData(allMilestones,holder.binding,"single",itemList,position)
            else
                setData(allMilestones,holder.binding,"multiple",itemList,position)
        }
    }



    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setData(
        allMilestones: List<EarnDataModel.IncentiveStructures.IncentiveList.Milestone>,
        binding: MilestoneTrackerNewAdapterBinding,
        type: String,
        itemList: List<EarnDataModel.IncentiveStructures.IncentiveList>,
        position: Int
    ) {
        if(type == "single") {
            hideSecondCompany(binding)
            var currentEarnedAmount = 0
            val totalEarnAmount = allMilestones[allMilestones.size-1].amount
            var currentTotalTripsCompleted = ""
            for(i in allMilestones.indices){
                Log.d("MILESTONE", "setData: "+allMilestones[i].achieved)
                if(allMilestones[0].achieved == "IN_PROGRESS"){
                    currentEarnedAmount = 0
                    currentTotalTripsCompleted = "${allMilestones[0].companyTargets!![0].target} ${allMilestones[0].companyTargets!![0].unit}"
                }else {
                    if (allMilestones[i].achieved == "IN_PROGRESS") {
                        currentEarnedAmount = allMilestones[i - 1].amount!!
                        currentTotalTripsCompleted = "${allMilestones[i - 1].companyTargets!![0].target} ${allMilestones[i - 1].companyTargets!![0].unit}"
                        break
                    } else {
                        currentEarnedAmount = allMilestones[allMilestones.size - 1].amount!!
                        currentTotalTripsCompleted = "${allMilestones[allMilestones.size - 1].companyTargets!![0].target} ${allMilestones[allMilestones.size - 1].companyTargets!![0].unit}"

                    }
                }
            }
            setupUI(binding,currentTotalTripsCompleted,currentEarnedAmount,totalEarnAmount!!,allMilestones)
        }
        else{
            showSecondCompany(binding)
            var currentEarnedAmount = 0
            var count = 0
            val totalEarnAmount = allMilestones[allMilestones.size-1].amount
            var currentTotalTripsCompleted = ""
            var currentTotalCount = 0
            for(i in allMilestones.indices){
                if(allMilestones[0].achieved == "IN_PROGRESS"){
                    currentEarnedAmount = 0
                    currentTotalCount = 0
                }else {
                    if (allMilestones[i].achieved == "IN_PROGRESS") {
                        currentEarnedAmount = allMilestones[i - 1].amount!!
                        break
                    } else {
                        currentEarnedAmount = allMilestones[allMilestones.size - 1].amount!!
                    }
                }
            }
            for (i in 0 until itemList[position].companies!!.size-1){
                currentTotalCount = itemList[position].companies!![i]!!.targetAchieved!! + itemList[position].companies!![i+1]!!.targetAchieved!!
            }
            for (i in itemList[position].companies!!){
                if(i!!.unit.equals("trips")){
                    count++
                }
            }
            if(count==2){
               currentTotalTripsCompleted =  ""+(itemList[position].companies?.get(0)?.targetAchieved?.plus(itemList[position].companies!![1]?.targetAchieved!!) ?: 0)+ (itemList[position].companies?.get(0)?.unit ?: "")
                binding.firstTrip.visibility = View.VISIBLE
                binding.lastTrip.visibility = View.VISIBLE
            }else{
                currentTotalTripsCompleted= ""
                binding.firstTrip.visibility = View.GONE
                binding.lastTrip.visibility = View.GONE
            }
            if(count==2) {
                binding.tvCompleted.text = "$currentTotalTripsCompleted trips Completed"
            }else{
                binding.tvCompleted.text = "$currentTotalCount Completed"
            }
            binding.lastTrip.text = currentTotalTripsCompleted
            binding.tvCurrentEarning.text = "₹$currentEarnedAmount"
            val charSequences: MutableList<CharSequence> = ArrayList()
            charSequences.add("0")
            charSequences.add(totalEarnAmount.toString())
            val charSequenceArray = charSequences.toTypedArray()
            binding.sbSingle5.tickMarkTextArray = charSequenceArray
            binding.sbSingle5.setRange(0F,totalEarnAmount!!.toFloat())
            binding.sbSingle5.setOnTouchListener { _, _ -> true }
            val anim = ValueAnimator.ofFloat(0F, currentEarnedAmount.toFloat())
            anim.duration = 3000
            anim.addUpdateListener { animation ->
                val animProgress = animation.animatedValue as Float
                binding.sbSingle5.setProgress(animProgress)
            }
            anim.start()
            binding.sbSingle5.setOnRangeChangedListener (object : OnRangeChangedListener {
                override fun onRangeChanged(
                    view: RangeSeekBar,
                    leftValue: Float,
                    rightValue: Float,
                    isFromUser: Boolean
                ) {
                    view.leftSeekBar.setIndicatorText("₹${leftValue.toInt()}")
                }

                override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

                }

                override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

                }

            })
        }
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun setupUI(
        binding: MilestoneTrackerNewAdapterBinding,
        currentTotalTripsCompleted: String,
        currentEarnedAmount: Int,
        totalEarnAmount:Int,
        allMilestones: List<EarnDataModel.IncentiveStructures.IncentiveList.Milestone>
    ) {
        binding.tvCompleted.text = "$currentTotalTripsCompleted Completed"
        binding.lastTrip.text = "${allMilestones[allMilestones.size-1].companyTargets!![0].target} ${allMilestones[allMilestones.size-1].companyTargets!![0].unit}"
        binding.tvCurrentEarning.text = "₹$currentEarnedAmount"
        val charSequences: MutableList<CharSequence> = ArrayList()
        charSequences.add("0")
        charSequences.add(totalEarnAmount.toString())
        val charSequenceArray = charSequences.toTypedArray()
        binding.sbSingle5.tickMarkTextArray = charSequenceArray
        binding.sbSingle5.setRange(0F,totalEarnAmount.toFloat())
        binding.sbSingle5.setOnTouchListener { _, _ -> true }
        val anim = ValueAnimator.ofFloat(0F, currentEarnedAmount.toFloat())
        anim.duration = 3000
        anim.addUpdateListener { animation ->
            val animProgress = animation.animatedValue as Float
            binding.sbSingle5.setProgress(animProgress)
        }
        anim.start()
        binding.sbSingle5.setOnRangeChangedListener (object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                view.leftSeekBar.setIndicatorText("₹${leftValue.toInt()}")
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

        })
    }

    private fun showSecondCompany(binding: MilestoneTrackerNewAdapterBinding) {
        binding.tvPlus.visibility = View.VISIBLE
        binding.ivTwo.visibility = View.VISIBLE
        binding.tvCompletedPlus.visibility = View.VISIBLE
        binding.ivCompletedViewTwoContainer.visibility = View.VISIBLE
        binding.ivTwoEnd.visibility = View.VISIBLE
        binding.tvPlusEnd.visibility = View.VISIBLE
    }

    private fun hideSecondCompany(binding: MilestoneTrackerNewAdapterBinding) {
        binding.tvPlus.visibility = View.GONE
        binding.tvCompletedPlus.visibility = View.GONE
        binding.tvCompletedPlus.visibility = View.GONE
        binding.ivCompletedViewTwoContainer.visibility = View.GONE
        binding.ivTwo.visibility = View.GONE
        binding.ivTwoEnd.visibility = View.GONE
        binding.tvPlusEnd.visibility = View.GONE
    }


    override fun getItemCount(): Int {
        return milestoneData.incentiveList.size
    }
}