package com.tcn.sdk.springdemo.publicbank.model

data class GenerateQr(
    val transactionAmount: Double = 0.0,
    val machineId: Int = 0
)