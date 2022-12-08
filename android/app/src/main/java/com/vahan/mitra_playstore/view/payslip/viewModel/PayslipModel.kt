package com.vahan.mitra_playstore.view.payslip.viewModel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PayslipModel @Inject constructor(private val repo: VahaanRepository) : ViewModel() {
    fun getPaySlipModel(startPage : String, endPage : String) = repo.getPayslip(startPage,endPage)

}