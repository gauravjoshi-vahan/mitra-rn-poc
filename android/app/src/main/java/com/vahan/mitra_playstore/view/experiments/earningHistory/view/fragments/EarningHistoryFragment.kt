package com.vahan.mitra_playstore.view.experiments.earningHistory.view.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentEarningHistoryBinding
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.experiments.earningHistory.models.WeeklyEarningHistoryDTO
import com.vahan.mitra_playstore.view.experiments.earningHistory.viewmodel.EarningHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_earning_history.*
import kotlinx.android.synthetic.main.fragment_rate_card_new.*
import kotlinx.android.synthetic.main.fragment_rate_card_new.spinner
import java.text.DecimalFormat


/**
 * Created By Prakhar
 * Date : 25 - Nov - 2020
 * This Fragment contains the weekly earning information with picture representation
 */

@AndroidEntryPoint
class EarningHistoryFragment : Fragment() {
    private lateinit var binding : FragmentEarningHistoryBinding
    private lateinit var weeklyEarningHistoryViewModel : EarningHistoryViewModel
    private lateinit var monthName : ArrayList<String>
    private var currentPos: Int = 0
    private var pos = currentPos
    val entries: ArrayList<BarEntry> = ArrayList()
    private var earningHistory = ArrayList<WeeklyEarningHistoryDTO.EarningsHistory.Data>()
    private val labels = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for requireContext() fragment
        binding = FragmentEarningHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInstrumentation()
        initView()
    }

    // init View 
    private fun initView() {
        weeklyEarningHistoryViewModel = ViewModelProvider(this)[EarningHistoryViewModel::class.java]
        getWeeklyHistoryEarning()
        clickListener()
    }

    private fun setInstrumentation(){
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        captureAllEvents(
            requireContext(),
            Constants.EARNING_REPORT_VIEWED,
            attribute,
            properties
        )
    }

    // clickListener for the items
    private fun clickListener() {
        binding.rlContainerView.setOnClickListener {
            findNavController().navigate(R.id.nav_fragment_weekly_goal)
        }
        binding.backBtn.setOnClickListener {
           requireActivity().onBackPressed()
        }
        binding.tvDate.setOnClickListener {
            spinner.performClick()
        }
    }

    // this method get the WeeklyEarningHistory from the Api show data based on month
    private fun getWeeklyHistoryEarning() {
        lifecycleScope.launchWhenStarted {
            weeklyEarningHistoryViewModel.getWeeklyHistory().collect{
                when(it) {
                    is ApiState.Success -> {

                        setUpSpinner(it.data)
                    }
                    is ApiState.Failure -> {

                    }
                    is ApiState.Loading -> {}
                }
            }
        }
    }

    // we are setting month data here for dropdown
    private fun setUpSpinner(data: WeeklyEarningHistoryDTO) {
        monthName = arrayListOf()
        for (i in 0 until (data.months?.size ?: 0)) {
             monthName.add(data.months?.get(i)!!)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.spinner_item, monthName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
        spinner.setSelection(currentPos)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                pos = position
                binding.tvDate.text = data.months?.get(position)!!
                setDataForBarGraph(data,position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // This method show BarGraphData and plot the graph
    private fun setDataForBarGraph(data: WeeklyEarningHistoryDTO,position: Int) {
        prepareBarGraphData(data,position)
        binding.apply {
            val bardataset = BarDataSet(entries, "Earned Amount")
            for (i in 0 until  earningHistory.size){
                val startTimeBE = earningHistory[i].startDate
                val endTimeBE = earningHistory[i].endDate
                val labelFirstStartTime = requireContext().dateConversionToString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",startTimeBE!!,"dd-MMM")
                val labelSecondEndTime = requireContext().dateConversionToString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",endTimeBE!!,"dd-MMM")
                labels.add("$labelFirstStartTime - $labelSecondEndTime")
            }
            val xAxis: XAxis = barchart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            val startColor =  ContextCompat.getColor(requireContext(), R.color.default_200)
            val data = BarData(labels, bardataset)
            barchart.data = data // set the data and list of labels into chart
            barchart.setDescription("") // set the description
            bardataset.setColors(intArrayOf(startColor))
            bardataset.isHighlightEnabled = false
            barchart.axisLeft.valueFormatter = MyYaxisValueFormatter()
            bardataset.valueFormatter = MyValueFormatter()
            bardataset.valueTextSize = 11F
            barchart.axisRight.isEnabled = false
            barchart.animateY(3000)
        }
       
    }

    //This method prepare a data for BarChart
    private fun prepareBarGraphData(data: WeeklyEarningHistoryDTO, position: Int) {
        val selectedMonth = data.months?.get(position)
        earningHistory.clear()
        entries.clear()
        labels.clear()
        binding.weeklyEarning.text = PrefrenceUtils.retriveData(requireContext(),Constants.LANDINGURL_WEEKLY_EARNING)
        for(i in 0 until data.earningsHistory!!.size){
            if(selectedMonth == data.earningsHistory[i]!!.month){
                for(j in 0 until data.earningsHistory[i]!!.data!!.size){
                    earningHistory.add(data.earningsHistory[i]!!.data?.get(j)!!)
                }
            }
        }
        for (i in 0 until  earningHistory.size){
            entries.add(BarEntry(earningHistory[i].earnings!!.toFloat(), i))
        }
    }


    // Here we use runtime polymorphism to update his parent
    // class Interface for showing string value in graph for barDataSet

    class MyValueFormatter(
        private val mFormat: DecimalFormat = DecimalFormat("###,###,###,##0")
    ) : ValueFormatter {

         override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
//             + "k"
            return mFormat.format(value) ;
        }
    }

    // Here we use runtime polymorphism to update his parent
    // class Interface for showing string value in graph for YLeftAxis
    class MyYaxisValueFormatter(
        private val mFormat: DecimalFormat = DecimalFormat("###,###,###,##0")
    ) : YAxisValueFormatter{

        override fun getFormattedValue(value: Float, yAxis: YAxis?): String {
//            + "K"
            return mFormat.format(value);
        }

    }


}