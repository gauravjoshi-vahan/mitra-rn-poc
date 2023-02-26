package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.Glide
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.EntryPoint2ItemBinding
import com.vahan.mitra_playstore.models.kotlin.BannerListDataModelNew
import com.vahan.mitra_playstore.utils.*
import java.util.ArrayList

class EntryPointAdapter(
    private val context: Context,
    val dynamicEntryList: ArrayList<BannerListDataModelNew.DynamicEntryPoint>
) : RecyclerView.Adapter<EntryPointAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: EntryPoint2ItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: EntryPoint2ItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: EntryPoint2ItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.entry_point2_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply{
            val dip = 8f
            val r: Resources = context.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
               tvTitle.text = dynamicEntryList[position].bannerName
            Log.d("bannerName", "onBindViewHolder: "+dynamicEntryList[position].bannerName)
            Glide
                .with(context)
                .load(dynamicEntryList[position].imageUrl)
                .placeholder(R.drawable.ic_app_icon)
                .into(this.ivGraph)

            rvContainer.setOnClickListener {
                actionPerformOnDynamicBanner(dynamicEntryList[position].landingUrl?:"", position, rvContainer)
            }
        }
    }

    override fun getItemCount(): Int {
        return dynamicEntryList.size
    }

    @SuppressLint("SuspiciousIndentation")
    private fun actionPerformOnDynamicBanner(landingUrl : String, position : Int, container : RelativeLayout) {
        val properties = Properties()
        val data = HashMap<String, String>()
        data[Constants.REDIRECTION_URL] = landingUrl
        data[Constants.POSITION] = position.toString()
        properties.addAttribute(Constants.REDIRECTION_URL, landingUrl)
        properties.addAttribute(Constants.POSITION, position)
        MoEHelper.getInstance(context)
            .trackEvent(Constants.ENTRY_POINT_TAPPED, properties)
        UXCam.logEvent(Constants.ENTRY_POINT_TAPPED, data)
        BlitzLlamaSDK.getSdkManager(context).triggerEvent(Constants.ENTRY_POINT_TAPPED)
        context.redirectionBasedOnAction(
            landingUrl,
            context,
            container,
            null,
            "BANNER",
            null
        )
    }
}

