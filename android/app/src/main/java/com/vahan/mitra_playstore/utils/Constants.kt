package com.vahan.mitra_playstore.utils

object Constants {

    const val WEEKLY_EARNING: String = "weekly_earnings"
    const val WEEKLY_EARNING_GOALS : String = "weeklyGoal"
    const val WEEKLY_EARNING_GRAPHS : String = "weeklyGraph"
    const val DEEPLINK : String = "https://play.google.com/store/apps/details?id=com.vahan.mitra_playstore="
    const val DOMAIN_URL_PREFIX : String = "https://mitraapp.page.link"
    const val RATE_CARD_VIEWED : String = "rate_card_viewed"
    const val SAVINGPAGEVIEWED : String = "savings_page_viewed"
    const val WITHDRAW: String = "WITHDRAWAL"
    const val SUCCESS: String = "SUCCESS"
    const val WEEKLY_PAGE_PAYOUT_CLICKED: String = "weekly_page_payout_clicked"
    const val VIEW_ORDER_CLICKED: String = "view_orders_clicked"
    const val TAPPED_DAILY_EARNINGS_BREAKUP: String = "tapped_daily_earnings_breakup"
    const val PAYSLIP_TAPPED: String = "payslip_tapped"
    const val WEEKLY_PAYOUT_VIEWED: String = "weekly_payout_viewed"
    const val EARNING_REPORT_VIEWED: String = "earnings_report_viewed"
    const val RIDER_WEEKLY_GOAL: String = "rider_weekly_goal"
    const val ISPANCARD: String = "IS_PAN_CARD"
    const val DL_FRONT_IMG: String = "DL_FRONT_IMG"
    const val DL_BACK_IMG: String = "DL_BACK_IMG"
    const val NUDGE_ICON_TEXT: String = "nudge_icon_text"
    const val NUDGE_ICON_TEXT_REMOTE_CONFIG: String = "nudge_icon_text_r_c"
    const val LEARN_MORE_EVENT: String = "cross_util_interested"
    const val CHECKDEVICEDETAILSONCES: String = "check_sms_info_details"
    const val CHECKSMSONCES: String = "check_device_info_details"
    const val SERVERHOSTNAME: String = "api.mitra.vahan.co"
    const val USERID: String = "userId"
    val REDIRECTION_URL: String = "url"
    val WEBVIEW_HANDLER_MODIFY: String = "webview_handler_modified"
    val WEBVIEW_HANDLER_REMOTE_CONFIG_MODIFY: String = "webview_handler_remote_config_modify"
    val FEEDBACK_TRIGGERS :String = "feedback_triggers"
    const val  FEEDBACK_TRIGGERS_REMOTE : String = "triggers_remote"
    val CHECKFORPAYROLL: String = "payroll"
    const val LANGUAGE = "language"
    const val LANGUAGE_API_RESP = "language_api"
    const val API_TOKEN = "TOKEN"
    const val AADHARCARDFRONT = "aadharcardfront"
    const val AADHARCARDBACK = "aadharcardback"
    const val PROFILEPHOTO = "profilephoto"
    const val TOKENCONSTANT = "Bearer "
    const val CHECKFORFIRSTTIME = "first"
    const val ACTIVITY_COACHMARKS = "activity_coachmarks"
    const val ADAPTER_COACHMARKS = "adapter_coachmarks"
    const val CHECKFORFIRSTTIMESLOTSCREEN = "first_slot_screen"
    const val UPDATE_LOCAL_USER_DATA = "user_data"
    const val CHECKFORVERSIONUPDATE = "check_for_version_update"
    const val HASHKEY = "hashkey"
    const val PRICE = "price"
    const val EPRICE = "Eprice"
    const val IFSCCODEKEY = "ifsc"
    const val EARNINGID = "id"
    const val DATABASETABLE_ONE = "NotificationInformation"
    const val DATABASETABLE_TWO = "LocalInfoMessNoti"
    const val DATABASE_NAME = "db_notification"
    const val DEVICE_ID = "device_id"
    const val CURRENT_NOTIFICATION_COUNT = "notification_count"
    const val PACKAGE_NAME = "package_name"
    const val MESSAGE = "message"
    const val ID_TYPE = "aadharback"
    const val IS_BOTTOM_FIRST_TIME = "aadharback"
    const val PANCARDIMAGE = "pancardverified"
    const val INSTALLED_APP_APPROVED = "installed_app_approved"
    const val NOTIFICATION_ASKED = "notification_asked"
    const val TEST_USER = "test_user"
    const val USERNAME = "user_name"
    const val PHONENUMBER = "phone_number"
    const val PHONE_NUMBER = "phoneNumber"
    const val CLICKED_ACTION = "clicked_action"
    const val NOTIFICATION_ID = "notification_id"
    const val INSURANCE_ELIGIBILITY = "insurance_eligibility"
    const val CHECK_BANK_STATUS = "check_back_status"
    const val CHECK_UPLOAD_STATUS = "check_upload_status"
    const val SHOWN_VERSION = "shown_version"
    const val LINK = "link"
    const val REFERRAL_CODE = "referralCode"
    const val REFERRAL_AMOUNT = "amount_earnable"
    const val DOLLAR_SYMBOL = "$"
    const val REFERRAL_COD:String = "AAZXO"
    const val AUTHORIZATIONKEYFRESHDESK = "enlHR0lmRzNNN0VBRldKS2VycQ=="

