package com.vahan.mitra_playstore.view.experiments.savingcalculator.data


// 8208262574

import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class WithDrawRequestModel(
    val newStatus: String
    )
