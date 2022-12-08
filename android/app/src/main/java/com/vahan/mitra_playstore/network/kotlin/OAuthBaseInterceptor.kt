package com.vahan.mitra_playstore.network.kotlin

import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import com.vahan.mitra_playstore.view.BaseApplication.Companion.context
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*
import javax.inject.Inject

class OAuthBaseInterceptor @Inject constructor(

) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().run {
            header(
                "Authorization",
                Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(context, Constants.API_TOKEN))
            header("device-id", PrefrenceUtils.retriveData(context, Constants.DEVICE_ID))
            header("app-build", "playstore")
            header("app-id", "mitra-play-store-app")
            header("app-vc", "${BuildConfig.VERSION_CODE}")
            header("app-version", "200")
            header("Accept-Language",
                PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE_API_RESP).lowercase(
                    Locale.ROOT
                )
            )

            build()
        }



        return chain.proceed(request)
    }
}