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



}
