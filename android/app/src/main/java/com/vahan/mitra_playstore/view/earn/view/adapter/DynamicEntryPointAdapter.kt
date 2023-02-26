package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.models.kotlin.BannerListDataModelNew
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.redirectionBasedOnAction

class DynamicEntryPointAdapter(
    val fa: Context,
    val dynamicBannerEntryPointList: ArrayList<BannerListDataModelNew.DynamicEntryPoint>
    ) : PagerAdapter() {

    override fun getCount(): Int {
        return dynamicBannerEntryPointList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageWidth(position: Int): Float {
        return if (dynamicBannerEntryPointList.size > 1) 0.5f else 1.0f
    }

    override fun instantiateItem(container: ViewGroup, position: Int): ViewGroup {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.entry_point2_item, container, false)
        val icon = view.findViewById<ImageView>(R.id.iv_graph)
        val relativeLayout = view.findViewById<RelativeLayout>(R.id.rv_container)
        container.addView(view)
            val dip = 8f
            val r: Resources = fa.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
            Glide
                .with(fa)
                .load(dynamicBannerEntryPointList[position].imageUrl)
                .transform(RoundedCorners(px.toInt()))
                .placeholder(R.drawable.ic_app_icon)
                .into(icon)

            relativeLayout.setOnClickListener {
                actionPerformOnDynamicBanner(dynamicBannerEntryPointList[position].landingUrl?:"", position, relativeLayout)
            }



        return view as ViewGroup
    }

    @SuppressLint("SuspiciousIndentation")
    private fun actionPerformOnDynamicBanner(landingUrl : String?, position : Int, container : RelativeLayout) {
        Log.d("hey",landingUrl.toString())
        val properties = Properties()
        val data = HashMap<String, String>()
        data[Constants.REDIRECTION_URL] = fa.getString(R.string.history_page)
        data[Constants.POSITION] = position.toString()
        properties.addAttribute(Constants.REDIRECTION_URL, landingUrl)
        properties.addAttribute(Constants.POSITION, position)
        MoEHelper.getInstance(fa)
            .trackEvent(Constants.BANNER_TAPPED, properties)
        UXCam.logEvent(Constants.BANNER_TAPPED, data)
        BlitzLlamaSDK.getSdkManager(fa).triggerEvent(Constants.BANNER_TAPPED)
        fa.redirectionBasedOnAction(
            landingUrl?:"",
            fa,
            container,
            null,
            "BANNER",
            null
        )
    }


}