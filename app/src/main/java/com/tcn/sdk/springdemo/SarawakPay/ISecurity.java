package com.tcn.sdk.springdemo.SarawakPay;

import android.util.Base64;

import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ISecurity {

    private final String signKeyName = "sign";
    private final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private RSAPrivateKey rsaPrivateKey;
    private RSAPublicKey rsaPublicKey;

    public void init() {
        //pkcs8 priv
        String privateKeyString = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDBjbRDLR5IEI5T\n" +
                "w10iEqRhNAirQatVg41hY0kLZffwW5DjyW1YNEAmHeCjKVMSNitnBw1y/+LWSbGB\n" +
                "tDjKJh7TLuUqh4F0JH2v6fTk3J2YvuJDKn4J3MATUd0WWYqXEFA2SCKU8txid/Sx\n" +
                "cc8ul9zJYpqotOKUX1feiRfVkLTn7Lkn01hTu1oQEu9R8DYx1UeUjNU4xa/r1Anw\n" +
                "Smb4UQB6qJMtoy/rpNkYhih21dEHIyn5ClaOxxtHXpZ0/8OkTl9annlO546gQgHz\n" +
                "Rfkv3Aao4KSiVnKdDh/h8LcUlRkLMjQfHRlGrXi9JcRoJQIqJcqmRuedhVEZtlBH\n" +
                "Qc8EIodVAgMBAAECggEBAKFwV0WcsWA8ZupYziqoT/E/nGdzi7v00QzE6l0WfPo+\n" +
                "9Zf5Hzntwv/H9eFBi6dqBUOW71HXOtWve94Kez+pEhblS4Hwqjo8YZVD1964YL7R\n" +
                "oBmTrLL/LDoxEu4cW2tq0OyluCpycv2yvibvHXbodeCHXSaQUVieOcVEpdudKjcy\n" +
                "mLUOatTk7XufFOQi5YGy65C1pQvS95EuaD37jIZTfwy14aHYASrioH4QuEEH9clv\n" +
                "98OEKRIn8OW4Mnz1PeXSEEY3c2J8E5MUMb1GGsuMLq6o1LozYFURsE5NdYL2FnUQ\n" +
                "Vm/MBCf+l7jSESLH1R6I3C6OnjgUnGXauEucYnfGq3UCgYEA7C3T/32VDdmWe8iM\n" +
                "dM/lysTnUIgeSBYFSvHdV2mfAupbXXgaNedWta+MIVNEmh+v5FkZMPVJXQpeC2Tz\n" +
                "514CwIWJPa2IwoAdVwANIspgkJQFDzvLCTF2cuA1KSu12w3f29MzG4Cdj/O233Do\n" +
                "ZIU5a7oatk4YmDBRU2CmXZSSCE8CgYEA0cwXfCbBBVa6RIesApbXDvtCtmLr4wXA\n" +
                "A+pP/oQDQJgMSgMjWHbZUVqmTwvs3JYAIP19dW256vb/IL42b7xKMkAGqkzh6e3R\n" +
                "diPBU1ZpZZx7ZIfiU1iY33pki5boZ8EEOxT0Pj49bodmhV4Tgu8Z7Q6U0mt6ZSeJ\n" +
                "LeU0J2NpKRsCgYBMpe7YStRV41jAIWzb+CCWduKvMw3IzUS4dtgjp0aQtqgiJHk1\n" +
                "JzfvzOIIarKn5kK6M+RGDETb6iJtRj0VyF/SRUQt/SSWIufitUSOFunR4gfmUrxo\n" +
                "5mGPlI4MJ4BkcBNHjzpV4Z7A+hJOX6E8BjSFbqd+pMe8lGnASmyTkET4ZwKBgQDH\n" +
                "jNjJvhLagSOrC2ERFWh5V4Sc9npn9ZAuKTDtZaOyTO1jeJimDGWofBC0HyOsrQBy\n" +
                "YKp0h2nPlCAXhNVCclYgdcXhNj+rwgj51giMvCSEAdNcS+N6Y0sFReVc4K0uAumD\n" +
                "SOsISSsldqSNNm5YnBSM5tGnU5OXo4NXpCt3S5c7AQKBgQCHjpXICPCcQ3yxJ4st\n" +
                "x08jZoJHLpFUKD333Z6AcfYMbwXAiFj4n7TNWd+DXNBqDBm622bjxnLgRr09+q1e\n" +
                "2wmUX+WP1/q9ghaF09IShMtttSa7Z2ThDEmTj9ahee9zxuTbMdoWIY/nRbNGMN/d\n" +
                "LompZyuNhs4rzdosT2PM9VtpGw==";
        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuDoQ/Vwpr4wA6jXNNEYl\n" +
                "5acMb9xAa9mvi6zcPHduScgPhE6nvqQM0/YMjl9iVVCUcsFamOo8cfqgQ94o1Qpa\n" +
                "JO1UnvN6C0ZfEUGAOpBxoCCqk3r3gagFMGAZGwKamg5aPU3HMdi43E5BpTx0fEvw\n" +
                "gRZ/jbvA+NSH8F1sW/KEvoJh/gyqcv0iNGwjF2niIr1fQ89VmsNWJcJNjMKmSsiz\n" +
                "EXucBimqTW4T9+mqdOt2hzLM8lMQjx7J6V5omvkzKqol5qyrILcWk0pyIR7BDPD5\n" +
                "xfTX5+xQniu7gZYyRirGr4JM/6AmOlxMTlMieNqe4k25lZ2o7D2TERMZqqPYkdWc\n" +
                "gQIDAQAB";
        if (rsaPrivateKey == null) {
            try {
                rsaPrivateKey = loadPrivateKeyByStr(privateKeyString);
            } catch (Exception e) {
            }
        }

        if (rsaPublicKey == null) {
            try {
                rsaPublicKey = loadPublicKeyByStr(publicKeyString);
            } catch (Exception e) {
            }
        }
    }

    public String sign(String data) {
        try {
            JSONObject jSONObject = new JSONObject(data);
            char[] chars = data.toCharArray();
            Arrays.sort(chars);
            String signData = String.valueOf(chars);
            byte[] sign = sign(rsaPrivateKey, signData.getBytes());
            jSONObject.put(signKeyName, Base64.encodeToString(sign, Base64.NO_WRAP));

            return jSONObject.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean checkSign(String data) {
        try {
            JSONObject jSONObject = new JSONObject(data);
            String sign = jSONObject.getString(signKeyName);
            jSONObject.remove(signKeyName);
            String temp = jSONObject.toString();
            char[] chars = temp.toCharArray();
            Arrays.sort(chars);
            String signData = String.valueOf(chars);

            return verify(rsaPublicKey, Base64.decode(sign, Base64.DEFAULT), signData.getBytes());
        } catch (Exception e) {
            return false;
        }
    }

    public String encrypt(String data) {
        String result = "";

        try {
            byte[] desKey = generateKey();
            byte[] encryptedData = des3Encrypt(desKey, data.getBytes());
            byte[] encryptedDesKey = encrypt(rsaPublicKey, desKey);
            String encryptedDesKeyLength = intToStrFormatBy0(6, encryptedDesKey.length);
            byte[] resultBytes = byteMergerAll(encryptedDesKeyLength.getBytes(), encryptedDesKey, encryptedData);
            result = Base64.encodeToString(resultBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return result;
        }

        return result;
    }

    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainTextData);
        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String data) {
        String result = "";
        int desKeyFormatLength = 6;

        try {
            byte[] encryptedMessage = Base64.decode(data, Base64.DEFAULT);
            byte[] keyLengthByte = new byte[desKeyFormatLength];
            System.arraycopy(encryptedMessage, 0, keyLengthByte, 0, desKeyFormatLength);
            String keyLengthStr = new String(keyLengthByte);
            int keyLengthInt = Integer.valueOf(keyLengthStr).intValue();
            byte[] encryptedDesKey = new byte[keyLengthInt];
            System.arraycopy(encryptedMessage, desKeyFormatLength, encryptedDesKey, 0, keyLengthInt);
            byte[] decryptedDesKey = decrypt(rsaPrivateKey, encryptedDesKey);
            byte[] encryptedData = new byte[encryptedMessage.length - desKeyFormatLength - keyLengthInt];
            System.arraycopy(encryptedMessage, desKeyFormatLength + keyLengthInt, encryptedData, 0, encryptedMessage.length - desKeyFormatLength - keyLengthInt);
            byte[] dataByte = des3Decrypt(decryptedDesKey, encryptedData);
            result = new String(dataByte);
        } catch (Exception e) {
            return result;
        }

        return result;
    }

    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherData);
        } catch (Exception e) {
            return null;
        }
    }

    public RSAPublicKey loadPublicKeyByStr(String publicKeyStr) {
        try {
            byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);

            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }
    }

    public RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) {
        try {
            byte[] buffer = Base64.decode(privateKeyStr, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        int countLength = 0;

        for (int i = 0; i < values.length; i++) {
            length_byte += (values[i]).length;
        }

        byte[] all_byte = new byte[length_byte];

        for (int j = 0; j < values.length; j++) {
            byte[] b = values[j];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }

        return all_byte;
    }

    public String intToStrFormatBy0(int formatLength, int num) {
        StringBuffer formatLengthTemp = new StringBuffer();

        for (int i = 0; i < formatLength; i++) {
            formatLengthTemp.append("0");
        }

        String numStr = String.valueOf(num);
        String result = formatLengthTemp.substring(0, formatLength - numStr.length()) + numStr;

        return result;
    }

    public byte[] generateKey() {
        KeyGenerator keyGen;

        try {
            keyGen = KeyGenerator.getInstance("DESede");
        } catch (Exception e) {
            return null;
        }

        keyGen.init(168);
        SecretKey secretKey = keyGen.generateKey();

        return secretKey.getEncoded();
    }

    public byte[] des3Encrypt(byte[] key, byte[] data) {
        byte[] cipherByte;
        SecretKey secretKey = new SecretKeySpec(key, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(1, secretKey);
            cipherByte = cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }

        return cipherByte;
    }

    public byte[] des3Decrypt(byte[] key, byte[] data) {
        byte[] cipherByte;
        SecretKey secretKey = new SecretKeySpec(key, "DESede");

        try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(2, secretKey);
            cipherByte = cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }

        return cipherByte;
    }

    public byte[] sign(PrivateKey privateKey, byte[] data) throws Exception {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);

            return signature.sign();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verify(PublicKey publicKey, byte[] signatureVerify, byte[] data) throws Exception {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);

            return signature.verify(signatureVerify);
        } catch (Exception e) {
            return false;
        }
    }
}
