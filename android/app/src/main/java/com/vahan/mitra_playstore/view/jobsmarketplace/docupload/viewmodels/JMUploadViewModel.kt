package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.viewmodels

import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.repository.VahaanRepository
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocInputFieldPostDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadStatusDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JMUploadViewModel @Inject constructor(private val repo : VahaanRepository) : ViewModel() {
    var dropdownDocsMapping = HashMap<Int, String>()
    var statusArr = ArrayList<JMDocUploadStatusDTO>()
    var successStatusArr = ArrayList<JMDocUploadStatusDTO>()
    var currentDocKey: String = ""
    var docsMap = HashMap<String, String>()

    fun getDocuments(siId: String) = repo.getJMUploadDocuments(siId)

    fun setDefaultDocStatus(){
        val status1 = JMDocUploadStatusDTO("pending", "Uploaded")
        val status2 = JMDocUploadStatusDTO("pending", "Sent for Verification")
        val status3 = JMDocUploadStatusDTO("pending", "Verified")
        statusArr.add(status1)
        statusArr.add(status2)
        statusArr.add(status3)
    }

    public fun setStatusArr(inp: String, imageType: String): ArrayList<JMDocUploadStatusDTO> {
        docsMap["drivingLicense"] = "Driving License"
        docsMap["aadhaarCard"] = "Aadhaar Card"
        docsMap["panCard"] = "PAN Card"
        docsMap["vehicleRc"] = "Vehicle RC"
        docsMap["userSelfie"] = "Profile Pic"
        docsMap["bankPassbookOrCancelledCheque"] = "Bank Doc"

        successStatusArr.clear()
        var status1 = JMDocUploadStatusDTO("pending", "Uploaded")
        var status2 = JMDocUploadStatusDTO("pending", "Sent for verification")
        var status3 = JMDocUploadStatusDTO("pending", "Verified")

//        successStatusArr.forEachIndexed { index, statusItem ->
//            if (inp == "upload_success") {
//            }
//        }
        if (inp == "upload_success") {
            status1 = JMDocUploadStatusDTO("success", "Uploaded")
            status2 = JMDocUploadStatusDTO("success", "Sent for verification")
            status3 = JMDocUploadStatusDTO("pending", "Verified")
        }
        if (inp == "upload_failure") {
            status1 = JMDocUploadStatusDTO("failed", "Uploaded")
            status2 = JMDocUploadStatusDTO("pending", "Sent for verification")
            status3 = JMDocUploadStatusDTO("pending", "${docsMap[imageType]} upload failed")
        }
        if (inp == "verify_success") {
            status1 = JMDocUploadStatusDTO("success", "Uploaded")
            status2 = JMDocUploadStatusDTO("success", "Sent for verification")
            status3 = JMDocUploadStatusDTO("success", "Verified")
        }
        if (inp == "verify_failure") {
            status1 = JMDocUploadStatusDTO("success", "Uploaded")
            status2 = JMDocUploadStatusDTO("success", "Sent for verification")
            status3 = JMDocUploadStatusDTO("failed", "${docsMap[imageType]} verification failed")
        }

        successStatusArr.add(status1)
        successStatusArr.add(status2)
        successStatusArr.add(status3)

        return successStatusArr
    }

    public fun postDocInputField(inpData: JMDocInputFieldPostDTO) = repo.postDocInpField(inpData)

}