package com.tcn.sdk.springdemo.DBUtils;


public class communicationNumberCls {
    public static int communicationNumber = 0;

    public static int getcommunicationNumber() {
        communicationNumberCls.communicationNumber++;
        if (communicationNumberCls.communicationNumber >= 255) {
            communicationNumberCls.communicationNumber = 0;
        }
        return communicationNumberCls.communicationNumber;
    }

    public static void setCommunicationNumber(int communicationNumber) {
        communicationNumberCls.communicationNumber = communicationNumber;
    }

}
