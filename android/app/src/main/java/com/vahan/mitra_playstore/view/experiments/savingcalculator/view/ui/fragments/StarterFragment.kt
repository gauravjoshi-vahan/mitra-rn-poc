package com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentStarterBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.adapters.OnBoardingSavingCalculator

class StarterFragment : Fragment() {

    private lateinit var binding: FragmentStarterBinding
    private lateinit var viewPagerAdapter: OnBoardingSavingCalculator
    private var dotscount = 0
    private var dots: Array<ImageView?>? = null
    private var location = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate( inflater, R.layout.fragment_starter, container, false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setupInstrumentation()
        viewPagerAdapter = OnBoardingSavingCalculator(requireContext())
        binding.staterViewPager.adapter = viewPagerAdapter
        sliderviewpager()
        clickListener()
    }



    private fun setupInstrumentation() {
        val attribute : HashMap<String,Any> = HashMap()
        val properties = Properties()
        properties.addAttribute("status", "onboarding")
        attribute["status"] = "onboarding"
        captureAllEvents(
            requireContext(),
            Constants.SAVINGPAGEVIEWED,
            attribute,
            properties
        )
    }

    private fun sliderviewpager() {
        dotscount = viewPagerAdapter.count
        dots = arrayOfNulls(dotscount)
        for (i in 0 until dotscount) {
            dots!![i] = ImageView(requireContext())
            dots!![i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
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
                requireContext(),
                R.drawable.ic_rectangle_gold
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

        binding.staterViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                location = position
                when (location) {
                    0 -> {
                        binding.btnNext.text = "Next"
                        binding.txtStarterTitle.text = "Start saving emergency fund today"
                        binding.txtStarterTitleDes.text = "Start saving money for emergency fund and give yourself & your family security."
                    }
                    1 -> {
                        binding.btnNext.text = "Next"
                        binding.txtStarterTitle.text = "Start with minimum ₹50 per week "
                        binding.txtStarterTitleDes.text = "Earn 5% interest and withdraw anytime you need"
                    }
                    2 -> {
                        binding.btnNext.text = "Get Started"
                        binding.txtStarterTitle.text = "Save money safely and securely with us"
                        binding.txtStarterTitleDes.text = "From your weekly payout, an amount will be securely kept aside as savings for your needs, which only you can withdraw"
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                Log.d("data", "onPageSelected $position")
                for (i in 0 until dotscount) {
                    dots!![i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.non_active_dot
                        )
                    )
                }
                dots!![position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_rectangle_gold
                    )
                )
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("data", "onPageScrollStateChanged $state")

            }
        })
//        view dots linked with viewpager ends
    }

    private fun clickListener() {
        binding.btnNext.setOnClickListener {
            if (location == 2) {
                findNavController().navigate(R.id.nav_why_saving_fragment)
            } else {
                binding.staterViewPager.setCurrentItem(location + 1, true)
                location++
            }

            when (location) {
                0 -> {
                    binding.txtStarterTitle.text = "Start saving emergency fund today"
                    binding.txtStarterTitleDes.text = "Start saving money for emergency fund and give yourself & your family security."
                }
                1 -> {
                    binding.txtStarterTitle.text = "Start with minimum ₹50 per week "
                    binding.txtStarterTitleDes.text = "Earn 5% interest and withdraw anytime you need"
                }
                2 -> {
                    binding.btnNext.text = "Get Started"
                    binding.txtStarterTitle.text = "Save money safely and securely with us"
                    binding.txtStarterTitleDes.text = "From your weekly payout, an amount will be securely kept aside as savings for your needs, which only you can withdraw"
                }

            }
        }
    }




}