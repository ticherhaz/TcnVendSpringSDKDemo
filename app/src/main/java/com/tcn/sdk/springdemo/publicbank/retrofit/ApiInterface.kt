package com.tcn.sdk.springdemo.publicbank.retrofit

import com.tcn.sdk.springdemo.publicbank.model.GenerateQr
import com.tcn.sdk.springdemo.publicbank.model.GenerateQrResponse
import com.tcn.sdk.springdemo.publicbank.model.QrPaymentStatus
import com.tcn.sdk.springdemo.publicbank.tools.ConstantApi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @POST(ConstantApi.ROUTER_PUBLIC_BANK_QR_GENERATE)
    suspend fun initGenerateQrCode(
        @Body() generateQr: GenerateQr
    ): Response<GenerateQrResponse>

    @POST(ConstantApi.ROUTER_PUBLIC_BANK_QR_PAYMENT_STATUS)
    suspend fun getQrPaymentStatus(
        @Body() qrPaymentStatus: QrPaymentStatus
    ): Response<String>
}