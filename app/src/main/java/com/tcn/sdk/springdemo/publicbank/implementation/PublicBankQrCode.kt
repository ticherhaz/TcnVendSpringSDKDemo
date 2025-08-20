package com.tcn.sdk.springdemo.publicbank.implementation

import com.tcn.sdk.springdemo.publicbank.model.GenerateQr
import com.tcn.sdk.springdemo.publicbank.model.GenerateQrResponse
import com.tcn.sdk.springdemo.publicbank.model.QrPaymentStatus
import com.tcn.sdk.springdemo.publicbank.repository.PublicBankRepository
import com.tcn.sdk.springdemo.publicbank.tools.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PublicBankQrCode {
    private val publicBankRepository: PublicBankRepository by lazy { PublicBankRepository() }

    fun initGenerateQrCode(
        machineId: String,
        transactionAmount: Double,
        callback: QrCodeCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val generateQr = GenerateQr(
                    machineId = machineId.toInt(),
                    transactionAmount = transactionAmount
                )

                val response = publicBankRepository.initGenerateQrCode(generateQr = generateQr)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { qrResponse ->
                            callback.onSuccess(qrResponse)
                        } ?: callback.onError("Empty response from server")
                    } else {
                        callback.onError("HTTP error: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    interface QrCodeCallback {
        fun onSuccess(response: GenerateQrResponse)
        fun onError(errorMessage: String)
    }

    // Create a variable to hold the polling job reference
    private var pollingJob: Job? = null

    fun getQrPaymentStatus(
        refNo: String,
        callback: QrPaymentStatusCallback,
        pollingInterval: Long = 5000L // 5 seconds
    ) {
        // Cancel any existing polling before starting a new one
        stopQrPaymentStatusPolling()

        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) { // Keep polling until coroutine is cancelled
                try {
                    val simpleDateFormatPatternDate =
                        SimpleDateFormat(Constant.PATTERN_DATE, Locale.getDefault())
                    val transDate = simpleDateFormatPatternDate.format(Date())

                    val qrPaymentStatus = QrPaymentStatus(
                        merchantReference = refNo,
                        transDate = transDate
                    )

                    val response = publicBankRepository
                        .getQrPaymentStatus(qrPaymentStatus = qrPaymentStatus)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { qrPaymentStatusResponse ->
                                // Stop polling on success
                                stopQrPaymentStatusPolling()
                                callback.onSuccess(qrPaymentStatusResponse)
                            } ?: run {
                                callback.onError("Empty response from server")
                            }
                        } else {
                            if (response.code() == 404) {
                                // No payment yet, so not found
                            } else {

                                stopQrPaymentStatusPolling()
                                callback.onError(response.message())
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        callback.onError(e.message ?: "Unknown error occurred")
                    }
                }

                if (isActive) {
                    delay(pollingInterval)
                }
            }
        }
    }

    private fun stopQrPaymentStatusPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun cleanUpQrPaymentStatus() {
        stopQrPaymentStatusPolling()
    }

    interface QrPaymentStatusCallback {
        fun onSuccess(response: String)
        fun onError(errorMessage: String)
    }
}