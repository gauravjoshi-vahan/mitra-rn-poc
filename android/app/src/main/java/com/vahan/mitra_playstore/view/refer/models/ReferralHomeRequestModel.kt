package com.vahan.mitra_playstore.view.refer.models

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ReferralHomeRequestModel(
    @Json(name = "limit")
    val limit : String? = null
) {
}