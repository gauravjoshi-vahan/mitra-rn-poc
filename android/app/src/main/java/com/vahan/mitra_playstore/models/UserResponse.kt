package com.vahan.mitra_playstore.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class UserResponse(

    @SerializedName("redirectURL")
    val redirectURL: String? = null,

    @SerializedName("cookie")
    val cookie: Cookie? = null,

   @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("userType")
    val userType: String? = null
)


data class Cookie(

    @SerializedName("firebaseToken")
    val firebaseToken: String? = null,

    @SerializedName("expiry")
    val expiry: String? = null,

    @SerializedName("accessToken")
    val accessToken: String? = null
)