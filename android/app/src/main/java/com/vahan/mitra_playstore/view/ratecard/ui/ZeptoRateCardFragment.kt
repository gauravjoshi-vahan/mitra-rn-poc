package com.vahan.mitra_playstore.view.ratecard.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentZeptoRateCardBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.ratecard.bottomSheet.BottomSheetCalculatorFragment
import com.vahan.mitra_playstore.view.ratecard.adapter.AdditionalPayAdapter
import com.vahan.mitra_playstore.view.ratecard.adapter.BasePayAdapter
import com.vahan.mitra_playstore.view.ratecard.adapter.MinGuaranteeZeptoAdapter
import com.vahan.mitra_playstore.view.ratecard.adapter.WeeklyBonusAdapter
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO
import com.vahan.mitra_playstore.view.ratecard.viewmodels.RateCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_rate_card_new.*

@AndroidEntryPoint
class ZeptoRateCardFragment : Fragment() {

    private lateinit var binding : FragmentZeptoRateCardBinding
    private lateinit var rateCardViewModel : RateCardViewModel
    private var currentPos: Int = 0
    private var dialogLoader: Dialog? = null
    private lateinit var rateCardStructure : RateCardDetailsDTO
    private var incentiveStructureList = ArrayList<RateCardDetailsDTO.Incentive>()
    private var calcDataList =  ArrayList<RateCardDetailsDTO.Incentive.CalculatorArray>()
    private var calcDataListLabel =  ArrayList<RateCardDetailsDTO.Incentive.CalculatorLabels>()
    private var companyName = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentZeptoRateCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initLoader()
        rateCardViewModel = ViewModelProvider(this)[RateCardViewModel::class.java]
        currentPos = requireArguments().getInt("position")
        getRateCardDetails()
        clickListener()
    }

    private fun clickListener() {
        binding.ivBackButton.setOnClickListener {
            findNavController().navigate(R.id.nav_home_fragment)
        }
        binding.rlCalculateIncome.setOnClickListener {
            setUpInstrumentation()
            BottomSheetCalculatorFragment(
                requireActivity(),
                calcDataList,
                rateCardStructure.incentiveList?.get(currentPos)?.calculatorLabels!!,
                rateCardStructure.incentiveList?.get(currentPos)?.weeklyBonus!!,
                rateCardStructure.incentiveList?.get(currentPos)?.additional!!,
                rateCardStructure.incentiveList?.get(currentPos)?.incentiveMapping
            ).show()
        }
        binding.tvSelectedCompanyName.setOnClickListener { spinner.performClick() }
    }

    private fun setUpInstrumentation() {
        val properties = Properties()
        properties.addAttribute("client","zepto")
        val attribute = HashMap<String,Any>()
        attribute["client"] = "zepto"
        captureAllEvents(requireContext(),
            "rate_card_calculator_tapped",
            attribute,
            properties
        )
    }

    private fun getRateCardDetails() {
        dialogLoader?.show()
        lifecycleScope.launchWhenStarted {
            rateCardViewModel.getAllRateCardDetails().collect {
                when (it) {
                    is ApiState.Success -> {
                        setData(currentPos,it.data)
                        setUpSpinner()
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

    private fun setUpSpinner() {
        companyName = arrayListOf()
        for(i in 0 until incentiveStructureList.size){
            companyName.add(incentiveStructureList[i].companyTitle!!)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.spinner_item, companyName)
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
                if(incentiveStructureList[position].version==1) {
                    // Open Cross Utils Fragments
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    findNavController().navigate(R.id.nav_rate_card_new, bundle)
                }
                else if(incentiveStructureList[position].version==2){
                    // New RateCard Ui for Zepto
                    binding.tvSelectedCompanyName.text = companyName[position]
                    setData(position,rateCardStructure)
                } else{
                    // Open Zomato
                    val bundle = Bundle()
                    bundle.putInt("position", position)
                    findNavController().navigate(R.id.nav_rate_card_new, bundle)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setData(currentPos: Int, rateCard: RateCardDetailsDTO) {
        incentiveStructureList = ArrayList()
        rateCardStructure = rateCard
        if(rateCard.incentiveList?.get(currentPos)!!.calculatorEnabled==true){
            binding.rlCalculateIncome.visibility = View.VISIBLE
        }
        dialogLoader?.dismiss()
        calcDataList.clear()
        for(i in 0 until (rateCard.incentiveList?.size ?: 0))
            incentiveStructureList.add(rateCard.incentiveList?.get(i)!!)


        for(i in 0 until (incentiveStructureList[currentPos].calculatorArray?.size ?: 0))
        calcDataList.add(incentiveStructureList[currentPos].calculatorArray!![i]!!)

        binding.apply {
            rvMinimumGuarantee.adapter = MinGuaranteeZeptoAdapter(requireContext(),
                incentiveStructureList[currentPos].minimumGuarantee,currentPos
            )
            rvBasePay.adapter = BasePayAdapter(requireContext(),
                incentiveStructureList[currentPos].basePay
            )
            rvAddition.adapter = AdditionalPayAdapter(requireContext(),
                incentiveStructureList[currentPos].additional
            )
        }
        setDataForWeeklyBonus(currentPos)

    }

    private fun setDataForWeeklyBonus(currentPos: Int) {
        binding.apply {
            tvWeeklyBonus.text = incentiveStructureList[currentPos].weeklyBonus?.title
            tvCompleteOrder.text = incentiveStructureList[currentPos].weeklyBonus?.orderHeader
            tvAdditionalAmount.text = incentiveStructureList[currentPos].weeklyBonus?.amountHeader
            tvWeeklyBasis.text = incentiveStructureList[currentPos].weeklyBonus?.frequency
            tvPerOrder.text = incentiveStructureList[currentPos].weeklyBonus?.unitLabel
            rvSlabsWeeklyEarning.adapter = WeeklyBonusAdapter(requireContext(),incentiveStructureList[currentPos].weeklyBonus?.condition)
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


}