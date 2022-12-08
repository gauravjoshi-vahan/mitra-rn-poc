package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GetPurPosesAndAmountModel(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null,
    @SerializedName("purposes")
    @Expose
    val purposes: List<Purpose>? = null,
    @SerializedName("amounts")
    @Expose val amounts: List<Int>? = null,
    @SerializedName("defaultInterestRate")
    @Expose
    val defaultInterestRate : Double? = null
) {

    data class Purpose(
        @SerializedName("key")
        @Expose val key: String? = null,
        @SerializedName("value")
        @Expose val value: String? = null,
        @SerializedName("icon")
        @Expose
        val icon: String? = null,
        var isSelected: Boolean? = false
    )

}