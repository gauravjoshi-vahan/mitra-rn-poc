package com.vahan.mitra_playstore.view.weeklyearnings.ui.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentDailyOrdersBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel
import com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters.DailyOrderDetailsAdapter
import com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters.DailyOrderTotalAmtAdapter
import com.vahan.mitra_playstore.view.weeklyearnings.viewmodels.WeeklyEarningsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DailyOrdersFragment : Fragment() {
    private lateinit var binding: FragmentDailyOrdersBinding
    private var dailyOrderModel: DailyOrderModel? = null
    private lateinit var weeklyEarningsViewModel: WeeklyEarningsViewModel
    private var dateKey: String = ""
    private lateinit var dialogLoader: Dialog
    private var companyKey: String = ""
    private var companyLogo: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_daily_orders,
            container, false
        )
        initView()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        clickListener()
        weeklyEarningsViewModel = ViewModelProvider(this)[WeeklyEarningsViewModel::class.java]
        dateKey = requireArguments().getString("date").toString()
        companyKey = requireArguments().getString("companyKey").toString()
        companyLogo = requireArguments().getString("companyIcon").toString()

        Glide.with(requireContext()).load(companyLogo).into(binding.ivCompanyLogo);

        apiCall()

    }

    private fun clickListener() {
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun apiCall() {
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            weeklyEarningsViewModel.getDailyOrder(dateKey, companyKey)
                .collect {
                    when (it) {
                        is ApiState.Success -> {
                            dialog.dismiss()
                            dailyOrderModel = it.data
                            setData()
                            setAdapter()
                        }
                        is ApiState.Failure -> {
                            dialog.dismiss()
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.nav_error_fragment)
                        }
                        ApiState.Loading -> {
                            dialog.show()
                        }
                    }
                }
        }
    }

    private fun setAdapter() {
        binding.rvTotalAmt.adapter =
            DailyOrderTotalAmtAdapter(requireActivity(), dailyOrderModel!!.orderLevelSummary)
        binding.rvOrderDetails.adapter =
            DailyOrderDetailsAdapter(requireActivity(), dailyOrderModel!!)
    }

    private fun setData() {
        binding.tvDailyOrders.text = dailyOrderModel?.heading
        binding.tvCompanyOrderDate.text = dailyOrderModel?.label
        if(dailyOrderModel?.orderLevelSummary!!.totalOrderSummary.size-1==0){
            binding.llTotalAmt.visibility = View.GONE
            binding.llTotalOrders.gravity = Gravity.END
        }else{
            binding.llTotalAmt.visibility = View.VISIBLE
            binding.tvTotalAmtValue.text =
                dailyOrderModel?.orderLevelSummary!!.totalOrderSummary[1].value
            binding.tvTotalAmt.text = dailyOrderModel?.orderLevelSummary!!.totalOrderSummary[1].label
        }
        if(dailyOrderModel?.orderLevelSummary!!.totalOrderSummary.isNotEmpty()){
            binding.tvTotalOrders.text = dailyOrderModel?.orderLevelSummary!!.totalOrderSummary[0].label
            binding.tvTotalOrderValue.text =
                dailyOrderModel?.orderLevelSummary!!.totalOrderSummary[0].value
        }else{
            binding.rlCardView.visibility=View.GONE
        }

        if(dailyOrderModel!!.orderLevelSummary.summaryBreakUp.isEmpty()){
            binding.rlDivider.visibility=View.GONE
        }else{
            binding.rlDivider.visibility=View.VISIBLE
        }
    }

    private fun createProgressDialog(): Dialog {
        dialogLoader = Dialog(requireContext())
        dialogLoader!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader!!.setCancelable(false)
        dialogLoader!!.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView =
            dialogLoader!!.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
        return dialogLoader as Dialog
    }
}

