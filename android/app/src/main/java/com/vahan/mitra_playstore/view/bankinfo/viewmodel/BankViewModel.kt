package com.vahan.mitra_playstore.view.bankinfo.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.models.kotlin.Account
import com.vahan.mitra_playstore.repository.VahaanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class BankViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {
    fun createAccount(account: Account) = repo.createAccount(account)

}