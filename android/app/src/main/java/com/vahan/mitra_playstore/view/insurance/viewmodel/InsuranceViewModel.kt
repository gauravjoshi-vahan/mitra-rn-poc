package com.vahan.mitra_playstore.view.insurance.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.view.insurance.repository.InsuranceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class InsuranceViewModel @Inject constructor(private val repo : InsuranceRepo) : ViewModel(){

    val checkForInsuranceEligible = repo.checkInsuranceEligible()

}