    @JvmField
    var CheckUpdateCount = "update_count"
    const val FIREBASE_TOKEN = "firebase_token"
    const val PERMISSION = "permission"

    //Remote Config File
    const val ONLY_TEST_USERS = "only_test_users"
    const val APK_URL = "apk_url"
    const val FORCE_UPDATE = "force_update"
    const val UPDATE_AVAILABLE = "update_available"
    const val ONLY_TEST_USERS_REMOTE_CONFIG = "only_test_users_rC"
    const val INSURANCE_REMOTE_CONFIG = "insurance_conditions_rC"
    const val OTHER_SETTINGS_REMOTE_CONFIG = "other_settings"
    const val APK_URL_REMOTE_CONFIG = "apk_url_rC"
    const val REMOTE_CONFIG_CASHOUT_CONDITION = "cashout_conditions"
    const val FORCE_UPDATE_REMOTE_CONFIG = "force_update_remote_config"
    const val UPDATE_AVAILABLE_REMOTE_CONFIG = "update_available_remote_config"
    const val UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION = "update_available_remote_config_version"
    const val VERSIONS = "version"
    const val CONTACT_SMS_PERMISSION = "contacts_sms_permission"
    const val CONTACT_SMS_PERMISSION_REMOTE_CONFIG = "contacts_permission_remote_config_version"
    const val NOTIFICATION_PERMISSION = "notifications_permission"
    const val NOTIFICATION_PERMISSION_REMOTE_CONFIG = "notification_permission_remote_config"
    const val INSTALLED_APP = "installed_apps_permission"
    const val INSTALLED_APP_REMOTE_CONFIG = "installed_app_remote_config"
    const val SUPPORT_NUMBER = "support_phone"
    const val PRIVACY_URL = "privacy_url"
    const val TERM_AND_CONDITION = "tc_and_privacy_url"
    const val TERM_AND_CONDITION_REMOTE_CONFIG = "tnc_url_remote_config"
    const val SUPPORT_NUMBER_REMOTE_CONFIG = "support_phone_remote_config"
    const val PRIVACY_URL_REMOTE_CONFIG = "privacy_url_remote_config"
    const val INSURE_CONDITION = "insure_conditions"
    const val OTHER_SETTINGS = "other_settings"
    const val FRESHCHAT_ENABLE_CONDITION = "freshchat"
    const val FRESHCHAT_ENABLE_CONDITION_REMOTE_CONFIG = "freshchat_remote_config"
    const val UPDATE_CONDITIONS = "update_conditions"
    const val UPDATE_CONDITIONS_REMOTE_CONFIG = "update_conditions_remote_config"
    const val LOAN_TERM_AND_CONDITION = "loan_privacy_url"
    const val LOAN_TERM = "loan_apply_condition"
    const val LOAN_TERM_REMOTE_CONFIG = "loan_remote_config"
    const val LOAN_TERM_AND_CONDITION_REMOTE_CONFIG = "loan_term_and_condition_remote_config"
    const val WEBVIEW_HANDLER = "webview_handler_home_page"
    const val WEBVIEW_HANDLER_REMOTE_CONFIG_HOME_PAGE = "webview_handler_remote_config_home"
    const val PERMISSION_REMOTE_CONFIG = "permission"
    val CHROME_URL: String = "open_in_chrome"
    val CHROME_URL_REMOTE_CONFIG: String = "chrome_url_remote_config"

    //Static Variable Check
    var checkRefreshAccount = false

    @JvmField
    var checkSessionSoftUpdate = false

    @JvmField
    var checkEarnFragmentState = false

    @JvmField
    var hideAndShowTopBar = true
    const val MINIMUM_ELIGIBLE = "minimum_eligible"
    const val ISFEEDBACKSESSION = "feedback_session"
    const val HOME_PAGE_VIEWED = "home_page_viewed"
    const val COD_PAYMENT_DONE = "cod_payment_done"
    const val TICKET_STATUS_PAGE_VIEWED = "ticket_status_page_viewed"
    const val TICKET_EXPANDED = "ticket_expanded"
    const val LOAN_APPLIED = "loan_applied"
    const val TXN_HISTORY = "txt_history"
    const val PAY_SLIP_VIEWED = "pay_slip_viewed"
    const val PROFILE_PAGE_VIEWED = "profile_page_viewed"
    const val CASHOUT_AVAIL = "cashout_availed"
    const val MILESTONE_DETAILS_VIEWED = "milestone_details_viewed"
    const val LOAN_APPLICATION_VIEWED = "loan_application_viewed"
    const val ISBANKPRIMARYACCOUNT = "primary_account"
    const val IS_VERIFICATION_STATUS_AADHAR = "verification_status_aadhar"
    const val IS_VERIFICATION_STATUS_PAN = "verification_status_pan"
    const val IS_VERIFICATION_STATUS_PROFILE = "verification_status_profile"

    // DYNAMIC_LANGUAGE_STRING
    const val language_selection_en = "Select Language"
    const val select_language_string_sub_string_en = "You can change this later from \nthe settings"
    const val english_string_en = "English"
    const val english_string_sub_string_en = "I prefer talking in English"
    const val hindi_string_en = "Hindi"
    const val hindi_sub_string_en = "I prefer talking in Hindi"
    val next_string_en: CharSequence = "Next"
    const val language_selection_hi = "भाषा का चयन करें"
    const val select_language_string_sub_string_hi = "आप इसे बाद में \nसेटिंग्स . से बदल सकते हैं"
    const val english_string_hi = "अंग्रेज़ी"
    const val english_string_sub_string_hi = "मुझे अंग्रेजी में बात करना पसंद है"
    const val hindi_string_hi = "हिंदी"
    const val hindi_sub_string_hi = "मुझे हिंदी में बात करना पसंद है"
    val next_string_hi: CharSequence = "अगला"
    const val TENURE_BACK = "tenure_back"

    const val AUTHENTICATION_FAILED = "Authentication failed"
    const val AUTHENTICATION_SUCCEEDED = "Authentication succeeded"
    const val AUTHENTICATION_ERROR = "Authentication error"

    const val BIOMETRIC_AUTHENTICATION = "Unlock Mitra App"
    const val USE_DEVICE_PASSWORD = "Use device password"
    const val BIOMETRIC_AUTHENTICATION_SUBTITLE = ""
    const val BIOMETRIC_AUTHENTICATION_DESCRIPTION =
        "Unlock your screen with PIN, pattern, password, face or fingerprint"

    const val AUTHENTICATE_OTHER = "Authenticate using Device Password/PIN"

    const val AVAILABLE = "Available"
    const val UNAVAILABLE = "Unavailable"
    const val TRUE = "True"
    const val FALSE = "False"
    const val CANCEL = "Cancel"

    const val PASSWORD_PIN_AUTHENTICATION = "Unlock Mitra App"
    const val PASSWORD_PIN_AUTHENTICATION_SUBTITLE = ""
    const val PASSWORD_PIN_AUTHENTICATION_DESCRIPTION =
        "Unlock your screen with PIN, pattern, password, face or fingerprint"

    const val USERTYPE: String = "user_type"
    const val USERTEST: String = "user_test"
    const val USERLOGIN: String = "user_login"

    const val AUTHORIZATION: String = "Authorization"
    const val DEVICEID: String = "device-id"
    const val APP_BUILD: String = "app-build"
    const val APP_ID: String = "app-id"
    const val APP_VC: String = "app-vc"
    const val APP_VERSION: String = "app-version"
    const val ACCEPT_LANGUAGE: String = "Accept-Language"
    const val NOTIFICATION_CODE_PACKAGE: String = "Notification_Code_package"
    const val NOTIFICATION_CODE_MESSAGE: String = "Notification_Code_message"
    const val COORDINATES: String = "coordinates"
    const val NEW_NOTIFICATION: String = "new_notification"
    const val TITLE: String = "title"
    const val BODY: String = "body"
    const val CLICK_ACTION: String = "click_action"
    const val API_NOT_CONNECTED: String = "API NOT CONNECTED"
    const val NETWORK_ERROR: String = "NETWORK ERROR"
    const val SOME_THING_WENT_WRONG: String = "SOME THING WENT WRONG"
    const val LOCATION_UPDATE: String = "location_update"

    const val BUNDLE_NAME: String = "name"
    const val BUNDLE_INSURANCE_NAME: String = "insurance_name"
    const val BUNDLE_INSURANCE_NAME_SUBHEADING: String = "insurance_name_subHeading"
    const val BUNDLE_PREMIUM_RATE: String = "premium"
    const val BUNDLE_INSURANCE_LOGO: String = "insurance_logo"
    const val BUNDLE_SUM_ASSURED: String = "sum_assured"
    const val BUNDLE_DUE_FROM: String = "due_from"
    const val BUNDLE_EXPIRE_FROM: String = "expire_from"
    const val BUNDLE_POLICY_STATUS: String = "policy_status"

    //NotificationListAdapter
    const val HOME: String = "Home"
    const val TYPE: String = "type"
    const val PROFILE: String = "profile"
    const val BORROW: String = "borrow"
    const val INSURE: String = "insure"
    const val CHATBOT: String = "chatBot"
    const val BANK_ACCOUNT: String = "bank_account"
    const val DOCUMENT: String = "document"

    //PreferenceUtils
    const val DEVICE_ID_CHECK: String = "deviceIdCheck"

    //AddBankFragment
    const val UPDATE_NOTIFICATION_VIEWED: String = "update_notification_viewed"
    const val UPDATE_NOTIFICATION_CLICKED: String = "update_notification_clicked"
    const val ACCOUNT_NUMBER: String = "accountNumber"
    const val ACCOUNT_NAME: String = "accountName"

    //ViewFragment
    const val BANK_EDIT_TAPPED:String = "bank_edit_tapped"

    //BottomSheetEarn
    const val BUNDLE_TIMEFRAME:String = "timeframe"
    const val MILESTONE_PROJECTION_CHANGED:String = "milestone_projections_changed"

    //BottomSheetV2
    const val BUNDLE_CASHOUT_FEE_LABEL:String = "cashoutFixedFeeLabel"
    const val BUNDLE_CASHOUT_FEE_PERCENTAGE:String = "cashoutFeePercentage"
    const val BUNDLE_CASHOUT_FEE_PERCENTAGE_ENABLED:String = "cashoutFeePercentageEnabled"

    //FeedbackBottomSheet
    const val CASHOUT:String = "cashout"
    const val PAYSLIP:String = "payslip"
    const val TXT:String = "txn"
    const val USER_FEEDBACK:String = "Userfeedback"

    //UploadDocumentFragment
    const val IMAGE_URL:String = "imageUrl"
    const val OTHER_SIDE_IMAGE_URL:String = "otherSideImageUrl"
    const val EXPECTED_TYPE:String = "expectedType"
    const val SOURCE:String = "source"

