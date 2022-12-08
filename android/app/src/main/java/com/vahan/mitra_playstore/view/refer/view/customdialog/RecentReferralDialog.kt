package com.vahan.mitra_playstore.view.refer.view.customdialog


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeNewRespModel
import com.vahan.mitra_playstore.view.refer.models.ReferralMilestoneModel
import com.vahan.mitra_playstore.view.refer.models.ReferralStatusNewModel


class RecentReferralDialog(
    val name:String,
    context: Context,
    val data: ReferralMilestoneModel,
    val arrTripsDoneReferralStatus: ArrayList<ReferralStatusNewModel.BonusAmountObject>,
    val arrTripsDoneReferralHome: ArrayList<ReferralHomeNewRespModel.BonusAmountObject>

): AlertDialog(context, com.vahan.mitra_playstore.R.style.DialogTheme) {

    private lateinit var binding: ShowRecentReferralBinding
    lateinit var textView:TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShowRecentReferralBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        initViews()

    }

    private fun initViews() {
        clickListener()
        setData()
    }

    private fun clickListener() {
        binding.ivCross.setOnClickListener {
           // dialog.dismiss()
            dismiss()
        }
    }


    private fun setData() {

        binding.tvReferralCompleted1.visibility = View.VISIBLE

        binding.amtEarned.visibility = View.GONE
        binding.ivGift.visibility = View.GONE
        binding.tvTotalTrips.visibility = View.GONE
        binding.ivScooter.visibility = View.GONE

        binding.tvDate4.visibility = View.GONE
        binding.tvNumber.text = name
        val referralStatusSize = (data.referralStages?.size)
        binding.tvReferralStatus.text = referralStatusSize?.let {
            data.referralStages?.get(it-1)?.label }
        binding.tvDate.text = referralStatusSize?.let {
            data.referralStages?.get(0)?.stageCompletedAt }
        binding.tvInvitationSent.text = referralStatusSize?.let {
            data.referralStages?.get(0)?.info }
        binding.tvReferralStarted.text = referralStatusSize?.let {
            data.referralStages?.get(0)?.label }
        binding.tvReferralStarted2.text = referralStatusSize?.let {
            data.referralStages?.get(0)?.label }

        binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))

        binding.llFinal.visibility = View.GONE

        val stage = referralStatusSize?.let {
            data.referralStages?.get(it-1)?.stage }

        val label = referralStatusSize?.let {
            data.referralStages?.get(it-1)?.label }

        binding.stepViewUi.value1.visibility = View.INVISIBLE
        binding.stepViewUi.value2.visibility = View.INVISIBLE
        binding.stepViewUi.value3.visibility = View.INVISIBLE

        if(stage == "2"){ // 2
            binding.stepViewUi.value1.visibility = View.INVISIBLE
            binding.stepViewUi.value2.visibility = View.INVISIBLE
            binding.stepViewUi.value3.visibility = View.INVISIBLE

            binding.amtEarned.visibility = View.GONE
            binding.ivGift.visibility = View.GONE
            binding.tvTotalTrips.visibility = View.GONE
            binding.ivScooter.visibility = View.GONE

            binding.tvReferralStatus.text = referralStatusSize.let {
                data.referralStages?.get(it-1)?.label }
            binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.referral_stage_two)
            binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context, R.color.joined_mitra_color))

            binding.ivMilestoneStage1.visibility = View.GONE
            binding.ivMilestoneStage2.visibility = View.VISIBLE
            binding.rl1.visibility = View.VISIBLE
            binding.tvReferralCompleted1.visibility = View.GONE
            binding.tvReferralCompleted2.visibility = View.VISIBLE
            binding.tvReferralStarted.visibility = View.GONE
            binding.tvReferralStarted2.visibility = View.VISIBLE
            binding.tvJoinedMitra1.visibility=View.GONE
            binding.tvJoinedMitra2.visibility=View.VISIBLE
            binding.tvJoinedMitra2.text = referralStatusSize?.let {
                data.referralStages?.get(1)?.label }

            binding.llFinal.visibility = View.GONE
            binding.tvDate.visibility = View.VISIBLE
            binding.tvDate.text = referralStatusSize.let {
                data.referralStages?.get(0)?.stageCompletedAt }

            binding.tvWhatsappButton.text = referralStatusSize.let {
                data.referralStages?.get(1)?.info }
            binding.rl1.setOnClickListener {
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

            binding.tvDate2.visibility = View.VISIBLE
            binding.tvDate2.text = referralStatusSize.let {
                data.referralStages?.get(1)?.stageCompletedAt }


        }
        else if(stage== "3")  {   // 3

            binding.stepViewUi.value1.visibility = View.INVISIBLE
            binding.stepViewUi.value2.visibility = View.INVISIBLE
            binding.stepViewUi.value3.visibility = View.INVISIBLE

            binding.tvJoinedMitra2.text = referralStatusSize?.let {
                data.referralStages?.get(1)?.label }

            val str = referralStatusSize?.let {
                data.referralStages?.get(1)?.label }

            Log.d("qqq", "setData: "+referralStatusSize?.let {
                data.referralStages?.get(1)?.label })

            binding.amtEarned.visibility = View.GONE
            binding.ivGift.visibility = View.GONE
            binding.tvTotalTrips.visibility = View.GONE
            binding.ivScooter.visibility = View.GONE

            binding.tvReferralStatus.text = referralStatusSize.let {
                data.referralStages?.get(it-1)?.label }

            binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.referral_stage_three)
            binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context, R.color.first_trip_color))

            binding.tvDaysLeft.text = data.numberOfDaysUntilExpiry

            binding.tvReferralStarted.visibility=View.GONE
            binding.tvReferralStarted2.visibility=View.VISIBLE
            binding.llFinal.visibility = View.GONE
            binding.ivMilestoneStage1.visibility = View.GONE
            binding.ivMilestoneStage2.visibility = View.GONE
            binding.ivMilestoneStage3.visibility = View.VISIBLE

            binding.tvReferralCompleted1.visibility = View.GONE
            binding.tvReferralCompleted2.visibility = View.VISIBLE

            binding.tvFirstTripDone3.setTextColor(ContextCompat.getColor(context, R.color.default_200))

            binding.tvDate.visibility = View.VISIBLE
            binding.tvDate.text = referralStatusSize.let {
                data.referralStages?.get(0)?.stageCompletedAt }

            binding.tvDate2.visibility = View.VISIBLE
            binding.tvDate2.text = referralStatusSize.let {
                data.referralStages?.get(1)?.stageCompletedAt }

            binding.tvDate3.visibility = View.VISIBLE
            binding.tvDate3.text = referralStatusSize.let {
                data.referralStages?.get(2)?.stageCompletedAt }

            binding.rl1.visibility = View.GONE
            binding.rl2.visibility = View.VISIBLE

            binding.rl2.setOnClickListener {
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

            binding.rl21.visibility = View.VISIBLE

            binding.tvFirstTripDone12.visibility=View.GONE
            binding.tvFirstTripDone3.visibility=View.VISIBLE

            binding.tvFirstTripDone3.text = referralStatusSize?.let {
                data.referralStages?.get(2)?.label }

            binding.tvJoinedMitra2.visibility = View.VISIBLE
            binding.tvJoinedMitra1.visibility = View.GONE
            binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))


        }

        else if(data.totalReferralStages=="9"){
            if(stage== "4" || stage== "5" || stage== "6" || stage== "7" || stage== "8"  ) {  //4

                val str = referralStatusSize?.let {
                    data.referralStages?.get(1)?.label }

                binding.tvJoinedMitra2.text = referralStatusSize?.let {
                    data.referralStages?.get(1)?.label }
                binding.tvReferralStatus.text = referralStatusSize.let {
                    data.referralStages?.get(it-1)?.label }

                binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.referral_stage_four)
                binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context, R.color.green_tv_referral))

                binding.tvDate4.visibility = View.VISIBLE
                binding.tvDate4.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.stageCompletedAt }
                binding.rl5.visibility = View.VISIBLE
                binding.rl5.setOnClickListener {
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
                binding.tvWhatsappButton3.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.info }
                binding.tvDaysLeft2.text = data.numberOfDaysUntilExpiry
