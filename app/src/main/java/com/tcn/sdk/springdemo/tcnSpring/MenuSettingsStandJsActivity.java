package com.tcn.sdk.springdemo.tcnSpring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.tcn.sdk.springdemo.R;
import com.ys.springboard.control.ConstantsDrive5inch;
import com.ys.springboard.control.MessageFromDrive;
import com.ys.springboard.control.MessageFromUI0203Crc;
import com.ys.springboard.control.TcnDriveCmdType;
import com.ys.springboard.control.TcnDrivesConstant;
import com.ys.springboard.control.TcnShareUseData;
import com.ys.springboard.control.TcnVendIF;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MenuSettingsStandJsActivity extends TcnMainActivity {
    private static final String TAG = "MenuSettingsStandJsActivity";
    private final SwitchButtonListener m_SwitchButtonListener = new SwitchButtonListener();
    private final ButtonEditClickListener m_ButtonEditClickListener = new ButtonEditClickListener();
    private int singleitem = 0;
    private OutDialog m_OutDialog = null;
    private Titlebar m_Titlebar = null;
    private ButtonSwitch menu_ys_light_check = null;
    private ButtonEditSelectD menu_ys_query_drive_fault = null;
    private ButtonEditSelectD menu_ys_clear_drive_fault = null;
    private ButtonEditSelectD menu_ys_action = null;
    private ButtonEditSelectD menu_ys_query_drive_info = null;
    private ButtonEditSelectD menu_ys_query_param = null;
    private ButtonEditSelectD menu_ys_set_param_select = null;
    private TextView tv_read_cmd, tv_read_string;
    private MenuSetTitleBarListener m_TitleBarListener = new MenuSetTitleBarListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_menu_settings_layout_standjs_board);
        TcnVendIF.getInstance().LoggerDebug(TAG, "onCreate()");
        TcnParamStandJs.init(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TcnVendIF.getInstance().LoggerDebug(TAG, "onResume()");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TcnVendIF.getInstance().LoggerDebug(TAG, "onPause()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TcnVendIF.getInstance().LoggerDebug(TAG, "onDestroy()");
        if (m_Titlebar != null) {
            m_Titlebar.removeButtonListener();
            m_Titlebar = null;
        }
        if (menu_ys_light_check != null) {
            menu_ys_light_check.removeButtonListener();
            menu_ys_light_check = null;
        }
        if (menu_ys_query_drive_fault != null) {
            menu_ys_query_drive_fault.removeButtonListener();
            menu_ys_query_drive_fault = null;
        }
        if (menu_ys_clear_drive_fault != null) {
            menu_ys_clear_drive_fault.removeButtonListener();
            menu_ys_clear_drive_fault = null;
        }

        if (menu_ys_query_drive_info != null) {
            menu_ys_query_drive_info.removeButtonListener();
            menu_ys_query_drive_info = null;
        }

        if (menu_ys_action != null) {
            menu_ys_action.removeButtonListener();
            menu_ys_action = null;
        }

        if (menu_ys_query_param != null) {
            menu_ys_query_param.removeButtonListener();
            menu_ys_query_param = null;
        }

        if (menu_ys_set_param_select != null) {
            menu_ys_set_param_select.removeButtonListener();
            menu_ys_set_param_select = null;
        }
        if (m_OutDialog != null) {
            m_OutDialog.deInit();
            m_OutDialog = null;
        }

        m_TitleBarListener = null;

    }

    private void initView() {
        m_Titlebar = findViewById(R.id.menu_setttings_titlebar);
        if (m_Titlebar != null) {
            m_Titlebar.setButtonType(Titlebar.BUTTON_TYPE_BACK);
            m_Titlebar.setButtonName(R.string.background_menu_settings);
            m_Titlebar.setTitleBarListener(m_TitleBarListener);
        }

        menu_ys_light_check = findViewById(R.id.menu_ys_light_check);
        if (menu_ys_light_check != null) {
            menu_ys_light_check.setButtonName(R.string.background_menu_drop_sensor_whole);
            menu_ys_light_check.setButtonListener(m_SwitchButtonListener);
            menu_ys_light_check.setTextSize(TcnVendIF.getInstance().getFitScreenSize(22));
            menu_ys_light_check.setSwitchState(TcnShareUseData.getInstance().isDropSensorCheck());
            String dataType = TcnShareUseData.getInstance().getTcnDataType();
            menu_ys_light_check.setVisibility(View.GONE);
        }

        menu_ys_query_drive_fault = findViewById(R.id.menu_ys_query_drive_fault);
        if (menu_ys_query_drive_fault != null) {
//			menu_ys_query_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
            menu_ys_query_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);

            menu_ys_query_drive_fault.setButtonName(getString(R.string.background_drive_query_fault));
            menu_ys_query_drive_fault.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
            menu_ys_query_drive_fault.setButtonQueryText(getString(R.string.background_drive_query));
            menu_ys_query_drive_fault.setButtonQueryTextColor("#ffffff");
            menu_ys_query_drive_fault.setButtonDisplayTextColor("#4e5d72");
            menu_ys_query_drive_fault.setButtonListener(m_ButtonEditClickListener);
        }


        menu_ys_clear_drive_fault = findViewById(R.id.menu_ys_clear_drive_fault);
        if (menu_ys_clear_drive_fault != null) {
            menu_ys_clear_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);

            menu_ys_clear_drive_fault.setButtonName(getString(R.string.background_drive_clean_fault));
            menu_ys_clear_drive_fault.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
            menu_ys_clear_drive_fault.setButtonQueryText(R.string.background_drive_clean);
            menu_ys_clear_drive_fault.setButtonQueryTextColor("#ffffff");
            menu_ys_clear_drive_fault.setButtonDisplayTextColor("#4e5d72");
            menu_ys_clear_drive_fault.setButtonListener(m_ButtonEditClickListener);
            menu_ys_clear_drive_fault.setVisibility(View.GONE);
        }

        menu_ys_query_drive_info = findViewById(R.id.menu_ys_query_drive_info);
        if (menu_ys_query_drive_info != null) {
            menu_ys_query_drive_info.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);

            menu_ys_query_drive_info.setButtonName("查询驱动板程序版本");
            menu_ys_query_drive_info.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
            menu_ys_query_drive_info.setButtonQueryText(R.string.background_drive_query);
            menu_ys_query_drive_info.setButtonQueryTextColor("#ffffff");
            menu_ys_query_drive_info.setButtonDisplayTextColor("#4e5d72");
            menu_ys_query_drive_info.setButtonListener(m_ButtonEditClickListener);
        }

        menu_ys_action = findViewById(R.id.menu_ys_action);
        if (menu_ys_action != null) {
            menu_ys_action.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_INPUT_QUERY);

            menu_ys_action.setButtonQueryText(getString(R.string.background_lift_do_start));
            menu_ys_action.setButtonQueryTextColor("#ffffff");
            menu_ys_action.setButtonDisplayTextColor("#4e5d72");
            menu_ys_action.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_action.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
            menu_ys_action.setButtonListener(m_ButtonEditClickListener);
        }

        menu_ys_query_param = findViewById(R.id.menu_ys_query_param);
        if (menu_ys_query_param != null) {
            menu_ys_query_param.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);

            menu_ys_query_param.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
            menu_ys_query_param.setButtonName(getString(R.string.background_lift_query_params));
            menu_ys_query_param.setButtonQueryText(getString(R.string.background_drive_query));
            menu_ys_query_param.setButtonQueryTextColor("#ffffff");
            menu_ys_query_param.setButtonDisplayTextColor("#4e5d72");
            menu_ys_query_param.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_query_param.setButtonEditTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_query_param.setButtonEditTextSizeSecond(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_query_param.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
            menu_ys_query_param.setButtonListener(m_ButtonEditClickListener);
        }

        menu_ys_set_param_select = findViewById(R.id.menu_ys_set_param_select);
        if (menu_ys_set_param_select != null) {
            menu_ys_set_param_select.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_INPUT_QUERY);

            menu_ys_set_param_select.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
            menu_ys_set_param_select.setButtonName(getString(R.string.background_lift_set_params));
            menu_ys_set_param_select.setButtonQueryText(getString(R.string.background_drive_set));
            menu_ys_set_param_select.setButtonQueryTextColor("#ffffff");
            menu_ys_set_param_select.setButtonDisplayTextColor("#4e5d72");
            menu_ys_set_param_select.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_set_param_select.setButtonEditTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_set_param_select.setButtonEditTextSizeSecond(TcnVendIF.getInstance().getFitScreenSize(16));
            menu_ys_set_param_select.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
            menu_ys_set_param_select.setButtonListener(m_ButtonEditClickListener);
        }

        tv_read_cmd = findViewById(R.id.tv_read_cmd);
        tv_read_string = findViewById(R.id.tv_read_string);
    }

    private void setDialogShow() {
        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(MenuSettingsStandJsActivity.this, "", getString(R.string.background_drive_setting));
            m_OutDialog.setShowTime(10000);
        }

        if (m_OutDialog != null) {
            m_OutDialog.setShowTime(3);
            m_OutDialog.setTitle(getString(R.string.background_tip_wait_amoment));
            m_OutDialog.show();
        }
    }

    private void showSelectDialog(final int type, String title, final EditText v, String selectData, final String[] str) {
        if (null == str) {
            return;
        }
        int checkedItem = -1;
        if ((selectData != null) && (selectData.length() > 0)) {
            for (int i = 0; i < str.length; i++) {
                if (str[i].equals(selectData)) {
                    checkedItem = i;
                    break;
                }
            }
        }

        singleitem = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(str, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                singleitem = which;
            }
        });
        builder.setPositiveButton(getString(R.string.background_backgroound_ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                v.setText(str[singleitem]);
            }
        });
        builder.setNegativeButton(getString(R.string.background_backgroound_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageFromDrive event) {
        TcnVendIF.getInstance().LoggerDebug(TAG, "onEventMainThread() getMsgType: " + event.getMsgType() + " getIntData1: " + event.getIntData1() + " getIntData2: " + event.getIntData2()
                + " getStringData1: " + event.getStringData1() + " getStringData2: " + event.getStringData2() + " getJsonObject: " + event.getJsonObject());
        switch (event.getMsgType()) {
            case ConstantsDrive5inch.CMD_SEND_TO_UI_STRING:
                if (event.getIntData1() == 1) {
                    // 具体的指令
                    if (tv_read_cmd != null) {
                        tv_read_cmd.setText(event.getStringData1());
                    }
                } else if (event.getIntData1() == 2) {
                    // 解析后的json
                    if (tv_read_string != null) {
                        if (event.getStringData1() != null) {
                            tv_read_string.setText(event.getStringData1());
                        }

                    }
                }
                break;
            case ConstantsDrive5inch.CMD_QUERY_PARAMETERS:
                String sq = "";
                switch (event.getStringData1()) {
                    case "CHTP":
                        sq = event.getIntData1() + ";" + event.getIntData2() + ";" + event.getIntData3() + ";" + event.getIntData4();
                        break;
                    case "CHLEDP":
                        sq = event.getIntData1() + ";" + event.getIntData2();
                        break;
                    case "CHGLP":
                        sq = event.getIntData1() + ";" + event.getIntData2();
                        break;
                    case "CHMDBP":
                        sq = event.getJsonObject().getAsString();
                        break;
                    case "CHSLP":
                        sq = event.getIntData1() + ";" + event.getIntData2() + ";" + event.getIntData3() + ";" + event.getIntData4();
                        break;

                }
                if (menu_ys_query_param != null) {
                    menu_ys_query_param.setButtonDisplayText(sq);
                }
                break;
            case ConstantsDrive5inch.CMD_SET_PARAMETERS:
                break;
            case ConstantsDrive5inch.CMD_SET_PERIPHERAL:
                break;
            case ConstantsDrive5inch.CMD_SET_ISSUED:
                break;
        }
    }

    private class MenuSetTitleBarListener implements Titlebar.TitleBarListener {

        @Override
        public void onClick(View v, int buttonId) {
            if (Titlebar.BUTTON_ID_BACK == buttonId) {
                MenuSettingsStandJsActivity.this.finish();
            }
        }
    }

    private class SwitchButtonListener implements ButtonSwitch.ButtonListener {

        @Override
        public void onSwitched(View v, boolean isSwitchOn) {
            int iId = v.getId();
            if (R.id.menu_ys_light_check == iId) {
                TcnShareUseData.getInstance().setDropSensorCheck(isSwitchOn);
            } else {

            }
        }
    }

    private class ButtonEditClickListener implements ButtonEditSelectD.ButtonListener {
        @Override
        public void onClick(View v, int buttonId) {
            if (null == v) {
                return;
            }
            if (TcnUtilityUI.isFastClick()) {
                return;
            }
            int id = v.getId();
            if (R.id.menu_ys_query_drive_fault == id) {
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    menu_ys_query_drive_fault.setButtonDisplayText("");
                    String strParamSecond = menu_ys_query_drive_fault.getButtonEditTextSecond();
                    if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
                        TcnUtilityUI.getToast(MenuSettingsStandJsActivity.this, getString(R.string.background_lift_tips_select_floor_no));
                    } else {
                        if (strParamSecond.contains("~")) {
                            int index = strParamSecond.indexOf("~");
                            strParamSecond = strParamSecond.substring(0, index).trim();
                        }
                        EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_QUERY_STATUS, Integer.parseInt(strParamSecond), -1, -1, -1, false,
                                null, null, null, null, null, null));
                    }
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
//					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_ys_query_drive_fault.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    showSelectDialog(-1, getString(R.string.background_please_choose), menu_ys_query_drive_fault.getButtonEditSecond(), "", TcnParamStandJs.STAND_ITEM_QUERY);
                } else {

                }

            } else if (R.id.menu_ys_query_drive_info == id) {
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    menu_ys_query_drive_info.setButtonDisplayText("");
                    EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_QUERY_MACHINE_INFO, -1, -1, -1, -1, false,
                            null, null, null, null, null, null));

                }
            } else if (R.id.menu_ys_action == id) {
                // 动作执行
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    menu_ys_action.setButtonDisplayText("");
                    String strParamSecond = menu_ys_action.getButtonEditTextSecond();
                    if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
                        TcnUtilityUI.getToast(MenuSettingsStandJsActivity.this, getString(R.string.background_lift_tips_select_floor_no));
                    } else {
                        JsonObject jsonObjectParam = null;
                        if (strParamSecond.contains("~")) {
                            int index = strParamSecond.indexOf("~");
                           /* int indexNext = strParamSecond.indexOf("~", index + 1);
                            String paramKey = strParamSecond.substring(0, index).trim();
                            String paramValue = strParamSecond.substring(index + 1, indexNext).trim();

                            TcnBoardIF.getInstance().LoggerDebug(TAG, "menu_ys_query_param paramKey: " + paramKey + " paramValue: " + paramValue);

                            jsonObjectParam = new JsonObject();
                            jsonObjectParam.addProperty(paramKey, paramValue); */

                            String strDoType = strParamSecond.substring(0, index).trim();
                            if (TcnVendIF.getInstance().isNumeric(strDoType)) {
                                int doType = Integer.parseInt(strDoType);
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_ACTION_DO, doType, -1, -1, -1, false,
                                        null, null, null, null, null, jsonObjectParam));
                            } else if (strParamSecond.contains("TestSelectGoods~")) {
                                String strSlotNo = menu_ys_action.getButtonEditInputText();
                                if (TcnVendIF.getInstance().isDigital(strSlotNo)) {
                                    EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 2, Integer.parseInt(strSlotNo), -1, -1, false,
                                            null, null, null, null, null, jsonObjectParam));
                                } else {
                                    EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 2, -1, -1, -1, false,
                                            null, null, null, null, null, jsonObjectParam));
                                }

                            } else if (strParamSecond.contains("TestShoppingCar~")) {
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 3, 25, -1, -1, false,
                                        null, null, null, null, null, jsonObjectParam));
                            } else if (strParamSecond.contains("TestPickUpGoodsCode~")) {
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 4, -1, -1, -1, false,
                                        null, "12345678", null, null, null, jsonObjectParam));
                            } else if (strParamSecond.contains("TestWaitPay~")) {
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 5, -1, -1, -1, false,
                                        null, null, null, null, null, jsonObjectParam));
                            } else if (strParamSecond.contains("Title~")) {
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 6, -1, -1, -1, false,
                                        null, null, null, null, null, jsonObjectParam));
                            } else if (strParamSecond.contains("Tips~")) {
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SHOW_PAGE, 7, -1, -1, -1, false,
                                        null, null, null, null, null, jsonObjectParam));
                            } else if (strParamSecond.contains("Update~")) {
                                EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_UPDATE_SOFT, -1, -1, -1, -1, false,
                                        null, null, null, null, null, null));
                            } else {

                            }

                        }

                    }
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    showSelectDialog(-1, getString(R.string.background_please_choose), menu_ys_action.getButtonEditSecond(), "", TcnParamStandJs.STAND_ITEM_ACTION);
                }
            } else if (R.id.menu_ys_query_param == id) {
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    menu_ys_query_param.setButtonDisplayText("");
                    String strParamSecond = menu_ys_query_param.getButtonEditTextSecond();
                    if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
                        TcnUtilityUI.getToast(MenuSettingsStandJsActivity.this, getString(R.string.background_lift_tips_select_floor_no));
                    } else {
                        JsonObject jsonObjectParam = null;
                        if (strParamSecond.contains("~")) {
                            int index = strParamSecond.indexOf("~");
                            int indexNext = strParamSecond.indexOf("~", index + 1);
                            String paramKey = strParamSecond.substring(0, index).trim();
                            String paramValue = strParamSecond.substring(index + 1, indexNext).trim();
                            int paramValueInt = -1;
                            try {
                                paramValueInt = Integer.parseInt(paramValue);
                            } catch (Exception e) {
                                TcnUtilityUI.getToast(MenuSettingsStandJsActivity.this, getString(R.string.background_coffee_data_error));
                            }
                            TcnVendIF.getInstance().LoggerDebug(TAG, "menu_ys_query_param paramKey: " + paramKey + " paramValue: " + paramValue);

                            jsonObjectParam = new JsonObject();
                            jsonObjectParam.addProperty(paramKey, paramValueInt);
                        }
                        EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_QUERY_PARAMETERS, -1, -1, -1, -1, false,
                                null, null, null, null, null, jsonObjectParam));
                    }
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
//					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_ys_query_param.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    showSelectDialog(-1, getString(R.string.background_please_choose), menu_ys_query_param.getButtonEditSecond(), "", TcnParamStandJs.STAND_ITEM_PARAM_QUERY);
                } else {

                }
            } else if (R.id.menu_ys_set_param_select == id) {
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    menu_ys_set_param_select.setButtonDisplayText("");
                    String strParamSecond = menu_ys_set_param_select.getButtonEditTextSecond();
                    if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
                        TcnUtilityUI.getToast(MenuSettingsStandJsActivity.this, getString(R.string.background_lift_tips_select_floor_no));
                    } else {
                        JsonObject jsonObjectParam = null;
                        if (strParamSecond.contains("~")) {
                            int index = strParamSecond.indexOf("~");
                            int indexNext = strParamSecond.indexOf("~", index + 1);
                            String paramKey = strParamSecond.substring(0, index).trim();
                            String paramKey1 = null;
                            String paramValue = menu_ys_set_param_select.getButtonEditInputText();
                            int paramValueint = -1;
                            try {
                                paramValueint = Integer.parseInt(paramValue);
                            } catch (Exception e) {
                                TcnUtilityUI.getToast(MenuSettingsStandJsActivity.this, getString(R.string.background_coffee_data_error));
                            }

                            jsonObjectParam = new JsonObject();
                            if (indexNext > 0) {
                                // 出现了两次
                                paramKey1 = strParamSecond.substring(index + 1, indexNext).trim();
                                JsonObject jsonObjectParam1 = new JsonObject();

                                jsonObjectParam1.addProperty(paramKey1, paramValueint);
                                jsonObjectParam.add(paramKey, jsonObjectParam1);
                            } else {
                                if (paramKey.startsWith("DRIVE")) {
                                    jsonObjectParam.addProperty(paramKey, paramValue);
                                } else if (paramKey.startsWith("COMB")) {
                                    jsonObjectParam.addProperty(paramKey, paramValue);
                                } else {
                                    if (TcnVendIF.getInstance().isDigital(paramValue)) {
                                        jsonObjectParam.addProperty(paramKey, paramValue);
                                    }
                                }

                            }

//							JsonObject jsonObjectParam1 = new JsonObject();
//							jsonObjectParam1.addProperty(paramKey1,paramValue1);
//							jsonObjectParam.addProperty(paramKey2,jsonObjectParam1.toString());
                        }
                        EventBus.getDefault().post(new MessageFromUI0203Crc(TcnDrivesConstant.BOARD_STAND_BOARD_JS, 0, TcnDriveCmdType.CMD_SET_PARAMETERS, -1, -1, -1, -1, false,
                                null, null, null, null, null, jsonObjectParam));
                    }
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
//					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_ys_query_param.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    showSelectDialog(-1, getString(R.string.background_please_choose), menu_ys_set_param_select.getButtonEditSecond(), "", TcnParamStandJs.STAND_ITEM_PARAM_SET);
                } else {

                }
            }
        }
    }
}
