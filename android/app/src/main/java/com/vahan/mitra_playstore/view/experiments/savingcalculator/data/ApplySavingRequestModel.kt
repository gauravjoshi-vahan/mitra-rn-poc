package com.vahan.mitra_playstore.view.experiments.savingcalculator.data

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class ApplySavingRequestModel(
    var amount : Int,
    var frequency : String
)