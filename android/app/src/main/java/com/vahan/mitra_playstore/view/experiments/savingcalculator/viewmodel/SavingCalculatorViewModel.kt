package com.vahan.mitra_playstore.view.experiments.savingcalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.ApplySavingRequestModel
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.WithDrawRequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavingCalculatorViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {

    fun getFetchDetails() = repo.getFetchDetails()
    fun withDrawAmount(data: WithDrawRequestModel) = repo.withDrawAmount(data)
    fun applySaving(data: ApplySavingRequestModel) = repo.applySaving(data)

}