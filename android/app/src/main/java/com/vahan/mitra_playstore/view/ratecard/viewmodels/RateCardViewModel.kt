package com.vahan.mitra_playstore.view.ratecard.viewmodels

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RateCardViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {

    fun getAllRateCardDetails() = repo.getAllRateCardDetails()


}