package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyEarningsDeductionsBinding
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel

class DeductionsAdapter(
    private val context: Context,
    private val weeklyEarningsData: WeeklyEarningsModel,
) : RecyclerView.Adapter<DeductionsAdapter.MyViewHolder>() {
    class MyViewHolder(deductionsBinding: WeeklyEarningsDeductionsBinding) :
        RecyclerView.ViewHolder(deductionsBinding.root) {
        val binding: WeeklyEarningsDeductionsBinding = deductionsBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DeductionsAdapter.MyViewHolder {
        val deductionsBinding: WeeklyEarningsDeductionsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_earnings_deductions, parent, false
        )
        return DeductionsAdapter.MyViewHolder(deductionsBinding)
    }

    override fun onBindViewHolder(
        holder: DeductionsAdapter.MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
//        val ind = 0
//        val item = weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(position)
        holder.binding.apply {
//            if (item != null) {
//                weeklyEarningsData.deductions?.forEachIndexed { _, deductionElement ->
//                    weeklyEarningsData.companyDetails?.forEachIndexed { _, compElement ->
//                        if (compElement != null && deductionElement !== null) {
//                            if (compElement.key == deductionElement.companyName) {
//                                // condition to avoid duplication of tabs
//                                if (tabLayout.tabCount < weeklyEarningsData.companyDetails.size)
//                                    tabLayout.addTab(tabLayout.newTab()
//                                        .setCustomView(createTabItemView(compElement.svgIcon,
//                                            compElement.companyName))
//                                        .setText(compElement.companyName))
//                            }
//                        }
//                    }
//                }
//            }
//            rvDeductionItem.adapter = DeductionsItemAdapter(context, 0, weeklyEarningsData)

//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    // Setting an adapter to show tab item content
//                    rvDeductionItem.adapter = DeductionsItemAdapter(context,
//                        tab.position,
//                        weeklyEarningsData)
//                }
//
//                // Reserve functions for tab operations
//                override fun onTabUnselected(tab: TabLayout.Tab) {}
//                override fun onTabReselected(tab: TabLayout.Tab) {}
//            })
        }
    }

//    private fun createTabItemView(imgUri: String?, tabTitle: String?): View? {
//        // Parent LinearLayout
//        val parent = LinearLayout(context)
//        parent.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT)
//        parent.orientation = LinearLayout.HORIZONTAL
//
//        // ImageView for parent
//        val imageView = ImageView(context)
//        val params = LinearLayout.LayoutParams(45, 45)
//        params.width = context.resources.getDimensionPixelSize(R.dimen.dimen_22dp)
//        params.height = context.resources.getDimensionPixelSize(R.dimen.dimen_22dp)
//        params.setMargins(0, 0, 16, 0)
//        imageView.layoutParams = params
//        // Set image in imageView
//        setImage(imgUri ?: "mitra", imageView)
//
//        // TextView for parent
//        val textView = TextView(context)
//        textView.text = tabTitle
//
//        val font = ResourcesCompat.getFont(context, R.font.poppinsbold)
//        textView.typeface = font;
//
//        parent.gravity = Gravity.CENTER
//        parent.addView(imageView)
//        parent.addView(textView)
//        return parent
//    }
//
//    fun setImage(url: String, imageView: ImageView) {
//        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
//            .`as`(PictureDrawable::class.java)
//            .error(R.drawable.dialog_icon)
//            .listener(SvgSoftwareLayerSetter())
//
//        val uri = Uri.parse(url)
//        requestBuilder.load(uri).into(imageView)
//    }

    override fun getItemCount(): Int {
        return weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.size ?: 0
    }
}