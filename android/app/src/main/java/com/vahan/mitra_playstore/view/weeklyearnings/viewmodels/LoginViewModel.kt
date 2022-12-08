package com.vahan.mitra_playstore.view.weeklyearnings.viewmodels

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.models.kotlin.LoginRequest
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {
    fun login(loginObj: LoginRequest) = repo.login(loginObj)
}