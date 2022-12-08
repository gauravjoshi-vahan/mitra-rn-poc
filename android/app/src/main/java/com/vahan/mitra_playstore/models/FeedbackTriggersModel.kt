package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FeedbackTriggersModel(@SerializedName("triggers")
                            @Expose
                            val trigger: List<FeedBackHandler>? = null
) {
    data class FeedBackHandler(
        @SerializedName("trigger_event")
        @Expose
        val trigger_event: String? = null,

    )
}
