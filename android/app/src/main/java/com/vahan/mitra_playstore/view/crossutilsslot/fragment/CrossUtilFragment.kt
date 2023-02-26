package com.vahan.mitra_playstore.view.crossutilsslot.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentCrossUtilBinding
import com.vahan.mitra_playstore.interfaces.SlotTimeClickListener
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.crossutilsslot.adapter.DaysItemAdapter
import com.vahan.mitra_playstore.view.crossutilsslot.adapter.SlotAvailabilityAdapter
import com.vahan.mitra_playstore.view.crossutilsslot.models.AttendanceDetailsDTO
import com.vahan.mitra_playstore.view.crossutilsslot.models.DateValuesDTO
import com.vahan.mitra_playstore.view.crossutilsslot.models.ReqModelSaveAttendance
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

@AndroidEntryPoint
class CrossUtilFragment : Fragment(), SlotTimeClickListener {

    /**
     * Updated by Prakhar Pandey
     * Date : 21-Nov-2022
     * In this update we are adding an Attendance flow for Workers
     */

    private lateinit var monthArray: List<String>
    private lateinit var monthArrayValues: List<String>
    private lateinit var binding: FragmentCrossUtilBinding
    private lateinit var dateList: ArrayList<DateValuesDTO>
    private lateinit var crossUtilSlots: ArrayList<EarnDataModel.CrossUtilSlots>
    private var dialogLoader: Dialog? = null
    private var comparingString: String? = ""
    private lateinit var viewEarnViewModel: EarnViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCrossUtilBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun initView() {
        initLoader()
        viewEarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]
        fetchAttendance()
        clickListener()
        captureAllEvents(
            requireContext(),
            "viewed_upcoming_slots",
            HashMap<String, Any>(),
            Properties()
        )
    }

    private fun fetchAttendance() {
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getFetchAttendance.collect {
                when (it) {
                    is ApiState.Success -> {
                        apiProfileInfo()
                        dialogLoader?.dismiss()
                        setLogicForAttendance(it.data.data)
                    }
                    is ApiState.Failure -> {

                    }
                    ApiState.Loading -> {

                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setLogicForAttendance(attendanceDetails: AttendanceDetailsDTO.Data?) {
        if (attendanceDetails?.isAttendanceApplicable == true) {
            if (attendanceDetails.checkInRequired == true) {
                binding.slotViewContainer.visibility = View.GONE
                binding.loginContainer.visibility = View.VISIBLE
                binding.tvHeading.text = "Mark your Attendance for the day"
                binding.tvCta.text = "Check in"
            } else if (attendanceDetails.checkOutRequired == true) {
                binding.loginContainer.visibility = View.VISIBLE
                binding.slotViewContainer.visibility = View.VISIBLE
                binding.tvHeading.text = "Close your Attendance for the day"
                binding.tvCta.text = "Check out"
            } else {
                binding.loginContainer.visibility = View.GONE
                binding.slotViewContainer.visibility = View.VISIBLE
            }
        }else{
            binding.slotViewContainer.visibility = View.VISIBLE
            binding.loginContainer.visibility = View.GONE
        }
    }

    private fun clickListener() {
        binding.ivCrossBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.ctaOpenApp.setOnClickListener {
            if (binding.tvCta.text.equals("Check in")) {
                val reqModelSaveAttendance = ReqModelSaveAttendance("CHECK_IN")
                lifecycleScope.launchWhenStarted {
                    viewEarnViewModel.saveAttendanceForUser(reqModelSaveAttendance).collect {
                        when (it) {
                            is ApiState.Success -> {
                                fetchAttendance()
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is ApiState.Failure -> {
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.nav_error_fragment)
                            }
                            ApiState.Loading -> {
                                dialogLoader?.show()
                            }
                        }
                    }
                }
            } else {
                val reqModelSaveAttendance = ReqModelSaveAttendance("CHECK_OUT")
                lifecycleScope.launchWhenStarted {
                    viewEarnViewModel.saveAttendanceForUser(reqModelSaveAttendance).collect {
                        when (it) {
                            is ApiState.Success -> {
                                fetchAttendance()
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is ApiState.Failure -> {
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
        }
    }

    private fun apiProfileInfo() {
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getEarnList.collect {
                when (it) {
                    is ApiState.Success -> {
                        dialogLoader?.dismiss()
                        pickNextCurrentDate()
                        setNudgeIconText()
                        it.data.crossUtilSlots?.let { it1 -> setData(it1) }
                    }
                    is ApiState.Failure -> {
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
    private fun setData(crossUtilSlotsD: List<EarnDataModel.CrossUtilSlots>) {
        binding.tvMonthName.text = ""
        crossUtilSlots = ArrayList()
        crossUtilSlots.addAll(crossUtilSlotsD)
        binding.apply {
            binding.rvMonth.adapter =
                DaysItemAdapter(this@CrossUtilFragment, requireActivity(), dateList)
            val calendar = Calendar.getInstance()
            val year: Int = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                calendar[Calendar.YEAR]
            } else {
                Year.now().value
            }
            val currentTime: String =
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            binding.rvSlotAvail.adapter =
                SlotAvailabilityAdapter(requireActivity(), crossUtilSlots, currentTime)
            binding.tvMonthName.text = comparingString + year.toString()
            val sourceString =
                if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE)
                        .equals("en")
                )
                    "<b>Hey, Work with multiple companies together and earn extra!</b>"
                else
                    "<b>नमस्ते! एक साथ कई कंपनियों के साथ काम करें और अतिरिक्त कमाई करें!</b>"
            binding.tvSlot.text = Html.fromHtml(sourceString)
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

    @SuppressLint("SimpleDateFormat")
    private fun pickNextCurrentDate() {
        comparingString = ""
        dateList = ArrayList()
        val sdf = SimpleDateFormat("EEEE dd-MMM-yyyy")
        for (i in 0..6) {
            val calendar: Calendar = GregorianCalendar()
            calendar.add(Calendar.DATE, i)
            val day: String = sdf.format(calendar.time)
            val daysArray = day.split(" ")
            monthArray = daysArray[1].split("-")
            comparingString += monthArray[1] + ","
            dateList.add(
                DateValuesDTO(
                    daysArray[0].substring(0, 3),
                    daysArray[1].substring(0, 2).toInt(),
                    day
                )
            )
        }
        monthArrayValues = comparingString!!.split(",")
        comparingString = ""
        for (i in monthArrayValues.indices - 1) {
            comparingString = monthArrayValues[0]
            if (comparingString != monthArrayValues[i]) {
                comparingString += "-" + monthArrayValues[i]
                break
            }
        }
    }

    private fun setNudgeIconText() {
        binding.tvEarningAmountNudge.text =
            PrefrenceUtils.retriveData(requireContext(), Constants.NUDGE_ICON_TEXT_REMOTE_CONFIG)
    }

    @SuppressLint("SimpleDateFormat")
    override fun slotTimeClickListener(position: Int) {
        val currentTime: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val currentDate: String =
            SimpleDateFormat("EEEE dd-MMM-yyyy", Locale.getDefault()).format(Date())
        val targetFormat: DateFormat = SimpleDateFormat("hh:mm a")
        val originalFormat: DateFormat = SimpleDateFormat("EEEE dd-MMM-yyyy")
        val date = originalFormat.parse(dateList[position].completeDate)
        val formattedDate = targetFormat.format(date)
        if (currentDate == dateList[position].completeDate) {
            binding.rvSlotAvail.adapter =
                SlotAvailabilityAdapter(requireActivity(), crossUtilSlots, currentTime)
        } else {
            binding.rvSlotAvail.adapter =
                SlotAvailabilityAdapter(requireActivity(), crossUtilSlots, formattedDate)
        }
    }

    companion object {
        var lastCheckedPosition = 0
    }


}