package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.LoanItemRowBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.earn.view.ui.CashoutPurposeFragment
import com.vahan.mitra_playstore.view.earn.model.Purpose

class CashoutPurposeAdapter(
    private val context: FragmentActivity,
    val activity: CashoutPurposeFragment,
    private val getCashoutPurposeList: MutableList<Purpose>,
) : RecyclerView.Adapter<CashoutPurposeAdapter.MyViewHolder>() {

    private var key = arrayOf(
        Constants.HOME_UTILITIES,
        Constants.HOUSE_RENT,
        Constants.FAMILY,
        Constants.VEHICLE,
        Constants.SHOPPING,
        Constants.FESTIVAL,
        Constants.MEDICAL,
        Constants.OTHER,
    )
    private var mPosition: Int = -1

    class MyViewHolder(itemBinding: LoanItemRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: LoanItemRowBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: LoanItemRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.loan_item_row, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply {
            if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
                categoryText.text = getCashoutPurposeList[position].value
            } else {
                categoryText.text = getCashoutPurposeList[position].valueHi
            }
            val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
                .`as`(PictureDrawable::class.java)
                .listener(SvgSoftwareLayerSetter())
            val uri = Uri.parse(getCashoutPurposeList[position].icon)
            requestBuilder.load(uri).into(categoryImage)


            fun setBackgroundOrange() {
                if (getCashoutPurposeList[position].key == key[0]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_homeutilities)
                }
                if (getCashoutPurposeList[position].key == key[1]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_houserent)
                }
                if (getCashoutPurposeList[position].key == key[2]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_family)
                }
                if (getCashoutPurposeList[position].key == key[3]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_vehicle)
                }
                if (getCashoutPurposeList[position].key == key[4]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_shopping)
                }
                if (getCashoutPurposeList[position].key == key[5]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_festival)
                }
                if (getCashoutPurposeList[position].key == key[6]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_medical)
                }
                if (getCashoutPurposeList[position].key == key[7]) {
                    categoryImage.setImageResource(R.drawable.ic_icon_other)
                }

            }

            if (mPosition == position) {
                rlFilterData.setBackgroundResource(R.drawable.upload_text_color)
                //set selected here
//                    tvInfoFilterData.isChecked = true
                categoryText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.default_200
                    )
                )
                setBackgroundOrange()
            } else {

                rlFilterData.setBackgroundResource(R.drawable.filter_row_item_bg)

                categoryText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_color_heading
                    )
                )
            }
            rlFilterData.setOnClickListener {
                mPosition = position;
                notifyDataSetChanged();
                activity.getPurposeSelectedData(getCashoutPurposeList[position].key)
            }

        }
    }

    override fun getItemCount(): Int {
        return getCashoutPurposeList.size
    }
}

