package com.tcn.sdk.springdemo.SarawakPay;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = DebugLog.TABLE_NAME)
public class DebugLog {

    public static final String TABLE_NAME = "debug_logs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BATCH_NO = "batch_no";
    public static final String COLUMN_REQUEST = "request";
    public static final String COLUMN_RESPONSE = "response";
    public static final String COLUMN_CREATED_AT = "created_at";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = COLUMN_BATCH_NO)
    private String batchNo;

    @ColumnInfo(name = COLUMN_REQUEST)
    private String request;

    @ColumnInfo(name = COLUMN_RESPONSE)
    private String response;

    @ColumnInfo(name = COLUMN_CREATED_AT)
    private String createdAt;

    public DebugLog() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchNo() {
        return batchNo == null ? "" : batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getRequest() {
        return request == null ? "" : request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response == null ? "" : response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
