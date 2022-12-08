package com.vahan.mitra_playstore.view.refer.models

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ReferralMilestoneRequestModel(
    @Json(name = "referredPhoneNumber")
    val referredPhoneNumber : String? = null
) {
}