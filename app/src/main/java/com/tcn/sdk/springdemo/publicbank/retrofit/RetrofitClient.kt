package com.tcn.sdk.springdemo.publicbank.retrofit

import com.tcn.sdk.springdemo.publicbank.tools.ConstantApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val DOMAIN = ConstantApi.BASE_URL
    const val X_FUNCTION_KEY = "9TfFiAB2OB9MaCp2DtkrlvoigxITDupIgm-JYXYUu9e4AzFuCv3K9g== "
    private val retrofitClient: Retrofit.Builder by lazy {

        val headerInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-functions-key", X_FUNCTION_KEY)
                .build()
            chain.proceed(request)
        }

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .addInterceptor(headerInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
    }

    val apiInterface: ApiInterface by lazy {
        retrofitClient
            .build()
            .create(ApiInterface::class.java)
    }

    private fun loggingInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        return loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}