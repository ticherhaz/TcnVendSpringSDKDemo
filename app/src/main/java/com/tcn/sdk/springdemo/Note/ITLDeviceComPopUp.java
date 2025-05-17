package com.tcn.sdk.springdemo.Note;

import com.ftdi.j2xx.FT_Device;

import java.util.ArrayList;

import device.itl.sspcoms.BarCodeReader;
import device.itl.sspcoms.DeviceEvent;
import device.itl.sspcoms.DeviceEventListener;
import device.itl.sspcoms.DeviceFileUpdateListener;
import device.itl.sspcoms.DevicePayoutEventListener;
import device.itl.sspcoms.DeviceSetupListener;
import device.itl.sspcoms.ItlCurrency;
import device.itl.sspcoms.ItlCurrencyValue;
import device.itl.sspcoms.PayoutRoute;
import device.itl.sspcoms.SSPComsConfig;
import device.itl.sspcoms.SSPDevice;
import device.itl.sspcoms.SSPPayoutEvent;
import device.itl.sspcoms.SSPSystem;
import device.itl.sspcoms.SSPUpdate;

/**
 * Created by tbeswick on 05/04/2017.
 */

public class ITLDeviceComPopUp extends Thread implements DeviceSetupListener, DeviceEventListener, DeviceFileUpdateListener, DevicePayoutEventListener {


    static final int READBUF_SIZE = 256;
    static final int WRITEBUF_SIZE = 4096;
    private static boolean isrunning = false;
    private static SSPSystem ssp;
    byte[] rbuf = new byte[READBUF_SIZE];
    byte[] wbuf = new byte[WRITEBUF_SIZE];
    int mReadSize = 0;
    private FT_Device ftDev = null;
    private SSPDevice sspDevice = null;


    public ITLDeviceComPopUp() {

        ssp = new SSPSystem();

        ssp.setOnDeviceSetupListener(this);
        ssp.setOnEventUpdateListener(this);
        ssp.setOnDeviceFileUpdateListener(this);
        ssp.setOnPayoutEventListener(this);

    }


    public void setup(FT_Device ftdev, int address, boolean escrow, boolean essp, long key) {

        ftDev = ftdev;
        ssp.SetAddress(address);
        ssp.EscrowMode(escrow);
        ssp.SetESSPMode(essp, key);

    }

    @Override
    public void run() {

        int readSize = 0;
        ssp.Run();

        isrunning = true;
        while (isrunning) {


            // poll for transmit data
            synchronized (ftDev) {
                int newdatalen = ssp.GetNewData(wbuf);
                if (newdatalen > 0) {
                    if (ssp.GetDownloadState() != SSPSystem.DownloadSetupState.active) {
                        ftDev.purge((byte) 1);
                    }
                    ftDev.write(wbuf, newdatalen);
                    ssp.SetComsBufferWritten(true);
                }
            }

            // poll for received
            synchronized (ftDev) {
                readSize = ftDev.getQueueStatus();
                if (readSize > 0) {
                    mReadSize = readSize;
                    if (mReadSize > READBUF_SIZE) {
                        mReadSize = READBUF_SIZE;
                    }
                    readSize = ftDev.read(rbuf, mReadSize);
                    ssp.ProcessResponse(rbuf, readSize);
                }
                //    } // end of if(readSize>0)
            }  // end of synchronized


            // coms config changes
            final SSPComsConfig cfg = ssp.GetComsConfig();
            if (cfg.configUpdate == SSPComsConfig.ComsConfigChangeState.ccNewConfig) {
                cfg.configUpdate = SSPComsConfig.ComsConfigChangeState.ccUpdating;
                CashNoteActivity.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CashNoteActivity.SetConfig(cfg.baud, cfg.dataBits, cfg.stopBits, cfg.parity, cfg.flowControl);
                    }
                });
                cfg.configUpdate = SSPComsConfig.ComsConfigChangeState.ccUpdated;
            }


            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void OnNewDeviceSetup(final SSPDevice dev) {

        // set local device object
        sspDevice = dev;
        // call to Main UI
        CashNoteActivity.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CashNoteActivity.DisplaySetUp(dev);
            }
        });

    }

    @Override
    public void OnDeviceDisconnect(final SSPDevice dev) {

        CashNoteActivity.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CashNoteActivity.DeviceDisconnected(dev);
            }
        });
    }


    @Override
    public void OnDeviceEvent(final DeviceEvent ev) {
        CashNoteActivity.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CashNoteActivity.DisplayEvents(ev);
            }
        });
    }


    @Override
    public void OnNewPayoutEvent(final SSPPayoutEvent ev) {

        CashNoteActivity.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CashNoteActivity.DisplayPayoutEvents(ev);
            }
        });

    }


    @Override
    public void OnFileUpdateStatus(final SSPUpdate sspUpdate) {

        CashNoteActivity.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CashNoteActivity.UpdateFileDownload(sspUpdate);
            }
        });

    }


    void Stop() {
        ssp.Close();
        isrunning = false;
    }


    boolean SetSSPDownload(final SSPUpdate update) {
        return ssp.SetDownload(update);

    }


    void SetEscrowMode(boolean mode) {
        if (ssp != null) {
            ssp.EscrowMode(mode);
        }

    }

    void SetDeviceEnable(boolean en) {
        if (ssp != null) {
            if (en) {
                ssp.EnableDevice();
            } else {
                ssp.DisableDevice();
            }
        }
    }

    void SetEscrowAction(SSPSystem.BillAction action) {
        if (ssp != null) {
            ssp.SetBillEscrowAction(action);
        }
    }


    void SetBarcocdeConfig(BarCodeReader cfg) {
        if (ssp != null) {
            ssp.SetBarCodeConfiguration(cfg);
        }
    }


    int GetDeviceCode() {
        if (ssp != null) {
            return sspDevice.headerType.getValue();
        } else {
            return -1;
        }
    }

    void SetPayoutRoute(ItlCurrency cur, PayoutRoute rt) {
        if (ssp != null) {
            ssp.SetPayoutRoute(cur, rt);
        }
    }


    void PayoutAmount(ItlCurrency cur) {
        if (ssp != null) {
            ssp.PayoutAmount(cur);
        }
    }

    void EmptyPayout() {
        if (ssp != null) {
            ssp.EmptyPayout();
        }
    }


    ArrayList<ItlCurrencyValue> GetBillPositions() {
        if (ssp != null) {
            return ssp.GetStoredBillPositions();
        } else {
            return null;
        }
    }

    void NFBillAction(SSPSystem.BillActionRequest action) {
        if (ssp != null) {
            ssp.BillPayoutAction(action);
        }
    }

}
