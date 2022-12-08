package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class InsuranceDetails(
    val insuranceEndDate: String?,
    val insuranceStartDate: String?,
    val premium: Int?,
    val sumAssured: Int?
)