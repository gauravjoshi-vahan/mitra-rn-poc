package com.vahan.mitra_playstore.network.kotlin

import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*
import javax.inject.Inject

class OAuthDeviceInterceptor @Inject constructor(

) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().run {
                    header(Constants.DEVICEID, PrefrenceUtils.retriveData(BaseApplication.context, Constants.DEVICE_ID))
                    header(Constants.APP_BUILD, "playstore")
                    header(Constants.APP_ID, "mitra-play-store-app")
                    header(Constants.ACCEPT_LANGUAGE, PrefrenceUtils.retriveLangData(BaseApplication.context, Constants.LANGUAGE_API_RESP).toLowerCase(Locale.ROOT))
                    header(Constants.APP_VC, ""+ BuildConfig.VERSION_CODE)
                    header(Constants.APP_VERSION, "200")

            build()
        }
        return chain.proceed(request)
    }
}