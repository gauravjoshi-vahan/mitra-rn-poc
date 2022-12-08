package com.vahan.mitra_playstore.view.refer.viewmodel

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import com.vahan.mitra_playstore.view.refer.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReferralStatusViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {

    fun getReferralCode(referralCode: ReferralCodeReqModel) = repo.getReferralCode(referralCode)
    fun getHomeReferralHomeData(data : ReferralHomeRequestModel) = repo.getHomeReferralHomeData(data)
    fun sentInviteReferral(data : ReferralInviteRequestModel) = repo.sentInviteReferral(data)
    fun getReferralStatusData(data : ReferralHomeRequestModel) = repo.getReferralStatusData(data)
    fun getHomeReferralNewHomeData(data : ReferralHomeRequestModel) = repo.getHomeReferralNewHomeData(data)
    fun getReferralStatusNewData(data : ReferralHomeRequestModel) = repo.getReferralStatusNewData(data)
    fun getReferralMilestoneData(data : ReferralMilestoneRequestModel) = repo.getReferralMilestoneData(data)
}