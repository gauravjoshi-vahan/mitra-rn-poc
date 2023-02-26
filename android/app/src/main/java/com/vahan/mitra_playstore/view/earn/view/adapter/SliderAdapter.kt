package com.vahan.mitra_playstore.view.earn.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.view.earn.model.TextModel


class SliderAdapter(
    private val activity: FragmentActivity,
    private val imagesList: ArrayList<TextModel>
) : PagerAdapter() {
    override fun getCount(): Int {
        return imagesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    override fun getPageWidth(position: Int): Float {
        return if (imagesList.size > 1) 0.9f else 1.0f
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageLayout = LayoutInflater.from(container.context)
            .inflate(R.layout.slider_item_container, container, false) as ViewGroup
        val heading = imageLayout.findViewById<TextView>(R.id.tv_heading)
        val headingSub = imageLayout.findViewById<TextView>(R.id.tv_heading_sub)
        val cardUpload = imageLayout.findViewById<TextView>(R.id.tv_card_upload)
        val cardViewContainer: CardView = imageLayout.findViewById(R.id.container_view)
        container.addView(imageLayout)
        heading.text = imagesList[position].heading
        headingSub.text = imagesList[position].subHeading
        if (imagesList[position].type.contains(Constants.BANK)) {
            cardUpload.text = activity.getString(R.string.tap_to_bank)
        } else {
            cardUpload.text = activity.getString(R.string.tap_to_upload)
        }
        cardViewContainer.setOnClickListener { view: View? ->
            if (imagesList[position].type.contains(Constants.BANK)) {
                Navigation.findNavController(imageLayout.findViewById(R.id.tv_heading))
                    .navigate(R.id.nav_fragment_add_bank)
            } else {
                Navigation.findNavController(imageLayout.findViewById(R.id.tv_heading))
                    .navigate(R.id.nav_upload_fragment)
            }
        }
        return imageLayout
    }
}