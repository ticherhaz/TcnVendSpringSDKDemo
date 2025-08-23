package com.tcn.sdk.springdemo.publicbank.model

data class QrPaymentStatusResponse(
    val status: Boolean = false,
    val refNo: String = ""
)