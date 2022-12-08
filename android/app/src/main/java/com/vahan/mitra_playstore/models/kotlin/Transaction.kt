package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep
import com.google.gson.JsonArray

@Keep
data class Transaction(
    val page:Int?,
    val perPage:Int?,
    val type: String?,
    val filters: List<String>?,
    val sorts: List<String>?
)
