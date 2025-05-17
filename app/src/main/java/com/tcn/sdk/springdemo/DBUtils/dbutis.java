package com.tcn.sdk.springdemo.DBUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tcn.sdk.springdemo.MainActivity;
import com.tcn.sdk.springdemo.Model.CartListModel;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class dbutis {

    static List<CartListModel> cartListModels;
    static String Hport = "/dev/ttyS1";
    private final ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
    public ReadThread thisreadthread;
    public Boolean dispenseInQueue = false;
    String allstatus = "";
    Boolean frommainActivity = false;
    byte[] ackBytes = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x42, 0x00, 0x43};
    Queue<byte[]> queue = new LinkedList<byte[]>();
    Context cont;
    //Contexts
    MainActivity maincont;
    Boolean interrupted;
    Handler mHandler = new Handler(Looper.getMainLooper());
    // Command Processing
    int communicationNumber = 0;
    List<Integer> RequestedProdnumber = new ArrayList<>();
    List<Integer> Dispensecommnumber = new ArrayList<>();
    List<Integer> DispenseProdnumber = new ArrayList<>();
    List<Integer> DispenseStatus = new ArrayList<>();
    //Resend Command
    Boolean justsentcommnad = false;
    byte[] commandInAction;
    int commandresendCount = 0;
    //    SerialPort mMySerialLib;
    private int no = 0;

    public dbutis(Context cont) {
        dbutis.this.cont = cont;
    }

    public dbutis(MainActivity maincont) {
        dbutis.this.maincont = maincont;
        dbutis.this.frommainActivity = true;
    }

    public static String readFileOnInternalStorage(Context mcoContext, String sFileName) {

        String lineData = "";
        File f = new File(sFileName);

        if (f.exists()) {
            try {
                FileInputStream fileInputStream = mcoContext.openFileInput(sFileName);

                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                lineData = bufferedReader.readLine();
                inputStreamReader.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
                e.printStackTrace();
            }
            // System.out.println(lineData);
            return String.valueOf(lineData);
        } else {
            return "";
        }
    }

    public static void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {

        FileOutputStream outputStream;

        try {

            outputStream = mcoContext.openFileOutput(sFileName, Context.MODE_PRIVATE);

            outputStream.write(sBody.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void startupThread() {
        try {
            if (dbutis.this.thisreadthread != null && dbutis.this.thisreadthread.isAlive()) {
                // dbutis.this.closeSerialPort();
            }
            dbutis.this.interrupted = false;
            dbutis.this.thisreadthread = new ReadThread();
            dbutis.this.thisreadthread.start();
        } catch (Exception e) {
            e.printStackTrace();
            //dbutis.this.closeSerialPort();
        }
    }

    public void interrup() {
//        thisreadthread.interrupt();
//        mMySerialLib.close();
//        mMySerialLib = null;
    }

    public void proccessCmd(byte[] cmd) {
//        System.out.println("logs-response is = " + ByteUtils.bytesToHexString(cmd));
//        System.out.println(frommainActivity + " GUJRXXX machine response is = " + Hport + " " + ByteUtils.bytesToHexString(cmd));
        try {
            //System.out.println("product no in" + Integer.parseInt(String.format("%02X", cmd[7]), 16) + "command=" + ByteUtils.bytesToHexString(cmd));
        } catch (Exception ex) {
        }

        if (0x41 == (short) (cmd[2] & 0xff)) {
            //poll

            if (dbutis.this.justsentcommnad) {
//                if (dbutis.this.commandresendCount > 5) {
                dbutis.this.commandresendCount = 0;
//                    int position = ByteUtils.bytesToInt(commandInAction[8], commandInAction[7]);
//                    int currentindex =  dbutis.this.DispenseProdnumber.indexOf(position);
//                    if(currentindex>0) {
//                        dbutis.this.DispenseProdnumber.remove(currentindex);
//                        dbutis.this.DispenseStatus.remove(currentindex);
//                    }

//                    dbutis.this.DispenseProdnumbersition);
//                    dbutis.this.DispenseStatus.add(0);
//                    dbutis.this.updatedispenseStatus(position, false);

                //dbutis.this.generateMainDispense(position + "");
                dbutis.this.justsentcommnad = false;
                //} else {
                //dbutis.this.sendSerialPort(dbutis.this.commandInAction);
                //dbutis.this.commandresendCount++;
                //}
            } else {
                if (dbutis.this.queue.size() == 0) {
                    dbutis.this.sendSerialPort(dbutis.this.ackBytes);
                    dbutis.this.justsentcommnad = false;
                    dbutis.this.commandresendCount = 0;
                } else {
                    dbutis.this.commandInAction = dbutis.this.queue.poll();
                    dbutis.this.sendSerialPort(dbutis.this.commandInAction);
                    if (0x16 != (short) (dbutis.this.commandInAction[2] & 0xff)) {
                        dbutis.this.justsentcommnad = true;
                        dbutis.this.commandresendCount = 1;
                    }

                }
            }

        } else if (0x42 == (short) (cmd[2] & 0xff)) {
            // ACK
            dbutis.this.justsentcommnad = false;
            dbutis.this.commandresendCount = 0;
        } else if (0x04 == (short) (cmd[2] & 0xff)) {

//            if (!dbutis.this.Dispensecommnumber.contains(Integer.parseInt(String.format("%02X", cmd[4]), 16))) {
            if (0x01 == (short) (cmd[5] & 0xff)) {
//                    System.out.println("product no dispensing" + Integer.parseInt(String.format("%02X", cmd[7]), 16) + "command="+ByteUtils.bytesToHexString(cmd));
                //dispensing
            } else if (0x02 == (short) (cmd[5] & 0xff)) {
                //dispensed succesfully
                dbutis.this.DispenseProdnumber.add(Integer.parseInt(String.format("%02X", cmd[7]), 16));
                dbutis.this.DispenseStatus.add(Integer.parseInt(String.format("%02X", cmd[5]), 16));
                //  dbutis.this.dispMap.put(Integer.parseInt(String.format("%02X", cmd[7]), 16),Integer.parseInt(String.format("%02X", cmd[5]), 16));
                dbutis.this.Dispensecommnumber.add(Integer.parseInt(String.format("%02X", cmd[4]), 16));

                dbutis.this.updatedispenseStatus(cmd[7], true);
                System.out.println("product no" + Integer.parseInt(String.format("%02X", cmd[7]), 16) + " dispensig status = Dispensed");

            } else {
                dbutis.this.DispenseProdnumber.add(Integer.parseInt(String.format("%02X", cmd[7]), 16));
                dbutis.this.DispenseStatus.add(Integer.parseInt(String.format("%02X", cmd[5]), 16));
                //   dbutis.this.dispMap.put(Integer.parseInt(String.format("%02X", cmd[7]), 16),Integer.parseInt(String.format("%02X", cmd[5]), 16));
                dbutis.this.Dispensecommnumber.add(Integer.parseInt(String.format("%02X", cmd[4]), 16));
                System.out.println("product no" + Integer.parseInt(String.format("%02X", cmd[7]), 16) + " dispensig status = NotDispensed. With status =" + Integer.parseInt(String.format("%02X", cmd[5]), 16));
                dbutis.this.updatedispenseStatus(cmd[7], false);
            }


//            }
            dbutis.this.sendSerialPort(dbutis.this.ackBytes);
            dbutis.this.justsentcommnad = false;
            dbutis.this.commandresendCount = 0;

            // ACK
        } else if (0x52 == (short) (cmd[2] & 0xff)) {
            dbutis.this.sendSerialPort(dbutis.this.ackBytes);
        } else if (0x23 == (short) (cmd[2] & 0xff)) {
            dbutis.this.sendSerialPort(dbutis.this.ackBytes);
            if (dbutis.this.DispenseStatus.size() >= dbutis.this.RequestedProdnumber.size()) {
                dbutis.this.productsDispensed();
            }
        } else {
            dbutis.this.sendSerialPort(dbutis.this.ackBytes);
            dbutis.this.justsentcommnad = false;
            dbutis.this.commandresendCount = 0;
        }

    }

    public void startmachine(List<CartListModel> a) {

        dbutis.this.startupThread();

        cartListModels = a;
        for (CartListModel crt : cartListModels) {
            int itmqty = Integer.parseInt(crt.itemqty);
            for (int i = 0; i < itmqty; i++) {
                dbutis.this.RequestedProdnumber.add(Integer.parseInt(crt.serial_port));
                dbutis.this.generateMainDispense(crt.serial_port);
                System.out.println("gujrxx itm number " + crt.serial_port);
            }
        }

    }


//    public void updatedispenseStatus(int cmd, boolean success) {
//
//
//        for (int i = 0; i < dbutis.this.cartListModels.size(); i++) {
//            CartListModel tempmodl = dbutis.this.cartListModels.get(i);
//            String mainnumber = dbutis.this.cartListModels.get(i).serial_port;
//            String dispno =cmd + "";
//            System.out.println("updating dispensig check " + mainnumber + " = " + dispno);
//            if (mainnumber.equals(dispno)) {
//                if (success)
//                    tempmodl.temp = 8;
//                else
//                    tempmodl.temp = 9;
//            }
//            dbutis.this.cartListModels.set(i, tempmodl);
//        }
//        if (dbutis.this.mcont != null) dbutis.this.mcont.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                dbutis.this.mcont.updatedispensestatus(dbutis.this.cartListModels);
//            }
//        });
//
//        if (dbutis.this.DispenseStatus.size() >= dbutis.this.RequestedProdnumber.size()) {
//            dbutis.this.productsDispensed();
//        }
//    }

    void updatedispenseStatus(byte cmd, boolean success) {


        int test = Integer.parseInt(String.format("%02X", cmd), 16);
        System.out.println("updating dispensig check test " + test);
        for (int i = 0; i < cartListModels.size(); i++) {
            CartListModel tempmodl = cartListModels.get(i);
            String mainnumber = cartListModels.get(i).serial_port;
            String dispno = Integer.parseInt(String.format("%02X", cmd), 16) + "";
            System.out.println("updating dispensig check " + mainnumber + " = " + dispno);
            if (mainnumber.equals(dispno)) {
                if (success) {
                    tempmodl.temp = 8;
                    cartListModels.get(i).setTemp(8);
                } else {
                    tempmodl.temp = 9;
                    cartListModels.get(i).setTemp(9);
                }
                //dbutis.this.cartListModels.set(i, tempmodl);
                System.out.println("updating dispensig check test1 " + cartListModels.get(i).getTemp());
            }
        }


    }

    public int getCommunicationNumber() {
        dbutis.this.communicationNumber = communicationNumberCls.getcommunicationNumber();
        System.out.println("dbutis.this.communicationNumber " + dbutis.this.communicationNumber);
        return dbutis.this.communicationNumber;
    }

    void productsDispensed() {

        dbutis.this.dispenseInQueue = false;


        for (int i : dbutis.this.RequestedProdnumber) {
            System.out.println("product id " + i + " is dispensed by status " + dbutis.this.DispenseStatus.get(dbutis.this.DispenseProdnumber.indexOf(i)));
            dbutis.this.allstatus += i + ":" + dbutis.this.DispenseStatus.get(DispenseProdnumber.indexOf(i)) + ",";
        }

        System.out.println("product status" + dbutis.this.allstatus);


//        Handler mHandler = new Handler(Looper.getMainLooper());

//        sdthankyou.show();
//        final String finalStatus = dbutis.this.allstatus;
//        if (dbutis.this.mcont != null) {
//            dbutis.this.mcont.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    dbutis.this.mcont.updatetransactiondb(finalStatus);
//                }
//            });
//        } else if (dbutis.this.maincont != null) {
//            dbutis.this.maincont.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    dbutis.this.maincont.updatetransection();
//                }
//            });
//            dbutis.this.cartListModels.clear();
//            dbutis.this.DispenseProdnumber.clear();
//            dbutis.this.DispenseStatus.clear();
//            dbutis.this.Dispensecommnumber.clear();

//        }

        //dbutis.this.closeSerialPort();
    }

    public void clear() {
        cartListModels.clear();
        dbutis.this.DispenseProdnumber.clear();
        dbutis.this.DispenseStatus.clear();
        dbutis.this.Dispensecommnumber.clear();
    }

    void generateMainDispense(String position) {

        int pos = Integer.parseInt(position);
//        short[] posByte = ByteUtils.Int2Short16_2(pos);

//        if (posByte.length == 1) {
//            short temp = posByte[0];
//            posByte = new short[2];
//            posByte[0] = 0;
//            posByte[1] = temp;
//        }


        //selection
        //ignore if no position number
        Integer selectionNUm = position.isEmpty() ? 0 : Integer.parseInt(position);


        //communication number
        int commNumber = getCommunicationNumber();

        //command text
        byte[] mainCommand = new byte[10];
        mainCommand[0] = (byte) 0xFA;
        mainCommand[1] = (byte) 0xFB;
        mainCommand[2] = 0x06;
        mainCommand[3] = 0x05;
        mainCommand[4] = (byte) Config.getNextNo();
        //drop sensor enabled
        mainCommand[5] = 0x01;
        //------------
        mainCommand[6] = 0x00;
//        mainCommand[7] = (byte) posByte[0];
//        mainCommand[8] = (byte) posByte[1];
//        mainCommand[mainCommand.length - 1] = (byte) ByteUtils.computerXor(mainCommand, 0, mainCommand.length - 1);
//
//        dbutis.this.queue.add(mainCommand);
//        System.out.println("gujrxx itm number queue added "+ ByteUtils.bytesToHexString(mainCommand) );
        //  sendcheckDropsensor((byte) commNumber);
        // return mainCommand;
    }

    public int getNextNo() {
        no++;
        if (no > 255) {
            no = 0;
        }
        return no;
    }

    public void generatePollTime() {


        //communication number
        int commNumber = getCommunicationNumber();

        //command text
        byte[] mainCommand = new byte[7];
        mainCommand[0] = (byte) 0xFA;
        mainCommand[1] = (byte) 0xFB;
        mainCommand[2] = 0x16;
        mainCommand[3] = 0x02;
        mainCommand[4] = (byte) commNumber;
        //interval
        mainCommand[5] = 0x05;
//        mainCommand[mainCommand.length - 1] = (byte) ByteUtils.computerXor(mainCommand, 0, mainCommand.length - 1);

        dbutis.this.queue.add(mainCommand);
        //  sendcheckDropsensor((byte) commNumber);
        // return mainCommand;
    }

    public byte[] readBytes(InputStream stream, int length) throws IOException {
        byte[] buffer = new byte[length];

        int total = 0;

        while (total < length) {
            int count = stream.read(buffer, total, length - total);
            if (count == -1) {
                break;
            }
            total += count;
        }

        if (total != length) {
            throw new IOException(String.format("Read wrong number of bytes. Got: %s, Expected: %s.", total, length));
        }

        return buffer;
    }

    public void sendSerialPort(byte[] byts) {
//        try {
//            dbutis.this.mMySerialLib.getOutputStream().write(byts);
//            dbutis.this.mMySerialLib.getOutputStream().flush();
//        } catch (Exception e) {
//            Log.i("test", e.toString());
//        }
//        System.out.println("gujrxxx sent is = " + ByteUtils.bytesToHexString(byts));
//        System.out.println("logs-sent is = " + ByteUtils.bytesToHexString(byts));
    }

    public void closeSerialPort() {
//        try {
//            dbutis.this.mBuffer.reset();
//            dbutis.this.mBuffer.flush();
//            dbutis.this.interrupted = true;
//            dbutis.this.thisreadthread.interrupt();
//            dbutis.this.mMySerialLib = null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void saveipforpaywave(String uniqueidval) {
        writeFileOnInternalStorage(cont, "ipforpaywave", uniqueidval);
    }

    public String getipforpaywave() {
        return (readFileOnInternalStorage(cont, "ipforpaywave"));
    }

    public void setmerchantcode(String uniqueidval) {
        writeFileOnInternalStorage(cont, "getmerchantcode", uniqueidval);

    }

    public void setmerchantkey(String uniqueidval) {
        writeFileOnInternalStorage(cont, "setmerchantkey", uniqueidval);

    }

    private class ReadThread extends Thread {
        private ReadThread() {
        }

        public void run() {

//            try {
//                dbutis.this.mMySerialLib = new SerialPort(new File(Hport), 57600, 0);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            super.run();

            while (!dbutis.this.interrupted) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
//                try {
//                    if (null == dbutis.this.mMySerialLib) {
//                        Thread.sleep(1000);
//                        continue;
//                    }
//
//                    int available = dbutis.this.mMySerialLib.getInputStream().available();
//                    if (0 == available) {
//                        Thread.sleep(100);
//                        continue;
//                    }
//
//
//                    try {
//                        byte[] data = readBytes(dbutis.this.mMySerialLib.getInputStream(), available);
//                        dbutis.this.mBuffer.write(data);
//                        while (!dbutis.this.interrupted) {
//                            if (Thread.currentThread().isInterrupted()) {
//                                break;
//                            }
//                            byte[] bytes = dbutis.this.mBuffer.toByteArray();
//                            int start = 0;
//                            int cmdCount = 0;
//                            boolean shuldBreak = false;
//                            for (; start <= bytes.length - 5; start++) {
//                                if ((short) (bytes[start] & 0xff) == 0xFA && (short) (bytes[start + 1] & 0xff) == 0xFB) {
//                                    try {
//                                        int len = bytes[start + 3];
//                                        byte[] cmd = new byte[len + 5];
//                                        System.arraycopy(bytes, start, cmd, 0, cmd.length);
//                                        cmdCount++;
//                                        dbutis.this.proccessCmd(cmd);
//
//                                        //Calculate how many remaining bytes to parse, if not jump out and wait to receive new bytes, if there is, continue processing
//                                        int remain = bytes.length - start - cmd.length;
//                                        if (0 == remain) {
//                                            shuldBreak = true;
//                                            dbutis.this.mBuffer.reset();
//                                            break;
//                                        }
//                                        byte[] buffer2 = new byte[remain];
//                                        System.arraycopy(bytes, start + cmd.length, buffer2, 0, buffer2.length);
//                                        dbutis.this.mBuffer.reset();
//                                        dbutis.this.mBuffer.write(buffer2);
//                                    } catch (Exception e) {
//                                        shuldBreak = true;
//                                        //The out-of-bounds exception is caused by incomplete data packets, and you can simply jump out
//                                    }
//                                    break;
//                                }
//                            }
//                            if (0 == cmdCount || shuldBreak) {
//                                break;
//                            }
//
//                        }
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                } catch (InterruptedException | IOException e) {
//                    e.printStackTrace();
//                    break;
//                }
            }
        }
    }


}