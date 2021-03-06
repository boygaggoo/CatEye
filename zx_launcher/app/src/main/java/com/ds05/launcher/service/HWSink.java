package com.ds05.launcher.service;

import android.content.Intent;
import android.util.Log;

import com.ds05.launcher.LauncherApplication;

/**
 * Created by Chongyang.Hu on 2017/2/28 0028.
 */

public final class HWSink {
    public static final String ACTION_DOORBELL_PRESSED = "com.ds05.launcher.service.ACTION.DoorbellPressed.Sound";
    public static final String ACTION_HUMAN_MONITOR_NOTIFY = "com.ds05.launcher.service.ACTION.HumanMonitorNotify.Sound";
    public static final String ACTION_DISPLAY_CAMERA_UI = "com.ds05.launcher.service.ACTION.DisplayCameraUI";
    public static final String ACTION_STOP_CAMERA_NOTIFY = "com.ds05.launcher.service.ACTION.StopCameraNotify";
    public static final String ACTION_STOP_CAMERA_NOTIFY_RESP = "com.ds05.launcher.service.ACTION.StopCameraNotifyRESP";

    public static final String EXTRA_STATUS = "HWSinkStatus";
    public static final int INVALIDE_STATUS = -1;
    public static final int STATUS_HUMAN_IN = 1;
    public static final int STATUS_HUMAN_OUT = 2;

    public static final String EXTRA_REASON = "HWSinkReason";
    public static final int REASON_INVALIDE = -1;
    public static final int REASON_DOORBELL_PRESSED = 1;
    public static final int REASON_DOORBELL_CANCEL = 2;
    public static final int REASON_ALARM_RING = 3;
    public static final int REASON_ALARM_STOP = 4;


    /** 配置参数到驱动的广播 */
    public static final String ACTION_CONFIG_DRIVER_PARAMS = "com.ds05.launcher.service.ACTION.ConfigDriverParams";
    /** 逆光补光灯是否开启 boolean */
    public static final String EXTRA_DRV_CFG_BACK_LIGHT_STATE = "BackFillLightState";
    /** 夜视补光灵敏度 int */
    public static final String EXTRA_DRV_CFG_NIGHT_LIGHT_SENSI = "NightFillLightSensitivity";
    public static final int NIGHT_LIGHT_SENSI_LOW = 1;
    public static final int NIGHT_LIGHT_SENSI_MIDDLE = 2;
    public static final int NIGHT_LIGHT_SENSI_HIGHT = 3;
    /** 光源频率 */
    public static final String EXTRA_DRV_CFG_LIGHT_SRC_FREQ = "LightSourceFrequency";
    public static final int LIGHT_SRC_FREQ_50HZ = 50;
    public static final int LIGHT_SRC_FREQ_60HZ = 60;
    /** 智能人体监测是否开启 boolean */
    public static final String EXTRA_DRV_CFG_HUMAN_MONITOR_STATE = "HumanMonitorState";
    /** 报警间隔时间 */
    public static final String EXTRA_DRV_CFG_ALARM_INTERVAL_TIME = "AlarmIntervalTime";
    public static final int ALARM_INTERVAL_TIME_30SEC = 30000;
    public static final int ALARM_INTERVAL_TIME_90SEC = 90000;
    public static final int ALARM_INTERVAL_TIME_180SEC = 180000;
    /** 智能报警时间 */
    public static final String EXTRA_DRV_CFG_AUTO_ALARM_TIME = "AutoAlarmTime";
    public static final int AUTO_ALARM_TIME_3SEC = 3000;
    public static final int AUTO_ALARM_TIME_8SEC = 8000;
    public static final int AUTO_ALARM_TIME_15SEC = 15000;
    public static final int AUTO_ALARM_TIME_25SEC = 25000;
    /** 监控灵敏度 */
    public static final String EXTRA_DRV_CFG_MONITOR_SENSI = "MonitorSensitivity";
    public static final int MONITOR_SENSI_HIGHT = 1;
    public static final int MONITOR_SENSI_LOW = 2;
    // wangjun add begin
    /** Door light control*/
    public static final String EXTRA_DRV_CFG_DOOR_LIGHT = "DoorLightSetting";
    public static final int DOOR_LIGHT_ON = 1;
    public static final int DOOR_LIGHT_OFF = 2;
    // wangjun add end

    /**拍摄模式*/
    public static final String EXTRA_DRV_CFG_SHOOTING_MODE = "ShootingMode";
    public static final int MONITOR_Recorder = 1;
    public static final int MONITOR_Capture = 0;

        public static void updateDriverConfig(Intent intent) {
        intent.setAction(ACTION_CONFIG_DRIVER_PARAMS);
        LauncherApplication.getContext().sendBroadcast(intent);
    }
}
