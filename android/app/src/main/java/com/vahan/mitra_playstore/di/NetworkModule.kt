package com.vahan.mitra_playstore.di
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vahan.mitra_playstore.network.kotlin.ApiNetwork
import com.vahan.mitra_playstore.network.kotlin.OAuthBaseInterceptor
import com.vahan.mitra_playstore.network.kotlin.OAuthDeviceInterceptor
import com.vahan.mitra_playstore.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    var BASE_URL = Constants.BASE_URL
    var BASE_URL_RAZORPAY = "https://ifsc.razorpay.com"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi
        .Builder()
        .run {
            add(KotlinJsonAdapterFactory())
                .build()
        }

    @Provides
    @Singleton
    @BaseApiService
    fun provideBaseApiService(
        moshi: Moshi,
        @BaseOkHttp client: okhttp3.OkHttpClient,
    ): ApiNetwork =
        Retrofit.Builder()
            .run {
                baseUrl(BASE_URL)
                addConverterFactory(MoshiConverterFactory.create(moshi))
                client(client)
                build()
            }.create(ApiNetwork::class.java)

    @Provides
    @Singleton
    @RozarPayApiService
    fun provideRozarPayApiService(
        moshi: Moshi,
        @BaseOkHttp client: okhttp3.OkHttpClient,
    ): ApiNetwork =
        Retrofit.Builder()
            .run {
                baseUrl(BASE_URL_RAZORPAY)
                addConverterFactory(MoshiConverterFactory.create(moshi))
                client(client)
                build()
            }.create(ApiNetwork::class.java)

    @Provides
    @Singleton
    @DeviceApiService
    fun provideDeviceApiService(
        moshi: Moshi,
        @DeviceOkHttp client: okhttp3.OkHttpClient,
    ): ApiNetwork =
        Retrofit.Builder()
            .run {
                baseUrl(BASE_URL)
                addConverterFactory(MoshiConverterFactory.create(moshi))
                client(client)
                build()
            }.create(ApiNetwork::class.java)


    @Provides
    @Singleton
    @BaseOkHttp
    fun provideBaseOkHttpClient(oAuthBaseInterceptor: OAuthBaseInterceptor): okhttp3.OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return okhttp3.OkHttpClient().newBuilder()
            .run {

                //comment for stg (1)
//                sslSocketFactory(
//                    TrustKit.getInstance().getSSLSocketFactory(Constants.SERVERHOSTNAME),
//                    TrustKit.getInstance().getTrustManager(Constants.SERVERHOSTNAME)
//                )
//                addInterceptor(OkHttp3Helper.getPinningInterceptor())
//                followRedirects(false)
//                followSslRedirects(false)

                //till here (1)

                connectTimeout(120, TimeUnit.MINUTES)
                readTimeout(120, TimeUnit.MINUTES)
                writeTimeout(120, TimeUnit.MINUTES)
                addInterceptor(oAuthBaseInterceptor)
//                addInterceptor(ChuckInterceptor(BaseApplication.context))
//                    .addInterceptor(
//                        ChuckerInterceptor.Builder(BaseApplication.context!!)
//                            .collector(ChuckerCollector(BaseApplication.context!!))
//                            .maxContentLength(250000L)
//                            .alwaysReadResponseBody(false)
//                            .build()
//                    )
                addInterceptor(interceptor)

                    //comment for stg (2)
//                    .hostnameVerifier { _, p1 ->
//                        val hv = HttpsURLConnection.getDefaultHostnameVerifier()
//                        hv.verify(Constants.SERVERHOSTNAME, p1)
//                    }
//                //till here (2)

                build()
            }
    }


    @Provides
    @Singleton
    @DeviceOkHttp
    fun provideDeviceOkHttpClient(oAuthDeviceInterceptor: OAuthDeviceInterceptor): okhttp3.OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return okhttp3.OkHttpClient().newBuilder()
            .run {

                //comment for stg (3)
//                sslSocketFactory(
//                    TrustKit.getInstance().getSSLSocketFactory(Constants.SERVERHOSTNAME),
//                    TrustKit.getInstance().getTrustManager(Constants.SERVERHOSTNAME)
//                )
//                addInterceptor(OkHttp3Helper.getPinningInterceptor())
//                followRedirects(false)
//                followSslRedirects(false)
                //till here (3)

                connectTimeout(120, TimeUnit.MINUTES)
                readTimeout(120, TimeUnit.MINUTES)
                writeTimeout(120, TimeUnit.MINUTES)
                addInterceptor(oAuthDeviceInterceptor)
                //addInterceptor(ChuckInterceptor(BaseApplication.context))
//                    .addInterceptor(
//                        ChuckerInterceptor.Builder(BaseApplication.context!!)
//                            .collector(ChuckerCollector(BaseApplication.context!!))
//                            .maxContentLength(250000L)
//                            .alwaysReadResponseBody(false)
//                            .build()
//                    )
                addInterceptor(interceptor)

                //comment for stg (4)
//                hostnameVerifier { _, p1 ->
//                    val hv = HttpsURLConnection.getDefaultHostnameVerifier()
//                    hv.verify(Constants.SERVERHOSTNAME, p1)
//                }

                //till here (4)

                build()
            }
    }
}