package com.vahan.mitra_playstore.view.weeklyearnings.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.WeeklyEarningsCompanyItemBinding
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel


class CompanyItemAdapter(
    private val context: Context,
    private val weeklyEarningsData: WeeklyEarningsModel,
) : RecyclerView.Adapter<CompanyItemAdapter.MyViewHolder>() {
    class MyViewHolder(companyItemBinding: WeeklyEarningsCompanyItemBinding) :
        RecyclerView.ViewHolder(companyItemBinding.root) {
        val binding: WeeklyEarningsCompanyItemBinding = companyItemBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val companyItemBinding: WeeklyEarningsCompanyItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.weekly_earnings_company_item, parent, false
        )
        return MyViewHolder(companyItemBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val item = weeklyEarningsData.weeklyIncentives?.get(position)
        holder.binding.apply {
            if (item != null) {
                weeklyEarningsData.companyDetails?.forEachIndexed{ _, element ->
                    if (element != null) {
                        if(element.key == item.key){
                            itemCompName.text = item.title
                            setImage(element.svgIcon?: "mitra", compIcon)
                        }
                        else if(item.key == "Mitra") {
                            itemCompName.text = item.title
                            setImage("Mitra", compIcon)
                        }
                    }
                }
                amountTxt.text = item.amountInRupees?.toInt().toString()
            }
        }
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

    override fun getItemCount(): Int {
        return weeklyEarningsData.weeklyIncentives?.size ?: 0
    }
}