package com.vahan.mitra_playstore.view.refer.view.customdialog


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ShowRecentReferralBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.ExperimentActivity
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeNewRespModel
import com.vahan.mitra_playstore.view.refer.models.ReferralMilestoneModel
import com.vahan.mitra_playstore.view.refer.models.ReferralStatusNewModel


class RecentReferralDialog(
    val name:String,
    context: Context,
    val data: ReferralMilestoneModel,
    private val arrTripsDoneReferralStatus: ArrayList<ReferralStatusNewModel.BonusAmountObject>,
    private val arrTripsDoneReferralHome: ArrayList<ReferralHomeNewRespModel.BonusAmountObject>
): AlertDialog(context, R.style.DialogTheme) {

    private lateinit var binding: ShowRecentReferralBinding
    lateinit var textView:TextView
    private val VISIBLE = View.VISIBLE
    private val GONE = View.GONE

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShowRecentReferralBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        initViews()

    }

    private fun initViews() {
        clickListener()
        setData()
    }

    private fun clickListener() {
        binding.ivCross.setOnClickListener {
            dismiss()
        }
        binding.rl5StgFive.setOnClickListener {
            tellMessageInWhatsApp()
        }
        binding.rl5.setOnClickListener {
                tellMessageInWhatsApp()
        }
        binding.rlTellYourFriendBlockStageThree.setOnClickListener {
            tellMessageInWhatsApp()
        }
        binding.rlTellYourFriendBlockStageTwo.setOnClickListener {
            val pm: PackageManager = context.packageManager
            try {
                val waIntent = Intent(Intent.ACTION_SEND)
                waIntent.type = "text/plain"
                var text = context.getString(R.string.joined_mitra_whatsapp_msg)
                waIntent.setPackage("com.whatsapp")
                waIntent.putExtra(Intent.EXTRA_TEXT, text)
                context.startActivity(Intent.createChooser(waIntent, "Share with"))
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.whatsapp_not_installed),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.rlWBStgOne.setOnClickListener {
            payoutLinkOpen()
        }
        binding.rlWBStgTwo.setOnClickListener {
            payoutLinkOpen()
        }
        binding.rlWBStgThree.setOnClickListener {
            payoutLinkOpen()
        }
        binding.rlWBStgFour.setOnClickListener {
            payoutLinkOpen()
        }
        binding.rlWBStgFive.setOnClickListener {
            payoutLinkOpen()
        }

    }

    private fun payoutLinkOpen() {
        context.startActivity(Intent(context, ExperimentActivity::class.java)
            .putExtra(
                "link",data.payoutDetails!!.link
            ))
    }

    private fun tellMessageInWhatsApp() {
        val pm: PackageManager = context.packageManager
        try {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "text/plain"
            var text = "Hello! Take more orders to earn more. Click https://mitraapp.page.link/domoretrips to open the Mitra App Now"
            waIntent.setPackage("com.whatsapp")
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(Intent.createChooser(waIntent, "Share with"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.whatsapp_not_installed),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private fun setData() {
        binding.rlWBStgOne.isEnabled = false
        binding.rlWBStgTwo.isEnabled = false
        binding.tvReferralStatus.text = data.referralStages?.get(data.referralStages!!.size-1)!!.label
        checkStatusOFStage()
    }

    private fun checkStatusOFStage() {
        for(i in 0 until data.referralStages!!.size){
            when(data.referralStages[i]!!.groupKey){
                Constants.REFERRAL_START -> {
                    setLevelOfDateReferralStart(data.referralStages[i]!!.stageCompletedAt)
                }
                Constants.JOINED_MITRA -> {
                    setLevelOfDateJM(data.referralStages[i]!!.stageCompletedAt!!)
                }
                Constants.FIRST_TRIP_DONE -> {
                    setLevelOfDateFTD(data.referralStages[i]!!.stageCompletedAt!!)
                }
            }
        }
        setStatusLabel(data.referralStages?.get(data.referralStages!!.size-1)!!.groupKey?:"")
        visibilityOfStages(data.referralStages?.get(data.referralStages!!.size-1)!!.groupKey?:"",
            data.payoutDetails!!)
        
    }

    private fun visibilityOfStages(
        stage: String,
        payoutDetails: ReferralMilestoneModel.PayoutDetails
    ) {
        when(stage){
            Constants.REFERRAL_START -> {
                showAndHideBasedOnStage(VISIBLE,GONE,GONE,GONE,GONE,GONE)
                if(payoutDetails.link==null)
                withdrawButtonVisibilityOnStage(GONE,GONE,GONE,GONE,GONE,GONE)
                else
                    withdrawButtonVisibilityOnStage(VISIBLE,GONE,GONE,GONE,GONE,GONE)
            }
            Constants.JOINED_MITRA -> {
                showAndHideBasedOnStage(GONE,VISIBLE,GONE,GONE,GONE,GONE)
                if(payoutDetails.link==null)
                    withdrawButtonVisibilityOnStage(GONE,GONE,GONE,GONE,GONE,GONE)
                else
                withdrawButtonVisibilityOnStage(GONE,VISIBLE,GONE,GONE,GONE,GONE)
            }
           Constants.FIRST_TRIP_DONE -> {
               showAndHideBasedOnStage(GONE,GONE,VISIBLE,GONE,GONE,GONE)
               if(payoutDetails.link==null)
                   withdrawButtonVisibilityOnStage(GONE,GONE,GONE,GONE,GONE,GONE)
               else
               withdrawButtonVisibilityOnStage(GONE,GONE,VISIBLE,GONE,GONE,GONE)
            }
            Constants.BONUS_EARNED -> {
                showAndHideBasedOnStage(GONE,GONE,GONE,VISIBLE,GONE,GONE)
                setDataForEarned()
                setDataForSeekBarBonusEarned()
                if(payoutDetails.link==null)
                    withdrawButtonVisibilityOnStage(GONE,GONE,GONE,GONE,GONE,GONE)
                else
                withdrawButtonVisibilityOnStage(GONE,GONE,GONE,VISIBLE,GONE,GONE)
            }
            Constants.REFERRAL_COMPLETED -> {
                setDataForEarned()
                showAndHideBasedOnStage(GONE,GONE,GONE,GONE,VISIBLE,GONE)
                setDataForSeekBarReferralCompleted()
                if(payoutDetails.link==null)
                    withdrawButtonVisibilityOnStage(GONE,GONE,GONE,GONE,GONE,GONE)
                else
                withdrawButtonVisibilityOnStage(GONE,GONE,GONE,GONE,VISIBLE,GONE)
            }

        }

    }

    private fun withdrawButtonVisibilityOnStage(
        isWithdrawStageOne: Int,
        isWithdrawStageTwo: Int,
        isWithDrawStageThree: Int,
        isWithDrawStageFour: Int,
        isWithDrawStageFive: Int,
        isWithdrawStageSix: Int
    ) {
        binding.rlWBStgOne.visibility = isWithdrawStageOne
        binding.rlWBStgTwo.visibility = isWithdrawStageTwo
        binding.rlWBStgThree.visibility = isWithDrawStageThree
        binding.rlWBStgFour.visibility = isWithDrawStageFour
        binding.rlWBStgFive.visibility = isWithDrawStageFive
    }

    private fun setStatusLabel(stages: String) {
        when(stages){
            Constants.REFERRAL_START-> {
                binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context,R.color.default_200))
                binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.border_filled_outlined)
            }
            Constants.JOINED_MITRA -> {
                binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context,R.color.joined_mitra_color))
                binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_yellow)
            }
            Constants.FIRST_TRIP_DONE -> {
                binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context,R.color.first_trip_color))
                binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_blue)
            }
            Constants.BONUS_EARNED -> {
                binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context,R.color.referral_bonus_earned_color))
                binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_green)
            }
            Constants.REFERRAL_COMPLETED -> {
                binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context,R.color.grey3))
                binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_grey)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataForEarned() {
        binding.tvTotalTripsBEStgFour.text = data.totalNumberOfTrips + context.getString(R.string._trips)
        binding.amtEarnedBEStgFour.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
        binding.tvTotalTripsRCStgFive.text = data.totalNumberOfTrips + context.getString(R.string._trips)
        binding.amtEarnedRCStgFive.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
//        binding.tvDaysLeft2BEStgFour.text = data.numberOfDaysUntilExpiry
//        binding.tvDaysLeft2StgFive.text = data.numberOfDaysUntilExpiry
        binding.tv2StgFive.text = data.referralStages!![(data.referralStages!!.size-1)]!!.info
        binding.tvDescBEStgFour.text = data.referralStages[(data.referralStages.size-1)]!!.info
    }

    private fun setLevelOfDateReferralStart(dateLevelRS: String?) {
        binding.tvDateRSDateStageOne.text = dateLevelRS
        binding.tvDateRSDateStageTwo.text = dateLevelRS
        binding.tvDateRSDateStageThree.text = dateLevelRS
        binding.tvDateRSDateStageFour.text = dateLevelRS
        binding.tvDateRSDateStageFive.text = dateLevelRS
    }


    private fun setLevelOfDateJM(dateLevelJM: String) {
        binding.tvDateJMStageTwo.text = dateLevelJM
        binding.tvDateJMStageThree.text = dateLevelJM
        binding.tvDateJMStageFour.text = dateLevelJM
        binding.tvDateJMStageFive.text = dateLevelJM
    }

    private fun setLevelOfDateFTD(dateLevelFTD: String) {
        binding.tvDateFTDStgThree.text = dateLevelFTD
        binding.tvDateFTDStgFour.text = dateLevelFTD
        binding.tvDateFTDStgFive.text = dateLevelFTD
    }

    private fun setDataForSeekBarBonusEarned() {
        if (data.totalNumberOfTrips?.toInt()!! in 10..30) {
            binding.stepViewUiFour.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
            binding.stepViewUiFour.txtTitleBottom.setTextColor(ContextCompat.getColor(context, R.color.black))
            if(arrTripsDoneReferralStatus.size>0){
                binding.stepViewUiFour.max1.text = arrTripsDoneReferralStatus[0].numberOfTrips.toString()
                binding.stepViewUiFour.max2.text = arrTripsDoneReferralStatus[1].numberOfTrips.toString()
                binding.stepViewUiFour.max3.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                binding.stepViewUiFour.value1.text = arrTripsDoneReferralStatus[0].bonusEarned.toString()
                binding.stepViewUiFour.value2.text = arrTripsDoneReferralStatus[1].bonusEarned.toString()
                binding.stepViewUiFour.value3.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
            }else{
                binding.stepViewUiFour.max1.text = arrTripsDoneReferralHome[0].numberOfTrips.toString()
                binding.stepViewUiFour.max2.text = arrTripsDoneReferralHome[1].numberOfTrips.toString()
                binding.stepViewUiFour.max3.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                binding.stepViewUiFour.value1.text = arrTripsDoneReferralHome[0].bonusEarned.toString()
                binding.stepViewUiFour.value2.text = arrTripsDoneReferralHome[1].bonusEarned.toString()
                binding.stepViewUiFour.value3.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
            }

            if (data.totalNumberOfTrips.toInt() <= 10) {
                binding.stepViewUiFour.seekbar55.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.INVISIBLE
                binding.stepViewUiFour.max1.visibility = View.INVISIBLE
                binding.stepViewUiFour.text55.text = data.totalNumberOfTrips
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            if (data.totalNumberOfTrips?.toInt() in 11..30) {
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.max1.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.text93.text = data.totalNumberOfTrips
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 16..19) {
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.max1.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.text71.text = data.totalNumberOfTrips
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() == 20) {
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.GONE
                binding.stepViewUiFour.text85.text = data.totalNumberOfTrips
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 21..25) {
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.VISIBLE
                binding.stepViewUiFour.text92.text = data.totalNumberOfTrips
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.text92.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 26..29) {
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.text93.text = data.totalNumberOfTrips
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.max2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            else if (data.totalNumberOfTrips.toInt() == 30) {
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar100.visibility = View.VISIBLE
                binding.stepViewUiFour.dot100.visibility = View.INVISIBLE
                binding.stepViewUiFour.max3.visibility = View.INVISIBLE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.VISIBLE
                binding.stepViewUiFour.text100.text = data.totalNumberOfTrips
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value3.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

        }

        if (data.totalNumberOfTrips?.toInt()!! in 31..100) {
            binding.stepViewUiFour.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
            binding.stepViewUiFour.txtTitleBottom.setTextColor(ContextCompat.getColor(context, R.color.black))
            if(arrTripsDoneReferralStatus.size>0){
                binding.stepViewUiFour.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                binding.stepViewUiFour.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                binding.stepViewUiFour.max3.text = arrTripsDoneReferralStatus[4].numberOfTrips.toString()

                binding.stepViewUiFour.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                binding.stepViewUiFour.value2.text = arrTripsDoneReferralStatus[3].bonusEarned.toString()
                binding.stepViewUiFour.value3.text = arrTripsDoneReferralStatus[4].bonusEarned.toString()
            }else{
                binding.stepViewUiFour.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                binding.stepViewUiFour.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                binding.stepViewUiFour.max3.text = arrTripsDoneReferralHome[4].numberOfTrips.toString()

                binding.stepViewUiFour.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                binding.stepViewUiFour.value2.text = arrTripsDoneReferralHome[3].bonusEarned.toString()
                binding.stepViewUiFour.value3.text = arrTripsDoneReferralHome[4].bonusEarned.toString()
            }

            if (data.totalNumberOfTrips?.toInt()!! in 31..40) {
                binding.stepViewUiFour.seekbar70.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar55.visibility = View.GONE

                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.max1.visibility = View.VISIBLE

                if(arrTripsDoneReferralStatus.size >0){
                    binding.stepViewUiFour.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                    binding.stepViewUiFour.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                }else{
                    binding.stepViewUiFour.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                    binding.stepViewUiFour.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                }
                binding.stepViewUiFour.text70.text = data.totalNumberOfTrips
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 41..49) {
                binding.stepViewUiFour.seekbar71.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.text71.text = data.totalNumberOfTrips
                if(arrTripsDoneReferralStatus.size>0){
                    binding.stepViewUiFour.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                }else{
                    binding.stepViewUiFour.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                }
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() == 50) {
                binding.stepViewUiFour.seekbar80.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.GONE

                binding.stepViewUiFour.text85.text = data.totalNumberOfTrips
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                if(arrTripsDoneReferralStatus.size>0){
                    binding.stepViewUiFour.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                    binding.stepViewUiFour.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                }else{
                    binding.stepViewUiFour.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                    binding.stepViewUiFour.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                }
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))

            }

            else if (data.totalNumberOfTrips.toInt() in 51..74) {
                binding.stepViewUiFour.seekbar92.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar100.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.text92.text = data.totalNumberOfTrips
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.max2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }



            else if (data.totalNumberOfTrips.toInt() >= 75) {
                binding.stepViewUiFour.seekbar100.visibility = View.VISIBLE
                binding.stepViewUiFour.seekbar92.visibility = View.GONE
                binding.stepViewUiFour.seekbar93.visibility = View.GONE
                binding.stepViewUiFour.seekbar80.visibility = View.GONE
                binding.stepViewUiFour.seekbar71.visibility = View.GONE
                binding.stepViewUiFour.seekbar70.visibility = View.GONE
                binding.stepViewUiFour.seekbar55.visibility = View.GONE
                binding.stepViewUiFour.dot100.visibility = View.INVISIBLE
                binding.stepViewUiFour.max3.visibility = View.INVISIBLE
                binding.stepViewUiFour.dot40.visibility = View.VISIBLE
                binding.stepViewUiFour.dot70.visibility = View.VISIBLE
                binding.stepViewUiFour.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFour.text100.text = data.totalNumberOfTrips
                binding.stepViewUiFour.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFour.value3.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

        }
    }

    private fun setDataForSeekBarReferralCompleted() {

        if (data.totalNumberOfTrips?.toInt()!! in 10..30) {
            binding.stepViewUiFour.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
            binding.stepViewUiFour.txtTitleBottom.setTextColor(ContextCompat.getColor(context, R.color.black))
            if(arrTripsDoneReferralStatus.size>0){
                binding.stepViewUiFive.max1.text = arrTripsDoneReferralStatus[0].numberOfTrips.toString()
                binding.stepViewUiFive.max2.text = arrTripsDoneReferralStatus[1].numberOfTrips.toString()
                binding.stepViewUiFive.max3.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                binding.stepViewUiFive.value1.text = arrTripsDoneReferralStatus[0].bonusEarned.toString()
                binding.stepViewUiFive.value2.text = arrTripsDoneReferralStatus[1].bonusEarned.toString()
                binding.stepViewUiFive.value3.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
            }else{
                binding.stepViewUiFive.max1.text = arrTripsDoneReferralHome[0].numberOfTrips.toString()
                binding.stepViewUiFive.max2.text = arrTripsDoneReferralHome[1].numberOfTrips.toString()
                binding.stepViewUiFive.max3.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                binding.stepViewUiFive.value1.text = arrTripsDoneReferralHome[0].bonusEarned.toString()
                binding.stepViewUiFive.value2.text = arrTripsDoneReferralHome[1].bonusEarned.toString()
                binding.stepViewUiFive.value3.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
            }

            if (data.totalNumberOfTrips.toInt() <= 10) {
                binding.stepViewUiFive.seekbar55.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.INVISIBLE
                binding.stepViewUiFive.max1.visibility = View.INVISIBLE
                binding.stepViewUiFive.text55.text = data.totalNumberOfTrips
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            if (data.totalNumberOfTrips?.toInt() in 11..30) {
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.max1.visibility = View.VISIBLE
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.text93.text = data.totalNumberOfTrips
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 16..19) {
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.max1.visibility = View.VISIBLE
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.text71.text = data.totalNumberOfTrips
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() == 20) {
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.GONE
                binding.stepViewUiFive.text85.text = data.totalNumberOfTrips
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 21..25) {
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.VISIBLE
                binding.stepViewUiFive.text92.text = data.totalNumberOfTrips
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.text92.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 26..29) {
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.VISIBLE
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.text93.text = data.totalNumberOfTrips
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.max2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            else if (data.totalNumberOfTrips.toInt() == 30) {
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar100.visibility = View.VISIBLE
                binding.stepViewUiFive.dot100.visibility = View.INVISIBLE
                binding.stepViewUiFive.max3.visibility = View.INVISIBLE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.VISIBLE
                binding.stepViewUiFive.text100.text = data.totalNumberOfTrips
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value3.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

        }





        if (data.totalNumberOfTrips?.toInt()!! > 31) {

            binding.stepViewUiFive.txtTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )
            binding.stepViewUiFive.txtTitleBottom.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )


            if(arrTripsDoneReferralStatus.size>0){
                binding.stepViewUiFive.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                binding.stepViewUiFive.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                binding.stepViewUiFive.max3.text = arrTripsDoneReferralStatus[4].numberOfTrips.toString()

                binding.stepViewUiFive.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                binding.stepViewUiFive.value2.text = arrTripsDoneReferralStatus[3].bonusEarned.toString()
                binding.stepViewUiFive.value3.text = arrTripsDoneReferralStatus[4].bonusEarned.toString()
            }else{
                binding.stepViewUiFive.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                binding.stepViewUiFive.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                binding.stepViewUiFive.max3.text = arrTripsDoneReferralHome[4].numberOfTrips.toString()

                binding.stepViewUiFive.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                binding.stepViewUiFive.value2.text = arrTripsDoneReferralHome[3].bonusEarned.toString()
                binding.stepViewUiFive.value3.text = arrTripsDoneReferralHome[4].bonusEarned.toString()
            }


            if (data.totalNumberOfTrips?.toInt()!! in 31..40) {
                binding.stepViewUiFive.seekbar70.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar55.visibility = View.GONE

                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.max1.visibility = View.VISIBLE

                if(arrTripsDoneReferralStatus.size >0){
                    binding.stepViewUiFive.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                    binding.stepViewUiFive.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                }else{
                    binding.stepViewUiFive.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                    binding.stepViewUiFive.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                }
                binding.stepViewUiFive.text70.text = data.totalNumberOfTrips
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() in 41..49) {
                binding.stepViewUiFive.seekbar71.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.text71.text = data.totalNumberOfTrips
                if(arrTripsDoneReferralStatus.size>0){
                    binding.stepViewUiFive.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                }else{
                    binding.stepViewUiFive.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                }
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() == 50) {
                binding.stepViewUiFive.seekbar80.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.GONE

                binding.stepViewUiFive.text85.text = data.totalNumberOfTrips
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                if(arrTripsDoneReferralStatus.size>0){
                    binding.stepViewUiFive.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                    binding.stepViewUiFive.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                }else{
                    binding.stepViewUiFive.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                    binding.stepViewUiFive.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                }
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))

            }

            else if (data.totalNumberOfTrips.toInt() in 51..74) {
                binding.stepViewUiFive.seekbar92.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar100.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.VISIBLE
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.text92.text = data.totalNumberOfTrips
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.max2.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            else if (data.totalNumberOfTrips.toInt() >= 75) {
                binding.stepViewUiFive.seekbar100.visibility = View.VISIBLE
                binding.stepViewUiFive.seekbar92.visibility = View.GONE
                binding.stepViewUiFive.seekbar93.visibility = View.GONE
                binding.stepViewUiFive.seekbar80.visibility = View.GONE
                binding.stepViewUiFive.seekbar71.visibility = View.GONE
                binding.stepViewUiFive.seekbar70.visibility = View.GONE
                binding.stepViewUiFive.seekbar55.visibility = View.GONE
                binding.stepViewUiFive.dot100.visibility = View.INVISIBLE
                binding.stepViewUiFive.max3.visibility = View.INVISIBLE
                binding.stepViewUiFive.dot40.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.visibility = View.VISIBLE
                binding.stepViewUiFive.dot70.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.dot40.setImageResource(R.drawable.ic_ellipse_green)
                binding.stepViewUiFive.text100.text = data.totalNumberOfTrips
                binding.stepViewUiFive.max1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value1.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value2.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.stepViewUiFive.value3.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
            }

        }
    }

    private fun showAndHideBasedOnStage(visibilityStageOne: Int, visibilityStageTwo: Int, visibilityStageThree: Int,
        visibilityStageFour: Int, visibilityStageFive: Int, visibilityStageSix: Int) {
        binding.referralStgOne.visibility = visibilityStageOne
        binding.referralStgTwo.visibility = visibilityStageTwo
        binding.referralStgThree.visibility = visibilityStageThree
        binding.referralStgFour.visibility = visibilityStageFour
        binding.referralStgFive.visibility = visibilityStageFive
    }

}