package com.tcn.sdk.springdemo.publicbank.repository

import com.tcn.sdk.springdemo.publicbank.model.GenerateQr
import com.tcn.sdk.springdemo.publicbank.model.GenerateQrResponse
import com.tcn.sdk.springdemo.publicbank.model.QrPaymentStatus
import com.tcn.sdk.springdemo.publicbank.retrofit.ApiInterface
import com.tcn.sdk.springdemo.publicbank.retrofit.RetrofitClient
import retrofit2.Response

class PublicBankRepository(private val apiInterface: ApiInterface = RetrofitClient.apiInterface) {

    suspend fun initGenerateQrCode(generateQr: GenerateQr): Response<GenerateQrResponse> {
        return RetrofitClient.apiInterface.initGenerateQrCode(generateQr = generateQr)
    }

    suspend fun getQrPaymentStatus(qrPaymentStatus: QrPaymentStatus): Response<String> {
        return apiInterface.getQrPaymentStatus(qrPaymentStatus = qrPaymentStatus)
    }
}