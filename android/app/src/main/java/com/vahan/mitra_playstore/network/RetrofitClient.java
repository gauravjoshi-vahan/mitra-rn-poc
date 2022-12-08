package com.vahan.mitra_playstore.network;


import com.vahan.mitra_android.utils.ConnectivityUtils;
import com.vahan.mitra_playstore.BuildConfig;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.PrefrenceUtils;
import com.vahan.mitra_playstore.view.BaseApplication;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    public static String BASE_URL = Constants.BASE_URL;
    public static String BASE_URL_RAZORPAY = "https://ifsc.razorpay.com";
    public static String BASE_URL_FRESHDESK = "https://vahan-commops.freshdesk.com";

    public OkHttpClient okHttpInterceptor = new OkHttpClient().newBuilder()
           // comment for stg (1)
//            .sslSocketFactory(TrustKit.getInstance().getSSLSocketFactory(Constants.SERVERHOSTNAME),
//                    TrustKit.getInstance().getTrustManager(Constants.SERVERHOSTNAME))
//            .addInterceptor(OkHttp3Helper.getPinningInterceptor())
//            .followRedirects(false)
//            .followSslRedirects(false)
            //till here(1)

            .connectTimeout(120, TimeUnit.MINUTES)
            .readTimeout(120, TimeUnit.MINUTES)
            .writeTimeout(120, TimeUnit.MINUTES)
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader(Constants.AUTHORIZATION, Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(BaseApplication.getContext(), Constants.API_TOKEN))
                        .addHeader(Constants.DEVICEID, PrefrenceUtils.retriveData(BaseApplication.getContext(), Constants.DEVICE_ID))
                        .addHeader(Constants.APP_BUILD, "playstore")
                        .addHeader(Constants.APP_ID, "mitra-play-store-app")
                        .addHeader(Constants.APP_VC, BuildConfig.VERSION_CODE + "")
                        .addHeader(Constants.APP_VERSION, "200")
                        .addHeader(Constants.ACCEPT_LANGUAGE, PrefrenceUtils.retriveLangData(BaseApplication.getContext(), Constants.LANGUAGE_API_RESP).toLowerCase(Locale.ROOT))
                        .build();
                return chain.proceed(request);
            })
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            //comment for stg (2)
//            .hostnameVerifier((hostname, session) -> {
//                HostnameVerifier hv =
//                        HttpsURLConnection.getDefaultHostnameVerifier();
//                return hv.verify(Constants.SERVERHOSTNAME, session);
//            })
            //till here (2)

            //           .addInterceptor(new ChuckInterceptor(Objects.requireNonNull(BaseApplication.getContext())))
//            .addInterceptor(
//                    new ChuckerInterceptor.Builder(BaseApplication.getContext())
//                            .collector(new ChuckerCollector(BaseApplication.getContext()))
//                            .maxContentLength(250000L)
//                            .alwaysReadResponseBody(false)
//                            .build()
//            )
            .build();

    public OkHttpClient okHttpClientOnlyDeviceId = new OkHttpClient().newBuilder()

            //comment for stg (3)
//            .sslSocketFactory(TrustKit.getInstance().getSSLSocketFactory(Constants.SERVERHOSTNAME),
//                    TrustKit.getInstance().getTrustManager(Constants.SERVERHOSTNAME))
//            .addInterceptor(OkHttp3Helper.getPinningInterceptor())
//            .followRedirects(false)
//            .followSslRedirects(false)
            //till here(3)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader(Constants.DEVICEID, PrefrenceUtils.retriveData(BaseApplication.getContext(), Constants.DEVICE_ID))
                        .addHeader(Constants.APP_BUILD, "playstore")
                        .addHeader(Constants.APP_ID, "mitra-play-store-app")
                        .addHeader(Constants.ACCEPT_LANGUAGE, PrefrenceUtils.retriveLangData(BaseApplication.getContext(), Constants.LANGUAGE_API_RESP).toLowerCase(Locale.ROOT))
                        .addHeader(Constants.APP_VC, BuildConfig.VERSION_CODE + "")
                        .addHeader(Constants.APP_VERSION, "200")
                        .build();
                return chain.proceed(request);
            })
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            //           .addInterceptor(new ChuckInterceptor(Objects.requireNonNull(BaseApplication.getContext())))

            //comment for stg (4)
//            .hostnameVerifier((hostname, session) -> {
//                HostnameVerifier hv =
//                        HttpsURLConnection.getDefaultHostnameVerifier();
//                return hv.verify(Constants.SERVERHOSTNAME, session);
//            })
            //till here (4)

            .build();

    public ApiServices getApiRetrofitInterceptor() {
//        if (ConnectivityUtils.INSTANCE.isOnline(Objects.requireNonNull(BaseApplication.getContext()))) {
//            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpInterceptor).build();
//        }
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpInterceptor).build();
        return retrofit.create(ApiServices.class);
    }

    public ApiServices razorPayIntegration() {
        if (ConnectivityUtils.INSTANCE.isOnline(Objects.requireNonNull(BaseApplication.getContext()))) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_RAZORPAY)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpRazorPay).build();
        }
        return retrofit.create(ApiServices.class);
    }

    public ApiServices freshDeskIntegration() {
        if (ConnectivityUtils.INSTANCE.isOnline(Objects.requireNonNull(BaseApplication.getContext()))) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_FRESHDESK)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpFreshDesk).build();
        }
        return retrofit.create(ApiServices.class);
    }

    public ApiServices getApiRetrofitOnlyDeviceId() {
       /* if (ConnectivityUtils.INSTANCE.isOnline(Objects.requireNonNull(BaseApplication.getContext()))) {

        }*/
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClientOnlyDeviceId).build();
        return retrofit.create(ApiServices.class);
    }

    public OkHttpClient okHttpRazorPay = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.MINUTES)
            .readTimeout(120, TimeUnit.MINUTES)
            .writeTimeout(120, TimeUnit.MINUTES)
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader(Constants.AUTHORIZATION, Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(BaseApplication.getContext(), Constants.API_TOKEN))
                        .addHeader(Constants.DEVICEID, PrefrenceUtils.retriveData(BaseApplication.getContext(), Constants.DEVICE_ID))
                        .addHeader(Constants.APP_BUILD, "playstore")
                        .addHeader(Constants.APP_ID, "mitra-play-store-app")
                        .addHeader(Constants.APP_VC, BuildConfig.VERSION_CODE + "")
                        .addHeader(Constants.APP_VERSION, "200")
                        .addHeader(Constants.ACCEPT_LANGUAGE, PrefrenceUtils.retriveLangData(BaseApplication.getContext(), Constants.LANGUAGE_API_RESP).toLowerCase(Locale.ROOT))
                        .build();
                return chain.proceed(request);
            })
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();



    public OkHttpClient okHttpFreshDesk = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.MINUTES)
            .readTimeout(120, TimeUnit.MINUTES)
            .writeTimeout(120, TimeUnit.MINUTES)
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", Constants.AUTHORIZATIONKEYFRESHDESK)
                        .build();
                return chain.proceed(request);
            })
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();
}