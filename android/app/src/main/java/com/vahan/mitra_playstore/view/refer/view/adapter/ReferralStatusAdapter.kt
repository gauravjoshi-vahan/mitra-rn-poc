package com.vahan.mitra_playstore.view.refer.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ContactStatusItemBinding
import com.vahan.mitra_playstore.view.refer.models.ReferralStatusNewModel

class ReferralStatusAdapter (
    private val context: Context,
    // private val list: List<ReferralHomeNewRespModel>?,
    private val referralStatusNewModel: ReferralStatusNewModel,
    private val fa: FirebaseAnalytics,
) : RecyclerView.Adapter<ReferralStatusAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: ContactStatusItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: ContactStatusItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: ContactStatusItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.contact_status_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply {
            tvName.text = referralStatusNewModel.referralStatus?.get(position)?.name ?: ""
            //tvStatus.text = list?.get(position)?.latestReferralStage ?: ""
            // tvStatus.text = list?.get(position)?.label
            rlContainerContact.setOnClickListener {

//                RecentReferralDialog(
//                    context
//                ).show()

//                if (list?.get(position)?.isEnabled == true) {
//                    list[position]?.isEnabled = false
//                    bgContainer.visibility = View.VISIBLE
//                    ivReferral.visibility = View.VISIBLE
//                    btnContainer.visibility = View.VISIBLE
//                    ivForwardArrowIcon.setImageDrawable(context.getDrawable(R.drawable.arrow_up_float))
//                } else {
//                    list?.get(position)?.isEnabled = true
//                    bgContainer.visibility = View.GONE
//                    ivReferral.visibility = View.GONE
//                    btnContainer.visibility = View.GONE
//                    ivForwardArrowIcon.setImageDrawable(context.getDrawable(R.drawable.arrow_down_float))
//                }
            }


//            when {
//                list?.get(position)?.latestReferralStage.equals("1") -> {
//                    inviteNow.visibility = View.VISIBLE
//                    amountEarned.visibility = View.GONE
//                    btInviteNow.visibility = View.GONE
//                    tvStatus.setTextColor(context.getColor(R.color.purple_200))
//                    tvStatus.setBackgroundResource(R.drawable.referral_started)
//                    ivReferral.setImageDrawable(context.getDrawable(R.drawable.ic_group_starting))
//                }
//                list?.get(position)?.latestReferralStage.equals("2") -> {
//                    inviteNow.visibility = View.GONE
//                    amountEarned.visibility = View.GONE
//                    btInviteNow.visibility = View.VISIBLE
//                    tvStatus.setTextColor(context.getColor(R.color.joined_mitra_color))
//                    tvStatus.setBackgroundResource(R.drawable.ic_joined_mitra)
//                    ivReferral.setImageDrawable(context.getDrawable(R.drawable.ic_group_joined))
//                }
//                list?.get(position)?.latestReferralStage.equals("3") -> {
//                    inviteNow.visibility = View.GONE
//                    amountEarned.visibility = View.GONE
//                    btInviteNow.visibility = View.VISIBLE
//                    tvStatus.setTextColor(context.getColor(R.color.first_trip_color))
//                    tvStatus.setBackgroundResource(R.drawable.ic_first_trip)
//                    ivReferral.setImageDrawable(context.getDrawable(R.drawable.ic_group_first))
//                }
//                else -> {
//                    inviteNow.visibility = View.GONE
//                    amountEarned.visibility = View.VISIBLE
//                    btInviteNow.visibility = View.GONE
//                    tvStatus.setTextColor(context.getColor(R.color.referral_bonus_earned_color))
//                    tvStatus.setBackgroundResource(R.drawable.ic_referral_bonus_earned)
//                    ivReferral.setImageDrawable(context.getDrawable(R.drawable.ic_group_referral_bonus))
//                    tvTotalEarn.text = "You have earned ₹${list?.get(position)?.earnedReferralAmount}"
//                }
//            }

//            btInviteNow.setOnClickListener {
//                val pm: PackageManager = context.packageManager
//                try {
//                    val waIntent = Intent(Intent.ACTION_SEND)
//                    waIntent.type = "text/plain"
//                    var text = ""
//                    text = if (list?.get(position)?.latestReferralStage.equals("2")) {
//                        setInstrumentation("Joined Mitra")
//                        "Hi, you have not yet taken your first ride on the Mitra App! Please complete your first ride to start earning! Open the Mitra App Now https://mitraapp.page.link/startyourtrip"
//                    } else {
//                        setInstrumentation("First Trip Done")
//                        "Hello! Take more orders to earn more. Click https://mitraapp.page.link/domoretrips to open the Mitra App Now"
//                    }
//                    waIntent.setPackage("com.whatsapp")
//                    waIntent.putExtra(Intent.EXTRA_TEXT, text)
//                    context.startActivity(Intent.createChooser(waIntent, "Share with"))
//                } catch (e: PackageManager.NameNotFoundException) {
//                    Toast.makeText(
//                        context,
//                        context.getString(R.string.whatsapp_not_installed),
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                }
//            }

        }
    }

    private fun setInstrumentation(type: String) {
        val bundle = Bundle()
        val properties = Properties()
        val data = HashMap<String, String>()
        val attribute = HashMap<String, Any>()
        bundle.putString("type", type)
        properties.addAttribute("type", type)
        data["type"] = type
        attribute["type"] = type
        fa.logEvent("referral_reminder", bundle)
        MoEHelper.getInstance(context).trackEvent("referral_reminder", properties)
        UXCam.logEvent("referral_home_viewed", data)
        Freshchat.trackEvent(context, "referral_reminder", attribute)


    }

    override fun getItemCount(): Int {
        return referralStatusNewModel.referralStatus?.size ?: 0
    }
}