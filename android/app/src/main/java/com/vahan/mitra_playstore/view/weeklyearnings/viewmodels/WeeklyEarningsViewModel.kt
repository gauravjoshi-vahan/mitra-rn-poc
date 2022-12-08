package com.vahan.mitra_playstore.view.weeklyearnings.viewmodels

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeeklyEarningsViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {

    fun getWeeklyEarnings(startDate: String, endDate: String) = repo.getWeeklyEarnings(startDate, endDate)

    fun getWeeklyPayslips(startDate: String, endDate: String) = repo.getWeeklyPayslips(startDate, endDate)

    fun getDailyOrder(date: String, company: String) = repo.getDailyOrder(date, company)
}