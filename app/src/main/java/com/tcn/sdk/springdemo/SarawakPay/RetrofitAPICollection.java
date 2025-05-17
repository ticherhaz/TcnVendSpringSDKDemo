package com.tcn.sdk.springdemo.SarawakPay;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitAPICollection {

    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    @POST("xservice/BarCodePaymentAction.createOrder.do")
    Call<String> orderPayment(@Body String body);

    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    @POST("xservice/BarCodePaymentAction.queryOrder.do")
    Call<String> orderQuery(@Body String body);

    @Headers({"Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    @POST("xservice/BarCodePaymentAction.closeOrder.do")
    Call<String> orderCancel(@Body String body);
}
