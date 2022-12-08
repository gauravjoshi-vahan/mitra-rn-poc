package com.vahan.mitra_playstore.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class LoginModel {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("redirectURL")
    @Expose
    var redirectURL: String? = null

    @SerializedName("userType")
    @Expose
    var userType: String? = null

    @SerializedName("cookie")
    @Expose
    var cookie: Cookie? = null

    class Cookie {
        @SerializedName("accessToken")
        @Expose
        var accessToken: String? = null

        @SerializedName("expiry")
        @Expose
         var expiry: String? = null

        @SerializedName("firebaseToken")
        @Expose
         var firebaseToken: String? = null
    }
}