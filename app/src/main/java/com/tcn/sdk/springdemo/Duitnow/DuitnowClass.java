package com.tcn.sdk.springdemo.Duitnow;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tcn.sdk.springdemo.DBUtils.configdata;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.DuitnowModel;
import com.tcn.sdk.springdemo.Model.TempTrans;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.paymentipay;
import com.tcn.sdk.springdemo.tcnSpring.MainActDispenseM4;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DuitnowClass {
    private final String TAG = "DuitnowClass";
    private final WeakReference<TypeProfuctActivity> typeProfuctActivity;
    private final double chargingprice;
    private final UserObj userObj;
    private final List<CartListModel> cartListModels;
    private configdata dbconfig;
    private RequestQueue requestQueue;
    private Dialog customDialog, customdialogdispense;
    private int countRetry = 0;
    private Handler handlerDuitNow;
    private List<CongifModel> congifModels;
    private String fid, mid, productsids = "";
    private Handler handlerRetry;
    private ProgressBar progressBar;
    private ImageView qrCodeImageView;
    private boolean isDialogActive = false;
    private myAsyncTask currentTask;

    public DuitnowClass(TypeProfuctActivity typeProfuctActivity, RequestQueue requestQueue,
                        Double chargingPrice, UserObj userObj, Dialog customdialogdispense,
                        List<CartListModel> cartListModels) {

        this.requestQueue = requestQueue;
        this.typeProfuctActivity = new WeakReference<>(typeProfuctActivity);
        this.chargingprice = chargingPrice;
        this.userObj = userObj;
        this.cartListModels = cartListModels;
        countRetry = 0;
        isDialogActive = true;

        callTypeProductActivity().runOnUiThread(() -> {
            initShowDialog();
        });
        callRegisterPayment();
    }

    private TypeProfuctActivity callTypeProductActivity() {
        return typeProfuctActivity.get();
    }

    private void callRegisterPayment() {
        new Thread(() -> {
            try {
                congifModels = new ArrayList<>();
                dbconfig = new configdata(callTypeProductActivity());
                congifModels = dbconfig.getAllItems();

                for (CongifModel cn : congifModels) {
                    fid = cn.getFid();
                    mid = cn.getMid();
                }

                for (CartListModel cn : cartListModels) {
                    String log = cn.getItemprice();
                    int qty;
                    qty = Integer.parseInt(cn.getItemqty());
                    for (int x = 0; x < qty; x++) {
                        productsids += cn.getProdid() + ",";
                    }
                }

                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(callTypeProductActivity());
                }

                JSONObject jsonParam = null;
                String url = "https://vendingapi.azurewebsites.net/api/ipay88/register";

                String traceNo = UUID.randomUUID().toString().toUpperCase();

                DuitnowModel duitnowModel = new DuitnowModel();
                duitnowModel.setrefNo(traceNo);

                jsonParam = new JSONObject(new Gson().toJson(duitnowModel));

                callTypeProductActivity().runOnUiThread(() -> {
                    final TextView priceTextView = customDialog.findViewById(R.id.pricetext);
                    priceTextView.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
                });

                final String requestBody = jsonParam.toString();
                long startTime = System.nanoTime();
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            System.out.println("Trans =" + response);
                            long endTime = System.nanoTime();
                            long duration = (endTime - startTime) / 1_000_000;
                            Log.d("ResponseTime", "ResponseTime register api took " + duration + " ms");

                            if (response.equalsIgnoreCase("Success")) {
                                callDialog(traceNo);
                            }
                            RollingLogger.i(TAG, "callregisterduitnow api call response-" + response);
                            Log.i("VOLLEY", response);
                        }, error -> {
                    Log.e("VOLLEY", error.toString());
                    RollingLogger.i(TAG, "callregisterduitnow api call error-" + error);
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("x-functions-key", "9TfFiAB2OB9MaCp2DtkrlvoigxITDupIgm-JYXYUu9e4AzFuCv3K9g== ");
                        return params;
                    }
                };

                stringRequest.setRetryPolicy((new DefaultRetryPolicy(20 * 1000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
                requestQueue.add(stringRequest);
            } catch (Exception ex) {
                RollingLogger.i(TAG, "registerduitnow transaction error - " + ex);
            }
        }).start();
    }

    private void initShowDialog() {
        customDialog = new Dialog(callTypeProductActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.activity_duitnow);
        customDialog.setCancelable(true);

        if (customDialog.getWindow() != null) {
            customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        customDialog.setCanceledOnTouchOutside(false);

        progressBar = customDialog.findViewById(R.id.progress_bar);
        qrCodeImageView = customDialog.findViewById(R.id.qr_code);

        customDialog.show();
        callTypeProductActivity().runOnUiThread(() -> {
            if (!callTypeProductActivity().isFinishing()) {
                hideQrCode();
            }
        });
    }

    private void callDialog(String traceno) {
        callTypeProductActivity().runOnUiThread(() -> {
            final Button backbtn = customDialog.findViewById(R.id.backbtn);
            backbtn.setOnClickListener(view -> {
                cleanup();
                callTypeProductActivity().enableSelectionProduct();
            });
        });

        if (handlerDuitNow != null) {
            handlerDuitNow.removeCallbacksAndMessages(null);
        }
        if (handlerRetry != null) {
            handlerRetry.removeCallbacksAndMessages(null);
        }

        new Thread(() -> {
            paymentipay ipay88ewallet = new paymentipay(typeProfuctActivity.get().getApplicationContext());
            long startTime = System.nanoTime();
            final JSONObject result = ipay88ewallet.Merchant_Scan_Duitnow(chargingprice, "", 0,
                    callTypeProductActivity(),
                    ipay88ewallet.merchantcode,
                    ipay88ewallet.merchantkey,
                    ipay88ewallet.mid,
                    traceno);

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            Log.d("ResponseTime", "ResponseTime qrcode took " + duration + " ms");
            final JSONObject jsonObject = result;
            try {
                final String QRCode = jsonObject.getString("QRCode");
                final String QRValue = jsonObject.getString("QRValue");

                callTypeProductActivity().runOnUiThread(() ->
                        Glide.with(typeProfuctActivity.get())
                                .load(QRCode)
                                .override(300, 300)
                                .into(qrCodeImageView)
                );
            } catch (JSONException ignored) {
            }

            callTypeProductActivity().runOnUiThread(() -> {
                if (!callTypeProductActivity().isFinishing()) {
                    showQrCode();
                }
            });

            currentTask = new myAsyncTask();
            String[] myTaskParams = {traceno, String.format("%.2f", chargingprice), ipay88ewallet.merchantcode};
            currentTask.execute(myTaskParams);
        }).start();
    }

    private void showQrCode() {
        progressBar.setVisibility(View.GONE);
        qrCodeImageView.setVisibility(View.VISIBLE);
    }

    private void hideQrCode() {
        int color = ContextCompat.getColor(callTypeProductActivity(), R.color.colorPrimaryDark);
        progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        qrCodeImageView.setVisibility(View.GONE);
    }

    private String parseXMLForTag(String xml, String tag) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            boolean foundTag = false;
            StringBuilder tagContents = new StringBuilder();

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(tag)) {
                            foundTag = true;
                        } else if (foundTag) {
                            tagContents.append("<" + parser.getName() + ">");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(tag)) {
                            return tagContents.toString();
                        } else if (foundTag) {
                            tagContents.append("</" + parser.getName() + ">");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (foundTag) {
                            tagContents.append(parser.getText());
                        }
                        break;
                }
                eventType = parser.next();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void callapitraceno(String traceno) {
        if (!isDialogActive) return;

        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(callTypeProductActivity());
            }
            JSONObject jsonParam = null;
            String url = "https://vendingapi.azurewebsites.net/api/ipay88/" + traceno + "/status";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                System.out.println("Trans =" + response);

                String status = "", transId = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    status = jsonObject.getString("status");
                    transId = jsonObject.getString("transId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equalsIgnoreCase("1")) {
                    dismissDialog();
                    String versionName = "";
                    try {
                        versionName = callTypeProductActivity().getPackageManager()
                                .getPackageInfo(callTypeProductActivity().getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    userObj.setMtd(userObj.getMtd() + " (" + transId + ") M4 " + versionName);

                    TempTrans(1, userObj, "");

                    callTypeProductActivity().runOnUiThread(() -> {
                        Gson gson = new Gson();
                        String jsonString = gson.toJson(userObj);
                        Intent intent = new Intent(callTypeProductActivity(), MainActDispenseM4.class);
                        intent.putExtra("userObjStr", jsonString);
                        callTypeProductActivity().startActivity(intent);
                    });
                } else {
                    handlerDuitNow = new Handler(Looper.getMainLooper());
                    handlerDuitNow.postDelayed(() -> {
                        if (isDialogActive) {
                            handlerDuitNow.removeCallbacksAndMessages(null);
                            callapitraceno(traceno);
                        }
                    }, 5000L);
                }
                RollingLogger.i(TAG, "callregisterduitnow api call response-" + response);
                Log.i("VOLLEY", response);
                if (handlerRetry != null) {
                    handlerRetry.removeCallbacksAndMessages(null);
                }
            }, error -> {
                Log.e("VOLLEY", error.toString());
                RollingLogger.i(TAG, "callregisterduitnow api call error-" + error);
                if (handlerRetry != null) {
                    handlerRetry.removeCallbacksAndMessages(null);
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("x-functions-key", "9TfFiAB2OB9MaCp2DtkrlvoigxITDupIgm-JYXYUu9e4AzFuCv3K9g== ");
                    return params;
                }
            };
            stringRequest.setRetryPolicy((new DefaultRetryPolicy(20 * 1000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            RollingLogger.i(TAG, "registerduitnow transaction error - " + ex);
        }
    }

    private void TempTrans(int Status, UserObj userObj, String refCode) {
        try {
            RollingLogger.i(TAG, "temp api call start");

            Date currentTime = Calendar.getInstance().getTime();

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(callTypeProductActivity());
            }
            String tag_json_obj = "json_obj_req";
            JSONObject jsonParam = null;
            String url = "https://vendingappapi.azurewebsites.net/Api/TempTrans";
            TempTrans transactionModel = new TempTrans();
            transactionModel.setAmount(userObj.getChargingprice());
            transactionModel.setTransDate(currentTime);
            transactionModel.setUserID(userObj.getUserid());
            transactionModel.setFranID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductIDs(productsids);
            transactionModel.setPaymentType(userObj.getMtd());
            transactionModel.setPaymentMethod(userObj.getIpaytype());
            transactionModel.setPaymentStatus(Status);
            transactionModel.setFreePoints("");
            transactionModel.setPromocode(userObj.getPromname());
            transactionModel.setPromoAmt(userObj.getPromoamt() + "");
            transactionModel.setVouchers("");
            transactionModel.setPaymentStatusDes(refCode);

            try {
                jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST, url, jsonParam,
                    response -> {
                        System.out.println(response.toString());
                        RollingLogger.i(TAG, "temp api call response-" + response);
                    },
                    error -> RollingLogger.i(TAG, "temp api call error-" + error.toString()));

            requestQueue.add(myReq);
        } catch (Exception ignored) {
        }
    }

    private void dismissDialog() {
        if (customDialog != null && customDialog.isShowing()) {
            isDialogActive = false;
            customDialog.dismiss();
        }
        cleanup();
    }

    public void cleanup() {
        isDialogActive = false;
        if (handlerDuitNow != null) {
            handlerDuitNow.removeCallbacksAndMessages(null);
        }
        if (handlerRetry != null) {
            handlerRetry.removeCallbacksAndMessages(null);
        }
        if (currentTask != null && currentTask.getStatus() != AsyncTask.Status.FINISHED) {
            currentTask.cancel(true);
        }
    }

    private class myAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... pParams) {
            if (!isDialogActive || isCancelled()) return null;

            String URL = "https://payment.ipay88.com.my/ePayment/Webservice/TxInquiryCardDetails/TxDetailsInquiry.asmx";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.encodingStyle = SoapEnvelope.ENC;

            final String referenceNo = pParams[0];
            final String amount = pParams[1];
            final String merchantCode = pParams[2];

            String bodyOut = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <TxDetailsInquiryCardInfo xmlns=\"https://www.mobile88.com/epayment/webservice\">\n" +
                    "      <MerchantCode>" + merchantCode + "</MerchantCode>\n" +
                    "      <ReferenceNo>" + referenceNo + "</ReferenceNo>\n" +
                    "      <Amount>" + amount + "</Amount>\n" +
                    "      <Version>" + "1.0" + "</Version>\n" +
                    "    </TxDetailsInquiryCardInfo>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope> ";

            String xml2 = bodyOut;
            try {
                StringEntity se = new StringEntity(xml2, HTTP.UTF_8);
                se.setContentType("text/xml");
                httpPost.addHeader("https://www.mobile88.com/epayment/webservice/TxDetailsInquiryCardInfo", "http://schemas.xmlsoap.org/soap/envelope/" + "TxDetailsInquiry.asmx");
                httpPost.setEntity(se);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity resEntity = httpResponse.getEntity();
                String xml = EntityUtils.toString(resEntity);
                String result = parseXMLForTag(xml, "TxDetailsInquiryCardInfoResult");
                String Status = parseXMLForTag(result, "Status");
                String TransId = parseXMLForTag(result, "TransId");

                if (Status.equalsIgnoreCase("0")) {
                    handlerRetry = new Handler(Looper.getMainLooper());
                    handlerRetry.postDelayed(() -> {
                        if (isDialogActive && !isCancelled() && countRetry < 6) {
                            handlerRetry.removeCallbacksAndMessages(null);
                            currentTask = new myAsyncTask();
                            String[] myTaskParams = {pParams[0], String.format("%.2f", chargingprice), merchantCode};
                            currentTask.execute(myTaskParams);
                            countRetry++;
                        } else if (!isDialogActive || isCancelled()) {
                            cleanup();
                        } else {
                            callapitraceno(pParams[0]);
                        }
                    }, 5000L);
                } else {
                    callTypeProductActivity().runOnUiThread(() -> {
                        dismissDialog();
                        String versionName = "";
                        try {
                            versionName = callTypeProductActivity().getPackageManager()
                                    .getPackageInfo(callTypeProductActivity().getPackageName(), 0).versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        userObj.setMtd(userObj.getMtd() + " (" + TransId + ") M4 " + versionName);

                        Gson gson = new Gson();
                        String jsonString = gson.toJson(userObj);
                        Intent intent = new Intent(callTypeProductActivity(), MainActDispenseM4.class);
                        intent.putExtra("userObjStr", jsonString);
                        callTypeProductActivity().startActivity(intent);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}