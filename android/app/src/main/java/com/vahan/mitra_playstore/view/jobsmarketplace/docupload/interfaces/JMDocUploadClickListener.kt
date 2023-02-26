package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.interfaces

import com.vahan.mitra_playstore.databinding.JmDocUploadListBinding
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadDTO

interface JMDocUploadClickListener {
    fun uploadDocClickListener(key: String, position : Int, listBinding: JmDocUploadListBinding, item: JMDocUploadDTO.Document)

    fun uploadDocNumberClickListener(key: String, position : Int, listBinding: JmDocUploadListBinding, inputField: JMDocUploadDTO.AdditionalInfo?)
}