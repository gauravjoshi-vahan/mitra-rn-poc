package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.MODE_FIXED
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyEarningsDaywiseBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.HashMap


class DayWiseAdapter(
    private val context: Context,
    private val firstDay: LocalDate,
    private val today: LocalDate,
    private val lastDay: LocalDate,
    private val weeklyEarningsData: WeeklyEarningsModel,
) : RecyclerView.Adapter<DayWiseAdapter.MyViewHolder>() {

    private lateinit var dayDate: Date
    private var companyKey: String? = ""

    class MyViewHolder(dayWiseBinding: WeeklyEarningsDaywiseBinding) :
        RecyclerView.ViewHolder(dayWiseBinding.root) {
        val binding: WeeklyEarningsDaywiseBinding = dayWiseBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val dayWiseBinding: WeeklyEarningsDaywiseBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_earnings_daywise, parent, false
        )
        return MyViewHolder(dayWiseBinding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val item = weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(position)
        holder.binding.apply {
            if (item != null) {
                weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(position)?.dailyBreakdown?.forEachIndexed { _, dailyElement ->
                    weeklyEarningsData.companyDetails?.forEachIndexed { _, compElement ->
                        if (compElement != null && dailyElement !== null) {
                            if (compElement.key == dailyElement.companyKey) {
                                // condition to avoid duplication of tabs
                                if (tabLayout.tabCount < weeklyEarningsData.companyDetails.size)
                                    tabLayout.addTab(tabLayout.newTab()
                                        .setCustomView(createTabItemView(compElement.svgIcon,
                                            compElement.companyName))
                                        .setText(compElement.companyName))
                            }
                        }
                    }
                }
                if(item.dailyBreakdown?.size  == 1){
                    tabLayout.tabMode = MODE_FIXED
                }
                val firstDayInstant = Instant.from(
                    firstDay.plusDays(position.toLong()).atStartOfDay(ZoneId.of("GMT"))
                )
                dayDate = Date.from(firstDayInstant)

                dayAndDateTxt.text =
                    "${
                        toTitleCase(
                            SimpleDateFormat(
                                "EEEE",
                                Locale.getDefault()
                            ).format(dayDate)
                        )
                    }, ${
                        SimpleDateFormat("dd", Locale.getDefault()).format(dayDate)
                    } ${SimpleDateFormat("MMMM", Locale.getDefault()).format(dayDate)} ${
                        SimpleDateFormat("yyyy", Locale.getDefault()).format(dayDate)
                    }"

                val todayInstant = Instant.from(today.atStartOfDay(ZoneId.of("GMT")))

                if (firstDayInstant.isAfter(todayInstant)) {
                    oeCurrency.visibility = View.GONE
                    dayAmount.visibility = View.GONE
                    dayWiseExpandBtn.visibility = View.GONE
                    hyphen.visibility = View.VISIBLE
                    return
                }

                dayAmount.text = item.amountInRupees?.toInt().toString()
                rvTabItem.adapter =
                    DayWiseTabContentAdapter(context, position, 0, weeklyEarningsData)

                if(item.dailyBreakdown?.get(0)?.isViewOrder == true){
                    btViewOrder.visibility = View.VISIBLE
                    btViewOrder.text = item.dailyBreakdown[0]?.viewOrderLabel
                }else{
                    btViewOrder.visibility = View.GONE
                }

                companyKey = weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(position)?.dailyBreakdown?.get(0)?.companyKey.toString()

                tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        setInstrumentationTappedEarningBreakup(holder, tab.text.toString())
                        if(item.dailyBreakdown?.get(tab.position)?.isViewOrder == true){
                            btViewOrder.visibility = View.VISIBLE
                        }else{
                            btViewOrder.visibility = View.GONE
                        }
                        companyKey =
                            weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.get(position)?.dailyBreakdown?.get(
                                tab.position
                            )?.companyKey.toString()
                        var tabItemInd = 0
                        // Setting an adapter to show tab item content
                        rvTabItem.adapter = DayWiseTabContentAdapter(context,
                            position,
                            tab.position,
                            weeklyEarningsData)
                    }
                    // Reserve functions for tab operations
                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
                // Expansion panel open close function
                onClickListener(holder)
                clickListeners(holder)
            }
        }
    }

    fun onClickListener(holder: MyViewHolder) {
        holder.binding.apply {
            dayWiseParent.setOnClickListener {
                if (dayWiseEarningsBody.visibility == View.VISIBLE) {
                    dayWiseEarningsBody.visibility = View.GONE
                    dayWiseExpandBtn.rotation = 0F
                } else {
                    dayWiseEarningsBody.visibility = View.VISIBLE
                    dayWiseExpandBtn.rotation = 180F
                }
            }
        }
    }

    private fun toTitleCase(string: String?): String? {
        // Check if String is null
        if (string == null) {
            return null
        }
        var whiteSpace = true
        val builder = StringBuilder(string) // String builder to store string
        val builderLength = builder.length
        // Loop through builder
        for (i in 0 until builderLength) {
            val c = builder[i] // Get character at builders position
            if (whiteSpace) {
                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, c.titlecaseChar())
                    whiteSpace = false
                }
            } else if (Character.isWhitespace(c)) {
                whiteSpace = true // Set character is white space
            } else {
                builder.setCharAt(i, c.lowercaseChar()) // Set character to lowercase
            }
        }
        return builder.toString() // Return builders text
    }

    fun setImage(url: String, imageView: ImageView) {
        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        val uri = Uri.parse(url)
        requestBuilder.load(uri).into(imageView)
    }

    private fun createTabItemView(imgUri: String?, tabTitle: String?): View? {
        // Parent LinearLayout
        val parent = LinearLayout(context)
        parent.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        parent.orientation = LinearLayout.HORIZONTAL

        // ImageView for parent
        val imageView = ImageView(context)
        val params = LinearLayout.LayoutParams(45, 45)
        params.width = context.resources.getDimensionPixelSize(R.dimen.dimen_22dp)
        params.height = context.resources.getDimensionPixelSize(R.dimen.dimen_22dp)
        params.setMargins(0, 0, 16, 0)
        imageView.layoutParams = params
        // Set image in imageView
        setImage(imgUri?: "mitra", imageView)

        // TextView for parent
        val textView = TextView(context)
        textView.text = tabTitle

        val font = ResourcesCompat.getFont(context, R.font.poppinsbold)
        textView.typeface = font;

        parent.gravity = Gravity.CENTER
        parent.id = View.generateViewId()
        parent.addView(imageView)
        parent.addView(textView)
        return parent
    }

    private fun setInstrumentationTappedEarningBreakup(holder: MyViewHolder, clientName: String) {
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        val bundle = Bundle()
        properties.addAttribute("day", holder.binding.dayAndDateTxt.text.toString())
        properties.addAttribute("client", clientName)
        bundle.putString("day", holder.binding.dayAndDateTxt.text.toString())
        bundle.putString("client", clientName)
        attribute["day"] = holder.binding.dayAndDateTxt.text.toString()
        attribute["client"] = clientName
        captureAllEvents(
            context,
            Constants.TAPPED_DAILY_EARNINGS_BREAKUP, attribute, properties
        )
    }

    private fun clickListeners(holder: MyViewHolder) {

        val property = Properties()
        val hashmap = HashMap<String,Any>()

        captureAllEvents(context, Constants.VIEW_ORDER_CLICKED, hashmap, property)

        holder.binding.btViewOrder.setOnClickListener{
            val bundle = Bundle()
            weeklyEarningsData.companyDetails?.forEachIndexed { _, compElement ->
                if (compElement != null) {
                    if (companyKey == compElement.key) {
                        bundle.putString("companyIcon", compElement.icon)
                    }
                }
            }
            var currentDayStr = holder.binding.dayAndDateTxt.text.toString()
            val format: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            currentDayStr = currentDayStr.replaceFirst(" ", "")
            currentDayStr = currentDayStr.split(",")[1]
//            Log.d("CDS", currentDayStr)
            val date = format.parse(currentDayStr) // error
//            if (date != null) {
//                Log.d("Unique 1", date.toString())
//            }
//
//            Log.d("Unique", "$date")
            weeklyEarningsData.companyDetails?.forEachIndexed { _, compElement ->
                if (companyKey == compElement?.key) {
                    bundle.putString("companyIcon", compElement?.icon)
                }
            }
            bundle.putString(
                "date",
                SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date!!).toString()
            )
            bundle.putString("companyKey", companyKey)
            Navigation.findNavController(holder.binding.dayWiseParent)
                .navigate(R.id.daily_orders_fragment, bundle)

        }
    }

    override fun getItemCount(): Int {
        return weeklyEarningsData.weeklyDayWiseBreakdown?.dailyEarnings?.size ?: 0
    }

}