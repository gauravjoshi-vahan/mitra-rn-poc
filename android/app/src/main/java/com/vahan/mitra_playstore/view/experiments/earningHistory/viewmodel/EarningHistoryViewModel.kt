package com.vahan.mitra_playstore.view.experiments.earningHistory.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EarningHistoryViewModel @Inject constructor(private val repo: VahaanRepository) : ViewModel(){

    fun getWeeklyHistory() = repo.getWeeklyHistory()
}