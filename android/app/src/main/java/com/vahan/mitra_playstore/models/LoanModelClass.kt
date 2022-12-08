package com.vahan.mitra_playstore.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LoanModelClass(

    @SerializedName("EC") var EC: EC? = EC(),
    @SerializedName("EW") var EW: EW? = EW(),
    @SerializedName("NE") var NE: NE? = NE(),
    @SerializedName("E") var E: E? = E(),
    @SerializedName("LA") var LA: LA? = LA()

)

data class EC(

    @SerializedName("view") var view: Boolean? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("title_hi") var titleHi: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("hi_desc") var hiDesc: String? = null,
    @SerializedName("button") var button: Int? = null,
    @SerializedName("button_text") var buttonText: String? = null,
    @SerializedName("button_text_hi") var buttonTextHi: String? = null

)

data class EW(

    @SerializedName("view") var view: Boolean? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("title_hi") var titleHi: String? = null,
    @SerializedName("hi_desc") var hiDesc: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("desc_hi") var descHi: String? = null,
    @SerializedName("button") var button: Int? = null,
    @SerializedName("button_text") var buttonText: String? = null,
    @SerializedName("button_text_hi") var buttonTextHi: String? = null

)

data class NE(

    @SerializedName("view") var view: Boolean? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("title_hi") var titleHi: String? = null,
    @SerializedName("desc") var desc: String? = null,
    @SerializedName("desc_hi") var descHi: String? = null,
    @SerializedName("button") var button: Int? = null,
    @SerializedName("button_text") var buttonText: String? = null,
    @SerializedName("button_text_hi") var buttonTextHi: String? = null

)

data class E(

    @SerializedName("view") var view: Boolean? = null,
    @SerializedName("tnc_url") var tncUrl: String? = null,
    @SerializedName("claim_desc") var claimDesc: String? = null,
    @SerializedName("claim_desc_hi") var claimDescHi: String? = null,
    @SerializedName("help_desc") var helpDesc: String? = null,
    @SerializedName("help_desc_hi") var helpDescHi: String? = null,
    @SerializedName("claim_button") var claimButton: Int? = null,
    @SerializedName("claim_phone") var claimPhone: String? = null,
    @SerializedName("help_button") var helpButton: Int? = null,
    @SerializedName("help_phone") var helpPhone: String? = null,
    @SerializedName("custom_amount") var customAmount: Boolean? = false

)

data class LA(
    @SerializedName("view")
    @Expose
    val view: Boolean? = null,

    @SerializedName("doc_url")
    @Expose
    val docUrl: String? = null,

    @SerializedName("button_one_desc")
    @Expose
    val buttonOneDesc: String? = null,

    @SerializedName("button_one_desc_hi")
    @Expose
    val buttonOneDescHi: String? = null,

    @SerializedName("button_two_desc")
    @Expose
    val buttonTwoDesc: String? = null,

    @SerializedName("button_two_desc_hi")
    @Expose
    val buttonTwoDescHi: String? = null,

    @SerializedName("button_one")
    @Expose
    val buttonOne: Int? = null,

    @SerializedName("button_one_text_hi")
    @Expose
    val buttonOneTextHi: String? = null,

    @SerializedName("button_one_text_en")
    @Expose
    val buttonOneTextEn: String? = null,

    @SerializedName("button_two_text_hi")
    @Expose
    val buttonTwoTextHi: String? = null,

    @SerializedName("button_two_text_en")
    @Expose
    val buttonTwoTextEn: String? = null,

    @SerializedName("button_one_phone")
    @Expose
    val buttonOnePhone: String? = null,

    @SerializedName("button_two")
    @Expose
    val buttonTwo: Int? = null,

    @SerializedName("button_two_phone")
    @Expose
    val buttonTwoPhone: String? = null,
)