    //ReferralStatusRespFragment
    const val DUPLICATE_COUNT:String= "duplicateCount"
    const val VALID_COUNT:String= "validCount"
    const val PAYMENT_SUCCESSFUL_FRAGMENT:String= "PaymentSuccessful Fragment"

    //ReferralStatusFragment
    const val REFERRAL_STATUS_VIEWED:String = "referral_status_viewed"

    //ReferralInviteFriendsFragment
    const val PHONE_BOOK:String = "phone_book"
    const val REFER_TYPE:String = "refer_type"

    //CaroselEffectSlider
    const val POSITION:String = "position"
    const val BANNER_TAPPED:String = "banner_tapped"
    const val LANDINGURL_PROFILE:String = "Profile"
    const val LANDINGURL_EARN:String = "Earn"
    const val LANDINGURL_HOME:String = "Home"
    const val LANDINGURL_LOAN:String = "Loan"
    const val LANDINGURL_NOTIFICATIONS:String = "Notifications"
    const val LANDINGURL_INSURE:String = "Insure"
    const val LANDINGURL_REFERRAL:String = "referral"
    const val LANDINGURL_SAVING_CALCULATOR:String = "savingCalculator"
    const val BANK:String = "bank"
    const val TRANSACTION_ID:String = "transactionId"
    const val CASHOUT_PURPOSE_VIEWED:String = "cashout_purpose_viewed"
    const val BUNDLE_CASHOUT_PURPOSE:String = "cashout_purpose"
    const val BUNDLE_cashout_purpose_selected:String = "cashout_purpose_selected"
    const val HOME_UTILITIES:String = "home_utilities"
    const val HOUSE_RENT:String = "house_rent"
    const val FAMILY:String = "family"
    const val VEHICLE:String = "vehicle"
    const val SHOPPING:String = "shopping"
    const val FESTIVAL:String = "festival"
    const val MEDICAL:String = "medical"
    const val OTHER:String = "other"

    //CashOutTransactionFragment
    const val CASHED_AMOUNT:String = "cashed_amount"
    const val CASHED_FEE_PERCENT:String = "cashout_fee_percent"
    const val CASHED_FIXED_FEE:String = "cashout_fixed_fee"

    //EarnFragmentV2
    const val TEST_ATTRIBUTE:String = "test_attribute"
    const val TEST_EVENT:String = "test_event"
    const val CASHOUT_DISABLED_TAPPED:String = "cashout_disabled_tapped"
    const val CASHOUT_ELIGIBLE_TAPPED:String = "cashout_eligible_tapped"
    const val CASHOUT_INELIGIBLE_TAPPED:String = "cashout_ineligible_tapped"
    const val CASHOUT_DISABLED_VIEWED:String = "cashout_disabled_viewed"
    const val CASHOUT_ELIGIBLE_VIEWED:String = "cashout_eligible_viewed"
    const val CASHOUT_INELIGIBLE_VIEWED:String = "cashout_ineligible_viewed"
    const val ELIGIBLE_AMOUNT:String = "eligible_amount"
    const val USERT_TYPE:String = "userType"
    const val ISCROSSUTILSLOTAVAILABLE : String = "cross_util"
    const val RECEIVED:String = "received"
    const val MILESTONE_VIEWED:String = "milestone_viewed"
    const val MITRA_BALANCE:String = "mitra_balance"
    const val STATUS:String = "status"
    const val COMPANY_NAME1:String = "companyName1"
    const val DATA_OF_JOINING1:String = "dateOfJoining1"
    const val USER_NAME:String = "userName"
    const val CASHOUT_ELIGIBILITY_STATUS:String = "cashoutEligibilityStatus"
    const val INSURANCE_ELIGIBILITY_STATUS:String = "insuranceEligibilityStatus"
    const val LOAN_ELIGIBILITY_STATUS:String = "loanEligibilityStatus"
    const val NUMBER:String = "number"
    const val STRING:String = "string"
    const val CASHOUT_FEE_PERCENT:String = "cashoutFeePercent"
    const val CASHOUT_FIXED_FEE:String = "cashoutFixedFee"
    const val RIDER_ID_1:String = "riderId1"
    const val CITY:String = "city"
    const val DOCUMENTS_UPLOADED:String = "documentsUploaded"
    const val BANK_ACCOUNT_DETAILS_AVAILABLE:String = "bankAccountDetailsAvailable"
    const val IS_TEST_USER:String = "isTestUser"

