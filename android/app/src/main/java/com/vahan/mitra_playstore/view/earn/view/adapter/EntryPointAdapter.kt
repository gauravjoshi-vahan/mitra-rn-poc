package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.EntryPoint2ItemBinding
import com.vahan.mitra_playstore.models.kotlin.BannerListDataModelNew
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.earn.view.ui.CashoutPurposeFragment
import com.vahan.mitra_playstore.view.earn.model.Purpose
import java.util.ArrayList

class EntryPointAdapter(
    private val context: Context,
    val list: ArrayList<BannerListDataModelNew.DynamicEntryPoint>
) : RecyclerView.Adapter<EntryPointAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: EntryPoint2ItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: EntryPoint2ItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: EntryPoint2ItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.entry_point2_item, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.binding.apply{
            Glide
                .with(context)
                .load(list[position].imageUrl)
                .placeholder(R.drawable.ic_app_icon)
                .into(this.ivGraph)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

