package com.vahan.mitra_playstore.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseApiService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RozarPayApiService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiService


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkHttp