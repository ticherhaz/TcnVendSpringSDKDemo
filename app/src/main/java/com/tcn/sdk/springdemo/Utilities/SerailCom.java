package com.tcn.sdk.springdemo.Utilities;//package com.tcn.sdk.springdemo.Utilities;
//
//import com.tcn.sdk.springdemo.DBUtils.dbutis;
//import com.yy.tools.util.CYVendingMachine;
//
//    public class SerailCom {
//
//        dbutis baseActivity;
//        CYVendingMachine cyVendingMachine;
//        public SerailCom(dbutis baseActivity) {
//            this.baseActivity = baseActivity;
//        }
//
//        public void startMachine(int trid, int laneNumber) {
//            cyVendingMachine = new CYVendingMachine(laneNumber, new CYVendingMachine.ShipmentListener() {
//                @Override
//                public void Shipped(int i) {
//                    switch (i) {
//
//                        case 0:
//                            //serial port no singal
//                            baseActivity.updatedispenseStatus(laneNumber,false);
//                            break;
//                        case 1:
//                            //shipment send
//                            baseActivity.updatedispenseStatus(laneNumber,false);
//                            break;
//                        case 2:
//                            //query start
//                            baseActivity.updatedispenseStatus(laneNumber,false);
//                            break;
//                        case 3:
//                            //shipment success
//                            baseActivity.updatedispenseStatus(laneNumber,true);
//                            break;
//                        case 4:
//                            //motor fault
//                            baseActivity.updatedispenseStatus(laneNumber,false);
//                            break;
//                        case 5:
//                            //Light eyes fault
//                            baseActivity.updatedispenseStatus(laneNumber,false);
//                            break;
//                        case 6:
//                            //set balance
//                            break;
//                        case 7:
//                            //coin return is working
//                            break;
//                        case 8:
//                            // coin return is not working
//                            break;
//                        case 9:
//                            //coin return is success
//                            break;
//                        case 10:
//                            //coin return failed
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//        }
//
//        public void UnRegisterMachineListener() {
//            cyVendingMachine.DestoryCYVendingMachine();
//        }
//
//}
//
//
