package com.vahan.mitra_playstore.view.experiments.weeklygoal.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentWeeklyGoalBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.experiments.weeklygoal.models.WeeklyGoalDTO
import com.vahan.mitra_playstore.view.experiments.weeklygoal.viewmodel.WeeklyGoalViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created By Prakhar
 * Date : 25 - Nov - 2020
 * This Fragment contains the weekly goals how much user want to earn
 */

@AndroidEntryPoint
class WeeklyGoalFragment : Fragment() {

    private lateinit var binding: FragmentWeeklyGoalBinding
    private lateinit var viewWeeklyGoalViewModel: WeeklyGoalViewModel
    private lateinit var dialogLoader: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWeeklyGoalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    // Initialized the view
    private fun initView() {
        viewWeeklyGoalViewModel = ViewModelProvider(this)[WeeklyGoalViewModel::class.java]
        initLoader(requireContext())
        clickListener()
    }

    // Init the loader
    fun initLoader(context: Context) {
        dialogLoader = Dialog(context)
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView? = dialogLoader.findViewById(R.id.animate_icon)
        val rotate = RotateAnimation(
            0f, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation?.startAnimation(rotate)
    }

    // Init the clickListener
    private fun clickListener() {
        binding.apply {
            checknowGotIt.setOnClickListener {
                if (binding.edtAmount.text.isNotEmpty()) {
                    showSavingGoal(binding.edtAmount.text.toString())
                } else {
                    Toast.makeText(requireContext(), "Please enter Some Amount", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    // We are fetching the hours from BE and showing in an app
    private fun showSavingGoal(amount: String) {
        dialogLoader.show()
        lifecycleScope.launchWhenStarted {
            viewWeeklyGoalViewModel.showSaveGoal(binding.edtAmount.text.toString()).collect {
                when (it) {
                    is ApiState.Success -> {
                        dialogLoader.dismiss()
                        if (it.data.success == true) {
                            showView(it.data)

                        }
                    }
                    is ApiState.Failure -> {
                        dialogLoader.dismiss()
                        findNavController().navigate(R.id.nav_error_fragment)
                    }
                    is ApiState.Loading -> {}
                }
            }
        }
    }

    //logic for setting the data
    @SuppressLint("SetTextI18n")
    private fun showView(weeklyGoal: WeeklyGoalDTO) {
        binding.rlDescriptionView.visibility = View.VISIBLE
        binding.tvCalculatedEarningDown.visibility = View.VISIBLE
        binding.tvCalculatedTime.text = "${weeklyGoal.supplyHours} Hours"
        binding.tvCalculatedMoney.text = Html.fromHtml("to earn ₹ <b>${binding.edtAmount.text}</b>")
//        setInstrumentation(binding.edtAmount.text.toString().toInt(), weeklyGoal.supplyHours ?: 0)
    }

    private fun setInstrumentation(amount: Int, time: Int) {
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        properties.addAttribute("amount", amount)
        properties.addAttribute("hours", time)
        attribute["amount"] = amount
        attribute["hours"] = time
        captureAllEvents(
            requireContext(),
            Constants.RIDER_WEEKLY_GOAL,
            attribute,
            properties
        )
    }


}