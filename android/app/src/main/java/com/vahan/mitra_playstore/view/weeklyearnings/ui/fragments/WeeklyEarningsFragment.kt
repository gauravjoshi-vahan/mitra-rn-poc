package com.vahan.mitra_playstore.view.weeklyearnings.ui.fragments

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.RequestBuilder
import com.google.android.material.tabs.TabLayout
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentWeeklyEarningsBinding
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WEPayslipModel
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel
import com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters.CompanyItemAdapter
import com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters.DayWiseAdapter
import com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters.DeductionsItemAdapter
import com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters.OtherEarningsAdapter
import com.vahan.mitra_playstore.view.weeklyearnings.viewmodels.WeeklyEarningsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_weekly_earnings.*
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.*


@AndroidEntryPoint
class WeeklyEarningsFragment : Fragment() {
    private lateinit var binding: FragmentWeeklyEarningsBinding
    private lateinit var weeklyEarningsViewModel: WeeklyEarningsViewModel
    private lateinit var weeklyEarningsData: WeeklyEarningsModel
    private lateinit var payslipData: WEPayslipModel

    private var isEarningsOpen = false
    private var isDeductionsOpen = false

    private var startDateStr: String = ""
    private var endDateStr: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    val today: LocalDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    var monday = today
//    var monday = LocalDate.of(2022, 7, 18);

