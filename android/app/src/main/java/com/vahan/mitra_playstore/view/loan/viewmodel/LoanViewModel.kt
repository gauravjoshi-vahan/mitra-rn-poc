package com.vahan.mitra_playstore.view.loan.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(private val repository: VahaanRepository) : ViewModel() {

   val getPurpose = repository.getPurPosesAndAmount()
}