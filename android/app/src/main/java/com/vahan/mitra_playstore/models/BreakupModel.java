package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class BreakupModel {
    @SerializedName("statusCode")
    @Expose
    int statusCode;
    @SerializedName("earningsBreakup")
    @Expose
    private EarningsBreakup earningsBreakup;
    @SerializedName("payslipUrl")
    @Expose
    private String payslipUrl;

    public EarningsBreakup getEarningsBreakup() {
        return earningsBreakup;
    }

    public void setEarningsBreakup(EarningsBreakup earningsBreakup) {
        this.earningsBreakup = earningsBreakup;
    }

    public String getPayslipUrl() {
        return payslipUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setPayslipUrl(String payslipUrl) {
        this.payslipUrl = payslipUrl;
    }
    public static class EarningsBreakup {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("dateRange")
        @Expose
        private String dateRange;
        @SerializedName("info")
        @Expose
        private String info;
        @SerializedName("grossEarnings")
        @Expose
        private GrossEarnings grossEarnings;
        @SerializedName("deductions")
        @Expose
        private Deductions deductions;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDateRange() {
            return dateRange;
        }

        public void setDateRange(String dateRange) {
            this.dateRange = dateRange;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public GrossEarnings getGrossEarnings() {
            return grossEarnings;
        }

        public void setGrossEarnings(GrossEarnings grossEarnings) {
            this.grossEarnings = grossEarnings;
        }

        public Deductions getDeductions() {
            return deductions;
        }

        public void setDeductions(Deductions deductions) {
            this.deductions = deductions;
        }
        public static class GrossEarnings {

            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("amount")
            @Expose
            private String amount;
            @SerializedName("items")
            @Expose
            private List<Item> items = null;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public List<Item> getItems() {
                return items;
            }

            public void setItems(List<Item> items) {
                this.items = items;
            }

            public class Item {

                @SerializedName("title")
                @Expose
                private String title;
                @SerializedName("helpText")
                @Expose
                private String helpText;
                @SerializedName("amount")
                @Expose
                private String amount;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getHelpText() {
                    return helpText;
                }

                public void setHelpText(String helpText) {
                    this.helpText = helpText;
                }

                public String getAmount() {
                    return amount;
                }

                public void setAmount(String amount) {
                    this.amount = amount;
                }

            }

        }

        public static class Deductions {

            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("amount")
            @Expose
            private String amount;
            @SerializedName("items")
            @Expose
            private List<Item__1> items = null;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public List<Item__1> getItems() {
                return items;
            }

            public void setItems(List<Item__1> items) {
                this.items = items;
            }
            public class Item__1 {

                @SerializedName("title")
                @Expose
                private String title;
                @SerializedName("helpText")
                @Expose
                private String helpText;
                @SerializedName("amount")
                @Expose
                private String amount;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getHelpText() {
                    return helpText;
                }

                public void setHelpText(String helpText) {
                    this.helpText = helpText;
                }

                public String getAmount() {
                    return amount;
                }

                public void setAmount(String amount) {
                    this.amount = amount;
                }

            }
        }
    }

}


