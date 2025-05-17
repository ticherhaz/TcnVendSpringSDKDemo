package com.tcn.sdk.springdemo.SarawakPay;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = Transaction.TABLE_NAME)
public class Transaction implements Serializable {

    public static final String TABLE_NAME = "transactions";
    public static final String COLUMN_ID = "id";

    //backer info
    public static final String COLUMN_BACKER_ID = "backer_id";
    public static final String COLUMN_BACKER_NAME = "backer_name";
    public static final String COLUMN_BACKER_PHONE = "backer_phone";
    public static final String COLUMN_BACKER_EMAIL = "backer_email";

    //campaign info
    public static final String COLUMN_CAMPAIGN_CODE = "campaign_code";
    public static final String COLUMN_CAMPAIGN_CATEGORY = "campaign_category";
    public static final String COLUMN_CAMPAIGN_NAME = "campaign_name";

    //e-wallet info
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_KEY_INDEX = "key_index";
    public static final String COLUMN_SIGNATURE = "signature";
    public static final String COLUMN_SIGNATURE_METHOD = "signature_method";
    public static final String COLUMN_BANK_REF_NUM = "bank_ref_num";
    public static final String COLUMN_BATCH_NO = "batch_no";
    public static final String COLUMN_RESPONSE_CODE = "response_code";
    public static final String COLUMN_RESPONSE_CODE_DESC = "response_code_desc";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_DISPLAY_MID = "display_mid";
    public static final String COLUMN_DISPLAY_TID = "display_tid";
    public static final String COLUMN_ENTRY_MODE = "entry_mode";
    public static final String COLUMN_FUNCTION = "function";
    public static final String COLUMN_INVOICE_NO = "invoice_no";
    public static final String COLUMN_MID = "mid";
    public static final String COLUMN_QR_CODE = "qr_code";
    public static final String COLUMN_SCHEME = "scheme";
    public static final String COLUMN_TID = "tid";
    public static final String COLUMN_TRACE_NO = "trace_no";
    public static final String COLUMN_TXN_DESC = "txn_desc";
    public static final String COLUMN_TXN_DT = "txn_dt";
    public static final String COLUMN_TXN_ID = "txn_id";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ORI_INVOICE_NO = "ori_invoice_no";
    public static final String COLUMN_ORI_BATCH_NO = "ori_batch_no";
    public static final String COLUMN_VOIDED_AT = "voided_at";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = COLUMN_BACKER_ID)
    private String backerId;

    @ColumnInfo(name = COLUMN_BACKER_NAME)
    private String backerName;

    @ColumnInfo(name = COLUMN_BACKER_PHONE)
    private String backerPhone;

    @ColumnInfo(name = COLUMN_BACKER_EMAIL)
    private String backerEmail;

    @ColumnInfo(name = COLUMN_CAMPAIGN_CODE)
    private String campaignCode;

    @ColumnInfo(name = COLUMN_CAMPAIGN_CATEGORY)
    private String campaignCategory;

    @ColumnInfo(name = COLUMN_CAMPAIGN_NAME)
    private String campaignName;

    @ColumnInfo(name = COLUMN_TYPE)
    private String type;

    @ColumnInfo(name = COLUMN_AMOUNT)
    private String amount;

    @ColumnInfo(name = COLUMN_KEY_INDEX)
    private String keyIndex;

    @ColumnInfo(name = COLUMN_SIGNATURE)
    private String signature;

    @ColumnInfo(name = COLUMN_SIGNATURE_METHOD)
    private String signatureMethod;

    @ColumnInfo(name = COLUMN_BANK_REF_NUM)
    private String bankRefNum;

    @ColumnInfo(name = COLUMN_BATCH_NO)
    private String batchNo;

    @ColumnInfo(name = COLUMN_RESPONSE_CODE)
    private String responseCode;

    @ColumnInfo(name = COLUMN_RESPONSE_CODE_DESC)
    private String responseCodeDesc;

    @ColumnInfo(name = COLUMN_CURRENCY)
    private String currency;

    @ColumnInfo(name = COLUMN_CUSTOMER_ID)
    private String customerId;

    @ColumnInfo(name = COLUMN_DISPLAY_MID)
    private String displayMid;

    @ColumnInfo(name = COLUMN_DISPLAY_TID)
    private String displayTid;

    @ColumnInfo(name = COLUMN_ENTRY_MODE)
    private String entryMode;

    @ColumnInfo(name = COLUMN_FUNCTION)
    private String function;

    @ColumnInfo(name = COLUMN_INVOICE_NO)
    private String invoiceNo;

    @ColumnInfo(name = COLUMN_MID)
    private String mid;

    @ColumnInfo(name = COLUMN_QR_CODE)
    private String qrCode;

    @ColumnInfo(name = COLUMN_SCHEME)
    private String scheme;

    @ColumnInfo(name = COLUMN_TID)
    private String tid;

    @ColumnInfo(name = COLUMN_TRACE_NO)
    private String traceNo;

    @ColumnInfo(name = COLUMN_TXN_DESC)
    private String transactionDesc;

    @ColumnInfo(name = COLUMN_TXN_DT)
    private String transactionDt;

    @ColumnInfo(name = COLUMN_TXN_ID)
    private String transactionId;

    @ColumnInfo(name = COLUMN_STATUS)
    private String status;

    @ColumnInfo(name = COLUMN_ORI_INVOICE_NO)
    private String oriInvoiceNo;

    @ColumnInfo(name = COLUMN_ORI_BATCH_NO)
    private String oriBatchNo;

    @ColumnInfo(name = COLUMN_VOIDED_AT)
    private String voidedAt;

    @ColumnInfo(name = COLUMN_CREATED_AT)
    private String createdAt;

    @ColumnInfo(name = COLUMN_UPDATED_AT)
    private String updatedAt;

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackerId() {
        return backerId == null ? "" : backerId;
    }

    public void setBackerId(String backerId) {
        this.backerId = backerId;
    }

    public String getBackerName() {
        return backerName == null ? "" : backerName;
    }

    public void setBackerName(String backerName) {
        this.backerName = backerName;
    }

    public String getBackerPhone() {
        return backerPhone == null ? "" : backerPhone;
    }

    public void setBackerPhone(String backerPhone) {
        this.backerPhone = backerPhone;
    }

    public String getBackerEmail() {
        return backerEmail == null ? "" : backerEmail;
    }

    public void setBackerEmail(String backerEmail) {
        this.backerEmail = backerEmail;
    }

    public String getCampaignCode() {
        return campaignCode == null ? "" : campaignCode;
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
    }

    public String getCampaignCategory() {
        return campaignCategory == null ? "" : campaignCategory;
    }

    public void setCampaignCategory(String campaignCategory) {
        this.campaignCategory = campaignCategory;
    }

    public String getCampaignName() {
        return campaignName == null ? "" : campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount == null ? "" : amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getKeyIndex() {
        return keyIndex == null ? "" : keyIndex;
    }

    public void setKeyIndex(String keyIndex) {
        this.keyIndex = keyIndex;
    }

    public String getSignature() {
        return signature == null ? "" : signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignatureMethod() {
        return signatureMethod == null ? "" : signatureMethod;
    }

    public void setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    public String getBankRefNum() {
        return bankRefNum == null ? "" : bankRefNum;
    }

    public void setBankRefNum(String bankRefNum) {
        this.bankRefNum = bankRefNum;
    }

    public String getBatchNo() {
        return batchNo == null ? "" : batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getResponseCode() {
        return responseCode == null ? "" : responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseCodeDesc() {
        return responseCodeDesc == null ? "" : responseCodeDesc;
    }

    public void setResponseCodeDesc(String responseCodeDesc) {
        this.responseCodeDesc = responseCodeDesc;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomerId() {
        return customerId == null ? "" : customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDisplayMid() {
        return displayMid == null ? "" : displayMid;
    }

    public void setDisplayMid(String displayMid) {
        this.displayMid = displayMid;
    }

    public String getDisplayTid() {
        return displayTid == null ? "" : displayTid;
    }

    public void setDisplayTid(String displayTid) {
        this.displayTid = displayTid;
    }

    public String getEntryMode() {
        return entryMode == null ? "" : entryMode;
    }

    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    public String getFunction() {
        return function == null ? "" : function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getInvoiceNo() {
        return invoiceNo == null ? "" : invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getMid() {
        return mid == null ? "" : mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getQrCode() {
        return qrCode == null ? "" : qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getScheme() {
        return scheme == null ? "" : scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getTid() {
        return tid == null ? "" : tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTraceNo() {
        return traceNo == null ? "" : traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getTransactionDesc() {
        return transactionDesc == null ? "" : transactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        this.transactionDesc = transactionDesc;
    }

    public String getTransactionDt() {
        return transactionDt == null ? "" : transactionDt;
    }

    public void setTransactionDt(String transactionDt) {
        this.transactionDt = transactionDt;
    }

    public String getTransactionId() {
        return transactionId == null ? "" : transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriInvoiceNo() {
        return oriInvoiceNo == null ? "" : oriInvoiceNo;
    }

    public void setOriInvoiceNo(String oriInvoiceNo) {
        this.oriInvoiceNo = oriInvoiceNo;
    }

    public String getOriBatchNo() {
        return oriBatchNo == null ? "" : oriBatchNo;
    }

    public void setOriBatchNo(String oriBatchNo) {
        this.oriBatchNo = oriBatchNo;
    }

    public String getVoidedAt() {
        return voidedAt == null ? "" : voidedAt;
    }

    public void setVoidedAt(String voidedAt) {
        this.voidedAt = voidedAt;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt == null ? "" : updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
