package com.vahan.mitra_playstore.repository

import com.vahan.mitra_playstore.di.BaseApiService
import com.vahan.mitra_playstore.di.DeviceApiService
import com.vahan.mitra_playstore.models.kotlin.LoginRequest
import com.vahan.mitra_playstore.models.kotlin.SendOtp
import com.vahan.mitra_playstore.network.kotlin.ApiNetwork
import com.vahan.mitra_playstore.utils.toResultFlow
import javax.inject.Inject

class VahanBeforeLoginRepo @Inject constructor(
    @DeviceApiService private val apiNetwork: ApiNetwork
) {

    fun sentOtp(otp: SendOtp) = toResultFlow {
        apiNetwork.sendOtp(otp)
    }

    fun login(loginRequest: LoginRequest) = toResultFlow {
        apiNetwork.login(loginRequest)
    }
}