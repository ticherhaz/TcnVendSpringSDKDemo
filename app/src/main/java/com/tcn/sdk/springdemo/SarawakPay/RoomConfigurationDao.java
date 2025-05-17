package com.tcn.sdk.springdemo.SarawakPay;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomConfigurationDao {

    //----------for Debug Log

    @Insert
    void insertDbDebugLog(DebugLog... debugLog);

    @Query("SELECT * FROM " + DebugLog.TABLE_NAME + " ORDER BY " + DebugLog.COLUMN_CREATED_AT + " DESC")
    List<DebugLog> getDbAllDebugLog();

    @Query("SELECT * FROM " + DebugLog.TABLE_NAME + " ORDER BY " + DebugLog.COLUMN_CREATED_AT + " DESC LIMIT 1")
    DebugLog getDbLatestDebugLog();

    @Query("SELECT * FROM " + DebugLog.TABLE_NAME + " where " + DebugLog.COLUMN_ID + " = :id")
    DebugLog getDbDebugLogById(int id);

    @Update
    void updateDbDebugLogEntry(DebugLog debugLog);

    @Query("DELETE FROM " + DebugLog.TABLE_NAME + " where " + DebugLog.COLUMN_BATCH_NO + " LIKE  :batchNo")
    void deleteDbAllDebugLogByBatch(String batchNo);

    @Query("DELETE FROM " + DebugLog.TABLE_NAME)
    void deleteDbAllDebugLog();

    //----------for Transaction

    @Insert
    void insertDbTransaction(Transaction... transaction);

    @Query("SELECT * FROM " + Transaction.TABLE_NAME + " ORDER BY " + Transaction.COLUMN_CREATED_AT + " DESC LIMIT 1")
    Transaction getDbLatestTransaction();

    @Query("SELECT * FROM " + Transaction.TABLE_NAME + " ORDER BY " + Transaction.COLUMN_CREATED_AT + " DESC")
    List<Transaction> getDbAllTransaction();

    @Query("SELECT * FROM " + Transaction.TABLE_NAME + " where " + Transaction.COLUMN_INVOICE_NO + " LIKE  :invoiceNo AND " + Transaction.COLUMN_BATCH_NO + " LIKE  :batchNo")
    Transaction getDbTransactionByInvoiceNoAndBatchNo(String invoiceNo, String batchNo);

    @Update
    void updateDbTransactionEntry(Transaction transaction);

    @Query("DELETE FROM " + Transaction.TABLE_NAME + " where " + Transaction.COLUMN_BATCH_NO + " LIKE  :batchNo")
    void deleteDbAllTransactionByBatchNo(String batchNo);

    @Query("DELETE FROM " + Transaction.TABLE_NAME)
    void deleteDbAllTransaction();
}
