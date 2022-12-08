package com.vahan.mitra_playstore.view.activities.enternumberactivity.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.models.kotlin.SendOtp
import com.vahan.mitra_playstore.models.kotlin.ValidateReferralRequestModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EnterNumberViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {
        fun sendOTP(number: SendOtp) = repo.sentOtp(number)
        fun validateOtp(number: ValidateReferralRequestModel)  = repo.validateOtp(number)
}