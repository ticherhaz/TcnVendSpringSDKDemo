package com.tcn.sdk.springdemo.publicbank.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.Hashtable

object QrCodeGenerator {

    /**
     * Generate QR code bitmap from string
     * @param content String content to encode in QR code
     * @param width desired width of QR code in pixels
     * @param height desired height of QR code in pixels
     * @return Bitmap of generated QR code or null if failed
     */
    fun generateQrCode(content: String, width: Int = 512, height: Int = 512): Bitmap? {
        return try {
            val hints = Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1) // White border margin
            }

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )

            bitmapFromBitMatrix(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate QR code with custom colors
     * @param content String content to encode
     * @param width desired width
     * @param height desired height
     * @param foregroundColor QR code color (default black)
     * @param backgroundColor background color (default white)
     */
    fun generateQrCode(
        content: String,
        width: Int = 512,
        height: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val hints = Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1)
            }

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )

            bitmapFromBitMatrix(bitMatrix, foregroundColor, backgroundColor)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapFromBitMatrix(
        matrix: BitMatrix,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix.get(x, y)) foregroundColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * Generate QR code with logo in center (commonly used for branded QR codes)
     */
    fun generateQrCodeWithLogo(
        content: String,
        logo: Bitmap? = null,
        width: Int = 512,
        height: Int = 512
    ): Bitmap? {
        val qrBitmap = generateQrCode(content, width, height) ?: return null
        logo ?: return qrBitmap

        return try {
            val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(combined)

            // Draw QR code
            canvas.drawBitmap(qrBitmap, 0f, 0f, null)

            // Draw logo in center
            val logoSize = (width * 0.2f).toInt() // Logo size is 20% of QR code size
            val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
            val left = (width - logoSize) / 2f
            val top = (height - logoSize) / 2f

            canvas.drawBitmap(scaledLogo, left, top, null)
            combined
        } catch (e: Exception) {
            e.printStackTrace()
            qrBitmap
        }
    }
}