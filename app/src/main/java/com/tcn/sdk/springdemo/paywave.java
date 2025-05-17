package com.tcn.sdk.springdemo;

public class paywave {
//    private ReadThread mReadThread;
//
//    String ipadress;
//
//    public paywave(String ipadress,SummaryActivity st)
//    {
//
//        this.ipadress = ipadress;
//        this.stt = st;
//        openport("");
//
//        databuf = new byte[1024];
//        this.statuscode = new byte[2];
//        cmdbuf = new byte[1024];
//        final String header="02";
//        final String initsalecomand="01";
//        String canelsalecomand="02";
//        String settlementcomand="03";
//        String showmessagecomand="05";
//        String body="02";
//        String end="02";
//
//
//
//    }
//
//
//
//
//
//
//    public static byte[] hexStringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//        return data;
//    }
//    private void proceedSale()
//    {
//
//        int iLen = hexStringToByteArray("00").length;
//        int iResp = proceedSingleSale(this.databuf, 0, iLen, this.databuf, 0);
//
//    }
//    byte CMD_PROCEED_SINGLE_SALE = 0x22;
//    public int proceedSingleSale(byte[] data, int dataoffset, int datalen, byte[] respdata, int respdataoffset)
//    {
//        int iResp;
//
//
//        try {
//            iResp =0;
//            send(CMD_PROCEED_SINGLE_SALE, data, dataoffset, datalen, this.statuscode, 0, respdata, respdataoffset, TO_SALE);
//        } catch (Exception e) {
//            iResp = 1;
//            System.out.println(e);
//        }
//        if (iResp != 0)
//            return iResp;
//
//        if (this.statuscode[0] != 0)
//            return 1;
//
//        return iResp;
//    }
//
//SummaryActivity stt;
//
//    private boolean isStart = false;
//    private Socket mMySerialLib = null;
//    private class ReadThread extends Thread {
//        private ReadThread() throws IOException {
//
//        }
//
//        public void run() {
//            super.run();
//            try {
//                System.out.println("IP FOR PAYWAVE AFTER INIT"+ipadress);
//                mMySerialLib = new Socket(ipadress, 5000);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            while (true) {
//                int i = 1;
//                int i2 = mMySerialLib != null ? 1 : 0;
//                if (!isStart) {
//                    i = 0;
//                }
//                if ((i2 & i) != 0) {
//                    try {
//                     final byte[] bArr = new byte[2];
//                        if (mMySerialLib.getInputStream() != null) {
//                            if (mMySerialLib.getInputStream().read(bArr) > 0) {
//                                stt.runOnUiThread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//
//                                System.out.println("Barla : " + ByteUtils.bytesToHexString(bArr));
//
//
//                                if (ByteUtils.bytesToHexString(bArr).equals("3230")||ByteUtils.bytesToHexString(bArr).equals("3230")) {
//                                    proceedSale();
//                                }
//
//                                if (ByteUtils.bytesToHexString(bArr).equals("1515")||ByteUtils.bytesToHexString(bArr).equals("1347")) {
//                                    stt.runOnUiThread(new Runnable() {
//
//                                        @Override
//                                        public void run() {
//
//                                            try {
//
//                                                stt.showerrdialog("failed");
//                                                closeSerialPort();
//
//                                            } catch (Exception ex) {
//                                            }
//                                        }
//                                    });
//                                }
//                                if (ByteUtils.bytesToHexString(bArr).equals("3938")||ByteUtils.bytesToHexString(bArr).equals("9186")) {
//                                    stt.runOnUiThread(new Runnable() {
//
//                                        @Override
//                                        public void run() {
//
//                                            try {
//
//                                                stt.Paywavedonepayment();
//                                                closeSerialPort();
//                                            } catch (Exception ex) {
//                                            }
//                                        }
//                                    });
//                                }
//                                    }
//                                });
//                                Thread.sleep(1000);
//                            }
//
//                        } else {
//                            return;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                }
//
//            }
//        }
//    }
//
//    public void openport(String str) {
//        try {
//
//
//            this.isStart = true;
//            this.mReadThread = new ReadThread();
//            this.mReadThread.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    /*
//     * long number to bcd byte array e.g. 123 --> (0000) 0001 0010 0011
//     * e.g. 12 ---> 0001 0010
//     */
//    public static String DecToBCDArray(double num) {
//        //- $15.99 encoded in 00 00 00 00 15 99
//        String decimal;
//        String finalvalue="";
//
//        String mainamount;
//        double doubleNumber = num;
//        String doubleAsString = String.valueOf(doubleNumber);
//        int indexOfDecimal = doubleAsString.indexOf(".");
//
//        decimal=doubleAsString.substring(indexOfDecimal).replace(".","");
//        mainamount =doubleAsString.substring(0, indexOfDecimal);
//        int leng =10-mainamount.length();
//        if(decimal.length()==1)decimal+="0";
//
//        for(int i=1;i<=leng;i++)
//        {
//
//
//
//            finalvalue=finalvalue+"0";
//        }
//        finalvalue+=mainamount+decimal;
//        return finalvalue;
//    }
//    public static String RemoveWhiteSpace(String InputText)
//    {
//        InputText = InputText.trim();
//        InputText = InputText.replace("\t", "");
//        return InputText.replace(" ", "");
//    }
//    public static int HexStringToByteArray(String data, byte[] HexOut, int HexOutOffset)
//    {
//        int iLen = 0;
//
//        data = RemoveWhiteSpace(data);
//
//        for (int i = 0; i < data.length(); i += 2)
//        {
//            HexOut[HexOutOffset + iLen] = Byte.parseByte(data.substring(i, 2),16);
//            iLen += 1;
//        }
//
//        return iLen;
//    }
//
//    byte[] databuf;
//    private byte[] statuscode;
//    public void initSingleSale(byte[] saleAmt, int saleAmtOffset, int saleAmtLen, byte[] respdata, int respdataoffset) throws Exception {
//
//        send(CMD_INIT_SINGLE_SALE, saleAmt, saleAmtOffset, saleAmtLen, this.statuscode, 0, respdata, respdataoffset, TO_SALE);
//    }
//
//
//
//    byte CMD_INIT_SINGLE_SALE = 0x21;
//    private byte[] cmdbuf;
//    public void send(byte command, byte[] data, int dataoffset, int datalen, byte[] respstatus, int respstatusoffset, byte[] respdata, int respdataoffset, int Timeoutms) throws Exception {
//        int iCmdLen;
//
//        iCmdLen = form_command(command, data, dataoffset, datalen, cmdbuf, 0);
//        if (iCmdLen < 0)
//            throw new Exception("Invalid data. Unable to form SCPI message");
//
//        sendSerialPort(cmdbuf);
//    }
//
//
//    private int form_command(byte command, byte[] data, int dataoffset, int datalen, byte[] destbuf, int destbufoffset)
//    {
//        int datalenpluscmd = 0;//= datalen+2;
//        int iOriDestBufOffset = destbufoffset;
//
//        if (command == 0xEC)
//        {
//            datalen = 0;
//        }
//
//        datalenpluscmd = datalen + 2;
//
//
//        //02 ... 3F 45 03
//
//
//        //STX
//        destbuf[destbufoffset++] = 0x02;
//        //DATALEN
//        destbuf[destbufoffset++] = (byte)(datalenpluscmd/0x100);
//        destbuf[destbufoffset++] = (byte)(datalenpluscmd%0x100);// Convert.ToByte(datalenpluscmd.ToString(), 16);
//        //SEQNO
//        if (command == 0xEC)
//        {
//            destbuf[destbufoffset++] = data[0];
//        }
//        else
//        {
//            destbuf[destbufoffset++] = SEQNO++;
//        }
//        //COMMAND
//        destbuf[destbufoffset++] = command;
//        //DATA
//        destbufoffset = arraycopy(data, dataoffset, destbuf, destbufoffset, datalen);
//        //CRC
//        int iCalcLen = destbufoffset - iOriDestBufOffset;
//        destbuf[destbufoffset++] = calculate_bcc(destbuf, iCalcLen);
//        //ETX
//        destbuf[destbufoffset++] = 0x03;
//
//        return (destbufoffset);
//    }
//
//    public static int arraycopy(byte[] source, int soureoffset, byte[] dest, int destoffset, int len)
//    {
//        System.arraycopy(source, soureoffset, dest, destoffset, len);
//        return destoffset + len;
//    }
//    private byte SEQNO = 0;
//    private byte calculate_bcc(byte[] data, int datalen)
//    {
//        int iCount;
//        byte BCC = 0;
//
//        BCC = data[0];
//        for (iCount = 1; iCount < datalen; iCount++)
//        {
//            BCC = (byte)(BCC ^ data[iCount]);
//        }
//        return BCC;
//    }
//
//    public void initSale(Double num)
//    {
//        //int iAmount = Convert.ToInt32(this.txtSaleAmt.Text, 10);
//        databuf = hexStringToByteArray(DecToBCDArray(num));
//        int iLen = databuf.length;
//
//        System.out.println("INIT SALE...");
//        System.out.println(num);
//        int iResp = 0;
//        try {
//
//            initSingleSale(this.databuf, 0, iLen, this.databuf, 0);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("RESP=" + iResp);
//        if (iResp >= 0)
//        {
//
//
//        }
//    }
//
//
//    public void closeSerialPort() {
//        try {
//            this.mMySerialLib = null;
//            this.isStart = false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendSerialPort(byte[] str) {
//        try {
//            this.mMySerialLib.getOutputStream().write(str);
//            this.mMySerialLib.getOutputStream().flush();
//        } catch (Exception e) {
//            Log.i("test", e.toString());
//        }
//    }
//
//    int TO_SALE = 30000;//ms


}
