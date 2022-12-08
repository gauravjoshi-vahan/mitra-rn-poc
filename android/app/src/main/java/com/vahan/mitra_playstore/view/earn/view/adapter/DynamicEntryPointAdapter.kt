package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.navigation.Navigation
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
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.ExperimentActivity

class DynamicEntryPointAdapter(val fa: Context, val fragmentArrayList: ArrayList<BannerListDataModelNew.DynamicEntryPoint>) : PagerAdapter() {

    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageWidth(position: Int): Float {
        return if (fragmentArrayList.size > 1) 0.5f else 1.0f
    }

    override fun instantiateItem(container: ViewGroup, position: Int): ViewGroup {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.entry_point2_item, container, false)
      //  val companyLabel = view.findViewById<TextView>(R.id.balance_heading)
      //  val balanceHistory = view.findViewById<TextView>(R.id.balance_history)
        val innerImage = view.findViewById<ImageView>(R.id.inner_image)
        val icon = view.findViewById<ImageView>(R.id.iv_graph)
//        val tvTitle = view.findViewById<TextView>(R.id.tv_slot)
       // val logos = view.findViewById<ImageView>(R.id.logos)
    //    val relativeLayoutOne = view.findViewById<RelativeLayout>(R.id.card_container)
        val relativeLayout = view.findViewById<RelativeLayout>(R.id.card_weight_container)
        container.addView(view)


            //relativeLayoutOne.visibility = View.GONE
            innerImage.visibility = View.VISIBLE
            val dip = 8f
            val r: Resources = fa.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
         //   tvTitle.text = fragmentArrayList[position].bannerName
            Glide
                .with(fa)
                .load(fragmentArrayList[position].imageUrl)
                .transform(RoundedCorners(px.toInt()))
                .placeholder(R.drawable.ic_app_icon)
                .into(icon)


//        relativeLayoutOne.setOnClickListener {
//            val properties = Properties()
//            val attribute = java.util.HashMap<String, Any>()
//            fa.captureAllEvents(fa, Constants.WEEKLY_PAGE_PAYOUT_CLICKED,attribute,properties)
//            Navigation.findNavController(relativeLayout).navigate(R.id.weekly_earnings_fragment)
//        }
            innerImage.setOnClickListener {
                actionPerformOnDynamicBanner(fragmentArrayList[position].landingUrl?:"", position, relativeLayout)
            }



        return view as ViewGroup
    }

    @SuppressLint("SuspiciousIndentation")
    private fun actionPerformOnDynamicBanner(landingUrl : String, position : Int, container : RelativeLayout) {
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
        if (landingUrl == "") { Toast.makeText(fa, fa.getString(R.string.link_not_found), Toast.LENGTH_LONG).show() }

        // This conditions check when we need to show the URL in Chrome Browser before moving into IN APP Web view
        else if(
            landingUrl.startsWith("tel:") ||
            landingUrl.startsWith("whatsapp:") ||
            landingUrl.contains("play.google.com") ||
            landingUrl.startsWith("mailto")

        ){
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(landingUrl))
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.setPackage("com.android.chrome")
            try {
                fa.startActivity(i)
            } catch (e: ActivityNotFoundException) {
                // Chrome is probably not installed
                // Try with the default browser
                i.setPackage(null)
                fa.startActivity(i)
            }
        }
        /*
        If this URL will be come from dynamic banner then move to REFERRAL HOME
         */
        else if (landingUrl.startsWith("https://mitra.vahan.co/referral")) {
            Navigation.findNavController(container)
                .navigate(R.id.nav_referral_home_fragment)
        }
        /*
       This condition checks if url contains userId
       then it should replace with generated UserId and concatenate with dynamic url
        */
        else if (landingUrl.contains("{userId}") || landingUrl.contains("{phoneNumber}")) {
            var updatedUrl =  landingUrl.replace("{userId}", PrefrenceUtils.retriveData(fa, Constants.USERID))
            updatedUrl =  updatedUrl.replace("{phoneNumber}", PrefrenceUtils.retriveData(fa, Constants.PHONENUMBER))
            fa.startActivity(
                Intent(fa, ExperimentActivity::class.java)
                    .putExtra(
                        "link",
                        updatedUrl)
            )
        }

        else {
            // Direct Open in In APP WebView
            if (landingUrl.contains(fa.getString(R.string.http)) || landingUrl.contains(fa.getString(R.string.https))) {
                fa.startActivity(
                    Intent(fa, ExperimentActivity::class.java)
                        .putExtra(Constants.LINK, landingUrl)
                )
            }
            // Moving to Profile Page
            else if (landingUrl == Constants.LANDINGURL_PROFILE) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_profile_fragment)
            }
            // Move to Home Page
            else if (landingUrl == Constants.LANDINGURL_HOME) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_home_fragment)
            }
            // Move to Borrow Fragment
            else if (landingUrl == Constants.LANDINGURL_LOAN) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_loan_application_fragment)
            }
            // Move to Notification Fragment
            else if (landingUrl == Constants.LANDINGURL_NOTIFICATIONS) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_fragment_notification)
            }
            // Move to Insurance Fragment
            else if (landingUrl == Constants.LANDINGURL_INSURE) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_insurance_fragment)
            }
            // Move to Referral Home Fragment
            else if (landingUrl == Constants.LANDINGURL_REFERRAL) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_referral_home_fragment)
            }
            // Move to Saving Calculator
            else if (landingUrl == Constants.LANDINGURL_SAVING_CALCULATOR) {
                Navigation.findNavController(container)
                    .navigate(R.id.nav_selected_plan_fragment)
            }
            // Move to Earning History Graph
            else if (landingUrl == Constants.WEEKLY_EARNING_GRAPHS){
                Navigation.findNavController(container)
                    .navigate(R.id.nav_fragment_graph)
            }
            // Move to Earning History Goal
            else if (landingUrl == Constants.WEEKLY_EARNING_GOALS){
                Navigation.findNavController(container)
                    .navigate(R.id.nav_fragment_weekly_goal)
            }


        }
    }


}