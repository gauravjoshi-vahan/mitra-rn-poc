package com.vahan.mitra_playstore.view.ratecard.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentRateCardNewBinding
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.earn.view.adapter.MilestoneAdapter
import com.vahan.mitra_playstore.view.earn.view.adapter.WeeklyIncentivesAdapter
import com.vahan.mitra_playstore.view.ratecard.adapter.VariablePayAdapter
import com.vahan.mitra_playstore.view.ratecard.adapter.ZomatoAdapter
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO
import com.vahan.mitra_playstore.view.ratecard.viewmodels.RateCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_borrow.*
import kotlinx.android.synthetic.main.fragment_rate_card_new.*


@AndroidEntryPoint
class RateCardFragment : Fragment() {
    private lateinit var viewRateViewModel: RateCardViewModel
    private lateinit var binding: FragmentRateCardNewBinding
    private lateinit var rateCardStructure : RateCardDetailsDTO
    private var incentiveStructureList = ArrayList<RateCardDetailsDTO.Incentive?>()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rate_card_new, container, false)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPos = arguments?.getInt("position")?:0
    }

    private fun initView() {
        initLoader()
        viewRateViewModel = ViewModelProvider(this@RateCardFragment)[RateCardViewModel::class.java]
        getRateCardDetails()
        clickListener()
    }

    private fun clickListener() {
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.tvTAndCStickyView.setOnClickListener { view ->
            val item = rateCardStructure.incentiveList
            val bundle = Bundle()
            bundle.putString("label", item?.get(currentPos)?.milestoneFooter?.label)
            bundle.putString("description", item?.get(currentPos)?.milestoneFooter?.description)
            Navigation.findNavController(view)
                .navigate(R.id.terms_and_condition_fragment, bundle)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRateCardDetails() {
        lifecycleScope.launchWhenStarted {
            viewRateViewModel.getAllRateCardDetails().collect {
                when (it) {
                    is ApiState.Success -> {
                        if(it.data.incentiveList!=null && it.data.incentiveList.isNotEmpty()) {
                            setData(pos, it.data)
                            setUpSpinner()
                            setUpRecyclerView()
                        }else{
                            dialogLoader?.dismiss()
                            Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        }
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
    private fun setData(position: Int, rateCard: RateCardDetailsDTO) {
        rateCardStructure = rateCard
        dialogLoader?.dismiss()
        for(i in 0 until (rateCard.incentiveList?.size ?: 0))
            incentiveStructureList.add(rateCard.incentiveList?.get(i))
        val bundle = Bundle()
        if (incentiveStructureList[pos]?.milestoneFooter != null) {
            bundle.putString("label", incentiveStructureList[pos]?.milestoneFooter?.label)
            bundle.putString("description", incentiveStructureList[pos]?.milestoneFooter?.description)
        }
        setupInstrumentation(position)
        setLabels(position)
        setPayoutStructure(position)
        setImage(incentiveStructureList[position]?.companies?.get(0)?.companyIcon.toString(), binding.ivCompanyLogoTripsCompleted)
        setIncentiveStructure(position)
    }

    private fun setIncentiveStructure(position :Int) {
        if (incentiveStructureList[position]?.spinnerKey == "Mitra") {
            val tncTxtUnderlined = SpannableString(binding.tvTAndCStickyView.text)
            tncTxtUnderlined.setSpan(UnderlineSpan(), 0, tncTxtUnderlined.length, 0)
            binding.tvTAndCStickyView.text = tncTxtUnderlined
            binding.rlStickyView.visibility = View.VISIBLE
            setImage("Mitra", binding.ivComapnyLogoStickyView)
            setImage("Mitra", binding.ivSelectedCompanyLogo)
            setImage("Mitra", binding.ivWeeklyIncentivesCompanyLogo)
        } else {
            binding.rlStickyView.visibility = View.GONE
            setImage(incentiveStructureList[position]?.companies?.get(0)?.companyIcon.toString(),
                binding.ivSelectedCompanyLogo)
            setImage(incentiveStructureList[position]?.companies?.get(0)?.companyIcon.toString(),
                binding.ivWeeklyIncentivesCompanyLogo)
        }
    }

    private fun setPayoutStructure(position : Int) {
        if (incentiveStructureList[position]?.payoutStructure?.size !== null && incentiveStructureList[position]?.payoutStructure?.size!! >= 1
        ) {
            variablePayAdapter = VariablePayAdapter(requireContext(), rateCardStructure, pos)
            binding.rvVariablePay.adapter = variablePayAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setLabels(position: Int) {
        binding.tvCompanyWeeklyIncentives.text = incentiveStructureList[position]?.companyTitle
        binding.tvWeekly.text = rateCardStructure.type
        binding.tvRateCard.text = rateCardStructure.heading
        binding.tvWeekName.text = rateCardStructure.label
        binding.tvOrderPay.text = rateCardStructure.orderPayTitle
        binding.tvEarning.text = rateCardStructure.orderPayLabel
        binding.tvOnReachingTarget.text = rateCardStructure.companyLabel
        binding.tvTripsCompleted.text =
            incentiveStructureList[position]?.companies?.get(0)?.targetAchieved.toString() + " " + context?.resources?.getString(R.string.trips_completed)
        binding.tvTripsLeftForMilestone.text = incentiveStructureList[position]?.companies?.get(0)?.messageLabel
    }

    private fun setupInstrumentation(position: Int) {
        val items = HashMap<String, Any>()
        val properties = Properties()
        properties.addAttribute("client",incentiveStructureList[position]?.companyTitle)
        items["client"] = incentiveStructureList[position]?.companyTitle!!
        captureAllEvents(requireContext(), Constants.RATE_CARD_VIEWED, items, properties)
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
        companyName.clear()
        for(i in 0 until incentiveStructureList.size){
            companyName.add(incentiveStructureList[i]?.companyTitle!!)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.spinner_item, companyName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
        spinner.setSelection(currentPos)
        binding.tvSelectedCompanyName.setOnClickListener { spinner.performClick() }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                pos = position
                if(incentiveStructureList[pos]?.version==1){
                    includeZomatoUi.visibility = View.GONE
                    includeRapidoUi.visibility = View.GONE
                    rlCrossUtilContainer.visibility = View.VISIBLE
                    binding.tvTripsCompleted.text =
                        incentiveStructureList[pos]!!.companies?.get(0)?.targetAchieved.toString() + " " + context!!.resources.getString(
                            R.string.trips_completed)
                    binding.tvTripsLeftForMilestone.text =
                        incentiveStructureList[pos]!!.companies?.get(0)?.messageLabel

                    // Cross Util Rate Card
                    if (companyName[position] == "Mitra") {
                        binding.ivAdd.visibility = View.VISIBLE
                        binding.ivCompanyLogoTripsCompleted2.visibility = View.VISIBLE
                        binding.tvTripsCompleted2.visibility = View.VISIBLE
                        binding.tvTripsLeftForMilestone2.visibility = View.VISIBLE
                        binding.rlRv1.visibility = View.GONE
                        binding.cl2.visibility = View.GONE
                        setImage(incentiveStructureList[pos]?.companies?.get(1)?.companyIcon.toString(),
                            binding.ivCompanyLogoTripsCompleted2)
                        binding.tvTripsCompleted2.text =
                            incentiveStructureList[pos]?.companies?.get(1)?.targetAchieved.toString() + " " + context!!.resources.getString(
                                R.string.trips_completed)
                        binding.tvTripsLeftForMilestone2.text =
                            incentiveStructureList[pos]?.companies?.get(1)?.messageLabel
                    }
                    else {
                        binding.ivAdd.visibility = View.GONE
                        binding.ivCompanyLogoTripsCompleted2.visibility = View.GONE
                        binding.tvTripsCompleted2.visibility = View.GONE
                        binding.tvTripsLeftForMilestone2.visibility = View.GONE
                        binding.cl2.visibility = View.VISIBLE
                        // binding.rl3.visibility = View.VISIBLE
                        binding.rlRv1.visibility = View.VISIBLE
                        variablePayAdapter = VariablePayAdapter(context!!, rateCardStructure, pos)
                        binding.rvVariablePay.adapter = variablePayAdapter
                    }
                    binding.tvSelectedCompanyName.text = companyName[position]
                    weeklyAdapter = WeeklyIncentivesAdapter(context!!, rateCardStructure, pos)
                    binding.rvWeeklyIncentives.adapter = weeklyAdapter
                    val adapter2 = MilestoneAdapter(context!!, rateCardStructure, pos)
                    binding.rvMilestone.adapter = adapter2
                }
                // Zepto Rate Card
                else if(incentiveStructureList[pos]?.version==2){
                    includeZomatoUi.visibility = View.GONE
                    includeRapidoUi.visibility = View.GONE
                    rlCrossUtilContainer.visibility = View.VISIBLE
                    val bundle = Bundle()
                    bundle.putInt("position",pos)
                    findNavController().navigate(R.id.nav_fragment_rate_card_zepto,bundle)
                }
                // Zomato Rate Card
                else if(incentiveStructureList[pos]?.version==3){
                    includeZomatoUi.visibility = View.VISIBLE
                    rlCrossUtilContainer.visibility = View.GONE
                    includeRapidoUi.visibility = View.GONE
                    setZomatoData()
                }
                // Rapido Rate Card
                else if(incentiveStructureList[pos]?.version==4){
                    includeZomatoUi.visibility = View.GONE
                    rlCrossUtilContainer.visibility = View.GONE
                    includeRapidoUi.visibility = View.VISIBLE
                    setRapidoData()
                }
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
        weeklyAdapter = WeeklyIncentivesAdapter(requireContext(), rateCardStructure, pos)
        binding.rvWeeklyIncentives.adapter = weeklyAdapter
        binding.rvMilestone.layoutManager = LinearLayoutManager(context)
        val adapter2 = MilestoneAdapter(requireContext(), rateCardStructure, pos)
        binding.rvMilestone.adapter = adapter2
        binding.rvMilestone.setHasFixedSize(true);
        binding.rvMilestone.isNestedScrollingEnabled = false;
        binding.rvWeeklyIncentives.setHasFixedSize(true);
        binding.rvWeeklyIncentives.isNestedScrollingEnabled = false;
    }

    private fun setZomatoData(){
        binding.includeZomatoUi.apply {
            tvOrderPay.text = incentiveStructureList[pos]?.orderPayTitle
            tvEarningsPerDay.text = incentiveStructureList[pos]?.orderPaylabel
            tvDailyMilestoneIncentives.text = incentiveStructureList[pos]?.milestoneIncentivesTitle
            tvWeekKey1.text = incentiveStructureList[pos]?.weekKey1
            tvWeekKey2.text = incentiveStructureList[pos]?.weekKey2
            binding.tvSelectedCompanyName.text = incentiveStructureList[pos]?.companyTitle
            incentiveStructureList[pos]?.companyIcon?.let { requireContext().setImage(requireContext(),it,ivCompanyIcon) }
            incentiveStructureList[pos]?.companyIcon?.let { requireContext().setImage(requireContext(),it,iv_selected_company_logo) }

            // setting weekly list data to MON-FRI by default
            viewOne.setBackgroundResource(R.drawable.round_orange_bg)
            viewTwo.setBackgroundResource(R.drawable.round_grey_bg)
            tvWeekKey1.setTextColor(resources.getColor(R.color.black_v2))
            tvWeekKey2.setTextColor(resources.getColor(R.color.grey))
            val url: String? = incentiveStructureList[pos]?.weeklyImageList?.get(0)
            Glide.with(requireContext()).load(url).into(ivWeeklyList)

//            if (url != null) {
//                requireContext().setImage(requireContext(),url,ivWeeklyList)
//            }

            // setting list data to MON-FRI
            llWeek1.setOnClickListener {
                viewOne.setBackgroundResource(R.drawable.round_orange_bg)
                viewTwo.setBackgroundResource(R.drawable.round_grey_bg)
                tvWeekKey1.setTextColor(resources.getColor(R.color.black_v2))
                tvWeekKey2.setTextColor(resources.getColor(R.color.grey))
                val url: String? = incentiveStructureList[pos]?.weeklyImageList?.get(0)
                Glide.with(requireContext()).load(url).into(ivWeeklyList)
            }

            // setting list data to SAT-SUN
            llWeek2.setOnClickListener {
                viewOne.setBackgroundResource(R.drawable.round_grey_bg)
                viewTwo.setBackgroundResource(R.drawable.round_orange_bg)
                tvWeekKey1.setTextColor(resources.getColor(R.color.grey))
                tvWeekKey2.setTextColor(resources.getColor(R.color.black_v2))
                val url: String? = incentiveStructureList[pos]?.weeklyImageList?.get(1)
                Glide.with(requireContext()).load(url).into(ivWeeklyList)

            }

            //setting order pay adapter
            rvOrderPay.adapter = ZomatoAdapter(requireContext(),
                incentiveStructureList,
                pos,
                "order_pay"
            )
            //setting Daily Milestone Incentives adapter
            rvDailyMilestoneIncentives.adapter = ZomatoAdapter(requireContext(),
                incentiveStructureList,
                pos,
                "daily_milestone_incentive"
            )
        }
    }
    private fun setRapidoData(){
        var selectedTab = 1
        binding.includeRapidoUi.apply {
            tvRideOption1.text = incentiveStructureList[pos]?.rideOption1
            tvRideOption2.text = incentiveStructureList[pos]?.rideOption2
            binding.tvSelectedCompanyName.text = incentiveStructureList[pos]?.companyTitle
            incentiveStructureList[pos]?.companyIcon?.let { requireContext().setImage(requireContext(),it,iv_selected_company_logo) }

            // setting rapido by default data
            viewOne.setBackgroundResource(R.drawable.round_orange_bg)
            viewTwo.setBackgroundResource(R.drawable.round_grey_bg)
            tvRideOption1.setTextColor(resources.getColor(R.color.black_v2))
            tvRideOption2.setTextColor(resources.getColor(R.color.grey))
            tvTitleRedirection.text = Html.fromHtml(incentiveStructureList[pos]?.rideOptionsList?.get(0)?.titleHtml).trim()
            val url: String? = incentiveStructureList[pos]?.rideOptionsList?.get(0)?.imageUrl
            Glide.with(requireContext()).load(url)
                .placeholder(R.drawable.ic_app_icon)
                .dontTransform()
                .into(ivRapidoPayList)


            // setting list data to ride option 1
            llRideOption1.setOnClickListener {
                selectedTab = 1
                viewOne.setBackgroundResource(R.drawable.round_orange_bg)
                viewTwo.setBackgroundResource(R.drawable.round_grey_bg)
                tvRideOption1.setTextColor(resources.getColor(R.color.black_v2))
                tvRideOption2.setTextColor(resources.getColor(R.color.grey))
                ivRapidoPayListTwo.visibility = View.GONE
                ivRapidoPayList.visibility = View.VISIBLE
                val url: String? = incentiveStructureList[pos]?.rideOptionsList?.get(0)?.imageUrl
                Glide.with(requireContext()).load(url)
                    .placeholder(R.drawable.ic_app_icon)
                    .dontTransform()
                    .into(ivRapidoPayList)
               tvTitleRedirection.text = Html.fromHtml(incentiveStructureList[pos]?.rideOptionsList?.get(0)?.titleHtml).trim()
            }

            // setting list data to ride option 2
            llRideOption2.setOnClickListener {
                selectedTab = 2
                viewOne.setBackgroundResource(R.drawable.round_grey_bg)
                viewTwo.setBackgroundResource(R.drawable.round_orange_bg)
                tvRideOption1.setTextColor(resources.getColor(R.color.grey))
                tvRideOption2.setTextColor(resources.getColor(R.color.black_v2))
                ivRapidoPayListTwo.visibility = View.VISIBLE
                ivRapidoPayList.visibility = View.GONE
                val url: String? = incentiveStructureList[pos]?.rideOptionsList?.get(1)?.imageUrl
                Glide.with(requireContext())
                    .load(url)
                    .placeholder(R.drawable.ic_app_icon)
                    .into(ivRapidoPayListTwo)
               tvTitleRedirection.text = Html.fromHtml(incentiveStructureList[pos]?.rideOptionsList?.get(1)?.titleHtml).trim()
            }

            llTextContainer.setOnClickListener {
                if(selectedTab == 1){
                    if(incentiveStructureList[pos]?.rideOptionsList?.get(0)?.redirectEnabled == true){
                        requireContext().redirectionBasedOnAction(
                            incentiveStructureList[pos]?.rideOptionsList?.get(0)?.redirectLink!!,
                            requireContext(),
                            llTextContainer,
                            null,
                            "FRAGMENTS",
                            null

                        )
                    }
                }else{
                    if(incentiveStructureList[pos]?.rideOptionsList?.get(1)?.redirectEnabled == true){
                        requireContext().redirectionBasedOnAction(
                            incentiveStructureList[pos]?.rideOptionsList?.get(1)?.redirectLink!!,
                            requireContext(),
                            llTextContainer,
                            null,
                            "FRAGMENTS",
                            null

                        )
                    }
                }
            }
        }
    }


}