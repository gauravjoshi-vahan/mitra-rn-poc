package com.vahan.mitra_playstore.view.experiments.mitrapaylater.view.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentMPLOnboardingBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.experiments.mitrapaylater.view.ui.adapter.MPLOnBoardingAdapter
import com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.adapters.OnBoardingSavingCalculator
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class MPLOnboardFragment : Fragment() {
    private lateinit var binding: FragmentMPLOnboardingBinding
    private lateinit var viewPagerAdapter: MPLOnBoardingAdapter
    private var dotscount = 0
    private var dots: Array<ImageView?>? = null
    private var location = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate( inflater, R.layout.fragment_m_p_l_onboarding, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView() {
       // setupInstrumentation()
        viewPagerAdapter = MPLOnBoardingAdapter(requireContext())
        binding.staterViewPager.adapter = viewPagerAdapter
        sliderviewpager()
        clickListener()
        setupInstrumentation()
        isMplAvailed()
    }

    private fun isMplAvailed() {
        binding.btnNext.isEnabled = false
        if(!PrefrenceUtils.retriveDataInBoolean(requireContext(),Constants.MPL_ENABLED)){
            Timer().schedule(3000) {
                lifecycleScope.launchWhenResumed {
                    Toast.makeText(requireContext(), "You are not eligible to use Mitra Pay Later now! ", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.nav_home_fragment)
                }
            }
        }else{
            binding.btnNext.isEnabled = true
        }
    }


    private fun setupInstrumentation() {
        val attribute : HashMap<String,Any> = HashMap()
        val properties = Properties()
        properties.addAttribute("status", "onboarding")
        attribute["status"] = "onboarding"
        captureAllEvents(
            requireContext(),
            Constants.MITRAPAYLATERPAGEVIEWED,
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
                R.drawable.ic_selected_mpl
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
            @SuppressLint("SetTextI18n")
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                location = position
                when (location) {
                    0 -> {
                        binding.btnNext.text = getString(R.string.next)
                        binding.txtStarterTitle.text = resources.getString(R.string.mpl_onboarding_starter_title1)
                        binding.txtStarterTitleDes.text = resources.getString(R.string.mpl_onboarding_starter_title_desc1)
                    }
                    1 -> {
                        binding.btnNext.text = getString(R.string.next)
                        binding.txtStarterTitle.text = resources.getString(R.string.mpl_onboarding_starter_title2)
                        binding.txtStarterTitleDes.text = resources.getString(R.string.mpl_onboarding_starter_title_desc2)
                    }
                    2 -> {
                        binding.btnNext.text = getString(R.string.get_started)
                        binding.txtStarterTitle.text = resources.getString(R.string.mpl_onboarding_starter_title3)
                        binding.txtStarterTitleDes.text = resources.getString(R.string.mpl_onboarding_starter_title_desc3)
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
                        R.drawable.ic_selected_mpl
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
                findNavController().navigate(R.id.nav_fragment_mpl)
            } else {
                binding.staterViewPager.setCurrentItem(location + 1, true)
                location++
            }

            when (location) {
                0 -> {
                    binding.txtStarterTitle.text = resources.getString(R.string.mpl_onboarding_starter_title1)
                    binding.txtStarterTitleDes.text = resources.getString(R.string.mpl_onboarding_starter_title_desc1)
                }
                1 -> {
                    binding.txtStarterTitle.text = resources.getString(R.string.mpl_onboarding_starter_title2)
                    binding.txtStarterTitleDes.text = resources.getString(R.string.mpl_onboarding_starter_title_desc2)
                }
                2 -> {
                    binding.btnNext.text = getString(R.string.get_started)
                    binding.txtStarterTitle.text = resources.getString(R.string.mpl_onboarding_starter_title3)
                    binding.txtStarterTitleDes.text = resources.getString(R.string.mpl_onboarding_starter_title_desc3)
                }

            }
        }
    }



}