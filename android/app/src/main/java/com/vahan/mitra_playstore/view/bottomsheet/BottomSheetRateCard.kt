package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentRateCard2Binding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.earn.view.adapter.MilestoneAdapter
import com.vahan.mitra_playstore.view.earn.view.adapter.WeeklyIncentivesAdapter
import kotlinx.android.synthetic.main.fragment_rate_card_new.*


class BottomSheetRateCard(
    context: Context,
    val data: EarnDataModel.IncentiveStructures,
    private val currentPos: Int,
) : BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {

    private lateinit var binding: FragmentRateCard2Binding
    val companyName = arrayListOf<String>()
    var pos = currentPos
    private var weeklyAdapter: WeeklyIncentivesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentRateCard2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        clickListener()
        setData(pos)
        setUpSpinner()
        setUpRecyclerView()

    }

    private fun clickListener() {
//        iv_back_button.setOnClickListener {
//            // behavior.state = BottomSheetBehavior.STATE_HIDDEN;
//            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        }
    }

    override fun onBackPressed() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN;
    }

    private fun setUpSpinner() {
        for (i in 0 until data.incentiveList.size) {
            data.incentiveList[i].spinnerKey?.let { companyName.add(it) }
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            context,
            R.layout.spinner_item, companyName
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
        spinner.setSelection(currentPos)
        binding.tvSelectedCompanyName.setOnClickListener {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    setData(position)
                    pos = position

                    binding.tvTripsCompleted.text =
                        data.incentiveList[pos].companies?.get(0)?.targetAchieved.toString() + " " + context.resources.getString(
                            R.string.trips_completed)
                    binding.tvTripsLeftForMilestone.text =
                        data.incentiveList[pos].companies?.get(0)?.messageLabel
                    binding.tvTripsLeftForMilestone.maxWidth = 173

                    if (companyName.get(position) == "Mitra") {
                        binding.ivAdd.visibility = View.VISIBLE
                        binding.ivCompanyLogoTripsCompleted2.visibility = View.VISIBLE
                        binding.tvTripsCompleted2.visibility = View.VISIBLE
                        binding.tvTripsLeftForMilestone2.visibility = View.VISIBLE

                        binding.rl3.visibility = View.GONE
                        binding.cl2.visibility = View.GONE

                        setImage(data.incentiveList[pos].companies?.get(1)?.companyIcon.toString(),
                            binding.ivCompanyLogoTripsCompleted2)
                        binding.tvTripsCompleted2.text =
                            data.incentiveList[pos].companies?.get(1)?.targetAchieved.toString() + " " + context.resources.getString(
                                R.string.trips_completed)
                        binding.tvTripsLeftForMilestone2.text =
                            data.incentiveList[pos].companies?.get(1)?.messageLabel


                    } else {
                        binding.ivAdd.visibility = View.GONE
                        binding.ivCompanyLogoTripsCompleted2.visibility = View.GONE
                        binding.tvTripsCompleted2.visibility = View.GONE
                        binding.tvTripsLeftForMilestone2.visibility = View.GONE

                        binding.rl3.visibility = View.VISIBLE
                        binding.cl2.visibility = View.VISIBLE
                    }

                    binding.tvSelectedCompanyName.text = companyName.get(position)

                    weeklyAdapter = WeeklyIncentivesAdapter(context, data, pos)
                    binding.rvWeeklyIncentives.adapter = weeklyAdapter

                    val adapter2 = MilestoneAdapter(context, data, pos)
                    binding.rvMilestone.adapter = adapter2

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                setData(position)
                pos = position

                binding.tvTripsCompleted.text =
                    data.incentiveList[pos].companies?.get(0)?.targetAchieved.toString() + " " + context.resources.getString(
                        R.string.trips_completed)
                binding.tvTripsLeftForMilestone.text =
                    data.incentiveList[pos].companies?.get(0)?.messageLabel

                if (companyName.get(position) == "Mitra") {
                    binding.ivAdd.visibility = View.VISIBLE
                    binding.ivCompanyLogoTripsCompleted2.visibility = View.VISIBLE
                    binding.tvTripsCompleted2.visibility = View.VISIBLE
                    binding.tvTripsLeftForMilestone2.visibility = View.VISIBLE

                    binding.rl3.visibility = View.GONE
                    binding.cl2.visibility = View.GONE

                    setImage(data.incentiveList[pos].companies?.get(1)?.companyIcon.toString(),
                        binding.ivCompanyLogoTripsCompleted2)
                    binding.tvTripsCompleted2.text =
                        data.incentiveList[pos].companies?.get(1)?.targetAchieved.toString() + " " + context.resources.getString(
                            R.string.trips_completed)
                    binding.tvTripsLeftForMilestone2.text =
                        data.incentiveList[pos].companies?.get(1)?.messageLabel


                } else {
                    binding.ivAdd.visibility = View.GONE
                    binding.ivCompanyLogoTripsCompleted2.visibility = View.GONE
                    binding.tvTripsCompleted2.visibility = View.GONE
                    binding.tvTripsLeftForMilestone2.visibility = View.GONE

                    binding.rl3.visibility = View.VISIBLE
                    binding.cl2.visibility = View.VISIBLE
                }

                binding.tvSelectedCompanyName.text = companyName.get(position)

                weeklyAdapter = WeeklyIncentivesAdapter(context, data, pos)
                binding.rvWeeklyIncentives.adapter = weeklyAdapter

                val adapter2 = MilestoneAdapter(context, data, pos)
                binding.rvMilestone.adapter = adapter2

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(position: Int) {
        binding.tvCompanyWeeklyIncentives.text = data.incentiveList[position].companyTitle
        binding.tvWeekly.text = data.type
        binding.tvRateCard.text = data.heading
        binding.tvWeekName.text = data.label
        binding.tvOrderPay.text = data.orderPayTitle
        binding.tvEarning.text = data.orderPayLabel

        if (data.incentiveList[position].payoutStructure?.size !== null && data.incentiveList[position].payoutStructure?.size!! >= 1) {
            binding.tvBasePay.text = data.incentiveList[position].payoutStructure?.get(0)?.name
            binding.basePayLabel.text = data.incentiveList[position].payoutStructure?.get(0)?.label
            binding.perOrderValue.text = data.incentiveList[position].payoutStructure?.get(0)?.value
            binding.perOrder.text = data.incentiveList[position].payoutStructure?.get(0)?.unitLabel
            binding.tvDistancePay.text = data.incentiveList[position].payoutStructure?.get(1)?.name
            binding.distancePayLabel.text =
                data.incentiveList[position].payoutStructure?.get(1)?.label
            binding.distancePayValue.text =
                data.incentiveList[position].payoutStructure?.get(1)?.value
            binding.perKm.text = data.incentiveList[position].payoutStructure?.get(1)?.unitLabel
            binding.tvPeekHoursIncentive.text =
                data.incentiveList[position].payoutStructure?.get(2)?.name
            binding.peekHoursPayLabel.text =
                data.incentiveList[position].payoutStructure?.get(2)?.label
            binding.peekHoursIncentiveAmt.text =
                data.incentiveList[position].payoutStructure?.get(2)?.value
            binding.perHrs.text = data.incentiveList[position].payoutStructure?.get(2)?.unitLabel
        }






        binding.tvOnReachingTarget.text = data.companyLabel
        binding.tvTripsCompleted.text =
            data.incentiveList[pos].companies?.get(0)?.targetAchieved.toString() + " " + context.resources.getString(
                R.string.trips_completed)
        binding.tvTripsLeftForMilestone.text =
            data.incentiveList[pos].companies?.get(0)?.messageLabel

        //Setting images for fragment_rate_card_new
        setImage(data.incentiveList[position].companies?.get(0)?.companyIcon.toString(),
            binding.ivCompanyLogoTripsCompleted)
        if (data.incentiveList[position].spinnerKey == "Mitra") {
            setImage("Mitra", binding.ivSelectedCompanyLogo)
            setImage("Mitra", binding.ivWeeklyIncentivesCompanyLogo)
        } else {
            setImage(data.incentiveList[position].companies?.get(0)?.companyIcon.toString(),
                binding.ivSelectedCompanyLogo)
            setImage(data.incentiveList[position].companies?.get(0)?.companyIcon.toString(),
                binding.ivWeeklyIncentivesCompanyLogo)
        }
    }

    private fun setUpRecyclerView() {
        binding.rvWeeklyIncentives.layoutManager = LinearLayoutManager(context)
        weeklyAdapter = WeeklyIncentivesAdapter(context, data, pos)
        binding.rvWeeklyIncentives.adapter = weeklyAdapter

        binding.rvMilestone.layoutManager = LinearLayoutManager(context)
        val adapter2 = MilestoneAdapter(context, data, pos)
        binding.rvMilestone.adapter = adapter2

        binding.rvMilestone.setHasFixedSize(true);
        binding.rvMilestone.setNestedScrollingEnabled(false);

        binding.rvWeeklyIncentives.setHasFixedSize(true);
        binding.rvWeeklyIncentives.setNestedScrollingEnabled(false);
    }

    private fun setImage(url: String, imageView: ImageView) {
        var uri: Uri = if (url == "Mitra") {
            Uri.parse(R.drawable.dialog_icon.toString())
        } else Uri.parse(url)

        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        requestBuilder.load(uri).into(imageView)
    }

}
