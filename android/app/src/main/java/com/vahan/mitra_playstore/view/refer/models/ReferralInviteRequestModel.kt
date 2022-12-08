package com.vahan.mitra_playstore.view.refer.models

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ReferralInviteRequestModel(
    @Json(name = "referralPhoneNumbers")
    val referralPhoneNumbers : List<String>? = null
) {
}