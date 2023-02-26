package com.vahan.mitra_playstore.view.experiments.mitrapaylater.view.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.vahan.mitra_playstore.R

class MPLOnBoardingAdapter(val context: Context) : PagerAdapter() {

    private lateinit var layoutInflater: LayoutInflater
    private val images =
        arrayOf(
            R.drawable.ic_mpl_onboarding1,
            R.drawable.ic_mpl_onboarding2,
            R.drawable.ic_mpl_onboarding3,
        )

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.onboarding_item, null)
        val imageView: ImageView = view.findViewById<View>(R.id.imageView) as ImageView
        imageView.setImageResource(images[position])
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }


    override fun getCount(): Int {
        return images.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}