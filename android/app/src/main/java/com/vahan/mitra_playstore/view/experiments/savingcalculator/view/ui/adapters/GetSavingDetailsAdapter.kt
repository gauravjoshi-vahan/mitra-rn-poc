package com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
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
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.ReasonDTO
import com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.fragments.WhySavingFragment

class GetSavingDetailsAdapter(
    private val context: FragmentActivity,
    val activity: WhySavingFragment,
    private val getLoanPurposeList: MutableList<ReasonDTO>,
    private val getAmountList: MutableList<Int>,
    private val type: Int,
) : RecyclerView.Adapter<GetSavingDetailsAdapter.MyViewHolder>() {

    private var key = arrayOf(
        Constants.HOME_UTILITIES,
        Constants.HOUSE_RENT,
        Constants.FAMILY,
        Constants.VEHICLE,
        Constants.SHOPPING,
        Constants.FESTIVAL,
        Constants.MEDICAL,
        Constants.OTHER
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
        holder.binding.categoryImage.visibility = View.GONE
        if (type == 0) {
            holder.binding.apply {
                categoryText.text = getLoanPurposeList[position].value
                val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
                    .`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                val uri = Uri.parse(getLoanPurposeList[position].icon)
                requestBuilder.load(uri).into(categoryImage)


                fun setBackgroundOrange() {
                    if (getLoanPurposeList[position].key == key[0]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_homeutilities)
                    }
                    if (getLoanPurposeList[position].key == key[1]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_houserent)
                    }
                    if (getLoanPurposeList[position].key == key[2]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_family)
                    }
                    if (getLoanPurposeList[position].key == key[3]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_vehicle)
                    }
                    if (getLoanPurposeList[position].key == key[4]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_shopping)
                    }
                    if (getLoanPurposeList[position].key == key[5]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_festival)
                    }
                    if (getLoanPurposeList[position].key == key[6]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_medical)
                    }
                    if (getLoanPurposeList[position].key == key[7]) {
                        categoryImage.setImageResource(R.drawable.ic_icon_other)
                    }

                }

                if (mPosition == position) {
                    rlFilterData.setBackgroundResource(R.drawable.upload_gold_text_color)
                    //set selected here
//                    tvInfoFilterData.isChecked = true
                    categoryText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.gold_200
                        )
                    )

                    setBackgroundOrange()
                } else {

                    rlFilterData.setBackgroundResource(R.drawable.filter_row_item_bg)

                    categoryText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black_v2
                        )
                    )
                }
                rlFilterData.setOnClickListener {
                    mPosition = position;
                    notifyDataSetChanged();
                    activity.getPurposeSelectedData(getLoanPurposeList[position].key!!)
                }

            }
        } else {

            holder.binding.apply {
                categoryText.text = "₹ ${getAmountList[position]}"
                if (mPosition == position) {
                    //set selected here
                    categoryImage.setBackgroundResource(R.drawable.ic_orange_tick)
                    categoryImage.visibility = View.VISIBLE
                    rlFilterData.setBackgroundResource(R.drawable.upload_gold_text_color)
                    categoryText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.gold_200
                        )
                    )
                } else {
                    categoryImage.visibility = View.GONE
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
                    activity.getAmountSelectedData(getAmountList[position])
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return if (type == 0) {
            getLoanPurposeList.size
        } else {
            getAmountList.size
        }

    }
}