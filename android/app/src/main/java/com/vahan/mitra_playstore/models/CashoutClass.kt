package com.vahan.mitra_playstore.model

import androidx.annotation.Keep

@Keep
data class cashoutClass(
        val amountInPaisa: Int,
        val message: String,
        val success: Boolean
)