package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class Milestones(
    @SerializedName("milestones")
    val milestones: List<Milestone>?
)