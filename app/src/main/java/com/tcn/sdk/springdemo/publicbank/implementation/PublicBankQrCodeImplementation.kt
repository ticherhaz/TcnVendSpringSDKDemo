package com.tcn.sdk.springdemo.publicbank.implementation

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.tcn.sdk.springdemo.Model.UserObj
import com.tcn.sdk.springdemo.R
import com.tcn.sdk.springdemo.publicbank.model.GenerateQrResponse
import com.tcn.sdk.springdemo.publicbank.model.QrPaymentStatusResponse
import com.tcn.sdk.springdemo.publicbank.retrofit.RetrofitClient
import com.tcn.sdk.springdemo.publicbank.tools.QrCodeGenerator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ticherhaz.vending_duitnow.model.TempTrans
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.Calendar
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PublicBankQrCodeImplementation(
    private val activity: Activity,
    private val userObj: UserObj,
    private val machineId: String = "",
    private val merchantCode: String = "",
    private val franchiseId: String = "",
    private val productIds: String = "",
    private var mtd: String = "",
    private val chargingPrice: Double,
    private val callback: DuitNowCallback
) {
    private val weakActivity = WeakReference(activity)
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        activity.runOnUiThread {
            showSweetAlertDialog("Coroutine Error", e.localizedMessage ?: "Unknown error")
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob() + exceptionHandler)
    private var requestQueue: RequestQueue? = null
    private var customDialog: Dialog? = null
    private var countdownTimer: CountDownTimer? = null

    private var paymentAlreadyMadeAndSuccess = false
    private var paymentCheckJob: Job? = null

    companion object {
        private const val COUNTDOWN_TIME = 120 * 1000L // 120 seconds for countdown
    }

    init {
        initShowDialog()
    }

    private fun initShowDialog() {
        weakActivity.get()?.let { activity ->
            customDialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_public_bank_duitnow)


                // Set transparent background and rounded corners
                window?.apply {
                    setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

                    // Set dialog dimensions (90% width, 50% height)
                    //val displayMetrics = context.resources.displayMetrics
                    //val width = (displayMetrics.widthPixels * 0.70).toInt()
                    //val height = (displayMetrics.heightPixels * 0.50).toInt()

                    //setLayout(width, height)

                    // Set window attributes
                    attributes = attributes.apply {
                        dimAmount = 0.5f // Background dimming
                        gravity = Gravity.CENTER // Position on screen
                    }
                }

                setCancelable(false)
                setCanceledOnTouchOutside(false)

                findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.iv_qr_code).visibility = View.GONE

                val title: String = activity.getString(R.string.proceed_with_duitnow_pay)
                val description: String = activity.getString(R.string.duitnow_pay_description)

                findViewById<TextView>(R.id.tv_title).text = title
                findViewById<TextView>(R.id.tv_description).text = description

                scope.launch {
                    showQrCodeDialog()
                }

                if (!isShowing) {

                    show()
                }
            }
        }
    }

    private fun showQrCodeDialog() {
        weakActivity.get()?.runOnUiThread {
            if (scope.isActive && customDialog?.isShowing == true) {
                customDialog?.apply {

                    val totalMessage: String = activity.getString(R.string.total_uppercase)
                    val priceMessage = totalMessage + ": RM ${"%.2f".format(chargingPrice)}"
                    findViewById<TextView>(R.id.tv_price).text = priceMessage

                    findViewById<ImageView>(R.id.iv_cancel).setOnClickListener {
                        handleImageViewCancelPressed()
                    }
                }

                paymentCheckJob = scope.launch(Dispatchers.IO) {
                    try {

                        handleQrCodeResult()

                    } catch (e: CancellationException) {
                        initOnLoggingEverything("ERROR CancellationException: QR code generation cancelled: " + e.localizedMessage)
                    } catch (e: Exception) {
                        handleQrCodeError(e.localizedMessage ?: "Unknown error")
                    }
                }
            }
        }
    }

    private fun handleQrCodeError(exception: String) {
        weakActivity.get()?.runOnUiThread {
            if (scope.isActive && customDialog?.isShowing == true) {
                val title = "QR Failed (Merchant Code: $merchantCode)"
                val message = "Error: $exception"
                showSweetAlertDialog(title, message)
            }
        }
        initOnLoggingEverything("ERROR: handleQrCodeError: $exception")
    }

    private fun showSweetAlertDialog(title: String, message: String) {
        weakActivity.get()?.runOnUiThread {
            try {
                val sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.apply {
                    setTitleText(title)
                    setContentText(message)
                    setConfirmButton("Exit") { theDialog ->
                        theDialog?.dismissWithAnimation()
                        dismissDialogDuitNow()
                        callback.enableAllUiAtTypeProductActivity()
                    }
                    if (!isShowing) {
                        show()
                    }
                }
            } catch (e: Exception) {
                initOnLoggingEverything("ERROR Exception: Error showing alert dialog: " + e.localizedMessage)
            }
        }
    }

    private fun handleQrCodeResult() {
        weakActivity.get()?.runOnUiThread {
            if (scope.isActive && customDialog?.isShowing == true) {
                try {

                    PublicBankQrCode.initGenerateQrCode(
                        machineId = machineId,
                        transactionAmount = chargingPrice,
                        object : PublicBankQrCode.QrCodeCallback {
                            override fun onSuccess(response: GenerateQrResponse) {

                                val refNo = response.refNo
                                val qrCode = response.qrCode

                                val bitmapQrCode = QrCodeGenerator.generateQrCode(qrCode, 300, 300)
                                if (bitmapQrCode != null) {

                                    // Get the QR code container and image view
                                    val qrCodeView =
                                        customDialog?.findViewById<ImageView>(R.id.iv_qr_code)!!
                                    qrCodeView.setImageBitmap(bitmapQrCode)

                                    showQrCode(refNo)

                                } else {
                                    val errorMessage = "Failed to generate QR code"

                                    initOnLoggingEverything(errorMessage)
                                    handleQrCodeError(errorMessage)
                                }
                            }

                            override fun onError(errorMessage: String) {
                                initOnLoggingEverything(errorMessage)
                                handleQrCodeError(errorMessage)
                            }
                        }
                    )


                } catch (e: Exception) {
                    handleQrCodeError(e.localizedMessage ?: "Unknown error")
                }
            }
        }
    }

    private fun startCountdown(dialog: Dialog) {
        dialog.findViewById<TextView>(R.id.tv_countdown)?.visibility = View.VISIBLE
        countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val countDownMessage = "Processing in ($secondsLeft sec)"
                dialog.findViewById<TextView>(R.id.tv_countdown)?.text =
                    countDownMessage
            }

            override fun onFinish() {
                if (!paymentAlreadyMadeAndSuccess) {

                    // We need to cancel the polling.
                    PublicBankQrCode.cleanUpQrPaymentStatus()

                    logTempTransaction(0, "Transaction failed, exceeded 120 seconds", 0.0)

                    // Delay 10 seconds and then exit
                    // Use the non-deprecated Handler constructor
                    val handlerDelay10Sec = Handler(Looper.getMainLooper())
                    val delayedRunnable = Runnable {
                        dismissDialogDuitNow()
                        callback.enableAllUiAtTypeProductActivity()
                    }
                    handlerDelay10Sec.postDelayed(delayedRunnable, 10000)

                    showTransactionFailedDialog {
                        // Cancel the delayed exit when button is clicked
                        handlerDelay10Sec.removeCallbacks(delayedRunnable)
                    }
                }
            }
        }.start()
    }

    private fun showTransactionFailedDialog(onExitButtonClicked: () -> Unit) {
        weakActivity.get()?.runOnUiThread {
            if (scope.isActive) {
                try {
                    val sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.apply {

                        val titleTransactionFailed: String =
                            activity.getString(R.string.transaction_failed)
                        val descriptionTransactionFailed: String =
                            activity.getString(R.string.transaction_failed_description)

                        setTitleText(titleTransactionFailed)
                        setContentText(descriptionTransactionFailed)
                        setCancelable(false)
                        setConfirmButton("Exit") { theDialog ->
                            onExitButtonClicked.invoke()

                            theDialog?.dismissWithAnimation()
                            dismissDialogDuitNow()
                            callback.enableAllUiAtTypeProductActivity()
                        }
                        if (!isShowing) {
                            show()
                        }
                    }
                } catch (e: Exception) {
                    initOnLoggingEverything("ERROR Exception: Error showing transaction failed dialog: " + e.localizedMessage)
                }
            }
        }
    }

    private fun handleImageViewCancelPressed() {
        weakActivity.get()?.let { activity ->
            if (scope.isActive) {
                try {
                    val sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.apply {

                        val titleTransactionCancel: String =
                            activity.getString(R.string.cancel_transaction)
                        val descriptionTransactionCancel: String =
                            activity.getString(R.string.cancel_transaction_description)

                        setTitleText(titleTransactionCancel)
                        setContentText(descriptionTransactionCancel)
                        setCancelable(false)
                        setConfirmButton("Yes") { theDialog ->
                            theDialog?.dismissWithAnimation()
                            dismissDialogDuitNow()
                            logTempTransaction(0, "Customer cancel the transaction", 0.0)
                            callback.enableAllUiAtTypeProductActivity()
                        }
                        setCancelButton("No") { theDialog ->
                            theDialog?.dismissWithAnimation()
                        }

                        if (!isShowing) {
                            show()
                        }
                    }
                } catch (e: Exception) {
                    initOnLoggingEverything("ERROR Exception: Error showing cancel dialog: " + e.localizedMessage)
                }
            }
        }
    }

    private suspend fun checkTransactionStatus(traceNo: String): String? {
        return try {
            val response = withContext(Dispatchers.IO) {
                suspendCoroutine<String> { continuation ->
                    val url = "https://vendingapi.azurewebsites.net/api/ipay88/$traceNo/status"
                    val request = object : StringRequest(
                        Method.GET, url,
                        { response -> continuation.resume(response) },
                        { error -> continuation.resumeWithException(error) }
                    ) {
                        override fun getHeaders() = mapOf(
                            "x-functions-key" to RetrofitClient.X_FUNCTION_KEY
                        )
                    }
                    request.retryPolicy = DefaultRetryPolicy(5000, 1, 1.0f) // 5s timeout, 1 retry
                    Volley.newRequestQueue(weakActivity.get()).add(request)
                }
            }

            initOnLoggingEverything("Transaction inquiry response 1-$traceNo")
            initOnLoggingEverything(
                "Transaction inquiry response 2-${
                    JSONObject(response).optString(
                        "status"
                    )
                }"
            )

            JSONObject(response).optString("status")
        } catch (e: Exception) {
            initOnLoggingEverything("ERROR Exception: checkTransactionStatus: ${e.localizedMessage}")
            null
        }
    }

    private fun handlePaymentSuccess(refNo: String) {
        paymentAlreadyMadeAndSuccess = true
        paymentCheckJob?.cancel() // Cancel the payment check job

        weakActivity.get()?.runOnUiThread {
            if (scope.isActive) {
                customDialog?.dismiss()
                updateUserTransaction(refNo)
                triggerDispense(refNo)
                logTempTransaction(1, refNo, chargingPrice)
            }
        }
    }

    private fun updateUserTransaction(transId: String) {
        weakActivity.get()?.let { activity ->
            try {
                val versionName = activity.packageManager
                    .getPackageInfo(activity.packageName, 0).versionName
                mtd = "$mtd ($transId) $versionName"
            } catch (e: PackageManager.NameNotFoundException) {
                initOnLoggingEverything("ERROR NameNotFoundException: updateUserTransaction: ${e.localizedMessage}")
            }
        }
    }

    private fun triggerDispense(transactionId: String) {
        weakActivity.get()?.let {
            callback.onPrepareStartDispensePopup(transactionId)
        }
    }

    private fun logTempTransaction(status: Int, refCode: String, paidPrice: Double) {
        scope.launch(Dispatchers.IO) {
            try {
                val transaction = TempTrans().apply {
                    amount = paidPrice
                    transDate = Calendar.getInstance().time
                    userID = userObj.getUserid()
                    franID = franchiseId
                    machineID = machineId
                    productIDs = productIds
                    paymentType = userObj.mtd
                    paymentMethod = userObj.getIpaytype()
                    paymentStatus = status
                    freePoints = ""
                    promocode = userObj.getPromname()
                    promoAmt = userObj.getPromoamt().toString()
                    vouchers = ""
                    paymentStatusDes = refCode
                }

                val response = withContext(Dispatchers.IO) {
                    suspendCoroutine { continuation ->
                        val request = JsonObjectRequest(
                            Request.Method.POST,
                            "https://vendingappapi.azurewebsites.net/Api/TempTrans",
                            JSONObject(Gson().toJson(transaction)),
                            { response -> continuation.resume(response.toString()) },
                            { error ->

                                initOnLoggingEverything("ERROR: Failed Continuation TempTran: ${error.localizedMessage}")
                                continuation.resumeWithException(error)
                            }
                        )
                        requestQueue?.add(request) ?: run {
                            Volley.newRequestQueue(weakActivity.get()).add(request)
                        }
                    }
                }
                initOnLoggingEverything("Transaction logged: $response")
            } catch (e: CancellationException) {
                initOnLoggingEverything("ERROR CancellationException: Temp transaction logging cancelled: ${e.localizedMessage}")
            } catch (e: Exception) {
                initOnLoggingEverything("ERROR Exception: Failed to log transaction: ${e.localizedMessage}")
            }
        }
    }

    fun dismissDialogDuitNow() {
        initOnLoggingEverything("Dismissing dialog and cancelling payment check")
        weakActivity.get()?.runOnUiThread {
            PublicBankQrCode.cleanUpQrPaymentStatus()

            countdownTimer?.cancel()
            countdownTimer = null
            paymentCheckJob?.cancel()
            paymentCheckJob = null
            customDialog?.dismiss()
            customDialog = null
            scope.coroutineContext.cancelChildren()
        }
    }

    private fun showQrCode(refNo: String) {
        weakActivity.get()?.runOnUiThread {
            if (scope.isActive && customDialog?.isShowing == true) {
                customDialog?.apply {
                    findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

                    val ivQrCode = findViewById<ImageView>(R.id.iv_qr_code)
                    ivQrCode.visibility = View.VISIBLE
                    startCountdown(this)

                    getQrPaymentStatus(refNo)
                }
            }
        }
    }

    private fun getQrPaymentStatus(refNo: String) {
        PublicBankQrCode.getQrPaymentStatus(
            refNo,
            object : PublicBankQrCode.QrPaymentStatusCallback {
                override fun onSuccess(response: QrPaymentStatusResponse) {
                    if (response.status) {
                        handlePaymentSuccess(response.refNo)
                    }
                }

                override fun onError(errorMessage: String) {

                    initOnLoggingEverything(errorMessage)
                    handleQrCodeError(errorMessage)
                }
            })
    }

    private fun initOnLoggingEverything(message: String) {
        callback.onLoggingEverything(message)
    }

    sealed class Result<out T> {
        data class Success<out T>(val value: T) : Result<T>()
        data class Failure(val errorMessage: String = "") : Result<Nothing>()
    }

    interface DuitNowCallback {
        fun onPrepareStartDispensePopup(transactionId: String)
        fun enableAllUiAtTypeProductActivity()
        fun onLoggingEverything(message: String)
    }
}