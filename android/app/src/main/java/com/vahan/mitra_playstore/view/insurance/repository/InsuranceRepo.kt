package com.vahan.mitra_playstore.view.insurance.repository

import com.vahan.mitra_playstore.di.BaseApiService
import com.vahan.mitra_playstore.network.kotlin.ApiNetwork
import com.vahan.mitra_playstore.utils.toResultFlow
import javax.inject.Inject

class InsuranceRepo @Inject constructor(
    @BaseApiService private val apiNetwork: ApiNetwork) {

    fun checkInsuranceEligible() = toResultFlow {
        apiNetwork.getInsuranceDetails()
    }

}