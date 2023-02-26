package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ArrowKeyMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moengage.core.internal.utils.MoEUtils.getSystemService
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.JmDocUploadListBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.bottomsheet.ShowViewNowDialog
import com.vahan.mitra_playstore.view.documentupload.viewmodel.UploadViewModels
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.interfaces.JMDocUploadClickListener
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.viewmodels.JMUploadViewModel
import kotlinx.android.synthetic.main.fragment_rate_card_new.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class JMDocUploadAdapter(
    private val listener: JMDocUploadClickListener,
    private val context: Context,
    private val activity: ViewModelStoreOwner,
    private val item: List<JMDocUploadDTO.Document?>,
    private val inputFields: ArrayList<JMDocUploadDTO.AdditionalInfo?>,
) : RecyclerView.Adapter<JMDocUploadAdapter.MyViewHolder>() {
    lateinit var viewModel: UploadViewModels
    lateinit var uploadViewModel: JMUploadViewModel
    var docsMap = HashMap<String, String>()
    private var selectedDoc: String = ""

    class MyViewHolder(itemBinding: JmDocUploadListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: JmDocUploadListBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: JmDocUploadListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.jm_doc_upload_list, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        docsMap["drivingLicense"] = "DRIVING_LICENSE"
        docsMap["aadhaarCard"] = "AADHAAR_CARD"
        docsMap["panCard"] = "PAN_CARD"
        docsMap["vehicleRc"] = "VEHICLE_RC"
        docsMap["userSelfie"] = "PROFILE_PIC"
        docsMap["bankPassbookOrCancelledCheque"] = "BANK_DOC"
        viewModel = ViewModelProvider(activity)[UploadViewModels::class.java]
        uploadViewModel = ViewModelProvider(activity)[JMUploadViewModel::class.java]

        var colorVal = R.color.default_200
        if (viewModel.disableAllUploadButtons) {
            colorVal = R.color.light_color_heading
        }
        holder.binding.uploadDoc.isEnabled = !viewModel.disableAllUploadButtons
        holder.binding.uploadDoc.setBackgroundColor(
            ContextCompat.getColor(
                context, colorVal
            )
        )

        if (uploadViewModel.currentDocKey != item[position]?.key) {
            //set default doc statuses
            uploadViewModel.statusArr.clear()
            uploadViewModel.setDefaultDocStatus()
            holder.binding.rvDocStatusItem.adapter =
                JMDocUploadStatusAdapter(context, uploadViewModel.statusArr, "")
        }
        if (item[position]?.componentType == "card") {
            setCardUI(holder, position, "card")
        } else if (item[position]?.componentType == "dropdown") {
            holder.binding.dropdownParent.visibility = View.VISIBLE
            item[holder.adapterPosition]?.dropdownDocs!!.add(0, "Choose one")

            if (uploadViewModel.dropdownDocsMapping.size > 0 && uploadViewModel.dropdownDocsMapping.containsKey(
                    position
                ) && uploadViewModel.dropdownDocsMapping[position] !== ""
            ) {
                item[position]?.key = uploadViewModel.dropdownDocsMapping[position]
                var currentlySelectedDoc = ""
                if (docsMap.filterKeys { it == uploadViewModel.dropdownDocsMapping[position] }.keys.first() !== "") {
                    currentlySelectedDoc =
                        docsMap.filterKeys { it == uploadViewModel.dropdownDocsMapping[position] }.keys.first()
                } else {
                    currentlySelectedDoc = "Choose one"
                    holder.binding.apply {
                        cardHeader.text = item[position]?.header
                        uploadDoc.isEnabled = false
                        uploadDoc.setBackgroundColor(
                            ContextCompat.getColor(
                                context, R.color.light_color_heading
                            )
                        )
                    }
                }
                val docInd =
                    item[holder.adapterPosition]?.dropdownDocs!!.indexOfFirst { it == currentlySelectedDoc }
                setupSpinner(
                    holder, position, uploadViewModel.dropdownDocsMapping[position], docInd
                )
                holder.binding.apply {
                    selectedDocTxt.text = docsMap[uploadViewModel.dropdownDocsMapping[position]]
                }
            }
            if (uploadViewModel.dropdownDocsMapping.size == 0 || !uploadViewModel.dropdownDocsMapping.containsKey(
                    position
                ) || uploadViewModel.dropdownDocsMapping[position] == ""
            ) {
                setupSpinner(holder, position, "", -1)

                holder.binding.apply {
                    cardHeader.text = item[position]?.header
                    uploadDoc.isEnabled = false
                    uploadDoc.setBackgroundColor(
                        ContextCompat.getColor(
                            context, R.color.light_color_heading
                        )
                    )
                }
            }
        }

        setUpInputFields(holder, position)
        clickListeners(holder, position)
        checkAPIData(holder, position)
        checkForDoc(holder, position)
    }

    private fun checkForDoc(holder: MyViewHolder, position: Int) {
        var condition = false
        var conditionArr = ArrayList<Boolean>()
        when (docsMap[item[position]?.key]) {
            "AADHAAR_CARD" -> {
                condition =
                    viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()
            }
            "PAN_CARD" -> {
                condition = viewModel.imagePath.isNotEmpty()
            }
            "DRIVING_LICENSE" -> {
                condition = viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()
            }
            "VEHICLE_RC" -> {
                condition = viewModel.frontRC.isNotEmpty() && viewModel.backRC.isNotEmpty()
            }
            "BANK_DOC" -> {
                condition = viewModel.bankDoc.isNotEmpty()
            }
        }

        if (condition && viewModel.disableAllUploadButtons) {
            holder.binding.uploadDoc.isEnabled = false
            holder.binding.uploadDoc.setBackgroundColor(
                ContextCompat.getColor(
                    context, R.color.light_color_heading
                )
            )
            listener.uploadDocClickListener(
                docsMap[item[position]?.key]!!, position, holder.binding, item[position]!!
            )
        }
    }

    private fun setUpInputFields(holder: MyViewHolder, position: Int) {
        if (inputFields.size > 0) {
            inputFields.forEachIndexed { _, field ->
                if (field?.docKey == item[position]?.key) {
                    holder.binding.apply {
                        inpFieldHeaderTxt.text = "Enter ${item[position]?.header} Number"
                        inpFieldParent.visibility = View.VISIBLE
                        if (!field?.value.equals("") && !field?.value.equals("[]")) {
                            docNumberTxt.setText(field?.value)
                            docNumberSubmit.visibility = View.GONE
                            editInpFieldImg.visibility = View.VISIBLE
                            docNumberTxt.isEnabled = false
                            docNumberTxt.setTextIsSelectable(true);
                            docNumberTxt.isCursorVisible = false;

                            docNumberTxt.setTextColor(ContextCompat.getColor(context, R.color.grey))
                            setInputFieldValues(field?.value ?: "", field?.docKey ?: "", holder)
                        } else {
                            docNumberTxt.setText("")
                            docNumberSubmit.visibility = View.GONE
                            when (field?.docKey) {
                                "panCard" -> {
                                    docNumberTxt.hint = "ABCDE1234F"
                                }
                                "drivingLicense" -> {
                                    docNumberTxt.hint = "MH0120230032897"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setInputFieldValues(inputVal: String, inputField: String, holder: MyViewHolder) {
        if (inputVal != "" && inputField != "") {
            when (inputField) {
                "panCard" -> {
                    viewModel.inputPanNumber = inputVal
                    holder.binding.docNumberTxt.hint = "ABCDE1234F"
                }
                "drivingLicense" -> {
                    viewModel.inputDLNumber = inputVal
                    holder.binding.docNumberTxt.hint = "MH0120230032897"
                }
            }
        }
    }

    private fun setCardUI(holder: MyViewHolder, position: Int, source: String) {
        var whichDoc = ""
        var frontPlaceholderImg = ""
        var backPlaceholderImg = ""

        var cardHeaderTxt = ""
        if (source == "card") {
            whichDoc = item[position]?.key!!
            cardHeaderTxt = item[position]?.header.toString()
            if (item[position]?.isCompleted == true) {
                frontPlaceholderImg = item[position]?.imageUrl ?: ""
                backPlaceholderImg = item[position]?.otherSideImageUrl ?: ""
            } else {
                when (whichDoc) {
                    "aadhaarCard" -> {
                        frontPlaceholderImg = "@drawable/ic_front_aadhar"
                        backPlaceholderImg = "@drawable/ic_aadhar_back"
                    }
                    "panCard" -> {
                        frontPlaceholderImg = "@drawable/ic_pandcard_3x"
                        backPlaceholderImg = ""
                    }
                    "drivingLicense" -> {
                        frontPlaceholderImg = "@drawable/ic_dl_4x"
                        backPlaceholderImg = "@drawable/driving_back"
                    }
                    "vehicleRc" -> {
                        frontPlaceholderImg = "@drawable/ic_rc_front"
                        backPlaceholderImg = "@drawable/ic_rc_back"
                    }
                    "bankPassbookOrCancelledCheque" -> {
                        frontPlaceholderImg = "@drawable/ic_rc_front"
                        backPlaceholderImg = ""
                    }
                }
            }
        } else if (source == "dropdown") {
            holder.binding.cardHeader.text = item[position]?.header
            whichDoc = selectedDoc
//            cardHeaderTxt = selectedDoc
            when (selectedDoc) {
                "aadhaarCard" -> {
                    frontPlaceholderImg = "@drawable/ic_front_aadhar"
                    backPlaceholderImg = "@drawable/ic_aadhar_back"
                }
                "panCard" -> {
                    frontPlaceholderImg = "@drawable/ic_pandcard_3x"
                    backPlaceholderImg = ""
                }
                "drivingLicense" -> {
                    frontPlaceholderImg = "@drawable/ic_dl_4x"
                    backPlaceholderImg = "@drawable/driving_back"
                }
                "vehicleRc" -> {
                    frontPlaceholderImg = "@drawable/ic_rc_front"
                    backPlaceholderImg = "@drawable/ic_rc_back"
                }
                "bankPassbookOrCancelledCheque" -> {
                    frontPlaceholderImg = "@drawable/ic_rc_front"
                    backPlaceholderImg = ""
                }
            }

        }
        holder.binding.apply {
            cardHeader.text = docsMap[whichDoc]
            cardHeader.text = item[position]?.header
            setImageFromStaticSource(frontPlaceholderImg, placeholderImg)
            setImageFromStaticSource(backPlaceholderImg, backSideImg)
            if (item[position]?.key == "aadhaarCard" || item[position]?.key == "drivingLicense" || item[position]?.key == "vehicleRc") {
                backSideImg.visibility = View.VISIBLE
            }
        }
    }

    private fun clickListeners(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            uploadDoc.setOnClickListener {
                clickActionForUploadDoc(holder, docsMap[item[position]?.key], position)
            }

            docViewBtn.setOnClickListener {
                fullScreenView(position)
            }

            docNumberSubmit.setOnClickListener {
                clickActionForUploadDocNumber(holder, position)
            }

            docNumberTxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
                ) {
                    Log.d("beforeTextChanged", "beforeTextChanged:$charSequence")
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    if (editable.toString().isNotEmpty()) {
                        docNumberSubmit.visibility = View.VISIBLE
                    } else {
                        docNumberSubmit.visibility = View.GONE
                    }
                    when (item[position]?.key) {
                        "panCard" -> {
                            viewModel.inputPanNumber = editable.toString()
                        }
                        "drivingLicense" -> {
                            viewModel.inputDLNumber = editable.toString()
                        }
                    }
                }
            })

            editInpFieldImg.setOnClickListener {
//                val variable: KeyListener = docNumberTxt.keyListener
//                docNumberTxt.keyListener = variable;
//                docNumberTxt.isEnabled = true
//                docNumberTxt.isFocusable = true

                docNumberTxt.setTextIsSelectable(false);
                docNumberTxt.isEnabled = true
                docNumberTxt.movementMethod = ArrowKeyMovementMethod.getInstance();
                docNumberTxt.isCursorVisible = true;
                docNumberTxt.isFocusable = true;
                docNumberTxt.isFocusableInTouchMode = true;
                docNumberTxt.isClickable = true;
                docNumberTxt.isLongClickable = true;
                docNumberTxt.requestFocus()
                docNumberTxt.setSelection(docNumberTxt.length())
                docNumberTxt.requestFocus()
                docNumberTxt.postDelayed(Runnable {
                    val keyboard =
                        getSystemService(
                            context,
                            Context.INPUT_METHOD_SERVICE
                        ) as InputMethodManager?
                    keyboard!!.showSoftInput(docNumberTxt, 0)
                }, 200)

                docNumberTxt.setTextColor(ContextCompat.getColor(context, R.color.black))
                docNumberSubmit.visibility = View.VISIBLE
                editInpFieldImg.visibility = View.GONE
            }

        }
    }

    private fun clickActionForUploadDocNumber(holder: MyViewHolder, position: Int) {
        var inputFieldToSend = inputFields[0]
        inputFields.forEachIndexed { index, field ->
            if (field?.docKey == item[position]?.key) {
                inputFieldToSend = inputFields[index]
            }
        }
        listener.uploadDocNumberClickListener(
            item[position]?.key!!, position, holder.binding, inputFieldToSend
        )
    }

    private fun clickActionForUploadDoc(holder: MyViewHolder, key: String?, position: Int) {
        //key will be AADHAR_CARD/ PAN_CARD/ DRIVING_LICENSE/ PROFILE_PIC
        var key = key
        var condition = false
        uploadViewModel.currentDocKey = docsMap[key] ?: ""

        when (key) {
            "AADHAAR_CARD" -> {
                condition =
                    viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()
            }
            "PAN_CARD" -> {
                condition = viewModel.imagePath.isNotEmpty()
            }
            "DRIVING_LICENSE" -> {
                condition = viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()
            }
            "VEHICLE_RC" -> {
                condition = viewModel.frontRC.isNotEmpty() && viewModel.backRC.isNotEmpty()
            }
            "BANK_DOC" -> {
                condition = viewModel.bankDoc.isNotEmpty()
            }
        }
        if (condition) {
            // Dat Fetch
            holder.binding.uploadDoc.isEnabled = false
            holder.binding.uploadDoc.isFocusable = false
            holder.binding.uploadDoc.isClickable = false
            holder.binding.uploadDoc.setBackgroundColor(
                ContextCompat.getColor(
                    context, R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                listener.uploadDocClickListener(key!!, position, holder.binding, item[position]!!)
            }
        } else {
            if (item[position]?.componentType == "dropdown") {
                if ((key == null) || (key == "")) {
                    key = docsMap[selectedDoc]
                    uploadViewModel.dropdownDocsMapping[position] = selectedDoc
                }
            }

            val bundle = Bundle()
            bundle.putString(Constants.ID_TYPE, key)
            Navigation.findNavController(
                holder.binding.jmDocUploadParent
//            ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment, bundle)
            ).navigate(R.id.action_nav_upload_fragment_to_nav_doc_selection_fragment, bundle)
        }
//        }
    }

    private fun setupSpinner(
        holder: MyViewHolder, position: Int, isDocSelectedVal: String?, selectedDocInd: Int
    ) {

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            context, R.layout.spinner_item, item[position]?.dropdownDocs!!
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.binding.apply {
            spinner.adapter = adapter
//          spinner.setSelection(currentPos)
            if (isDocSelectedVal !== "" && selectedDocInd != -1) {
                selectedDocTxt.text = docsMap[isDocSelectedVal]
                spinner.setSelection(
                    selectedDocInd
                )
            } else {
                selectedDocTxt.text = "Choose one"
                spinner.setSelection(
                    0
                )
            }
            selectedDocTxt.setOnClickListener {
                spinner.performClick()
            }
        }

        holder.binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    selectedDoc = item[holder.adapterPosition]?.dropdownDocs!![position]
                    holder.binding.apply {
                        if (item[holder.adapterPosition]?.dropdownDocs!![position] !== "Choose one") {
                            item[holder.adapterPosition]?.key =
                                item[holder.adapterPosition]?.dropdownDocs!![position]
                            uploadViewModel.dropdownDocsMapping[holder.adapterPosition] =
                                item[holder.adapterPosition]?.dropdownDocs!![position]
                            selectedDocTxt.text = docsMap[selectedDoc]
                            if (!viewModel.disableAllUploadButtons) {
                                uploadDoc.isEnabled = true
                                uploadDoc.setBackgroundColor(
                                    ContextCompat.getColor(
                                        context, R.color.default_200
                                    )
                                )
                            }
                        } else {
                            selectedDocTxt.text =
                                item[holder.adapterPosition]?.dropdownDocs!![position]
                            uploadDoc.isEnabled = false
                            uploadDoc.setBackgroundColor(
                                ContextCompat.getColor(
                                    context, R.color.light_color_heading
                                )
                            )
                        }
                    }
                    if (isDocSelectedVal == "" && selectedDocInd == -1) setCardUI(
                        holder, holder.adapterPosition, "dropdown"
                    )
                    else {
                        setCardUI(holder, holder.adapterPosition, "dropdown")
                        updateUI(holder, holder.adapterPosition)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // sometimes you need nothing here
                }
            }
    }

    private fun checkAPIData(holder: MyViewHolder, position: Int) {
        val doc = item[position]
        val imageUrlBackAadhaar: String?
        val imageUrlFrontAadhaar: String?
        val imageUrlPan: String?
        val imageUrlBackDL: String?
        val imageUrlFrontDL: String?
        val imageUrlBackRC: String?
        val imageUrlFrontRC: String?
        val imageUrlBankDoc: String?
        if (doc != null) {
            if (docsMap[doc.key].equals(docsMap["aadhaarCard"], ignoreCase = true)) {
                if (doc.isCompleted == true) {
                    if (doc.imageUrl != null && doc.imageUrl.toString().startsWith("https")) {
                        imageUrlFrontAadhaar = doc.imageUrl
                        PrefrenceUtils.insertData(
                            context, Constants.AADHARCARDFRONT, imageUrlFrontAadhaar
                        )
                    } else {
                        PrefrenceUtils.insertData(
                            context, Constants.AADHARCARDFRONT, ""
                        )
                    }

                    if (doc.otherSideImageUrl != null && doc.otherSideImageUrl.toString()
                            .startsWith("https")
                    ) {
                        imageUrlBackAadhaar = doc.otherSideImageUrl
                        PrefrenceUtils.insertData(
                            context, Constants.AADHARCARDBACK, imageUrlBackAadhaar
                        )
                    } else {
                        PrefrenceUtils.insertData(
                            context, Constants.AADHARCARDBACK, ""
                        )
                    }
                } else {
                    PrefrenceUtils.insertData(
                        context, Constants.AADHARCARDFRONT, ""
                    )
                    PrefrenceUtils.insertData(
                        context, Constants.AADHARCARDBACK, ""
                    )
                }
            }

            if (docsMap[doc.key].equals(docsMap["panCard"], ignoreCase = true)) {
                if (doc.isCompleted == true) {
                    if (doc.imageUrl != null && doc.imageUrl.toString().startsWith("https")) {
                        imageUrlPan = doc.imageUrl
                        PrefrenceUtils.insertData(context, Constants.PANCARDIMAGE, imageUrlPan)
                    } else {
                        PrefrenceUtils.insertData(context, Constants.PANCARDIMAGE, "")
                    }
                } else {
                    PrefrenceUtils.insertData(context, Constants.PANCARDIMAGE, "")
                }
            }

            if (docsMap[doc.key].equals(docsMap["drivingLicense"], ignoreCase = true)) {
                if (doc.isCompleted == true) {
                    if (doc.imageUrl != null && doc.imageUrl.toString().startsWith("https")) {
                        imageUrlFrontDL = doc.imageUrl
                        PrefrenceUtils.insertData(context, Constants.DL_FRONT_IMG, imageUrlFrontDL)
                    } else {
                        PrefrenceUtils.insertData(context, Constants.DL_FRONT_IMG, "")
                    }

                    if (doc.otherSideImageUrl != null && doc.otherSideImageUrl.toString()
                            .startsWith("https")
                    ) {
                        imageUrlBackDL = doc.otherSideImageUrl
                        PrefrenceUtils.insertData(context, Constants.DL_BACK_IMG, imageUrlBackDL)
                    } else {
                        PrefrenceUtils.insertData(context, Constants.DL_BACK_IMG, "")
                    }

                } else {
                    PrefrenceUtils.insertData(context, Constants.DL_BACK_IMG, "")
                    PrefrenceUtils.insertData(context, Constants.DL_FRONT_IMG, "")
                }
            }

            if (docsMap[doc.key].equals(docsMap["vehicleRc"], ignoreCase = true)) {
                if (doc.isCompleted == true) {
                    if (doc.imageUrl != null && doc.imageUrl.toString().startsWith("https")) {
                        imageUrlFrontRC = doc.imageUrl
                        PrefrenceUtils.insertData(
                            context, Constants.RCFRONT, imageUrlFrontRC
                        )
                    } else {
                        PrefrenceUtils.insertData(
                            context, Constants.RCFRONT, ""
                        )
                    }

                    if (doc.otherSideImageUrl != null && doc.otherSideImageUrl.toString()
                            .startsWith("https")
                    ) {
                        imageUrlBackRC = doc.otherSideImageUrl
                        PrefrenceUtils.insertData(
                            context, Constants.RCBACK, imageUrlBackRC
                        )
                    } else {
                        PrefrenceUtils.insertData(
                            context, Constants.RCBACK, ""
                        )
                    }
                } else {
                    PrefrenceUtils.insertData(
                        context, Constants.RCFRONT, ""
                    )
                    PrefrenceUtils.insertData(
                        context, Constants.RCBACK, ""
                    )
                }
            }

            if (docsMap[doc.key].equals(
                    docsMap["bankPassbookOrCancelledCheque"], ignoreCase = true
                )
            ) {
                if (doc.isCompleted == true) {
                    if (doc.imageUrl != null && doc.imageUrl.toString().startsWith("https")) {
                        imageUrlBankDoc = doc.imageUrl
                        PrefrenceUtils.insertData(context, Constants.BANKDOCIMAGE, imageUrlBankDoc)
                    } else {
                        PrefrenceUtils.insertData(context, Constants.BANKDOCIMAGE, "")
                    }
                } else {
                    PrefrenceUtils.insertData(context, Constants.BANKDOCIMAGE, "")
                }
            }
        } else {
            PrefrenceUtils.insertData(context, Constants.AADHARCARDBACK, "")
            PrefrenceUtils.insertData(context, Constants.AADHARCARDFRONT, "")
            PrefrenceUtils.insertData(context, Constants.PANCARDIMAGE, "")
            PrefrenceUtils.insertData(context, Constants.DL_BACK_IMG, "")
            PrefrenceUtils.insertData(context, Constants.DL_FRONT_IMG, "")
            PrefrenceUtils.insertData(context, Constants.RCFRONT, "")
            PrefrenceUtils.insertData(context, Constants.RCBACK, "")
            PrefrenceUtils.insertData(context, Constants.BANKDOCIMAGE, "")
        }
        updateUI(holder, position)
    }

    private fun updateUI(holder: MyViewHolder, position: Int) {
        val cacheAadharCardFront = PrefrenceUtils.retriveData(context, Constants.AADHARCARDFRONT)
        val cacheAadharCardBack = PrefrenceUtils.retriveData(context, Constants.AADHARCARDBACK)
        val cachePanCard = PrefrenceUtils.retriveData(context, Constants.PANCARDIMAGE)
        val cacheDLFront = PrefrenceUtils.retriveData(context, Constants.DL_FRONT_IMG)
        val cacheDLBack = PrefrenceUtils.retriveData(context, Constants.DL_BACK_IMG)
        val cacheRCFront = PrefrenceUtils.retriveData(context, Constants.RCFRONT)
        val cacheRCBack = PrefrenceUtils.retriveData(context, Constants.RCBACK)
        val cacheBankDoc = PrefrenceUtils.retriveData(context, Constants.BANKDOCIMAGE)

        var condition1 = false
        var condition2 = false
        var condition3 = false

        var viewModelFrontImage = ""
        var viewModelBackImage = ""
        var cacheFrontImage = ""
        var cacheBackImage = ""

        when (docsMap[item[position]?.key]) {
            "AADHAAR_CARD" -> {
                condition1 =
                    (cacheAadharCardBack.isNotEmpty() && cacheAadharCardFront.isNotEmpty() || viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty())
                condition2 =
                    (viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty())
                condition3 = (cacheAadharCardBack.isNotEmpty() && cacheAadharCardFront.isNotEmpty())
                viewModelFrontImage = viewModel.frontAdhaarCard
                viewModelBackImage = viewModel.backAdhaarCard
                cacheFrontImage = cacheAadharCardFront
                cacheBackImage = cacheAadharCardBack
            }
            "PAN_CARD" -> {
                condition1 = (cachePanCard.isNotEmpty() || viewModel.imagePath.isNotEmpty())
                condition2 = (viewModel.imagePath.isNotEmpty())
                condition3 = (cachePanCard.isNotEmpty())
                viewModelFrontImage = viewModel.imagePath
                cacheFrontImage = cachePanCard
            }
            "DRIVING_LICENSE" -> {
                condition1 =
                    ((cacheDLFront.isNotEmpty() && cacheDLBack.isNotEmpty()) || viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty())
                condition2 =
                    (viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty())
                condition3 = (cacheDLBack.isNotEmpty() && cacheDLFront.isNotEmpty())
                viewModelFrontImage = viewModel.frontDLCard
                viewModelBackImage = viewModel.backDLCard
                cacheFrontImage = cacheDLFront
                cacheBackImage = cacheDLBack
            }
            "VEHICLE_RC" -> {
                condition1 =
                    (cacheRCBack.isNotEmpty() && cacheRCFront.isNotEmpty() || viewModel.frontRC.isNotEmpty() && viewModel.backRC.isNotEmpty())
                condition2 = (viewModel.frontRC.isNotEmpty() && viewModel.backRC.isNotEmpty())
                condition3 = (cacheRCBack.isNotEmpty() && cacheRCFront.isNotEmpty())
                viewModelFrontImage = viewModel.frontRC
                viewModelBackImage = viewModel.backRC
                cacheFrontImage = cacheRCFront
                cacheBackImage = cacheRCBack
            }
            "BANK_DOC" -> {
                condition1 = (cacheBankDoc.isNotEmpty() || viewModel.bankDoc.isNotEmpty())
                condition2 = (viewModel.bankDoc.isNotEmpty())
                condition3 = (cacheBankDoc.isNotEmpty())
                viewModelFrontImage = viewModel.bankDoc
                cacheFrontImage = cacheBankDoc
            }
        }
        if (condition1) {
            // Positive Flow...
            if (condition2) {
                holder.binding.placeholderImg.let {
                    Glide.with(context).load(viewModelFrontImage).into(it)
                }
                if (item[position]?.key == "aadhaarCard" || item[position]?.key == "drivingLicense" || item[position]?.key == "vehicleRc") {
                    holder.binding.backSideImg.let {
                        Glide.with(context).load(viewModelBackImage).into(it)
                    }
                }
            } else if (condition3) {
                holder.binding.placeholderImg.let {
                    Glide.with(context).load(cacheFrontImage).into(it)
                }
                if (item[position]?.key == "aadhaarCard" || item[position]?.key == "drivingLicense" || item[position]?.key == "vehicleRc") {
                    holder.binding.backSideImg.let {
                        Glide.with(context).load(cacheBackImage).into(it)
                    }
                }
                holder.binding.uploadDoc.visibility = View.GONE
                holder.binding.docViewBtn.visibility = View.VISIBLE
                holder.binding.missingDoc.text = context.getString(R.string._success)
                holder.binding.uploadStatusFrameLayout.setBackgroundResource(R.drawable.green_rect)
                holder.binding.missingDoc.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_green_tick2, 0, 0, 0
                )
                holder.binding.missingDoc.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.green
                    )
                )
                holder.binding.missingDoc.compoundDrawablePadding = 16
                holder.binding.rvDocStatusItem.adapter = JMDocUploadStatusAdapter(
                    context,
                    uploadViewModel.setStatusArr("verify_success", item[position]?.key.toString()),
                    item[position]?.key.toString()
                )
            }
        } else if (uploadViewModel.currentDocKey == item[position]?.key) {
            holder.binding.rvDocStatusItem.adapter = JMDocUploadStatusAdapter(
                context, uploadViewModel.setStatusArr(
                    "verify_failure", item[position]?.key ?: ""
                ), item[position]?.key ?: ""
            )
        }
    }

    private fun fullScreenView(position: Int) {
        var cacheFront = ""
        var cacheBack = ""
        var viewModelFront = ""
        var viewModelBack = ""

        var condition1 = false
        var condition2 = false

        when (docsMap[item[position]?.key]) {
            "AADHAAR_CARD" -> {
                cacheFront = PrefrenceUtils.retriveData(context, Constants.AADHARCARDFRONT)
                cacheBack = PrefrenceUtils.retriveData(context, Constants.AADHARCARDBACK)
                viewModelFront = viewModel.frontAdhaarCard
                viewModelBack = viewModel.backAdhaarCard
            }
            "PAN_CARD" -> {
                cacheFront = PrefrenceUtils.retriveData(context, Constants.PANCARDIMAGE)
                viewModelFront = viewModel.imagePath
            }
            "DRIVING_LICENSE" -> {
                cacheFront = PrefrenceUtils.retriveData(context, Constants.DL_FRONT_IMG)
                cacheBack = PrefrenceUtils.retriveData(context, Constants.DL_BACK_IMG)
                viewModelFront = viewModel.frontDLCard
                viewModelBack = viewModel.backDLCard
            }
            "VEHICLE_RC" -> {
                cacheFront = PrefrenceUtils.retriveData(context, Constants.RCFRONT)
                cacheBack = PrefrenceUtils.retriveData(context, Constants.RCBACK)
                viewModelFront = viewModel.frontRC
                viewModelBack = viewModel.backRC
            }
            "BANK_DOC" -> {
                cacheFront = PrefrenceUtils.retriveData(context, Constants.BANKDOCIMAGE)
                viewModelFront = viewModel.bankDoc
            }
        }

        if (item[position]?.key == "aadhaarCard" || item[position]?.key == "drivingLicense" || item[position]?.key == "vehicleRc") {
            condition1 = (cacheFront.isNotEmpty() && cacheBack.isNotEmpty())
            condition2 = viewModelFront.isNotEmpty() && viewModelBack.isNotEmpty()
        } else {
            condition1 = cacheFront.isNotEmpty()
            condition2 = viewModelFront.isNotEmpty()
        }

        if (condition1) {
            ShowViewNowDialog(
                context, arrayOf(cacheFront, cacheBack)
            ).show()
        } else if (condition2) {
            ShowViewNowDialog(
                context, arrayOf(viewModelFront, viewModelBack)
            ).show()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setImageFromStaticSource(imageSrc: String, imageView: ImageView) {
        val resID: Int = context.resources.getIdentifier(imageSrc, "drawable", context.packageName)
        imageView.setImageResource(resID)
    }

    override fun getItemCount(): Int {
        return item.size ?: 0
    }
}