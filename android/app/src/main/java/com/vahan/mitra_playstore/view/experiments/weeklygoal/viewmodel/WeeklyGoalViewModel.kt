package com.vahan.mitra_playstore.view.experiments.weeklygoal.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeeklyGoalViewModel @Inject constructor(private val repo: VahaanRepository) : ViewModel(){

    fun showSaveGoal(reqParm: String) = repo.showSaveGoal(reqParm)
}