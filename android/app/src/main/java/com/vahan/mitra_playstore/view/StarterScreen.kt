package com.vahan.mitra_playstore.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.vahan.mitra_playstore.view.adapter.SliderAdapter
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityStarterScreenBinding
import com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui.EnterNumberActivity


class StarterScreen : BaseActivity() {

    private lateinit var binding: ActivityStarterScreenBinding
    private var dotscount = 0
    private var dots: Array<ImageView?>? = null
    private lateinit var viewPagerAdapter: SliderAdapter
    private var location = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStarterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for full screen
//        window.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        //adapter for images
        viewPagerAdapter = SliderAdapter(this)
        binding.staterViewPager.adapter = viewPagerAdapter
//        view dots linked with viewpager
        sliderviewpager()


        //skip btn
//        binding.txtStarterSkipstep.setOnClickListener {
//            startActivity(Intent(this, EnterNumberActivity::class.java))
//            finishAffinity()
//        }
        binding.btnNext.setOnClickListener {
            if (location == 3) {
                startActivity(Intent(this, ReactActivity::class.java))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
            } else {
                binding.staterViewPager.setCurrentItem(location + 1, true)
                location++
            }

            when (location) {
                0 -> {
                    binding.txtStarterTitle.text = getString(R.string.starter_one_1)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_one_sub)
                }
                1 -> {
                    binding.txtStarterTitle.text = getString(R.string.starter_two_2)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_two_sub)
                }
                2 -> {
//                    binding.btnNext.text = "getString(R.string.get_started)"
                    binding.txtStarterTitle.text = getString(R.string.starter_three_3)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_three_sub)
                }
                3 -> {
                    binding.btnNext.text = getString(R.string.get_started)
                    binding.txtStarterTitle.text = getString(R.string.starter_four_4)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_four_sub)
                }
            }
        }
    }

    private fun sliderviewpager() {
        dotscount = viewPagerAdapter.count
        dots = arrayOfNulls(dotscount)
        for (i in 0 until dotscount) {
            dots!![i] = ImageView(this)
            dots!![i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.non_active_dot
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.SliderDots.addView(dots!![i], params)
        }
        dots!![0]!!.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.active_dot
            )
        )
        dots!![0]?.setOnClickListener {
            location = 0
            binding.staterViewPager.setCurrentItem(location, true)
        }
        dots!![1]?.setOnClickListener {
            location = 1
            binding.staterViewPager.setCurrentItem(location, true)
        }
        dots!![2]?.setOnClickListener {
            location = 2
            binding.staterViewPager.setCurrentItem(location, true)
        }
        dots!![3]?.setOnClickListener {
            location = 3
            binding.staterViewPager.setCurrentItem(location, true)
        }
        binding.staterViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                location = position
                if (location == 0) {
                    binding.txtStarterTitle.text = getString(R.string.starter_one_1)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_one_sub)
                    binding.btnNext.text = getString(R.string.next)
                } else if (location == 1) {
                    binding.txtStarterTitle.text = getString(R.string.starter_two_2)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_two_sub)
                    binding.btnNext.text = getString(R.string.next)
                } else if (location == 2) {
                    binding.btnNext.text = getString(R.string.next)
                    binding.txtStarterTitle.text = getString(R.string.starter_three_3)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_three_sub)
                } else if (location == 3) {
                    binding.btnNext.text = getString(R.string.get_started)
                    binding.txtStarterTitle.text = getString(R.string.starter_four_4)
                    binding.txtStarterTitleDes.text = getString(R.string.starter_four_sub)
                }
            }

            override fun onPageSelected(position: Int) {
                Log.d("data", "onPageSelected $position")
                for (i in 0 until dotscount) {
                    dots!![i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.non_active_dot
                        )
                    )
                }
                dots!![position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.active_dot
                    )
                )
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("data", "onPageScrollStateChanged $state")

            }
        })
//        view dots linked with viewpager ends
    }
}
