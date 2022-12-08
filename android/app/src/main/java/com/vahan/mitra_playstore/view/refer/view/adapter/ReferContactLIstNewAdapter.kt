package com.vahan.mitra_playstore.view.refer.view.adapter

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ContactStatusItemBinding
import com.vahan.mitra_playstore.interfaces.ReferralMilestoneOnClick
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeNewRespModel


class ReferContactListNewAdapter(
    private val context: Context,
    private val listener: ReferralMilestoneOnClick,
    // private val list: List<ReferralHomeNewRespModel>?,
    private val referralHomeNewRespModel: ReferralHomeNewRespModel,
    private val fa: FirebaseAnalytics,
) : RecyclerView.Adapter<ReferContactListNewAdapter.MyViewHolder>() {

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
            tvName.text = referralHomeNewRespModel.referralStatus?.get(position)?.name ?: ""
            tvStatus.text =
                referralHomeNewRespModel.referralStatus?.get(position)?.latestReferralStageTitle ?: ""

            when (referralHomeNewRespModel.referralStatus?.get(position)?.latestReferralStageTitle) {
                "Referral Started" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.default_200))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.border_filled_outlined)

                }
                "Joined Mitra" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.joined_mitra_color))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_yellow)

                }
                "First Trip Done" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.first_trip_color))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_blue)

                }
                "Bonus Earned" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.referral_bonus_earned_color))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_green)

                }
                "Referral Completed" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.grey3))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_filled_grey)

                }
            }
            if(referralHomeNewRespModel.referralStatus?.get(position)?.message !== ""){
                tvSlot.text = referralHomeNewRespModel.referralStatus?.get(position)?.message
            }
            else{
                tvSlot.visibility = View.GONE
            }

            //tvStatus.text = list?.get(position)?.latestReferralStage ?: ""
            // tvStatus.text = list?.get(position)?.label
            rlContainerContact.setOnClickListener {

              referralHomeNewRespModel.referralStatus?.get(position)?.let { it1 ->
                  it1.referredPhoneNumber?.let { it2 ->
                      referralHomeNewRespModel.referralStatus[position]?.name?.let { it3 ->
                          listener.onClick(
                              it2,
                              it3
                          )
                      }
                  }
              }

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
//        val bundle = Bundle()
//        val properties = Properties()
//        val data = HashMap<String, String>()
//        val attribute = HashMap<String, Any>()
//        bundle.putString("type", type)
//        properties.addAttribute("type", type)
//        data["type"] = type
//        attribute["type"] = type
//        fa.logEvent("referral_reminder", bundle)
//        MoEHelper.getInstance(context).trackEvent("referral_reminder", properties)
//        UXCam.logEvent("referral_home_viewed", data)
//        Freshchat.trackEvent(context, "referral_reminder", attribute)


    }

    override fun getItemCount(): Int {
        return referralHomeNewRespModel.referralStatus?.size ?: 0
    }
}