package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.interfaces.CoachmarkListener
import com.vahan.mitra_playstore.models.kotlin.BannerListDataModelNew
import com.vahan.mitra_playstore.utils.*


/**
 * This Adapter is used for Dynamic Banner
 * Update At : 11-Nov-2022 By Prakhar
 * fa is a context for Fragment
 * bannerList contains the Dynamic banner data info
 */
class DynamicBannerAdapter(
    private val coachmarkListener: CoachmarkListener,
    private val fa: FragmentActivity,
    private val bannerList: ArrayList<BannerListDataModelNew.DynamicBanner>
) : PagerAdapter() {

    override fun getCount(): Int {
        return bannerList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageWidth(position: Int): Float {
        return if (bannerList.size > 1) 0.9f else 1.0f
    }

    override fun instantiateItem(container: ViewGroup, position: Int): ViewGroup {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.fragment_earn_carosel, container, false)
        val companyLabel = view.findViewById<TextView>(R.id.balance_heading)
        val balanceHistory = view.findViewById<TextView>(R.id.balance_history)
        val innerImage = view.findViewById<ImageView>(R.id.inner_image)
        val logos = view.findViewById<ImageView>(R.id.logos)
        val relativeLayoutOne = view.findViewById<RelativeLayout>(R.id.card_container)
        val relativeLayout = view.findViewById<FrameLayout>(R.id.card_weight_container)
        container.addView(view)
        val landingUrl = bannerList[position].landingUrl?:""
        val imgUrl = bannerList[position].imageUrl?:""
        if (position == 0) {
            relativeLayoutOne.visibility = View.VISIBLE
            innerImage.visibility = View.GONE
            val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(fa)
                .`as`(PictureDrawable::class.java)
                .listener(SvgSoftwareLayerSetter())
            val uri = Uri.parse(bannerList[position].companyLogo)
            requestBuilder.load(uri).into(logos)
            companyLabel.text = bannerList[position].title
            balanceHistory.text = bannerList[position].balance
            PrefrenceUtils.insertData(fa,Constants.LANDINGURL_WEEKLY_EARNING,bannerList[position].balance)
        } else {
            relativeLayoutOne.visibility = View.GONE
            innerImage.visibility = View.VISIBLE
            val dip = 8f
            val r: Resources = fa.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
            Glide
                .with(fa)
                .load(imgUrl)
                .transform(RoundedCorners(px.toInt()))
                .placeholder(R.drawable.ic_app_icon)
                .into(innerImage)
        }

        relativeLayoutOne.setOnClickListener {
            val properties = Properties()
            val attribute = java.util.HashMap<String, Any>()
            captureAllEvents(fa, Constants.WEEKLY_PAGE_PAYOUT_CLICKED, attribute, properties)
            Navigation.findNavController(relativeLayout).navigate(R.id.weekly_earnings_fragment)
        }
        innerImage.setOnClickListener {
          actionPerformOnDynamicBanner(landingUrl,position,relativeLayout)
        }
        if (position==0 && PrefrenceUtils.retriveDataInBoolean(fa, Constants.CHECKFORFIRSTTIME)){
            startCoachMarks(view)
        }
        if(!PrefrenceUtils.retriveDataInBoolean(fa, Constants.CHECKFORFIRSTTIME)){
            view.findViewById<ImageView>(R.id.forward_white).setColorFilter(ContextCompat.getColor(fa, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        return view as ViewGroup
    }


    private fun startCoachMarks(view: View) {
        TapTargetView.showFor(fa,
            TapTarget.forView(view.findViewById(R.id.forward_white),
                fa.getString(R.string.cardview_coach_mark_desc)) // All options below are optional
                .outerCircleColor(R.color.default_200) // Specify a color for the outer circle
                .outerCircleAlpha(0.9f) // Specify the alpha amount for the outer circle
                .targetCircleColor(com.vahan.mitra_playstore.R.color.white) // Specify a color for the target circle
                .titleTextSize(16) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.black_v2) // Specify the color of the title text
                .descriptionTextSize(16) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.white) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.DEFAULT_BOLD) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                .tintTarget(false) // Whether to tint the target view's color
                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath) // Specify a custom drawable to draw as the target
                .targetRadius(50),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    //doSomething()
                    coachmarkListener.coachmarkCallback()
                }
            })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun actionPerformOnDynamicBanner(landingUrl : String, position : Int, container : FrameLayout) {
        Log.d("hey",landingUrl.toString())
        val properties = Properties()
        val data = HashMap<String, String>()
        data[Constants.REDIRECTION_URL] = landingUrl
        data[Constants.POSITION] = position.toString()
        properties.addAttribute(Constants.REDIRECTION_URL, landingUrl)
        properties.addAttribute(Constants.POSITION, position)
        MoEHelper.getInstance(fa)
            .trackEvent(Constants.BANNER_TAPPED, properties)
        UXCam.logEvent(Constants.BANNER_TAPPED, data)
        BlitzLlamaSDK.getSdkManager(fa).triggerEvent(Constants.BANNER_TAPPED)
        fa.redirectionBasedOnAction(
            landingUrl,
            fa,
            container,
            null,
            "BANNER",
            null
        )
    }




}