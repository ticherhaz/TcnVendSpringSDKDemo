package com.tcn.sdk.springdemo;

import com.tcn.springboard.control.GroupInfo;
import com.tcn.springboard.control.TcnVendIF;

import java.util.ArrayList;
import java.util.List;

public class UIComBack {
    public static final String[] SLOT_LIST = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60",
            "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80"
    };
    private static final String TAG = "UIComBack";
    public static String[] HEAT_COOL_OFF_SWITCH_SELECT = {"制冷", "加热", "关闭"}; //"Cooling", "heating", "off"
    private static UIComBack m_Instance = null;
    private final List<GropInfoBack> m_GrpShowListAll = new ArrayList<GropInfoBack>();
    private final List<GropInfoBack> m_GrpShowListSpring = new ArrayList<GropInfoBack>();
    private final List<GropInfoBack> m_GrpShowLattice = new ArrayList<GropInfoBack>();

    public static synchronized UIComBack getInstance() {
        if (null == m_Instance) {
            m_Instance = new UIComBack();
        }
        return m_Instance;
    }

    public int getGroupSpringId(String data) {
        int iId = -1;
        if ((null == data) || (data.length() < 1)) {
            return iId;
        }

        for (GropInfoBack info : m_GrpShowListSpring) {
            if (data.equals(info.getShowText())) {
                iId = info.getGrpID();
            }
        }
        return iId;
    }

    public boolean isMutiGrpSpring() {
        boolean bRet = false;
        List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListSpring();
        if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
            bRet = true;
        }
        return bRet;
    }

    public List<GropInfoBack> getGroupListAll() {
        m_GrpShowListAll.clear();
        List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListAll();
        if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
            for (int i = 0; i < mGroupInfoList.size(); i++) {
                GroupInfo info = mGroupInfoList.get(i);
                GropInfoBack mGropInfoBack = new GropInfoBack();
                mGropInfoBack.setID(i);
                mGropInfoBack.setGrpID(info.getID());
                if (info.getID() == 0) {
                    mGropInfoBack.setShowText("主柜"); //Master machine
                } else {
                    mGropInfoBack.setShowText("副柜" + info.getID()); //slave machine
                }
                m_GrpShowListAll.add(mGropInfoBack);
            }
        }
        return m_GrpShowListAll;
    }

    public String[] getGroupListSpringShow() {
        List<String> m_RetList = new ArrayList<String>();
        if (m_GrpShowListSpring.size() < 1) {
            List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListSpring();
            if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
                for (int i = 0; i < mGroupInfoList.size(); i++) {
                    GroupInfo info = mGroupInfoList.get(i);
                    GropInfoBack mGropInfoBack = new GropInfoBack();
                    mGropInfoBack.setID(i);
                    mGropInfoBack.setGrpID(info.getID());
                    if (info.getID() == 0) {
                        mGropInfoBack.setShowText("主柜");//Master machine
                    } else {
                        mGropInfoBack.setShowText("副柜" + info.getID()); //slave machine
                    }
                    m_GrpShowListSpring.add(mGropInfoBack);
                }
            }
        }

        for (GropInfoBack info : m_GrpShowListSpring) {
            m_RetList.add(info.getShowText());
        }
        if (m_RetList.size() < 1) {
            return null;
        }
        String[] strArry = new String[m_RetList.size()];
        for (int i = 0; i < m_RetList.size(); i++) {
            strArry[i] = m_RetList.get(i);
        }
        return strArry;
    }

    public int getGroupLatticeId(String data) {
        int iId = -1;
        if ((null == data) || (data.length() < 1)) {
            return iId;
        }

        for (GropInfoBack info : m_GrpShowLattice) {
            if (data.equals(info.getShowText())) {
                iId = info.getGrpID();
            }
        }
        return iId;
    }

    public String[] getGroupListLatticeShow() {
        List<String> m_RetList = new ArrayList<String>();
        if (m_GrpShowLattice.size() < 1) {
            List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListLattice();
            if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
                for (int i = 0; i < mGroupInfoList.size(); i++) {
                    GroupInfo info = mGroupInfoList.get(i);
                    GropInfoBack mGropInfoBack = new GropInfoBack();
                    mGropInfoBack.setID(i);
                    mGropInfoBack.setGrpID(info.getID());
                    if (info.getID() == 0) {
                        mGropInfoBack.setShowText("主柜");//Master machine
                    } else {
                        mGropInfoBack.setShowText("副柜" + info.getID()); //slave machine
                    }
                    m_GrpShowLattice.add(mGropInfoBack);
                }
            }
        }

        for (GropInfoBack info : m_GrpShowLattice) {
            m_RetList.add(info.getShowText());
        }
        if (m_RetList.size() < 1) {
            return null;
        }
        String[] strArry = new String[m_RetList.size()];
        for (int i = 0; i < m_RetList.size(); i++) {
            strArry[i] = m_RetList.get(i);
        }
        return strArry;
    }
}