    //ErrorFragment
    const val PAGE:String = "page"
    const val PER_PAGE:String = "perPage"
    const val FILTERS:String = "filters"
    const val SORTS:String = "sorts"

    //HistoryFragment
    const val DATE:String = "date"
    const val SELECT_COMPANY:String = "Select Company"
    const val FILTER_APPLIED:String = "filter_applied"
    const val SORT_APPLIED:String = "sort_applied"

    //SplashActivity
    const val WEBVIEW:String = "webview"
    const val REFERRAL:String = "referral"
    const val MORNING_TRIGGERED:String = "morning_triggered"

    //SalaryViewActivity
    const val _TYPE:String = "Type"

    //NotificationViewActivity
    const val WEBVIEW_OPENED:String = "webview_opened"

    //LanguageSelectionActivity
    const val CRASH:String = "crash"
    const val SELECTED_LANGUAGE:String = "selectedLanguage"
    const val LANGUAGE_SELECTED:String = "language_selected"
    const val LANGUAGE_CHANGED:String = "language_changed"

    //ExperimentActivity
    const val DESIGNATION:String = "destination"

    //HomeActivity
    const val USER_TRANSITION_VIEWED = "user_transition_viewed"
    const val NEW_LEAD = "new_lead"
    const val NEW_LEAD_REFERRAL = "referral_redirect_new_lead"
    const val NEW_LEAD_REFERRAL_TYPE = "new_lead_refferal"
    const val NON_PAYROLL = "non_payroll"
    const val PAYROLL = "payroll"
    const val TEL_INTENT = "tel_intent"
    const val WHATSAPP_INTENT = "whatsapp_intent"
    const val MAIL_INTENT = "mail_intent"
    const val DOWNLOAD = "download"
    const val LOGOUT = "logout"
    const val UPLOAD = "upload"
    const val VERIFYBANK = "verify_bank"
    const val PROFILE_PIC = "profile_pic"
    const val BANK_DOC_UPLOAD = "bank_doc_upload"
    const val USER_TRANSITION_TAPPED = "user_transition_tapped"

    //EnterOtpActivity
    const val MOBILE_NUMBER:String = "mobileNumber"
    const val MOBILE_NUMBER2:String = "mobile_number"
    const val OTP:String = "otp"
    const val LOGIN:String = "login"

    //EnterNumberActivity
    const val TC_URL:String = "tc_url"

    //BaseApplication
    const val DEMO_APP:String = "demoapp"
    const val HAS_SENT_INSTALL:String = "has_sent_install"
    const val EXISTING:String= "existing"

    //WorkerScheduler
    const val UPDATELOCATION:String = "updateLocation"

    //UpdateLocationWorker
    const val LATTITUDE:String = "lattitude"
    const val LONGITUDE:String = "longitude"

    //workmanager
    const val LAT:String = "lat"
    const val LNG:String = "lng"

    //MainActivity
    const val HOME_FRAGMENT:String = "earn_fragment"
    const val WEEKLY_EARNINGS_FRAGMENT:String = "earn_fragment"
    const val UPLOAD_DOCUMENT_FRAGMENT:String = "upload_document_fragment"
    const val INSURANCE_FRAGMENT:String = "insurance_fragment"
    const val INSURANCE_FRAGMENT2:String = "Insurance Fragment"
    const val LOAN_APPLY_FRAGMENT:String = "loan_apply_fragment"
    const val LOAN_RESULT_FRAGMENT:String = "loan_result_fragment"
    const val LOAN_RESULT_FRAGMENT2:String = "LoanResult Fragment"
    const val LOAN_BORROW_FRAGMENT:String = "loan_borrow_fragment"
    const val PROFILE_FRAGMENT:String = "profile_fragment"
    const val PROFILE_FRAGMENT2:String = "Profile Fragment"
    const val REFERRAL_HOME:String = "referral_home"
    const val ALL_NOTIFICATION:String = "All Notification"
    const val ADDRESS:String = "address"
    const val SMS_LIMIT_BREACHED:String = "sms_limit_breached"
    const val ALL_CONTACTS:String = "All Contacts"
    const val INSTALLED_APPS:String = "installed_apps"
    const val MESSAGES:String = "messages"
    const val RESTART:String = "RESTART"
    const val UTC:String = "UTC"

