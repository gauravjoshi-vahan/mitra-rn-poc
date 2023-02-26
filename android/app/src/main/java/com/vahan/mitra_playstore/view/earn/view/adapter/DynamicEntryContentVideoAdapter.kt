package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.MitraBonusVideosItemBinding
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.earn.model.DynamicEntryContentVideoModel

class DynamicEntryContentVideoAdapter(
    private val context: Context,
    private val dynamicEntryContentVideoList: List<DynamicEntryContentVideoModel.VideoDetail>

) : RecyclerView.Adapter<DynamicEntryContentVideoAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: MitraBonusVideosItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: MitraBonusVideosItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: MitraBonusVideosItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.mitra_bonus_videos_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply {
            Glide
                .with(context)
                .load(dynamicEntryContentVideoList[position].thumbnail)
                .placeholder(R.drawable.ic_app_icon)
                .into(ivThumbnail)

            tvHeading.text = dynamicEntryContentVideoList[position].title

            rlContainer.setOnClickListener {
                actionPerformOnDynamicBanner(dynamicEntryContentVideoList[position].url, rlContainer)
            }
        }
    }

    override fun getItemCount(): Int {
        return dynamicEntryContentVideoList.size
    }
    @SuppressLint("SuspiciousIndentation")
    private fun actionPerformOnDynamicBanner(landingUrl : String?, container : RelativeLayout) {
        val properties = Properties()
        val data = HashMap<String,Any>()
        captureAllEvents(context,Constants.HOME_PAGE_VIDEO_PLAYED,data,properties)
        context.startBlitzSurvey(context,Constants.HOME_PAGE_VIDEO_PLAYED)
        context.redirectionBasedOnAction(
            landingUrl?:"",
            context,
            container,
            null,
            "BANNER",
            null
        )
    }
}

