package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class EarnDataModelJava {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("jobDetails")
    @Expose
    private JobDetails jobDetails;
    @SerializedName("walletDetails")
    @Expose
    private WalletDetails walletDetails;
    @SerializedName("currentEarnings")
    @Expose
    private CurrentEarnings currentEarnings;
    @SerializedName("payouts")
    @Expose
    private List<Object> payouts = null;
    @SerializedName("documents")
    @Expose
    private Documents documents;
    @SerializedName("bankAccountDetails")
    @Expose
    private BankAccountDetails bankAccountDetails;
    @SerializedName("cashoutDetails")
    @Expose
    private CashoutDetails cashoutDetails;
    @SerializedName("milestones")
    @Expose
    private List<Milestone> milestones = null;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public JobDetails getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(JobDetails jobDetails) {
        this.jobDetails = jobDetails;
    }

    public WalletDetails getWalletDetails() {
        return walletDetails;
    }

    public void setWalletDetails(WalletDetails walletDetails) {
        this.walletDetails = walletDetails;
    }

    public CurrentEarnings getCurrentEarnings() {
        return currentEarnings;
    }

    public void setCurrentEarnings(CurrentEarnings currentEarnings) {
        this.currentEarnings = currentEarnings;
    }

    public List<Object> getPayouts() {
        return payouts;
    }

    public void setPayouts(List<Object> payouts) {
        this.payouts = payouts;
    }

    public Documents getDocuments() {
        return documents;
    }

    public void setDocuments(Documents documents) {
        this.documents = documents;
    }

    public BankAccountDetails getBankAccountDetails() {
        return bankAccountDetails;
    }

    public void setBankAccountDetails(BankAccountDetails bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

    public CashoutDetails getCashoutDetails() {
        return cashoutDetails;
    }

    public void setCashoutDetails(CashoutDetails cashoutDetails) {
        this.cashoutDetails = cashoutDetails;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }


    public class BankAccountDetails {

        @SerializedName("available")
        @Expose
        private Boolean available;
        @SerializedName("error")
        @Expose
        private String error;

        public Boolean getBankDetailsEditAvailable() {
            return isBankDetailsEditAvailable;
        }

        public void setBankDetailsEditAvailable(Boolean bankDetailsEditAvailable) {
            isBankDetailsEditAvailable = bankDetailsEditAvailable;
        }

        @SerializedName("isBankDetailsEditAvailable")
        @Expose
        private Boolean isBankDetailsEditAvailable;

        public Boolean getAvailable() {
            return available;
        }

        public void setAvailable(Boolean available) {
            this.available = available;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }

    public class BasicPay {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("items")
        @Expose
        private List<Item> items = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

    }

    public class CashoutDetails {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("amountEligible")
        @Expose
        private Double amountEligible;
        @SerializedName("amountEligibleLabel")
        @Expose
        private String amountEligibleLabel;
        @SerializedName("minAmountEligible")
        @Expose
        private Integer minAmountEligible;
        @SerializedName("isCashoutFeeEnabled")
        @Expose
        private boolean isCashoutEnabled;
        @SerializedName("cashoutFeePercentage")
        @Expose
        private Double cashoutFeePercentage;
        @SerializedName("cashoutFixedFee")
        @Expose
        private Double cashoutFixedFee;
        @SerializedName("cashoutFixedFeeLabel")
        @Expose
        private String cashoutFixedFeeLabel;

        public String getCashoutFixedFeeLabel() {
            return cashoutFixedFeeLabel;
        }

        public void setCashoutFixedFeeLabel(String cashoutFixedFeeLabel) {
            this.cashoutFixedFeeLabel = cashoutFixedFeeLabel;
        }



        public Double getCashoutFixedFee() {
            return cashoutFixedFee;
        }

        public void setCashoutFixedFee(Double cashoutFixedFee) {
            this.cashoutFixedFee = cashoutFixedFee;
        }

        public boolean isCashoutEnabled() {
            return isCashoutEnabled;
        }

        public void setCashoutEnabled(boolean cashoutEnabled) {
            isCashoutEnabled = cashoutEnabled;
        }

        public Double getCashoutFeePercentage() {
            return cashoutFeePercentage;
        }

        public void setCashoutFeePercentage(Double cashoutFeePercentage) {
            this.cashoutFeePercentage = cashoutFeePercentage;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Double getAmountEligible() {
            return amountEligible;
        }

        public void setAmountEligible(Double amountEligible) {
            this.amountEligible = amountEligible;
        }

        public String getAmountEligibleLabel() {
            return amountEligibleLabel;
        }

        public void setAmountEligibleLabel(String amountEligibleLabel) {
            this.amountEligibleLabel = amountEligibleLabel;
        }

        public Integer getMinAmountEligible() {
            return minAmountEligible;
        }

        public void setMinAmountEligible(Integer minAmountEligible) {
            this.minAmountEligible = minAmountEligible;
        }

    }

    public class CurrentEarnings {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("dateRange")
        @Expose
        private String dateRange;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("detailedViewAvailable")
        @Expose
        private Boolean detailedViewAvailable;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDateRange() {
            return dateRange;
        }

        public void setDateRange(String dateRange) {
            this.dateRange = dateRange;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean getDetailedViewAvailable() {
            return detailedViewAvailable;
        }

        public void setDetailedViewAvailable(Boolean detailedViewAvailable) {
            this.detailedViewAvailable = detailedViewAvailable;
        }

    }

    public class CurrentEarnings__1 {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("items")
        @Expose
        private List<Item__2> items = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Item__2> getItems() {
            return items;
        }

        public void setItems(List<Item__2> items) {
            this.items = items;
        }

    }


    public class IncentivePay {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("structure")
        @Expose
        private String structure;
        @SerializedName("structureLabel")
        @Expose
        private String structureLabel;
        @SerializedName("tierOrderLabel")
        @Expose
        private String tierOrderLabel;
        @SerializedName("tierName")
        @Expose
        private String tierName;
        @SerializedName("items")
        @Expose
        private List<Item__1> items = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getStructure() {
            return structure;
        }

        public void setStructure(String structure) {
            this.structure = structure;
        }

        public String getStructureLabel() {
            return structureLabel;
        }

        public void setStructureLabel(String structureLabel) {
            this.structureLabel = structureLabel;
        }

        public String getTierOrderLabel() {
            return tierOrderLabel;
        }

        public void setTierOrderLabel(String tierOrderLabel) {
            this.tierOrderLabel = tierOrderLabel;
        }

        public String getTierName() {
            return tierName;
        }

        public void setTierName(String tierName) {
            this.tierName = tierName;
        }

        public List<Item__1> getItems() {
            return items;
        }

        public void setItems(List<Item__1> items) {
            this.items = items;
        }

    }

    public class Item {

        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("value")
        @Expose
        private Double value;
        @SerializedName("unit")
        @Expose
        private String unit;
        @SerializedName("unitLabel")
        @Expose
        private String unitLabel;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUnitLabel() {
            return unitLabel;
        }

        public void setUnitLabel(String unitLabel) {
            this.unitLabel = unitLabel;
        }
    }

    public class Item__1 {

        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("values")
        @Expose
        private List<Value> values = null;
        @SerializedName("maxValue")
        @Expose
        private Double maxValue;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<Value> getValues() {
            return values;
        }

        public void setValues(List<Value> values) {
            this.values = values;
        }

        public Double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(Double maxValue) {
            this.maxValue = maxValue;
        }

    }

    public class Item__2 {

        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("value")
        @Expose
        private Double value;
        @SerializedName("unit")
        @Expose
        private String unit;
        @SerializedName("unitValue")
        @Expose
        private String unitValue;
        @SerializedName("extraEarningsLabel")
        @Expose
        private String extraEarningsLabel;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUnitValue() {
            return unitValue;
        }

        public void setUnitValue(String unitValue) {
            this.unitValue = unitValue;
        }

        public String getExtraEarningsLabel() {
            return extraEarningsLabel;
        }

        public void setExtraEarningsLabel(String extraEarningsLabel) {
            this.extraEarningsLabel = extraEarningsLabel;
        }

    }

    public class JobDetails {

        @SerializedName("companyEmployeeId")
        @Expose
        private String companyEmployeeId;
        @SerializedName("dateOfJoiningStr")
        @Expose
        private String dateOfJoiningStr;
        @SerializedName("dateOfJoining")
        @Expose
        private String dateOfJoining;
        @SerializedName("companyId")
        @Expose
        private String companyId;
        @SerializedName("companyName")
        @Expose
        private String companyName;
        @SerializedName("companyKey")
        @Expose
        private String companyKey;
        @SerializedName("companyLogo")
        @Expose
        private String companyLogo;
        @SerializedName("companyIcon")
        @Expose
        private String companyIcon;
        @SerializedName("payrollUserCompanyId")
        @Expose
        private String payrollUserCompanyId;
        @SerializedName("nextTransferLabel")
        @Expose
        private String nextTransferLabel;

        public String getCompanyKey() {
            return companyKey;
        }

        public void setCompanyKey(String companyKey) {
            this.companyKey = companyKey;
        }


        public String getCompanyEmployeeId() {
            return companyEmployeeId;
        }

        public void setCompanyEmployeeId(String companyEmployeeId) {
            this.companyEmployeeId = companyEmployeeId;
        }

        public String getDateOfJoiningStr() {
            return dateOfJoiningStr;
        }

        public void setDateOfJoiningStr(String dateOfJoiningStr) {
            this.dateOfJoiningStr = dateOfJoiningStr;
        }

        public String getDateOfJoining() {
            return dateOfJoining;
        }

        public void setDateOfJoining(String dateOfJoining) {
            this.dateOfJoining = dateOfJoining;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyLogo() {
            return companyLogo;
        }

        public void setCompanyLogo(String companyLogo) {
            this.companyLogo = companyLogo;
        }

        public String getCompanyIcon() {
            return companyIcon;
        }

        public void setCompanyIcon(String companyIcon) {
            this.companyIcon = companyIcon;
        }

        public String getPayrollUserCompanyId() {
            return payrollUserCompanyId;
        }

        public void setPayrollUserCompanyId(String payrollUserCompanyId) {
            this.payrollUserCompanyId = payrollUserCompanyId;
        }

        public String getNextTransferLabel() {
            return nextTransferLabel;
        }

        public void setNextTransferLabel(String nextTransferLabel) {
            this.nextTransferLabel = nextTransferLabel;
        }

    }

    public class Milestone {

        @SerializedName("earningsTitle")
        @Expose
        private String earningsTitle;
        @SerializedName("viewDetails")
        @Expose
        private Boolean viewDetails;
        @SerializedName("earningsValue")
        @Expose
        private Double earningsValue;
        @SerializedName("companyName")
        @Expose
        private String companyName;
        @SerializedName("companyIcon")
        @Expose
        private String companyIcon;
        @SerializedName("payoutStructure")
        @Expose
        private PayoutStructure payoutStructure;

        public String getEarningsTitle() {
            return earningsTitle;
        }

        public void setEarningsTitle(String earningsTitle) {
            this.earningsTitle = earningsTitle;
        }

        public Double getEarningsValue() {
            return earningsValue;
        }

        public void setEarningsValue(Double earningsValue) {
            this.earningsValue = earningsValue;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyIcon() {
            return companyIcon;
        }

        public void setCompanyIcon(String companyIcon) {
            this.companyIcon = companyIcon;
        }

        public PayoutStructure getPayoutStructure() {
            return payoutStructure;
        }

        public void setPayoutStructure(PayoutStructure payoutStructure) {
            this.payoutStructure = payoutStructure;
        }

        public Boolean getViewDetails() {
            return viewDetails;
        }

        public void setViewDetails(Boolean viewDetails) {
            this.viewDetails = viewDetails;
        }
    }

    public class PayoutStructure {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("basicPay")
        @Expose
        private BasicPay basicPay;
        @SerializedName("incentivePay")
        @Expose
        private IncentivePay incentivePay;
        @SerializedName("currentEarnings")
        @Expose
        private CurrentEarnings__1 currentEarnings;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public BasicPay getBasicPay() {
            return basicPay;
        }

        public void setBasicPay(BasicPay basicPay) {
            this.basicPay = basicPay;
        }

        public IncentivePay getIncentivePay() {
            return incentivePay;
        }

        public void setIncentivePay(IncentivePay incentivePay) {
            this.incentivePay = incentivePay;
        }

        public CurrentEarnings__1 getCurrentEarnings() {
            return currentEarnings;
        }

        public void setCurrentEarnings(CurrentEarnings__1 currentEarnings) {
            this.currentEarnings = currentEarnings;
        }

    }

    public class Settings {

        @SerializedName("isTestUser")
        @Expose
        private Boolean isTestUser;

        public Boolean getIsTestUser() {
            return isTestUser;
        }

        public void setIsTestUser(Boolean isTestUser) {
            this.isTestUser = isTestUser;
        }

    }

    public class User {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("jfUserId")
        @Expose
        private String jfUserId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("phoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("settings")
        @Expose
        private Settings settings;
        @SerializedName("insuranceEligibilityStatus")
        @Expose
        private String insuranceEligibilityStatus;
        @SerializedName("cashoutEligibilityStatus")
        @Expose
        private String cashoutEligibilityStatus;
        @SerializedName("loanEligibilityStatus")
        @Expose
        private String loanEligibilityStatus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getJfUserId() {
            return jfUserId;
        }

        public void setJfUserId(String jfUserId) {
            this.jfUserId = jfUserId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Settings getSettings() {
            return settings;
        }

        public void setSettings(Settings settings) {
            this.settings = settings;
        }

        public String getInsuranceEligibilityStatus() {
            return insuranceEligibilityStatus;
        }

        public void setInsuranceEligibilityStatus(String insuranceEligibilityStatus) {
            this.insuranceEligibilityStatus = insuranceEligibilityStatus;
        }

        public String getCashoutEligibilityStatus() {
            return cashoutEligibilityStatus;
        }

        public void setCashoutEligibilityStatus(String cashoutEligibilityStatus) {
            this.cashoutEligibilityStatus = cashoutEligibilityStatus;
        }

        public String getLoanEligibilityStatus() {
            return loanEligibilityStatus;
        }

        public void setLoanEligibilityStatus(String loanEligibilityStatus) {
            this.loanEligibilityStatus = loanEligibilityStatus;
        }

    }

    public class Value {

        @SerializedName("min")
        @Expose
        private Double min;
        @SerializedName("max")
        @Expose
        private Double max;
        @SerializedName("value")
        @Expose
        private Double value;

        public Double getMin() {
            return min;
        }

        public void setMin(Double min) {
            this.min = min;
        }

        public Double getMax() {
            return max;
        }

        public void setMax(Double max) {
            this.max = max;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

    }

    public class WalletDetails {

        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("walletBalance")
        @Expose
        private Double walletBalance;
        @SerializedName("walletBalanceLabel")
        @Expose
        private String walletBalanceLabel;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Double getWalletBalance() {
            return walletBalance;
        }

        public void setWalletBalance(Double walletBalance) {
            this.walletBalance = walletBalance;
        }

        public String getWalletBalanceLabel() {
            return walletBalanceLabel;
        }

        public void setWalletBalanceLabel(String walletBalanceLabel) {
            this.walletBalanceLabel = walletBalanceLabel;
        }

    }

    public static class Documents {
        @SerializedName("statusCode")
        @Expose
        public int statusCode;
        @SerializedName("uploaded")
        @Expose
        private Boolean uploaded;
        @SerializedName("error")
        @Expose
        private String error;
        @SerializedName("documents")
        @Expose
        private List<Document> documents = null;

        public Boolean getUploaded() {
            return uploaded;
        }

        public void setUploaded(Boolean uploaded) {
            this.uploaded = uploaded;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public List<Document> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Document> documents) {
            this.documents = documents;
        }


        public class Document {

            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("imageUrl")
            @Expose
            private String imageUrl;
            @SerializedName("otherSideImageUrl")
            @Expose
            private String otherSideImageUrl;
            @SerializedName("idNumber")
            @Expose
            private String idNumber;
            @SerializedName("createdAt")
            @Expose
            private String createdAt;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getOtherSideImageUrl() {
                return otherSideImageUrl;
            }

            public void setOtherSideImageUrl(String otherSideImageUrl) {
                this.otherSideImageUrl = otherSideImageUrl;
            }

            public String getIdNumber() {
                return idNumber;
            }

            public void setIdNumber(String idNumber) {
                this.idNumber = idNumber;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

        }
    }
}