    //ReferralHomeFragment
    const val REFERRAL_HOME_VIEWED:String  ="referral_home_viewed"

    //viewModel
    const val HEADING:String = "heading"
    const val SIPLY:String = "Siply"
    const val PAYMENT_SLIP_VIEW_FRAGMENT:String = "PaymentSlipView Fragment"
    const val PAYSLIP_DOWNLOADED:String = "payslip_downloaded"
    const val PAYMENT_SLIP_FRAGMENT:String = "PaymentSlip Fragment"
    const val PAYSLIP_LIST_VIEWED:String = "payslip_list_viewed"

    //payslip
    const val PAYSLIP_VIEWED:String = "payslip_viewed"

    //notification
    const val NOTIFICATION_FRAGMENT:String = "Notification Fragment"

    //loan
    const val AMOUNT:String = "amount"
    const val PURPOSE:String = "purpose"
    const val INTEREST:String = "interest"
    const val EMI:String = "emi"
    const val PERIOD:String = "period"
    const val DURATION:String = "duration"
    const val TENURE_SELECTION_FRAGMENT:String = "TenureSelection Fragment"

    //TenureSelectionFragment
    const val TENURE:String = "tenure"
    const val LOAN_TENURE_PICKED:String = "loan_tenure_picked"
    const val LOAN_TENURE_VIEWED:String = "loan_tenure_viewed"
    const val LOANS_LIST_VIEWED:String = "loans_list_viewed"
    const val INTEREST_RATE:String = "interest_rate"

    //loan
    const val LOAN_INFO_FRAGMENT:String = "LoanInfo Fragment"
    const val EMI_PERIOD:String = "emiPeriod"
    const val LOAN_SUMMARY_VIEWED:String = "loan_summary_viewed"
    const val LOAN_APPLICATION_FRAGMENT:String = "LoanApplication Fragment"
    const val LOAN_PURPOSE_VIEWED:String = "loan_purpose_viewed"
    const val LOAN_AMOUNT_VIEWED:String = "loan_amount_viewed"
    const val REASON:String = "reason"
    const val LOAN_REASON_PICKED:String = "loan_reason_picked"
    const val BORROW_FRAGMENT:String = "Borrow Fragment"
    const val BORROW_USER_INFO:String = "borrow_user_info"
    const val BORROW_PAGE_VIEWED:String = "borrow_page_viewed"

    //insurance
    const val INSURANCE_INFO_FRAGMENT:String = "InsuranceInfo Fragment"
    const val INSURE_DETAILS_VIEWED:String = "insure_details_viewed"
    const val INSURE_LIST_VIEWED:String = "insure_list_viewed"
    const val INSURE_USER_INFO:String = "insure_user_info"
    const val INSURE_INTERESTED:String = "insure_interested"
    const val INSURE_PAGE_VIEWED:String = "insure_page_viewed"

    //history
    const val TXN_HISTORY_CREDIT_VIEWED:String = "txn_history_credit_viewed"
    const val FAILED:String = "Failed"
    const val FAILEDCAPS:String = "FAILED"
    const val TXN_HISTORY_FAILED:String = "txn_history_failed"
    const val CREDIT:String = "credit"
    const val CREDITCAPS:String = "CREDIT"
    const val DEBIT:String = "debit"
    const val DEBITCAPS:String = "DEBIT"



    // code check
    //cashout
    const val user_prev_state = "0"

    const val BASE_URL = "https://stg-api.mitra.vahan.co"
//    const val BASE_URL = "https://api.mitra.vahan.co"

}
