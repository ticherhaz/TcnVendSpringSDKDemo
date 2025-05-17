package com.tcn.sdk.springdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.tcn.sdk.springdemo.DBUtils.configdata;
import com.tcn.sdk.springdemo.DBUtils.dbutis;
import com.tcn.sdk.springdemo.Dispense.AppLogger;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;

import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class paymentipay {

    /* access modifiers changed from: private */
    public static String METHOD_NAME = "EntryPageFunctionality";
    /* access modifiers changed from: private */
    public static String NAMESPACE = "https://www.mobile88.com";
    /* access modifiers changed from: private */
    public static String SOAP_ACTION = "https://www.mobile88.com/IGatewayService/EntryPageFunctionality";
    /* access modifiers changed from: private */
    public static String URL = "https://payment.ipay88.com.my/ePayment/WebService/MHGatewayService/GatewayService.svc";
    private final List<String> inputlist = new ArrayList();
    /* access modifiers changed from: private */
    public String AuthCode;
    /* access modifiers changed from: private */
    public String ErrDesc;
    /* access modifiers changed from: private */
    public String paymentid;
    public String QRCode;
    /* access modifiers changed from: private */
    public String QRValue;
    public String merchantcode = "M22515";
    public String merchantkey = "3ENiVsq71P";
    /* access modifiers changed from: private */
    public int TimeOut = 5000;
    /* access modifiers changed from: private */
    public String TransId;
    public String mid = "";
    /* renamed from: cc */
    Activity f152cc;
    ProgressDialog progressDialog;
    configdata configdb;
    private String refno;
    private List<CongifModel> congifModels;

    public paymentipay(Context cont) {
        dbutis mydb = new dbutis(cont);
        configdb = new configdata(cont);
        congifModels = configdb.getAllItems();
        if (!congifModels.isEmpty()) {
            try {

                for (CongifModel config : congifModels) {

                    merchantcode = config.getMerchantcode();
                    merchantkey = config.getMerchantkey();

                }
            } catch (Exception ex) {
            }
        }

    }

    public paymentipay() {
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public String getrefno() {
        return this.refno;
    }

    public String getAuthCode() {
        return this.AuthCode;
    }

    public String getTransId() {
        return this.TransId;
    }

    public String getErrDesc() {
        return this.ErrDesc;
    }

    public JSONObject Merchant_ScanNew(double d, String str, Integer num, Activity activity, String merchantcodep,
                                       String merchantkeyp, String mid) {

        System.out.println("ipay result is d : " + d + " str : " + str + " num :" + num);
        String str2;
        this.f152cc = activity;
        this.refno = UUID.randomUUID().toString().toUpperCase();
        JSONObject job = new JSONObject();
        merchantcode = merchantcodep;
        merchantkey = merchantkeyp;
        this.mid = mid;

        str2 = (String.format("%.2f", d)).replace(".", "");

        String sb = merchantkeyp +
                merchantcodep +
                this.refno +
                str2 +
                "MYR" +
                str;
//        String sha256 = sha256(sb.toString());
        String sha512 = buildHmacSignature(sb, merchantkeyp);
        this.inputlist.add(currencyFormat(d));
        this.inputlist.add(str);
        this.inputlist.add(this.refno);
//        this.inputlist.add(sha256);
        this.inputlist.add(sha512);
        this.inputlist.add(num.toString());
        try {

            String status = (String) new CallWebService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new List[]{this.inputlist}).get();
            job.put("Status", status);

            if (status.equals("1")) {
                job.put("AuthCode", AuthCode);
                job.put("paymentid", Integer.parseInt(paymentid));
                job.put("TransId", TransId);
            } else {
                job.put("ErrDesc", ErrDesc);
                RollingLogger.i("ipay scan ErrDesc-", ErrDesc);
            }
            return job;
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Merchant_Scan: ");
            sb2.append(e);
            Log.d("Error", sb2.toString());
            Log.d("Error", ErrDesc);
            RollingLogger.i("ipay scan Error1-", sb2.toString());
            RollingLogger.i("ipay scan Error2-", ErrDesc);
            return job;
        }
    }

    public JSONObject Merchant_Scan_Duitnow(double d, String str, Integer num, Activity activity, String merchantcodep,
                                            String merchantkeyp, String mid, String traceNo) {

        System.out.println("ipay result is d : " + d + " str : " + str + " num :" + num);
        String str2;
        this.f152cc = activity;
        this.refno = traceNo;//UUID.randomUUID().toString().toUpperCase();
        JSONObject job = new JSONObject();
        merchantcode = merchantcodep;
        merchantkey = merchantkeyp;
        this.mid = mid;

        str2 = (String.format("%.2f", d)).replace(".", "");

        StringBuilder sb = new StringBuilder();
        sb.append(merchantkey);
        sb.append(merchantcode);
        sb.append(this.refno);
        sb.append(str2);
        sb.append("MYR");
        sb.append(str);
        String sha256 = sha256(sb.toString());
        //String sha512 = buildHmacSignature(sb.toString(), "zxACvhK1Sp");
        this.inputlist.add(currencyFormat(d));
        this.inputlist.add(str);
        this.inputlist.add(this.refno);
        this.inputlist.add(sha256);
        //this.inputlist.add(sha512);
        this.inputlist.add(num.toString());

        Log.d("???", "SINI:: duitnow:: " + sb);
        try {

            String status = (String) new CallWebServiceDuitNow().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new List[]{this.inputlist}).get();
            job.put("Status", status);

            if (status.equals("1")) {
                job.put("AuthCode", AuthCode);
                job.put("paymentid", Integer.parseInt(paymentid));
                job.put("TransId", TransId);
                job.put("QRCode", QRCode);
                job.put("QRValue", QRValue);
            } else {
                job.put("ErrDesc", ErrDesc);
                RollingLogger.i("ipay scan ErrDesc-", ErrDesc);
            }
            return job;
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Merchant_Scan: ");
            sb2.append(e);
            Log.d("Error", sb2.toString());
            Log.d("Error", ErrDesc);
            RollingLogger.i("ipay scan Error1-", sb2.toString());
            RollingLogger.i("ipay scan Error2-", ErrDesc);
            return job;
        }
    }

    public JSONObject Merchant_Scan(double d, String str, Integer num, Activity activity) {

        System.out.println("ipay result is d : " + d + " str : " + str + " num :" + num);
        String str2;
        this.f152cc = activity;
        this.refno = UUID.randomUUID().toString().toUpperCase();
        JSONObject job = new JSONObject();
        //  int i = (int) (String.format("%.2f", d));
//            if (String.valueOf(i).length() < 3) {
//                str2 = String.format("%03d", new Object[]{Integer.valueOf(Integer.parseInt(String.valueOf(i)))});
//            } else {

        str2 = (String.format("%.2f", d)).replace(".", "");

        // }
        //    str2 = String.valueOf(d).replace(".","");

        String sb = merchantkey +
                merchantcode +
                this.refno +
                str2 +
                "MYR" +
                str;
//        String sha256 = sha256(sb.toString());
        String sha512 = buildHmacSignature(sb, merchantkey);
        this.inputlist.add(currencyFormat(d));
        this.inputlist.add(str);
        this.inputlist.add(this.refno);
//        this.inputlist.add(sha256);
        this.inputlist.add(sha512);
        this.inputlist.add(num.toString());
        try {

            String status = (String) new CallWebService().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new List[]{this.inputlist}).get();
            job.put("Status", status);

            if (status.equals("1")) {
                job.put("AuthCode", AuthCode);
                job.put("paymentid", Integer.parseInt(paymentid));
                job.put("TransId", TransId);
            } else {
                job.put("ErrDesc", ErrDesc);
            }
            return job;
        } catch (Exception e) {
            String sb2 = "Merchant_Scan: " +
                    e;
            Log.d("Error", sb2);
            Log.d("Error", ErrDesc);
            return job;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private String currencyFormat(double d) {
        return new DecimalFormat("###,###,##0.00").format(d);
    }

    public String sha256(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(str.getBytes(StandardCharsets.UTF_8));

            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                String hexString = Integer.toHexString(b & 0xFF);
                if (hexString.length() == 1) {
                    stringBuffer.append('0');
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            //  RSLog.appendLog(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String buildHmacSignature(String value, String secret) {
        String result;
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(),
                    "HmacSHA512");
            hmacSHA512.init(secretKeySpec);

            byte[] digest = hmacSHA512.doFinal(value.getBytes());
            BigInteger hash = new BigInteger(1, digest);
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Problemas calculando HMAC", ex);
        }
        return result;
    }

    public JSONObject Merchant_Scan_Duitnow_Requery(double d, Activity activity, String traceNo) {

        String str2;
        this.f152cc = activity;
        this.refno = traceNo;
        JSONObject job = new JSONObject();

        str2 = (String.format("%.2f", d)).replace(".", "");

        //0 code, 1 refno, 2 currency
        StringBuilder sb = new StringBuilder();
        sb.append("M41288_S0001"); //code
        sb.append(this.refno);
        sb.append("MYR");
        this.inputlist.add("M41288_S0001");
        this.inputlist.add(this.refno);
        this.inputlist.add(String.format("%.2f", d));
        try {

            String status = (String) new CallWebServiceDuitNowRequery().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new List[]{this.inputlist}).get();
            job.put("Status", status);

            if (status.equals("1")) {
                job.put("AuthCode", AuthCode);
                job.put("paymentid", Integer.parseInt(paymentid));
                job.put("TransId", TransId);
                RollingLogger.i("ipay scan status 1-", ErrDesc);
            } else {
                job.put("ErrDesc", ErrDesc);
                RollingLogger.i("ipay scan status 0 ErrDesc-", ErrDesc);
            }
            return job;
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            RollingLogger.i("ipay scan exception", e.getLocalizedMessage());
            return job;
        }
    }

    private class CallWebService extends AsyncTask<List<String>, Void, String> {
        private CallWebService() {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {

        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {


        }

        public String doInBackground(List<String>... listArr) {
            String str = "Info";
            String str2 = "Status";
            String str3 = "doInBackground: ";
            String str4 = "";
            String str5 = "http://schemas.datacontract.org/2004/07/MHPHGatewayService.Model";

            try {
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapObject soapObject2 = new SoapObject(NAMESPACE, "requestModelObj");
                PropertyInfo propertyInfo = new PropertyInfo();
                propertyInfo.setName("Amount");
                propertyInfo.setValue(listArr[0].get(0));
                propertyInfo.setType(String.class);
                propertyInfo.setNamespace(str5);
                soapObject2.addProperty(propertyInfo);
                PropertyInfo propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("BackendURL");
                propertyInfo2.setValue(str4);
                propertyInfo2.setType(String.class);
                propertyInfo2.setNamespace(str5);
                soapObject2.addProperty(propertyInfo2);
                PropertyInfo propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("BarcodeNo");
                propertyInfo3.setValue(listArr[0].get(1));
                propertyInfo3.setType(String.class);
                propertyInfo3.setNamespace(str5);
                soapObject2.addProperty(propertyInfo3);
                PropertyInfo propertyInfo4 = new PropertyInfo();
                propertyInfo4.setName("Currency");
                propertyInfo4.setValue("MYR");
                propertyInfo4.setType(String.class);
                propertyInfo4.setNamespace(str5);
                soapObject2.addProperty(propertyInfo4);
                PropertyInfo propertyInfo5 = new PropertyInfo();
                propertyInfo5.setName("MerchantCode");
                propertyInfo5.setValue(merchantcode);
                propertyInfo5.setType(String.class);
                propertyInfo5.setNamespace(str5);
                soapObject2.addProperty(propertyInfo5);
                PropertyInfo propertyInfo6 = new PropertyInfo();
                propertyInfo6.setName("PaymentId");
                propertyInfo6.setValue(listArr[0].get(4));
                propertyInfo6.setType(String.class);
                propertyInfo6.setNamespace(str5);
                soapObject2.addProperty(propertyInfo6);
                PropertyInfo propertyInfo7 = new PropertyInfo();
                propertyInfo7.setName("ProdDesc");
                propertyInfo7.setValue("RSKioskv2");
                propertyInfo7.setType(String.class);
                propertyInfo7.setNamespace(str5);
                soapObject2.addProperty(propertyInfo7);
                PropertyInfo propertyInfo8 = new PropertyInfo();
                propertyInfo8.setName("RefNo");
                propertyInfo8.setValue(listArr[0].get(2));
                propertyInfo8.setType(String.class);
                propertyInfo8.setNamespace(str5);
                soapObject2.addProperty(propertyInfo8);
                PropertyInfo propertyInfo9 = new PropertyInfo();
                propertyInfo9.setName("Remark");
                propertyInfo9.setValue(mid);
                propertyInfo9.setType(String.class);
                propertyInfo9.setNamespace(str5);
                soapObject2.addProperty(propertyInfo9);
                PropertyInfo propertyInfo10 = new PropertyInfo();
                propertyInfo10.setName("Signature");
                propertyInfo10.setValue(listArr[0].get(3));
                propertyInfo10.setType(String.class);
                propertyInfo10.setNamespace(str5);
                soapObject2.addProperty(propertyInfo10);
                PropertyInfo propertyInfo11 = new PropertyInfo();
                propertyInfo11.setName("SignatureType");
//                propertyInfo11.setValue("SHA256");
                propertyInfo11.setValue("HMACSHA512");
                propertyInfo11.setType(String.class);
                propertyInfo11.setNamespace(str5);
                soapObject2.addProperty(propertyInfo11);
                PropertyInfo propertyInfo12 = new PropertyInfo();
                propertyInfo12.setName("TerminalID");
                propertyInfo12.setValue(str4);
                propertyInfo12.setType(String.class);
                propertyInfo12.setNamespace(str5);
                soapObject2.addProperty(propertyInfo12);
                PropertyInfo propertyInfo13 = new PropertyInfo();
                propertyInfo13.setName("UserContact");
                propertyInfo13.setValue("0193336711");
                propertyInfo13.setType(String.class);
                propertyInfo13.setNamespace(str5);
                soapObject2.addProperty(propertyInfo13);
                PropertyInfo propertyInfo14 = new PropertyInfo();
                propertyInfo14.setName("UserEmail");
                propertyInfo14.setValue("rs@ratnar.com");
                propertyInfo14.setType(String.class);
                propertyInfo14.setNamespace(str5);
                soapObject2.addProperty(propertyInfo14);
                PropertyInfo propertyInfo15 = new PropertyInfo();
                propertyInfo15.setName("UserName");
                propertyInfo15.setValue("Ratnar");
                propertyInfo15.setType(String.class);
                propertyInfo15.setNamespace(str5);
                soapObject2.addProperty(propertyInfo15);
                PropertyInfo propertyInfo16 = new PropertyInfo();
                propertyInfo16.setName("lang");
                propertyInfo16.setValue("ISO-8859-1");
                propertyInfo16.setType(String.class);
                propertyInfo16.setNamespace(str5);
                soapObject2.addProperty(propertyInfo16);
                PropertyInfo propertyInfo17 = new PropertyInfo();
                propertyInfo17.setName("xField1");
                propertyInfo17.setValue(str4);
                propertyInfo17.setType(String.class);
                propertyInfo17.setNamespace(str5);
                soapObject2.addProperty(propertyInfo17);
                PropertyInfo propertyInfo18 = new PropertyInfo();
                propertyInfo18.setName("xField2");
                propertyInfo18.setValue(str4);
                propertyInfo18.setType(String.class);
                propertyInfo18.setNamespace(str5);
                soapObject2.addProperty(propertyInfo18);

                //asasas
                soapObject.addSoapObject(soapObject2);
                SoapSerializationEnvelope soapSerializationEnvelope = new SoapSerializationEnvelope(110);
                soapSerializationEnvelope.implicitTypes = true;
                soapSerializationEnvelope.dotNet = true;
                soapSerializationEnvelope.setOutputSoapObject(soapObject);
                new HttpTransportSE(URL, 60000).call(SOAP_ACTION, soapSerializationEnvelope);
                String sb = str3 +
                        soapSerializationEnvelope.getResponse().toString();
                Log.d(str, sb);
                SoapObject soapObject3 = (SoapObject) soapSerializationEnvelope.getResponse();
                String sb2 = str3 +
                        soapObject3.getProperty(str2).toString();
                Log.d(str, sb2);

                if (soapObject3.getProperty(str2).toString().equals("1")) {
                    try {
                        paymentid = soapObject3.getProperty("PaymentId").toString();
                        TransId = soapObject3.getProperty("TransId").toString();
                        AuthCode = soapObject3.getProperty("AuthCode").toString();

                    } catch (Exception s) {
                    }
                } else {
                    ErrDesc = soapObject3.getProperty("ErrDesc").toString();
                    System.out.println("ErrDesc " + ErrDesc);
                    RollingLogger.i("ipay ErrDesc-", ErrDesc);
                }
                return soapObject3.getProperty(str2).toString();

            } catch (Exception e) {
                //RSLog.appendLog(e.getMessage());
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str3);
                sb3.append(e);
                Log.d("Error", sb3.toString());
                RollingLogger.i("ipay Error-", sb3.toString());
                return str4;
            }
        }
    }

    private class CallWebServiceDuitNow extends AsyncTask<List<String>, Void, String> {
        private CallWebServiceDuitNow() {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {

        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {


        }

        public String doInBackground(List<String>... listArr) {
            String str = "Info";
            String str2 = "Status";
            String str3 = "doInBackground: ";
            String str4 = "";
            String str5 = "http://schemas.datacontract.org/2004/07/MHPHGatewayService.Model";

            try {
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapObject soapObject2 = new SoapObject(NAMESPACE, "requestModelObj");
                PropertyInfo propertyInfo = new PropertyInfo();
                propertyInfo.setName("Amount");
                propertyInfo.setValue(listArr[0].get(0));
                propertyInfo.setType(String.class);
                propertyInfo.setNamespace(str5);
                soapObject2.addProperty(propertyInfo);
                PropertyInfo propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("BackendURL");
                propertyInfo2.setValue("https://vendingapi.azurewebsites.net/api/ipay88/backend");
                propertyInfo2.setType(String.class);
                propertyInfo2.setNamespace(str5);
                soapObject2.addProperty(propertyInfo2);
                PropertyInfo propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("BarcodeNo");
                propertyInfo3.setValue(listArr[0].get(1));
                propertyInfo3.setType(String.class);
                propertyInfo3.setNamespace(str5);
                soapObject2.addProperty(propertyInfo3);
                PropertyInfo propertyInfo4 = new PropertyInfo();
                propertyInfo4.setName("Currency");
                propertyInfo4.setValue("MYR");
                propertyInfo4.setType(String.class);
                propertyInfo4.setNamespace(str5);
                soapObject2.addProperty(propertyInfo4);
                PropertyInfo propertyInfo5 = new PropertyInfo();
                propertyInfo5.setName("MerchantCode");
                propertyInfo5.setValue(merchantcode);
                Log.d("???", "merchancode at doInbacgkround:: " + merchantcode);
                propertyInfo5.setType(String.class);
                propertyInfo5.setNamespace(str5);
                soapObject2.addProperty(propertyInfo5);
                PropertyInfo propertyInfo6 = new PropertyInfo();
                propertyInfo6.setName("PaymentId");
                propertyInfo6.setValue(888);
                propertyInfo6.setType(String.class);
                propertyInfo6.setNamespace(str5);
                soapObject2.addProperty(propertyInfo6);
                PropertyInfo propertyInfo7 = new PropertyInfo();
                propertyInfo7.setName("ProdDesc");
                propertyInfo7.setValue("RSKioskv2");
                propertyInfo7.setType(String.class);
                propertyInfo7.setNamespace(str5);
                soapObject2.addProperty(propertyInfo7);
                PropertyInfo propertyInfo8 = new PropertyInfo();
                propertyInfo8.setName("RefNo");
                propertyInfo8.setValue(listArr[0].get(2));
                propertyInfo8.setType(String.class);
                propertyInfo8.setNamespace(str5);
                soapObject2.addProperty(propertyInfo8);
                PropertyInfo propertyInfo9 = new PropertyInfo();
                propertyInfo9.setName("Remark");
                propertyInfo9.setValue(mid);
                propertyInfo9.setType(String.class);
                propertyInfo9.setNamespace(str5);
                soapObject2.addProperty(propertyInfo9);
                PropertyInfo propertyInfo10 = new PropertyInfo();
                propertyInfo10.setName("Signature");
                propertyInfo10.setValue(listArr[0].get(3));
                propertyInfo10.setType(String.class);
                propertyInfo10.setNamespace(str5);
                soapObject2.addProperty(propertyInfo10);
                PropertyInfo propertyInfo11 = new PropertyInfo();
                propertyInfo11.setName("SignatureType");
                propertyInfo11.setValue("SHA256");
                // propertyInfo11.setValue("HMACSHA512");
                propertyInfo11.setType(String.class);
                propertyInfo11.setNamespace(str5);
                soapObject2.addProperty(propertyInfo11);
                PropertyInfo propertyInfo12 = new PropertyInfo();
                propertyInfo12.setName("TerminalID");
                propertyInfo12.setValue(str4);
                propertyInfo12.setType(String.class);
                propertyInfo12.setNamespace(str5);
                soapObject2.addProperty(propertyInfo12);
                PropertyInfo propertyInfo13 = new PropertyInfo();
                propertyInfo13.setName("UserContact");
                propertyInfo13.setValue("0193336711");
                propertyInfo13.setType(String.class);
                propertyInfo13.setNamespace(str5);
                soapObject2.addProperty(propertyInfo13);
                PropertyInfo propertyInfo14 = new PropertyInfo();
                propertyInfo14.setName("UserEmail");
                propertyInfo14.setValue("rs@ratnar.com");
                propertyInfo14.setType(String.class);
                propertyInfo14.setNamespace(str5);
                soapObject2.addProperty(propertyInfo14);
                PropertyInfo propertyInfo15 = new PropertyInfo();
                propertyInfo15.setName("UserName");
                propertyInfo15.setValue("Ratnar");
                propertyInfo15.setType(String.class);
                propertyInfo15.setNamespace(str5);
                soapObject2.addProperty(propertyInfo15);
                PropertyInfo propertyInfo16 = new PropertyInfo();
                propertyInfo16.setName("lang");
                propertyInfo16.setValue("ISO-8859-1");
                propertyInfo16.setType(String.class);
                propertyInfo16.setNamespace(str5);
                soapObject2.addProperty(propertyInfo16);
                PropertyInfo propertyInfo17 = new PropertyInfo();
                propertyInfo17.setName("xField1");
                propertyInfo17.setValue(str4);
                propertyInfo17.setType(String.class);
                propertyInfo17.setNamespace(str5);
                soapObject2.addProperty(propertyInfo17);
                PropertyInfo propertyInfo18 = new PropertyInfo();
                propertyInfo18.setName("xField2");
                propertyInfo18.setValue(str4);
                propertyInfo18.setType(String.class);
                propertyInfo18.setNamespace(str5);
                soapObject2.addProperty(propertyInfo18);

                //asasas
                soapObject.addSoapObject(soapObject2);
                SoapSerializationEnvelope soapSerializationEnvelope = new SoapSerializationEnvelope(110);
                soapSerializationEnvelope.implicitTypes = true;
                soapSerializationEnvelope.dotNet = true;
                soapSerializationEnvelope.setOutputSoapObject(soapObject);
                URL = "https://payment.ipay88.com.my/ePayment/WebService/MHGatewayService/GatewayService.svc";
                new HttpTransportSE(URL, 60000).call(SOAP_ACTION, soapSerializationEnvelope);
                String sb = str3 +
                        soapSerializationEnvelope.getResponse().toString();
                Log.d(str, sb);
                SoapObject soapObject3 = (SoapObject) soapSerializationEnvelope.getResponse();
                String sb2 = str3 +
                        soapObject3.getProperty(str2).toString();
                Log.d(str, sb2);
                Log.i("???", "doinbackground 2");
                if (soapObject3.getProperty(str2).toString().equals("1")) {
                    try {
                        paymentid = soapObject3.getProperty("PaymentId").toString();
                        TransId = soapObject3.getProperty("TransId").toString();
                        if (soapObject3.getProperty("AuthCode") != null) {
                            AuthCode = soapObject3.getProperty("AuthCode").toString();
                        }
                        QRCode = soapObject3.getProperty("QRCode").toString();
                        QRValue = soapObject3.getProperty("QRValue").toString();
                    } catch (Exception s) {
                    }
                } else {
                    ErrDesc = soapObject3.getProperty("ErrDesc").toString();
                    System.out.println("ErrDesc " + ErrDesc);
                    RollingLogger.i("ipay ErrDesc-", ErrDesc);
                }
                return soapObject3.getProperty(str2).toString();

            } catch (Exception e) {
                //RSLog.appendLog(e.getMessage());
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str3);
                sb3.append(e);
                Log.d("Error", sb3.toString());
                RollingLogger.i("ipay Error-", sb3.toString());
                return str4;
            }
        }
    }

    private class CallWebServiceDuitNowRequery extends AsyncTask<List<String>, Void, String> {
        private CallWebServiceDuitNowRequery() {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {

        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {


        }

        public String doInBackground(List<String>... listArr) {
            String str = "Info";
            String str2 = "Status";
            String str3 = "doInBackground: ";
            String str4 = "";
            String str5 = "http://schemas.datacontract.org/2004/07/MHPHGatewayService.Model";
            //0 code, 1 refno, 2 currency
            try {
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapObject soapObject2 = new SoapObject(NAMESPACE, "requestModelObj");
                PropertyInfo propertyInfo = new PropertyInfo();
                propertyInfo.setName("Amount");
                propertyInfo.setValue(listArr[0].get(2));
                propertyInfo.setType(String.class);
                propertyInfo.setNamespace(str5);
                soapObject2.addProperty(propertyInfo);

                PropertyInfo propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("MerchantCode");
                propertyInfo2.setValue(listArr[0].get(0));
                propertyInfo2.setType(String.class);
                propertyInfo2.setNamespace(str5);
                soapObject2.addProperty(propertyInfo2);

                PropertyInfo propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("ReferenceNo");
                propertyInfo3.setValue(listArr[0].get(1));
                propertyInfo3.setType(String.class);
                propertyInfo3.setNamespace(str5);
                soapObject2.addProperty(propertyInfo3);

                //asasas
                soapObject.addSoapObject(soapObject2);
                SoapSerializationEnvelope soapSerializationEnvelope = new SoapSerializationEnvelope(110);
                soapSerializationEnvelope.implicitTypes = true;
                soapSerializationEnvelope.dotNet = true;
                soapSerializationEnvelope.setOutputSoapObject(soapObject);
                URL = "https://payment.ipay88.com.my/ePayment/Webservice/TxInquiryCardDetails/TxDetailsInquiry.asmx";
                new HttpTransportSE(URL, 60000).call(SOAP_ACTION, soapSerializationEnvelope);
                String sb = str3 +
                        soapSerializationEnvelope.getResponse().toString();
                Log.d(str, sb);
                SoapObject soapObject3 = (SoapObject) soapSerializationEnvelope.getResponse();
                String sb2 = str3 +
                        soapObject3.getProperty(str2).toString();
                Log.d(str, sb2);

                if (soapObject3.getProperty(str2).toString().equals("1")) {
                    try {
                        paymentid = soapObject3.getProperty("PaymentId").toString();
                        TransId = soapObject3.getProperty("TransId").toString();
                        if (soapObject3.getProperty("AuthCode") != null) {
                            AuthCode = soapObject3.getProperty("AuthCode").toString();
                        }
                    } catch (Exception s) {
                    }
                } else {
                    ErrDesc = soapObject3.getProperty("ErrDesc").toString();
                    System.out.println("ErrDesc " + ErrDesc);
                    RollingLogger.i("ipay ErrDesc-", ErrDesc);
                }
                return soapObject3.getProperty(str2).toString();

            } catch (Exception e) {
                //RSLog.appendLog(e.getMessage());
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str3);
                sb3.append(e);
                Log.d("Error", sb3.toString());
                RollingLogger.i("ipay Error-", sb3.toString());
                return str4;
            }
        }
    }
}

