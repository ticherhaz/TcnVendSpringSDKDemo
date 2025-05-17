package com.tcn.sdk.springdemo.tcnSpring;

import android.content.Context;

public class TcnParamStandJs {
    public static String[] STAND_ITEM_QUERY = null;
    public static String[] STAND_ITEM_PARAM_QUERY = null;
    public static String[] STAND_ITEM_PARAM_SET = null;
    public static String[] STAND_ITEM_ACTION = null;
    private static Context mContext = null;

    public static void init(Context context) {
        mContext = context;

        STAND_ITEM_QUERY = new String[]{
                "0~通用查询，VMC上报当前需要上报的参数，下一次SN改变后上次的不再发送 ", "1~查询收入金额", "2~查询按键", "3~查询门开关状态", "4~查询温控状态", "5~查询灯带状态",
                "6~查询玻璃门除雾状态", "7~查询光检状态", "8~查询货道状态", "9~查询MDB外设状态", "200~强制要求主板一次应答所有状态"
        };

        STAND_ITEM_PARAM_QUERY = new String[]{
                "CHTP~0~查询所有温控参数", "CHTP~1~温控模式", "CHTP~2~目标温度", "CHTP~3~化霜时间", "CHTP~4~持续工作时间", "CHLEDP~0~查询所有灯带参数",
                "CHLEDP~1~灯带开关", "CHLEDP~2~电源输出百分比", "CHGLP~0~查询所有玻璃除雾参数", "CHGLP~1~玻璃除雾开关", "CHGLP~2~电源输出百分比",
                "CHMDBP~0~查询所有MDB设备参数", "CHMDBP~1~硬币器参数", "CHMDBP~2~纸币器参数", "CHMDBP~3~非现金设备参数", "CHMDBP~4~通用参数",
                "CHSLP~0~查询所有货道相关参数", "CHSLP~1~货道排列方式", "CHSLP~2~电机抖动次数", "CHSLP~3~货道驱动模式", "CHSLP~4~货道组合状态",
        };

        STAND_ITEM_PARAM_SET = new String[]{
                "TMODE~温控模式", "TARGET~目标温度", "FROST~化霜时间", "TWORK~持续工作时间", "LEDL~灯带开关", "LPOWER~灯带开关电源输出百分比",
                "GLHL~玻璃除雾开关", "GPOWER~玻璃除雾电源输出百分比", "COIN~ENABLE~设置MDB硬币器使能", "COIN~CHANGE~设置MDB硬币器退币开关", "BILL~ENABLE~设置MDB纸币器使能",
                "BILL~CHANGE~设置MDB纸币器退币开关", "BILL~ECROW~设置MDB纸币器找零开关", "BILL~CHTYPE~设置MDB纸币器找零方式", "CASHLESS~ENABLE~非现金设备使能开关",
                "CASHLESS~ENABLE~非现金设备使能开关", "CASHLESS~ALIMIT~投入金额限制",
                "ARRAY~货道排列方式", "SHAKE~电机抖动次数", "DRIVE~货道驱动模式", "COMB~货道组合状态",
        };

        STAND_ITEM_ACTION = new String[]{
                "0~光检自检", "1~货道检测", "2~预装纸币硬币", "3~清空纸币找零", "TestSelectGoods~测试选货道界面", "TestShoppingCar~测试购物车选货界面", "TestPickUpGoodsCode~测试取货码界面"
                , "TestWaitPay~测试等待支付界面", "Title~状态栏内容", "Tips~提示窗口内容", "Update~更新驱动板程序"
        };
    }
}
