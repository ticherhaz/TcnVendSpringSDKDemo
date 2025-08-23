package com.tcn.sdk.springdemo.publicbank.model

data class QrPaymentStatus(
    val merchantReference: String = "", //merchantReference tu bila request generate QR code akan dpt
    val transDate: String = ""  //yyyy-MM-dd
)