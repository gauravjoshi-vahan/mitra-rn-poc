package com.vahan.mitra_playstore.view.ratecard.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentRateCardNewBinding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.earn.view.adapter.MilestoneAdapter
import com.vahan.mitra_playstore.view.earn.view.adapter.WeeklyIncentivesAdapter
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import com.vahan.mitra_playstore.view.ratecard.adapter.VariablePayAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_rate_card_new.*
import java.util.HashMap


@AndroidEntryPoint
class RateCardFragment : Fragment() {
    private lateinit var viewEarnViewModel: EarnViewModel
    private var dataModel: EarnDataModel? = null
    private lateinit var binding: FragmentRateCardNewBinding
    private var data: EarnDataModel.IncentiveStructures? = null
    private var currentPos: Int = 0
    private var dialogLoader: Dialog? = null
    private var companyName = arrayListOf<String>()
    var pos = currentPos
    private var weeklyAdapter: WeeklyIncentivesAdapter? = null
    private var variablePayAdapter: VariablePayAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_rate_card_new,
            container,
            false
        )

        initView()
        return binding.root
    }

    private fun clickListener() {
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvTAndCStickyView.setOnClickListener { view ->
            val item = data?.incentiveList!![pos]
            val bundle = Bundle()
            bundle.putString("label", item.milestoneFooter?.label)
            bundle.putString("description", item.milestoneFooter?.description)

            Navigation.findNavController(view)
                .navigate(R.id.terms_and_condition_fragment, bundle)
        }

    }

    private fun initView() {
        initLoader()
        viewEarnViewModel = ViewModelProvider(this@RateCardFragment)[EarnViewModel::class.java]
        currentPos = requireArguments().getInt("position")
        apiEarnInfo()
        clickListener()
    }


    @SuppressLint("SetTextI18n")
    private fun apiEarnInfo() {
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getEarnList.collect {
                when (it) {
                    is ApiState.Success -> {
                        dialogLoader?.dismiss()
                        dataModel = it.data
                        data = it.data.incentiveStructures
                        val bundle = Bundle()
                        if (data?.incentiveList?.get(pos)?.milestoneFooter != null) {
                            bundle.putString("label",
                                data!!.incentiveList[pos].milestoneFooter?.label)
                            bundle.putString("description",
                                data!!.incentiveList[pos].milestoneFooter?.description)
                        }
                        setData(pos)
                        setUpSpinner()
                        setUpRecyclerView()
                    }
                    is ApiState.Failure -> {
                        dialogLoader?.dismiss()
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_error_fragment)
                    }
                    ApiState.Loading -> {
                        dialogLoader?.show()
                    }
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setData(position: Int) {

        val items = HashMap<String, Any>()
        val properties = Properties()
        properties.addAttribute("client",data?.incentiveList?.get(position)?.companyTitle)
        items["client"] = data?.incentiveList?.get(position)?.companyTitle!!
        captureAllEvents(requireContext(), Constants.RATE_CARD_VIEWED, items, properties)

        // binding.tvTAndCStickyView.text = data!!.incentiveList[pos].milestoneFooter?.label

        binding.tvCompanyWeeklyIncentives.text = data?.incentiveList?.get(position)?.companyTitle
        binding.tvWeekly.text = data?.type
        binding.tvRateCard.text = data?.heading
        binding.tvWeekName.text = data?.label
        binding.tvOrderPay.text = data?.orderPayTitle
        binding.tvEarning.text = data?.orderPayLabel

        if (data?.incentiveList!![position].payoutStructure?.size !== null && data?.incentiveList?.get(
                position)?.payoutStructure?.size!! >= 1
        ) {
            variablePayAdapter = VariablePayAdapter(requireContext(), data!!, pos)
            binding.rvVariablePay.adapter = variablePayAdapter
//            binding.tvBasePay.text = data?.incentiveList!![position].payoutStructure?.get(0)?.name
//            binding.basePayLabel.text =
//                data!!.incentiveList[position].payoutStructure?.get(0)?.label
//            binding.perOrderValue.text =
//                data!!.incentiveList[position].payoutStructure?.get(0)?.value
//            binding.perOrder.text =
//                data!!.incentiveList[position].payoutStructure?.get(0)?.unitLabel
//            binding.tvDistancePay.text =
//                data!!.incentiveList[position].payoutStructure?.get(1)?.name
//            binding.distancePayLabel.text =
//                data!!.incentiveList[position].payoutStructure?.get(1)?.label
//            binding.distancePayValue.text =
//                data!!.incentiveList[position].payoutStructure?.get(1)?.value
//            binding.perKm.text = data!!.incentiveList[position].payoutStructure?.get(1)?.unitLabel
//            binding.tvPeekHoursIncentive.text =
//                data!!.incentiveList[position].payoutStructure?.get(2)?.name
//            binding.peekHoursPayLabel.text =
//                data!!.incentiveList[position].payoutStructure?.get(2)?.label
//            binding.peekHoursIncentiveAmt.text =
//                data!!.incentiveList[position].payoutStructure?.get(2)?.value
//            binding.perHrs.text = data!!.incentiveList[position].payoutStructure?.get(2)?.unitLabel
        }
        binding.tvOnReachingTarget.text = data!!.companyLabel
        binding.tvTripsCompleted.text =
            data!!.incentiveList[pos].companies?.get(0)?.targetAchieved.toString() + " " + context?.resources?.getString(
                R.string.trips_completed)
        binding.tvTripsLeftForMilestone.text =
            data!!.incentiveList[pos].companies?.get(0)?.messageLabel

        //Setting images for fragment_rate_card_new
        setImage(data!!.incentiveList[position].companies?.get(0)?.companyIcon.toString(),
            binding.ivCompanyLogoTripsCompleted)
        if (data!!.incentiveList[position].spinnerKey == "Mitra") {
            val tncTxtUnderlined = SpannableString(binding.tvTAndCStickyView.text)
            tncTxtUnderlined.setSpan(UnderlineSpan(), 0, tncTxtUnderlined.length, 0)
            binding.tvTAndCStickyView.text = tncTxtUnderlined
            binding.rlStickyView.visibility = View.VISIBLE
            setImage("Mitra", binding.ivComapnyLogoStickyView)
            setImage("Mitra", binding.ivSelectedCompanyLogo)
            setImage("Mitra", binding.ivWeeklyIncentivesCompanyLogo)
        } else {
            binding.rlStickyView.visibility = View.GONE
            setImage(data?.incentiveList!![position].companies?.get(0)?.companyIcon.toString(),
                binding.ivSelectedCompanyLogo)
//            setImage(data?.incentiveList!![position].companies?.get(0)?.companyIcon.toString(),
//                binding.ivComapnyLogoStickyView)
            setImage(data!!.incentiveList[position].companies?.get(0)?.companyIcon.toString(),
                binding.ivWeeklyIncentivesCompanyLogo)
        }
    }

    private fun setImage(url: String, imageView: ImageView) {
        val uri: Uri = if (url == "Mitra") {
            Uri.parse(R.drawable.dialog_icon.toString())
        } else Uri.parse(url)

        val requestBuilder: RequestBuilder<PictureDrawable> = Glide.with(requireContext())
            .`as`(PictureDrawable::class.java)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        requestBuilder.load(uri).into(imageView)
    }

    private fun setUpSpinner() {
        companyName = arrayListOf()
        for (i in 0 until data!!.incentiveList.size) {
            data!!.incentiveList[i].spinnerKey?.let { companyName.add(it) }
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, companyName
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
        spinner.setSelection(currentPos)
        binding.tvSelectedCompanyName.setOnClickListener {
            spinner.performClick()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                setData(position)
                pos = position

                binding.tvTripsCompleted.text =
                    data!!.incentiveList[pos].companies?.get(0)?.targetAchieved.toString() + " " + context!!.resources.getString(
                        R.string.trips_completed)
                binding.tvTripsLeftForMilestone.text =
                    data!!.incentiveList[pos].companies?.get(0)?.messageLabel

                if (companyName[position] == "Mitra") {
                    binding.ivAdd.visibility = View.VISIBLE
                    binding.ivCompanyLogoTripsCompleted2.visibility = View.VISIBLE
                    binding.tvTripsCompleted2.visibility = View.VISIBLE
                    binding.tvTripsLeftForMilestone2.visibility = View.VISIBLE

                 //   binding.rl3.visibility = View.GONE
                    binding.rlRv1.visibility = View.GONE
                    binding.cl2.visibility = View.GONE

                    setImage(data!!.incentiveList[pos].companies?.get(1)?.companyIcon.toString(),
                        binding.ivCompanyLogoTripsCompleted2)
                    binding.tvTripsCompleted2.text =
                        data!!.incentiveList[pos].companies?.get(1)?.targetAchieved.toString() + " " + context!!.resources.getString(
                            R.string.trips_completed)
                    binding.tvTripsLeftForMilestone2.text =
                        data!!.incentiveList[pos].companies?.get(1)?.messageLabel


                } else {
                    binding.ivAdd.visibility = View.GONE
                    binding.ivCompanyLogoTripsCompleted2.visibility = View.GONE
                    binding.tvTripsCompleted2.visibility = View.GONE
                    binding.tvTripsLeftForMilestone2.visibility = View.GONE

                    binding.cl2.visibility = View.VISIBLE

                   // binding.rl3.visibility = View.VISIBLE
                    binding.rlRv1.visibility = View.VISIBLE
                    variablePayAdapter = VariablePayAdapter(context!!, data!!, pos)
                    binding.rvVariablePay.adapter = variablePayAdapter

                }

                binding.tvSelectedCompanyName.text = companyName[position]

                weeklyAdapter = WeeklyIncentivesAdapter(context!!, data!!, pos)
                binding.rvWeeklyIncentives.adapter = weeklyAdapter

                val adapter2 = MilestoneAdapter(context!!, data!!, pos)
                binding.rvMilestone.adapter = adapter2

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    fun initLoader() {
        dialogLoader = Dialog(requireContext())
        dialogLoader!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader!!.setCancelable(false)
        dialogLoader!!.setContentView(R.layout.layout_loader)
        val imageViewAnimation = dialogLoader!!.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }

    private fun setUpRecyclerView() {
        binding.rvWeeklyIncentives.layoutManager = LinearLayoutManager(context)
        weeklyAdapter = WeeklyIncentivesAdapter(requireContext(), data!!, pos)
        binding.rvWeeklyIncentives.adapter = weeklyAdapter

        binding.rvMilestone.layoutManager = LinearLayoutManager(context)
        val adapter2 = MilestoneAdapter(requireContext(), data!!, pos)
        binding.rvMilestone.adapter = adapter2

        binding.rvMilestone.setHasFixedSize(true);
        binding.rvMilestone.isNestedScrollingEnabled = false;

        binding.rvWeeklyIncentives.setHasFixedSize(true);
        binding.rvWeeklyIncentives.isNestedScrollingEnabled = false;
    }
}