    @RequiresApi(Build.VERSION_CODES.O)
    var sunday = today
//    var sunday = LocalDate.of(2022, 7, 24);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_weekly_earnings,
            container,
            false
        )
        initView()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        weeklyEarningsViewModel = ViewModelProvider(this)[WeeklyEarningsViewModel::class.java]
        setStartAndEndDate(true)
        setVisibility()
        getWeeklyEarnings()
        getWeeklyPayslips()
        setVisibility()
        clickListener()
    }

    private fun setVisibility() {
        binding.rvCompItemParent.visibility = View.GONE
        binding.otherParentRL.visibility = View.GONE
        binding.dayWiseParentLayout.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStartAndEndDate(isInitCall: Boolean = false) {
        // Logic to get current week's Monday & Sunday
        if (isInitCall) {
            // Go backward to get Monday
            while (monday.dayOfWeek != DayOfWeek.MONDAY) {
                monday = monday.minusDays(1)
            }
            // Go forward to get Sunday
            while (sunday.dayOfWeek != DayOfWeek.SUNDAY) {
                sunday = sunday.plusDays(1)
            }
        }
        val mondayInstant = Instant.from(monday.atStartOfDay(ZoneId.of("GMT")))
        val mondayDayDate = Date.from(mondayInstant)

        val sundayInstant = Instant.from(sunday.atStartOfDay(ZoneId.of("GMT")))
        val sundayDayDate = Date.from(sundayInstant)
        startDateStr = SimpleDateFormat("MMMM", Locale.getDefault()).format(mondayDayDate)
            .substring(0, 3) + " " + SimpleDateFormat("dd", Locale.getDefault()).format(
            mondayDayDate
        )
        endDateStr = SimpleDateFormat("MMMM", Locale.getDefault()).format(sundayDayDate)
            .substring(0, 3) + " " + SimpleDateFormat("dd", Locale.getDefault()).format(
            sundayDayDate
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeeklyEarnings() {
        binding.startDate.text = startDateStr
        binding.endDate.text = endDateStr

        // Weekly earnings API call
        lifecycleScope.launchWhenStarted {
//          weeklyEarningsViewModel.getWeeklyEarnings("2022-07-17", "2022-07-24")
            weeklyEarningsViewModel.getWeeklyEarnings(monday.toString(), sunday.toString())
                .collect {
                    when (it) {
                        is ApiState.Success -> {
                            setInstrumentation(startDateStr, endDateStr)
                            hideLoading()
                            weeklyEarningsData = it.data
                            setDeductionsView()
                            setCommonData()
                            setCompanyWeeklyIncentivesView()
                            setOtherEarningsView()
                            setDayWiseEarningsView()
//                            Handler().postDelayed({
//                                hideBtnLoading()
//                            }, 2000)
                        }
                        is ApiState.Failure -> {
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.nav_error_fragment)
                        }
                        ApiState.Loading -> {
                            showLoading()
                        }
                    }
                }
        }
    }

    private fun setInstrumentation(startDateStr: String, endDateStr: String) {
        val properties = Properties()
        val bundle = Bundle()
        val attribute = HashMap<String, Any>()
        properties.addAttribute("start_date", startDateStr)
        properties.addAttribute("end_date", endDateStr)
        bundle.putString("start_date", startDateStr)
        bundle.putString("end_date", endDateStr)
        attribute["start_date"] = startDateStr
        attribute["end_date"] = endDateStr
        captureAllEvents(
            requireContext(),
            Constants.WEEKLY_PAYOUT_VIEWED,
            attribute,
            properties
        )
        requireContext().startBlitzSurvey(
            requireContext(),
            Constants.WEEKLY_PAYOUT_VIEWED
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeeklyPayslips() {
        showBtnLoading()
        lifecycleScope.launchWhenStarted {
            weeklyEarningsViewModel.getWeeklyPayslips(monday.toString(), sunday.toString())
                .collect {
                    when (it) {
                        is ApiState.Success -> {
                            payslipData = it.data
                            binding.apply {
                                if (payslipData.paySlipDateLabel !== null && payslipData.paySlipDateLabel !== "" && payslipData.paySlipUrl !== null && payslipData.paySlipUrl !== "") {
                                    payslipBtn.visibility = View.VISIBLE
                                    val params = RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    params.setMargins(30, 20, 30, 150)
                                    params.addRule(RelativeLayout.BELOW, R.id.deductionsHeader);
                                    deductionsBody.layoutParams = params
//                                    scrollable.layoutParams = setWeights(4.2f)
//                                    scrollable.requestLayout()
                                } else {
                                    payslipBtn.visibility = View.GONE
                                    val params = RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    params.setMargins(30, 20, 30, 0)
                                    params.addRule(RelativeLayout.BELOW, R.id.deductionsHeader);
                                    deductionsBody.layoutParams = params
//                                    scrollable.layoutParams = setWeights(10.0f)
//                                    scrollable.requestLayout()
                                }
                            }
                            hideBtnLoading()
                        }
                        is ApiState.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        ApiState.Loading -> {
                            showBtnLoading()
                        }
                    }
                }
        }
    }

    private fun setCommonData() {
        binding.apply {
            rvOtherItem.visibility = View.VISIBLE

            if (weeklyEarningsData.netPayout?.netPayoutValue?.toString() !== "" && weeklyEarningsData.netPayout?.netPayoutValue?.toString() !== null)
                totalEarningsTxt.text =
                    weeklyEarningsData.netPayout?.netPayoutValue.toString()
            earningsAmount.text =
                weeklyEarningsData.duration?.earningsInRupees?.toInt().toString()
            weHeader.text = weeklyEarningsData.pageHeader
            weSubHeader.text = weeklyEarningsData.pageSubHeader

            if (weeklyEarningsData.duration?.earningsInRupees?.toInt() == 0) {
                binding.earningsParent.visibility = View.GONE
            } else {
                binding.earningsParent.visibility = View.VISIBLE
            }
        }
    }

    private fun setCompanyWeeklyIncentivesView() {
        // Logic for Company Wise Weekly Incentives
        binding.apply {
            if (weeklyEarningsData.weeklyIncentives?.isNotEmpty() == true) {
                rvCompItemParent.visibility = View.VISIBLE
                rvCompItem.adapter =
                    CompanyItemAdapter(requireActivity(), weeklyEarningsData)

            } else {
                rvCompItemParent.visibility = View.GONE
            }
        }
    }

    private fun setOtherEarningsView() {
        // Logic for Other Earnings Expansion Panel
        binding.apply {
            if (weeklyEarningsData.otherEarnings?.totalInRupees?.toInt() != 0 && (weeklyEarningsData.otherEarnings?.otherEarningsBreakdown?.isNotEmpty() == true)) {
                otherParent.visibility = View.VISIBLE
                oeAmount.text =
                    weeklyEarningsData.otherEarnings!!.totalInRupees.toInt()
                        .toString()
                totalAmt.text =
                    weeklyEarningsData.otherEarnings!!.totalInRupees.toInt()
                        .toString()
                rvOtherItem.adapter =
                    OtherEarningsAdapter(requireActivity(), weeklyEarningsData)
            } else {
                otherParent.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDayWiseEarningsView() {
        // Logic for Day-Wise Earnings Expansion Panel
        if (weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.isNotEmpty() == true) {
            binding.dayWiseParentLayout.visibility = View.VISIBLE
            binding.dayWiseView.adapter =
                DayWiseAdapter(
                    requireActivity(),
                    monday, today, sunday,
                    weeklyEarningsData
                )

        } else {
            binding.dayWiseParentLayout.visibility = View.GONE
        }
    }

    private fun setDeductionsView() {
        val item = weeklyEarningsData.weeklyDeduction?.companyDeduction
        binding.deductionsAmount.text =
            weeklyEarningsData.weeklyDeduction?.totalDeduction.toString()
        if (item != null && item.isNotEmpty() && weeklyEarningsData.weeklyDeduction?.totalDeduction != 0) {
            binding.deductionsParent.visibility = View.VISIBLE
            weeklyEarningsData.weeklyDeduction?.companyDeduction?.forEachIndexed { _, deductionElement ->
                weeklyEarningsData.companyDetails?.forEachIndexed { _, compElement ->
                    if (compElement != null && deductionElement !== null) {
                        if (compElement.key == deductionElement.companyKey) {
                            // condition to avoid duplication of tabs
                            if (binding.deductionsView.tabLayout.tabCount < weeklyEarningsData.companyDetails?.size!!)
                                binding.deductionsView.tabLayout.addTab(
                                    binding.deductionsView.tabLayout.newTab()
                                        .setCustomView(
                                            createTabItemView(
                                                compElement.svgIcon,
                                                compElement.companyName
                                            )
                                        )
                                        .setText(compElement.companyName)
                                )
                        }
                    }
                }
            }

            if (item.size == 1) {
                binding.deductionsView.tabLayout.tabMode = TabLayout.MODE_FIXED
            }
        } else {
            binding.deductionsParent.visibility = View.GONE
        }
        binding.deductionsView.rvDeductionItem.adapter =
            DeductionsItemAdapter(requireActivity(), 0, weeklyEarningsData)
    }

    private fun createTabItemView(imgUri: String?, tabTitle: String?): View? {
        // Parent LinearLayout
        val parent = LinearLayout(context)
        parent.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        parent.orientation = LinearLayout.HORIZONTAL

        // ImageView for parent
        val imageView = ImageView(context)
        val params = LinearLayout.LayoutParams(45, 45)
        params.width = requireContext().resources.getDimensionPixelSize(R.dimen.dimen_22dp)
        params.height = requireContext().resources.getDimensionPixelSize(R.dimen.dimen_22dp)
        params.setMargins(0, 0, 16, 0)
        imageView.layoutParams = params
        // Set image in imageView
        setImage(imgUri ?: "mitra", imageView)

        // TextView for parent
        val textView = TextView(context)
        textView.text = tabTitle

        val font = ResourcesCompat.getFont(requireContext(), R.font.poppinsbold)
        textView.typeface = font;

        parent.gravity = Gravity.CENTER
        parent.addView(imageView)
        parent.addView(textView)
        return parent
    }

    fun setImage(url: String, imageView: ImageView) {
        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(requireContext())
            .`as`(PictureDrawable::class.java)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        val uri = Uri.parse(url)
        requestBuilder.load(uri).into(imageView)
    }

    private fun showLoading() {
        binding.apply {
            showLoaderContent.visibility = View.VISIBLE
            hideLoaderContent.visibility = View.GONE
            shimmerLayout1.visibility = View.VISIBLE
            shimmerLayout1.startShimmerAnimation()
        }
    }

    private fun hideLoading() {
        binding.apply {
            showLoaderContent.visibility = View.GONE
            hideLoaderContent.visibility = View.VISIBLE
            shimmerLayout1.visibility = View.GONE
            shimmerLayout1.stopShimmerAnimation()
        }
    }

    private fun showBtnLoading() {
        binding.apply {
            showBtnLoader.visibility = View.VISIBLE
            btnShimmer.visibility = View.VISIBLE
            btnShimmer.startShimmerAnimation()
        }
    }

    private fun hideBtnLoading() {
        binding.apply {
            showBtnLoader.visibility = View.GONE
            btnShimmer.stopShimmerAnimation()
            btnShimmer.visibility = View.GONE
        }
    }

    private fun setWeights(
        newWeight: Float,
        width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    ): LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            newWeight
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickListener() {
        binding.deductionsHeader.setOnClickListener {
            if (isDeductionsOpen) {
//                setDeductionsView()
                binding.deductionsBody.visibility = View.GONE
                binding.deductionsDropdown.setImageResource(R.drawable.ic_drop_down)
                isDeductionsOpen = false
            } else {
//                setDeductionsView()
                binding.deductionsBody.visibility = View.VISIBLE
                binding.deductionsDropdown.setImageResource(R.drawable.ic_arrow_up)
                isDeductionsOpen = true
            }
        }
        binding.earningsHeader.setOnClickListener {
            if (isEarningsOpen) {
                // binding.rvCompItemParent.visibility = View.VISIBLE
//                setCompanyWeeklyIncentivesView()
//                binding.otherParentRL.visibility = View.VISIBLE
                // binding.dayWiseParentLayout.visibility = View.VISIBLE
//                setDayWiseEarningsView()
                binding.earningsBody.visibility = View.GONE
                binding.earningDropdown.setImageResource(R.drawable.ic_drop_down);
                isEarningsOpen = false
            } else {
                // binding.rvCompItemParent.visibility = View.GONE
//                setCompanyWeeklyIncentivesView()
//                binding.otherParentRL.visibility = View.GONE
                // binding.dayWiseParentLayout.visibility = View.GONE
//                setDayWiseEarningsView()
                binding.earningsBody.visibility = View.VISIBLE
                binding.earningDropdown.setImageResource(R.drawable.ic_arrow_up);
                isEarningsOpen = true
            }
        }
        binding.apply {
            otherEarningsHeader.setOnClickListener {
                if (otherEarningsBody.visibility == View.VISIBLE) {
                    otherEarningsBody.visibility = View.GONE
                    expandBtn.rotation = 90F
                } else {
                    otherEarningsBody.visibility = View.VISIBLE
                    expandBtn.rotation = 270F
                }
            }
            lastWeekBtn.setOnClickListener {
                monday = monday.minusDays(7)
                sunday = sunday.minusDays(7)
                setStartAndEndDate(false)
                getWeeklyEarnings()
                getWeeklyPayslips()
                nextWeekBtn.isClickable = true
            }
            nextWeekBtn.setOnClickListener {
                val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
                val selectedWeek: Int = sunday.get(weekFields.weekOfWeekBasedYear())
                val presentWeek: Int = today.get(weekFields.weekOfWeekBasedYear())
                if (selectedWeek > presentWeek) {
                    nextWeekBtn.isClickable = false
                    return@setOnClickListener
                }
                monday = monday.plusDays(7)
                sunday = sunday.plusDays(7)
                setStartAndEndDate(false)
                getWeeklyEarnings()
                getWeeklyPayslips()
            }
            backBtn.setOnClickListener { requireActivity().onBackPressed() }

            payslipBtn.setOnClickListener {
                setInstrumentationPayslipClicked()
                val bundle = Bundle()
                bundle.putString(Constants.HEADING, payslipData.paySlipDateLabel)
                bundle.putString(Constants.REDIRECTION_URL, payslipData.paySlipUrl)
                Navigation.findNavController(
                    binding.root
                ).navigate(R.id.nav_payslip_view, bundle)
            }

            deductionsView.tabLayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    // Setting an adapter to show tab item content
                    deductionsView.rvDeductionItem.adapter = DeductionsItemAdapter(
                        requireContext(),
                        tab.position,
                        weeklyEarningsData
                    )
                }

                // Reserve functions for tab operations
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    private fun setInstrumentationPayslipClicked() {
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        captureAllEvents(
            requireContext(),
            Constants.PAYSLIP_TAPPED,
            attribute,
            properties
        )
    }
}