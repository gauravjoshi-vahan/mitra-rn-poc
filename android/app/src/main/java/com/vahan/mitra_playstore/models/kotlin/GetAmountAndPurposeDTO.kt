package com.vahan.mitra_playstore.models.kotlin


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class GetAmountAndPurposeDTO(
    @Json(name = "amounts")
    val amounts: List<Int>?,
    @Json(name = "defaultInterestRate")
    val defaultInterestRate: Double?,
    @Json(name = "emiAmounts")
    val emiAmounts: List<Int?>?,
    @Json(name = "interestRates")
    val interestRates: List<Double?>?,
    @Json(name = "purposes")
    val purposes: List<Purpose>?,
    @Json(name = "tenure")
    val tenure: List<Tenure?>?
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Purpose(
        @Json(name = "icon")
        val icon: String?,
        @Json(name = "key")
        val key: String?,
        @Json(name = "value")
        val value: String?
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class Tenure(
        @Json(name = "durationType")
        val durationType: String?,
        @Json(name = "value")
        val value: Int?
    )
}