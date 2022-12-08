package com.vahan.mitra_playstore.view.payslip.view.ui.adapter

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.RequestBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.PayslipItemRowBinding
import com.vahan.mitra_playstore.models.PayslipDataModel
import com.vahan.mitra_playstore.models.kotlin.PayslipDTO
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter


class PayslipAdapter(
    private val paySlip: ArrayList<PayslipDTO.Payslip>?,
    private val icon: String?
) : RecyclerView.Adapter<PayslipAdapter.MyViewHolder>() {

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var fb: FirebaseAnalytics

    class MyViewHolder(
        itemBinding: PayslipItemRowBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val binding: PayslipItemRowBinding = itemBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemBinding: PayslipItemRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.payslip_item_row, parent, false
        )
        view = itemBinding.root
        context = itemBinding.root.context
        fb = FirebaseAnalytics.getInstance(context)
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            tvKey.text = paySlip?.get(position)?.dateLabel
            dateTv.text = paySlip?.get(position)?.dateTime

            val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
                .`as`<PictureDrawable>(PictureDrawable::class.java)
                .error(R.drawable.dialog_icon)
                .listener(SvgSoftwareLayerSetter())
            val uri = Uri.parse(icon)
            requestBuilder.load(uri).into(ivViewBankDetails)

            helpAndSettingContainer.setOnClickListener {
                val bundle = Bundle()
                fb.logEvent(Constants.PAYSLIP_VIEWED, bundle)
                val properties = Properties()
                MoEHelper.getInstance(context).trackEvent(Constants.PAYSLIP_VIEWED, properties)
                BlitzLlamaSDK.getSdkManager(context).triggerEvent(Constants.PAYSLIP_VIEWED)

                if (paySlip!![position].paySlipUrl != null) {
                    val bundle = Bundle()
                    bundle.putString(Constants.HEADING, paySlip[position].dateLabel)
                    bundle.putString(Constants.REDIRECTION_URL, paySlip[position].paySlipUrl)
                    Navigation.findNavController(tvKey).navigate(R.id.nav_payslip_view, bundle)
                } else {
                    Toast.makeText(context, context.getString(R.string.no_records), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return paySlip?.size ?: 0
    }
}