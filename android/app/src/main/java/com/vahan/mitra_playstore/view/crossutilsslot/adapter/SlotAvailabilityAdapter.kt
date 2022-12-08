package com.vahan.mitra_playstore.view.crossutilsslot.adapter


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.SlotAvailabilityBinding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import java.util.*


class SlotAvailabilityAdapter(
    val requireActivity: FragmentActivity,
    private val crossUtilSlots: List<EarnDataModel.CrossUtilSlots>,
    private val currentTime: String,
) :
    RecyclerView.Adapter<SlotAvailabilityAdapter.MyViewHolder>(){

    private var temp : String = ""
    private lateinit var first : ViewGroup

    class MyViewHolder (itemBinding: SlotAvailabilityBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val binding : SlotAvailabilityBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       first = parent
        val itemBinding: SlotAvailabilityBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.slot_availability, parent, false)
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        checkForSessions(holder.binding)
        for (i in 0 until crossUtilSlots[position].slots!!.size){
            temp+= crossUtilSlots[position].slots!![i]+"\n"
            }
        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(requireActivity)
            .`as`(PictureDrawable::class.java)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        val uri = Uri.parse(crossUtilSlots[position].svgIcon)
        requestBuilder.load(uri).into(holder.binding.ivIconMitra)
        holder.binding.tvTimeSlot.text = temp.trim().substring(0, temp.length-1)
        temp = ""
        holder.binding.tvTitle.text = crossUtilSlots[position].companyName
        holder.binding.ctaOpenApp.setOnClickListener {
            if(crossUtilSlots[position].deepLink==""){
                try {
                    requireActivity.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=in.shipsy.riderapp.ondemand.zepto")))
                } catch (anfe: ActivityNotFoundException) {
                    requireActivity.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=in.shipsy.riderapp.ondemand.zepto")))
                }
            }else {
                try {
                    requireActivity.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${crossUtilSlots[position].deepLink ?: "in.shipsy.riderapp.ondemand.zepto"}")))
                } catch (anfe: ActivityNotFoundException) {
                    requireActivity.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${crossUtilSlots[position].deepLink ?: "in.shipsy.riderapp.ondemand.zepto"}")))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return crossUtilSlots.size
    }

    private fun checkForSessions(binding: SlotAvailabilityBinding) {
        if (PrefrenceUtils.retriveDataInBoolean(requireActivity, Constants.CHECKFORFIRSTTIMESLOTSCREEN)) {
            PrefrenceUtils.insertDataInBoolean(requireActivity, Constants.CHECKFORFIRSTTIMESLOTSCREEN, false)
            startCoachMarks(binding, null)
        } else {
            if (!PrefrenceUtils.retriveData(requireActivity, Constants.SHOWN_VERSION)
                    .equals(BuildConfig.VERSION_NAME, ignoreCase = true)
            ) {
                startCoachMarks(binding, null)
            }
        }
    }

    private fun startCoachMarks(binding: SlotAvailabilityBinding, itemView: View?) {
        TapTargetView.showFor(requireActivity,
            TapTarget.forView(binding.ctaOpenApp,
                requireActivity.getString(R.string.slot_coach_mark_desc)) // description
                .outerCircleColor(R.color.default_200) // Specify a color for the outer circle
                .outerCircleAlpha(0.9f) // Specify the alpha amount for the outer circle
                .targetCircleColor(com.vahan.mitra_playstore.R.color.white) // Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white) // Specify the color of the title text
                .descriptionTextSize(16) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.red) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.DEFAULT_BOLD) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                .tintTarget(false) // Whether to tint the target view's color
                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath) // Specify a custom drawable to draw as the target
                .targetRadius(80),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                   //doSomething()
                }
            })
    }

}