//                binding.tvReferralCompleted.visibility = View.GONE
//                binding.tvReferralCompletedContainer4.visibility = View.VISIBLE


                binding.tvReferralCompleted1.visibility = View.GONE
                binding.tvReferralCompleted2.visibility = View.GONE
                binding.tvReferralCompleted3.visibility = View.GONE
                binding.tvReferralCompleted5.visibility = View.GONE
                binding.tvReferralCompleted4.visibility = View.VISIBLE
                binding.tvReferralCompletedContainer5.visibility = View.GONE



                binding.tvDate.visibility = View.VISIBLE
                binding.tvDate.text = referralStatusSize.let {
                    data.referralStages?.get(0)?.stageCompletedAt
                }

                binding.tvDate2.visibility = View.VISIBLE
                binding.tvDate2.text = referralStatusSize.let {
                    data.referralStages?.get(1)?.stageCompletedAt
                }

                binding.tvDate3.visibility = View.VISIBLE
                binding.tvDate3.text = referralStatusSize.let {
                    data.referralStages?.get(2)?.stageCompletedAt
                }

                binding.ivMilestoneStage1.visibility = View.GONE
                binding.ivMilestoneStage2.visibility = View.GONE
                binding.ivMilestoneStage3.visibility = View.GONE
                binding.ivMilestoneStage4.visibility = View.VISIBLE

                binding.tvReferralStarted.visibility = View.GONE
                binding.tvReferralStarted2.visibility = View.VISIBLE

                binding.tvFirstTripDone12.visibility = View.GONE
                binding.tvFirstTripDone3.visibility = View.VISIBLE

                binding.tvFirstTripDone3.text = referralStatusSize.let {
                    data.referralStages?.get(2)?.label }

                binding.tvBonusEarned2.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.label }

                binding.tvBonusEarned.visibility = View.GONE
                binding.tvBonusEarned2.visibility = View.VISIBLE

                binding.tvBonusEarned.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvBonusEarned2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvFirstTripDone3.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.default_200
                    )
                )
                binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra1.setTextColor(ContextCompat.getColor(context, R.color.default_200))

                binding.llFinal.visibility = View.VISIBLE

                binding.amtEarned.visibility = View.VISIBLE
                binding.ivGift.visibility = View.VISIBLE
                binding.tvTotalTrips.visibility = View.VISIBLE
                binding.ivScooter.visibility = View.VISIBLE

                binding.tvTotalTrips.text = data.totalNumberOfTrips + context.getString(R.string._trips)
                binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)


                //for 4th stage
                if (data.totalNumberOfTrips?.toInt()!! in 10..30) {
                    binding.stepViewUi.txtTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.stepViewUi.txtTitleBottom.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )




                    if(arrTripsDoneReferralStatus!=null){
                        binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[0].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[1].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[0].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralStatus[1].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                    }else{
                        binding.stepViewUi.max1.text = arrTripsDoneReferralHome[0].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralHome[1].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralHome[0].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralHome[1].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                    }

                    if (data.totalNumberOfTrips.toInt()!! == 10) {
                        binding.stepViewUi.seekbar55.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.INVISIBLE
                        binding.stepViewUi.max1.visibility = View.INVISIBLE
                        binding.stepViewUi.text55.text = data.totalNumberOfTrips
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                    if (data.totalNumberOfTrips?.toInt() in 11..15) {

                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.max1.visibility = View.VISIBLE
                        binding.stepViewUi.text70.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 16..19) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text71.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() == 20) {
                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.GONE
                        binding.stepViewUi.text85.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 21..25) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text92.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.text92.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 26..29) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text93.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    else if (data.totalNumberOfTrips.toInt() == 30) {
                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.VISIBLE
                        binding.stepViewUi.dot100.visibility = View.GONE
                        binding.stepViewUi.max3.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text100.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value3.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                }

                if (data.totalNumberOfTrips?.toInt()!! in 31..100) {

                    binding.stepViewUi.txtTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.stepViewUi.txtTitleBottom.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )


                    if(arrTripsDoneReferralStatus!=null){
                        binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralStatus[4].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralStatus[3].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralStatus[4].bonusEarned.toString()
                    }else{
                        binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralHome[4].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralHome[3].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralHome[4].bonusEarned.toString()
                    }

                    binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)



                    if (data.totalNumberOfTrips?.toInt()!! in 31..40) {
                        binding.stepViewUi.seekbar70.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.max1.visibility = View.VISIBLE

                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                            binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                            binding.stepViewUi.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                        }

                        binding.stepViewUi.text70.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 41..49) {
                        binding.stepViewUi.seekbar71.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE



                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text71.text = data.totalNumberOfTrips
                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                        }
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() == 50) {
                        binding.stepViewUi.seekbar80.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.GONE
                        binding.stepViewUi.text85.text = data.totalNumberOfTrips

                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                            binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                            binding.stepViewUi.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                        }
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 51..75) {
                        binding.stepViewUi.seekbar92.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text92.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 76..99) {
                        binding.stepViewUi.seekbar93.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text93.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                    else if (data.totalNumberOfTrips.toInt() == 100) {

                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)

                        binding.stepViewUi.seekbar100.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.dot100.visibility = View.GONE
                        binding.stepViewUi.max3.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text100.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value3.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                }



            }

            else if(stage== "9" && data.totalReferralStages=="9"){  //9

                if(data.referralStages?.get(3)?.label != "Bonus Earned"){
                    binding.tvBonusEarned3.text = context.getString(R.string.bonus_earned)

                    binding.tvJoinedMitra2.text = referralStatusSize.let {
                        data.referralStages?.get(1)?.label }

                    binding.amtEarned.visibility = View.VISIBLE
                    binding.amtEarned.text = context.getString(R.string.rupee)+context.getString(R.string._0)
                    binding.ivGift.visibility = View.VISIBLE
                    binding.ivGift.setImageResource(R.drawable.ic_gift_faded);
                    binding.tvTotalTrips.visibility = View.VISIBLE
                    binding.ivScooter.visibility = View.VISIBLE
                    binding.ivScooter.setImageResource(R.drawable.ic_scooter_faded)
                    binding.llFinal.visibility = View.VISIBLE
                    binding.rl5.visibility = View.GONE
                    binding.rl2.visibility = View.GONE

                    binding.tvTotalTrips.text = data.totalNumberOfTrips + context.getString(R.string._trips)

                    binding.tvReferralStatus.text = referralStatusSize.let {
                        data.referralStages?.get(it-1)?.label }

                    binding.tvReferralStatus.background = ContextCompat.getDrawable(context, R.drawable.referral_last_stage)
                    binding.tvReferralStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_grey))

                    binding.rl4.visibility = View.VISIBLE

                    binding.tvDate4.visibility = View.VISIBLE
                    binding.tvDate4.text = referralStatusSize.let {
                        data.referralStages?.get(3)?.stageCompletedAt }

                    binding.tvDate.visibility = View.VISIBLE
                    binding.tvDate.text = referralStatusSize.let {
                        data.referralStages?.get(0)?.stageCompletedAt }

                    binding.tvDate2.visibility = View.VISIBLE
                    binding.tvDate2.text = referralStatusSize.let {
                        data.referralStages?.get(1)?.stageCompletedAt }

                    binding.tvDate3.visibility = View.VISIBLE
                    binding.tvDate3.text = referralStatusSize.let {
                        data.referralStages?.get(2)?.stageCompletedAt }

                    binding.tvDate4.visibility = View.INVISIBLE

                    binding.tvDate5.visibility = View.VISIBLE
                    binding.tvDate5.text = referralStatusSize.let {
                        data.referralStages?.get(3)?.stageCompletedAt }

                    binding.tvDaysLeft2.visibility = View.GONE
                    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                        binding.tv2.text = "Congrats! You have earned ₹" + data.totalAmountEarned
                    } else {
                        binding.tv2.text = "बधाई! आपने ₹" + data.totalAmountEarned + " कमाए हैं"
                    }

                    binding.ivMilestoneStage1.visibility = View.GONE
                    binding.ivMilestoneStage2.visibility = View.GONE
                    binding.ivMilestoneStage3.visibility = View.GONE
                    binding.ivMilestoneStage4.visibility = View.GONE
                    binding.ivMilestoneStage5.visibility = View.GONE
                    binding.ivMilestoneStage6.visibility = View.VISIBLE

                    binding.tvReferralStarted3.visibility = View.VISIBLE
                    binding.tvReferralStarted2.visibility = View.GONE
                    binding.tvReferralStarted.visibility = View.GONE

                    binding.tvFirstTripDone4.visibility = View.VISIBLE
                    binding.tvFirstTripDone3.visibility = View.GONE
                    binding.tvFirstTripDone12.visibility = View.GONE

                    binding.tvFirstTripDone4.text = referralStatusSize?.let {
                        data.referralStages?.get(2)?.label }

                    binding.tvBonusEarned3.visibility = View.VISIBLE
                    binding.tvBonusEarned2.visibility = View.INVISIBLE
                    binding.tvBonusEarned.visibility = View.GONE

                    binding.rl4.visibility = View.VISIBLE

                    binding.tvReferralCompletedContainer4.visibility = View.GONE
                    binding.tvReferralCompleted.visibility = View.GONE
                    binding.tvReferralCompleted5.visibility = View.VISIBLE
                    binding.tvReferralCompleted3.visibility = View.GONE

                    binding.tvReferralCompleted5.text = referralStatusSize?.let {
                        data.referralStages?.get(it-1)?.label }

                    binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                    binding.tvFirstTripDone3.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                    binding.tvFirstTripDone4.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                    binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                    binding.tvJoinedMitra1.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                    binding.tvReferralCompleted5.setTextColor(ContextCompat.getColor(context, R.color.default_200))

                    binding.tvTotalTrips.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.amtEarned.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    // binding.tvReferralCompleted3.setTextColor(ContextCompat.getColor(context, R.color.purple_200))


                }

                else{
                binding.tvBonusEarned3.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.label }

                binding.tvJoinedMitra2.text = referralStatusSize.let {
                    data.referralStages?.get(1)?.label }

                binding.amtEarned.visibility = View.VISIBLE
                binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                binding.ivGift.visibility = View.VISIBLE
                binding.tvTotalTrips.visibility = View.VISIBLE
                binding.ivScooter.visibility = View.VISIBLE
                binding.llFinal.visibility = View.VISIBLE
                binding.rl5.visibility = View.GONE
                binding.rl2.visibility = View.GONE

                binding.tvTotalTrips.text = data.totalNumberOfTrips + context.getString(R.string._trips)

                binding.tvReferralStatus.text = referralStatusSize.let {
                    data.referralStages?.get(it-1)?.label }
                binding.rl4.visibility = View.VISIBLE

                binding.tvDate4.visibility = View.VISIBLE
                binding.tvDate4.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.stageCompletedAt }

                binding.tvDate.visibility = View.VISIBLE
                binding.tvDate.text = referralStatusSize.let {
                    data.referralStages?.get(0)?.stageCompletedAt }

                binding.tvDate2.visibility = View.VISIBLE
                binding.tvDate2.text = referralStatusSize.let {
                    data.referralStages?.get(1)?.stageCompletedAt }

                binding.tvDate3.visibility = View.VISIBLE
                binding.tvDate3.text = referralStatusSize.let {
                    data.referralStages?.get(2)?.stageCompletedAt }

                binding.tvDate4.visibility = View.VISIBLE
                binding.tvDate4.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.stageCompletedAt }

                binding.tvDate5.visibility = View.VISIBLE
                binding.tvDate5.text = referralStatusSize.let {
                    data.referralStages?.get(4)?.stageCompletedAt }

                binding.tvDaysLeft2.visibility = View.GONE
                if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                    binding.tv2.text = "Congrats! you have earned ₹"+data.totalAmountEarned
                } else {
                    binding.tv2.text = "बधाई! आपने ₹"+data.totalAmountEarned+" कमाए हैं"
                }

                binding.ivMilestoneStage1.visibility = View.GONE
                binding.ivMilestoneStage2.visibility = View.GONE
                binding.ivMilestoneStage3.visibility = View.GONE
                binding.ivMilestoneStage4.visibility = View.GONE
                binding.ivMilestoneStage5.visibility = View.VISIBLE

                binding.tvReferralStarted3.visibility = View.VISIBLE
                binding.tvReferralStarted2.visibility = View.GONE
                binding.tvReferralStarted.visibility = View.GONE

                binding.tvFirstTripDone4.visibility = View.VISIBLE
                binding.tvFirstTripDone3.visibility = View.GONE
                binding.tvFirstTripDone12.visibility = View.GONE

                binding.tvFirstTripDone4.text = referralStatusSize?.let {
                    data.referralStages?.get(it-1)?.label }

                binding.tvBonusEarned3.visibility = View.VISIBLE
                binding.tvBonusEarned2.visibility = View.INVISIBLE
                binding.tvBonusEarned.visibility = View.GONE

                binding.rl4.visibility = View.VISIBLE

                binding.tvReferralCompletedContainer4.visibility = View.GONE
                binding.tvReferralCompleted.visibility = View.GONE
                binding.tvReferralCompleted5.visibility = View.VISIBLE
                binding.tvReferralCompleted3.visibility = View.GONE

                binding.tvReferralCompleted5.text = referralStatusSize?.let {
                    data.referralStages?.get(it-1)?.label }

                binding.tvBonusEarned.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvBonusEarned2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvBonusEarned3.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvFirstTripDone3.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvFirstTripDone4.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra1.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvReferralCompleted5.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                // binding.tvReferralCompleted3.setTextColor(ContextCompat.getColor(context, R.color.purple_200))


                if (data.totalNumberOfTrips?.toInt()!! in 10..30) {
                    binding.stepViewUi.txtTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.stepViewUi.txtTitleBottom.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )




                    if(arrTripsDoneReferralStatus!=null){
                        binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[0].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[1].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[0].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralStatus[1].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                    }else{
                        binding.stepViewUi.max1.text = arrTripsDoneReferralHome[0].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralHome[1].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralHome[0].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralHome[1].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                    }
                    
                    if (data.totalNumberOfTrips.toInt()!! == 10) {
                        binding.stepViewUi.seekbar55.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.INVISIBLE
                        binding.stepViewUi.max1.visibility = View.INVISIBLE
                        binding.stepViewUi.text55.text = data.totalNumberOfTrips
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                    if (data.totalNumberOfTrips?.toInt() in 11..15) {

                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.max1.visibility = View.VISIBLE
                        binding.stepViewUi.text70.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 16..19) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text71.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() == 20) {
                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.GONE
                        binding.stepViewUi.text85.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 21..25) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text92.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.text92.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 26..29) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text93.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    else if (data.totalNumberOfTrips.toInt() == 30) {
                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.VISIBLE
                        binding.stepViewUi.dot100.visibility = View.GONE
                        binding.stepViewUi.max3.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text100.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value3.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                }

                if (data.totalNumberOfTrips?.toInt()!! >30) {

                    binding.stepViewUi.txtTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.stepViewUi.txtTitleBottom.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )


                    if(arrTripsDoneReferralStatus!=null){
                        binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralStatus[4].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralStatus[3].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralStatus[4].bonusEarned.toString()
                    }else{
                        binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralHome[4].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralHome[3].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralHome[4].bonusEarned.toString()
                    }

                    binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)



                    if (data.totalNumberOfTrips?.toInt()!! in 31..40) {
                        binding.stepViewUi.seekbar70.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.max1.visibility = View.VISIBLE

                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                            binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                            binding.stepViewUi.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                        }
                        

                        binding.stepViewUi.text70.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 41..49) {
                        binding.stepViewUi.seekbar71.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE



                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text71.text = data.totalNumberOfTrips
                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                        }
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() == 50) {
                        binding.stepViewUi.seekbar80.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.GONE
                        binding.stepViewUi.text85.text = data.totalNumberOfTrips

                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                            binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                            binding.stepViewUi.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                        }
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 51..75) {
                        binding.stepViewUi.seekbar92.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text92.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 76..99) {
                        binding.stepViewUi.seekbar93.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text93.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                    else if (data.totalNumberOfTrips.toInt() >= 100) {

                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)

                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                        binding.stepViewUi.seekbar100.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.dot100.visibility = View.INVISIBLE
                        binding.stepViewUi.max3.visibility = View.INVISIBLE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text100.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value3.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                }
                }
            }
        }

        else if(data.totalReferralStages=="4"){
            if(stage== "4") {  //4

                binding.stepViewUi.txtTitle.text = context.getString(R.string.trips_taken)
                binding.stepViewUi.txtTitleBottom.visibility = View.INVISIBLE
                binding.stepViewUi.value1.visibility = View.INVISIBLE
                binding.stepViewUi.value2.visibility = View.INVISIBLE
                binding.stepViewUi.value3.visibility = View.INVISIBLE
                binding.rl4.visibility = View.INVISIBLE
                binding.rl5.visibility = View.GONE
                binding.rlFinal.visibility = View.VISIBLE

                val str = referralStatusSize?.let {
                    data.referralStages?.get(1)?.label }


                binding.tvJoinedMitra2.text = referralStatusSize?.let {
                    data.referralStages?.get(1)?.label }
                binding.tvReferralStatus.text = referralStatusSize.let {
                    data.referralStages?.get(it-1)?.label }
                binding.tvDate4.visibility = View.VISIBLE
                binding.tvDate4.text = referralStatusSize.let {
                    data.referralStages?.get(3)?.stageCompletedAt }
               // binding.rl5.visibility = View.VISIBLE
//                binding.rl5.setOnClickListener {
//                    val pm: PackageManager = context.packageManager
//                    try {
//                        val waIntent = Intent(Intent.ACTION_SEND)
//                        waIntent.type = "text/plain"
//                        var text = "Hello! Take more orders to earn more. Click https://mitraapp.page.link/domoretrips to open the Mitra App Now"
//                        waIntent.setPackage("com.whatsapp")
//                        waIntent.putExtra(Intent.EXTRA_TEXT, text)
//                        context.startActivity(Intent.createChooser(waIntent, "Share with"))
//                    } catch (e: PackageManager.NameNotFoundException) {
//                        Toast.makeText(
//                            context,
//                            context.getString(R.string.whatsapp_not_installed),
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                    }
//                }
//                binding.tvWhatsappButton3.text = referralStatusSize.let {
//                    data.referralStages?.get(3)?.info }
                binding.tvDaysLeft2.text = data.numberOfDaysUntilExpiry
//                binding.tvReferralCompleted.visibility = View.GONE
//                binding.tvReferralCompletedContainer4.visibility = View.VISIBLE


                binding.tvReferralCompleted1.visibility = View.GONE
                binding.tvReferralCompleted2.visibility = View.GONE
                binding.tvReferralCompleted3.visibility = View.GONE
                binding.tvReferralCompleted5.visibility = View.VISIBLE
                binding.tvReferralCompleted4.visibility = View.GONE



                binding.tvDate.visibility = View.VISIBLE
                binding.tvDate.text = referralStatusSize.let {
                    data.referralStages?.get(0)?.stageCompletedAt
                }

                binding.tvDate2.visibility = View.VISIBLE
                binding.tvDate2.text = referralStatusSize.let {
                    data.referralStages?.get(1)?.stageCompletedAt
                }

                binding.tvDate3.visibility = View.VISIBLE
                binding.tvDate3.text = referralStatusSize.let {
                    data.referralStages?.get(2)?.stageCompletedAt
                }

                binding.ivMilestoneStage1.visibility = View.GONE
                binding.ivMilestoneStage2.visibility = View.GONE
                binding.ivMilestoneStage3.visibility = View.GONE
                binding.ivMilestoneStage4.visibility = View.GONE
                binding.ivMilestoneStage5.visibility = View.VISIBLE

                binding.tvReferralStarted.visibility = View.GONE
                binding.tvReferralStarted2.visibility = View.VISIBLE

                binding.tvFirstTripDone12.visibility = View.GONE
                binding.tvFirstTripDone3.visibility = View.VISIBLE

                binding.tvFirstTripDone3.text = referralStatusSize?.let {
                    data.referralStages?.get(2)?.label }

//                binding.tvBonusEarned2.text = referralStatusSize?.let {
//                    data.referralStages?.get(3)?.label }

                binding.tvBonusEarned.visibility = View.GONE
                binding.tvBonusEarned2.visibility = View.VISIBLE
                binding.tvBonusEarned2.text = context.getString(R.string.bonus_earned)

                binding.tvBonusEarned.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvReferralCompleted5.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvBonusEarned2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvFirstTripDone3.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.default_200
                    )
                )
                binding.tvJoinedMitra2.setTextColor(ContextCompat.getColor(context, R.color.default_200))
                binding.tvJoinedMitra1.setTextColor(ContextCompat.getColor(context, R.color.default_200))

                binding.llFinal.visibility = View.VISIBLE

                binding.amtEarned.visibility = View.VISIBLE
                binding.ivGift.visibility = View.VISIBLE
                binding.tvTotalTrips.visibility = View.VISIBLE
                binding.ivScooter.visibility = View.VISIBLE

                binding.tvTotalTrips.text = data.totalNumberOfTrips + context.getString(R.string._trips)
                binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)


                //for 4th stage
                if (data.totalNumberOfTrips?.toInt()!! in 10..30) {
                    binding.stepViewUi.txtTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.stepViewUi.txtTitleBottom.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )




                    if(arrTripsDoneReferralStatus!=null){
                        binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[0].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[1].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[0].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralStatus[1].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                    }else{
                        binding.stepViewUi.max1.text = arrTripsDoneReferralHome[0].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralHome[1].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()

                        binding.stepViewUi.value1.text = arrTripsDoneReferralHome[0].bonusEarned.toString()
                        binding.stepViewUi.value2.text = arrTripsDoneReferralHome[1].bonusEarned.toString()
                        binding.stepViewUi.value3.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                    }
                    if (data.totalNumberOfTrips.toInt()!! == 10) {
                        binding.stepViewUi.seekbar55.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.INVISIBLE
                        binding.stepViewUi.max1.visibility = View.INVISIBLE
                        binding.stepViewUi.text55.text = data.totalNumberOfTrips
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                    if (data.totalNumberOfTrips?.toInt() in 11..15) {

                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.max1.visibility = View.VISIBLE
                        binding.stepViewUi.text70.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 16..19) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text71.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() == 20) {
                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.GONE
                        binding.stepViewUi.text85.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 21..25) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text92.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.text92.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 26..29) {
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text93.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    else if (data.totalNumberOfTrips.toInt() == 30) {
                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar100.visibility = View.VISIBLE
                        binding.stepViewUi.dot100.visibility = View.GONE
                        binding.stepViewUi.max3.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text100.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value3.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                }

                if (data.totalNumberOfTrips?.toInt()!! >30 && data.totalNumberOfTrips?.toInt()!! <= 100) {

                    binding.stepViewUi.txtTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                    binding.stepViewUi.txtTitleBottom.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )


                     if(arrTripsDoneReferralStatus!=null){
                        binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralStatus[4].numberOfTrips.toString()

                         binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                         binding.stepViewUi.value2.text = arrTripsDoneReferralStatus[3].bonusEarned.toString()
                         binding.stepViewUi.value3.text = arrTripsDoneReferralStatus[4].bonusEarned.toString()
                    }else{
                        binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                        binding.stepViewUi.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                        binding.stepViewUi.max3.text = arrTripsDoneReferralHome[4].numberOfTrips.toString()

                         binding.stepViewUi.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                         binding.stepViewUi.value2.text = arrTripsDoneReferralHome[3].bonusEarned.toString()
                         binding.stepViewUi.value3.text = arrTripsDoneReferralHome[4].bonusEarned.toString()
                    }
                    binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)



                    if (data.totalNumberOfTrips?.toInt()!! in 31..40) {
                        binding.stepViewUi.seekbar70.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.max1.visibility = View.VISIBLE
                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                            binding.stepViewUi.value1.text = arrTripsDoneReferralStatus[2].bonusEarned.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                            binding.stepViewUi.value1.text = arrTripsDoneReferralHome[2].bonusEarned.toString()
                        }

                        binding.stepViewUi.text70.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 41..49) {
                        binding.stepViewUi.seekbar71.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE



                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text71.text = data.totalNumberOfTrips
                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                        }
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() == 50) {
                        binding.stepViewUi.seekbar80.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.GONE
                        binding.stepViewUi.text85.text = data.totalNumberOfTrips

                        if(arrTripsDoneReferralStatus!=null){
                            binding.stepViewUi.max1.text = arrTripsDoneReferralStatus[2].numberOfTrips.toString()
                            binding.stepViewUi.max2.text = arrTripsDoneReferralStatus[3].numberOfTrips.toString()
                        }else{
                            binding.stepViewUi.max1.text = arrTripsDoneReferralHome[2].numberOfTrips.toString()
                            binding.stepViewUi.max2.text = arrTripsDoneReferralHome[3].numberOfTrips.toString()
                        }
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 51..75) {
                        binding.stepViewUi.seekbar92.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE

                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text92.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                    }

                    else if (data.totalNumberOfTrips.toInt() in 76..99) {
                        binding.stepViewUi.seekbar93.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar100.visibility = View.GONE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text93.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.max2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                    else if (data.totalNumberOfTrips.toInt() == 100) {

                        binding.amtEarned.text = context.getString(R.string.rupee)+data.totalAmountEarned +context.getString(R.string._earned)

                        binding.stepViewUi.seekbar100.visibility = View.VISIBLE
                        binding.stepViewUi.seekbar92.visibility = View.GONE
                        binding.stepViewUi.seekbar93.visibility = View.GONE
                        binding.stepViewUi.seekbar80.visibility = View.GONE
                        binding.stepViewUi.seekbar71.visibility = View.GONE
                        binding.stepViewUi.seekbar70.visibility = View.GONE
                        binding.stepViewUi.seekbar55.visibility = View.GONE
                        binding.stepViewUi.dot100.visibility = View.GONE
                        binding.stepViewUi.max3.visibility = View.GONE
                        binding.stepViewUi.dot40.visibility = View.VISIBLE
                        binding.stepViewUi.dot40.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.dot70.visibility = View.VISIBLE
                        binding.stepViewUi.dot70.setImageResource(R.drawable.ic_ellipse_green)
                        binding.stepViewUi.text100.text = data.totalNumberOfTrips
                        binding.stepViewUi.max1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value1.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value2.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                        binding.stepViewUi.value3.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }

                }



            }
        }

    }

}