package com.rgk.factory;

import android.content.Context;
import android.content.res.Resources;

import com.rgk.factory.backled.BackLED;
import com.rgk.factory.battery.Battery;
import com.rgk.factory.bluetooth.Bluetooth;
import com.rgk.factory.earphone.EarPhone;
import com.rgk.factory.flash.FlashlightManager;
import com.rgk.factory.gps.GPS;
import com.rgk.factory.hall.Hall;
import com.rgk.factory.headset.HeadSet;
import com.rgk.factory.key.KeyTestActivity;
import com.rgk.factory.lcd.LCD;
import com.rgk.factory.lcdblur.LcdBlur;
import com.rgk.factory.loundspeaker.LoundSpeaker;
import com.rgk.factory.maincamera.MainCamera;
import com.rgk.factory.memory.Memory;
import com.rgk.factory.microphone.MicrPhone;
import com.rgk.factory.notificationlight.NotificationLight;
import com.rgk.factory.otg.Otg;
import com.rgk.factory.sdcard.SdCard;
import com.rgk.factory.sensor.DistanceSensor;
import com.rgk.factory.sensor.GravitySensor;
import com.rgk.factory.sensor.Gyroscope;
import com.rgk.factory.sensor.LightSensor;
import com.rgk.factory.sensor.MSensor;
import com.rgk.factory.sensor.SensorCalibration;
import com.rgk.factory.tdcoder.CaptureActivity;
import com.rgk.factory.telephony.Telephony;
import com.rgk.factory.touchpanel.TouchPanel;
import com.rgk.factory.versioninfo.MotherboardInfo;
import com.rgk.factory.versioninfo.VersionInfo;
import com.rgk.factory.vibrator.Vibration;
import com.rgk.factory.wifi.WiFi;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static String TAG = "Util";
    private Context mContext;
    public static List<String> autoTestList, singleTestTag, singleTestTitle;
    public static List<Class> autoTestClass, singleTestClass;
    public static String[] TestItems;
    public static Object[] TestItemClass;
    public static boolean backLed = true, lcd = true, touchPanel = true, key = true, vibrator = true, gravitySensor = true, calibration = true,
            distanceSensor = true, lightSensor = true, loundSpeaker = true, mainCamera = true, flash = true,
            headset = true, earphone = true, battery = true, telephony = true, memory = true, sdCard = true, gps = true,
            bluetooth = true, wiFi = true, microphone = true, tdCode = true, hall = true, otg = true, gyroscope = true, magnetic = true,
            notificationLight, lcdBlur = true;
    public static boolean gravitySensorNoZAxis;
    public static int AP_CFG_REEB_PRODUCT_INFO_LID, AP_CFG_RDEB_FILE_WIFI_LID, AP_CFG_CUSTOM_FILE_GPS_LID;
    public static boolean defaultEnglish;

    public Util(Context context) {
        mContext = context;
        autoTestList = new ArrayList<String>();
        autoTestClass = new ArrayList<Class>();
        singleTestTag = new ArrayList<String>();
        singleTestTitle = new ArrayList<String>();
        singleTestClass = new ArrayList<Class>();
    }

    public void initValue() {
        readResources(mContext);
        setAutoTestItems();
        setSingleTestItems(mContext);
    }

    private void setSingleTestItems(Context context) {
        singleTestTag.add(VersionInfo.TAG);
        singleTestTitle.add(context.getResources().getString(R.string.soft_version));
        singleTestClass.add(VersionInfo.class);
        singleTestTag.add(MotherboardInfo.TAG);
        singleTestTitle.add(context.getResources().getString(R.string.board_info));
        singleTestClass.add(MotherboardInfo.class);

        if (backLed) {
            singleTestTag.add(BackLED.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.BackLED));
            singleTestClass.add(BackLED.class);
        }
        if (lcd) {
            singleTestTag.add(LCD.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.lcd));
            singleTestClass.add(LCD.class);
        }
        if (touchPanel) {
            singleTestTag.add(TouchPanel.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.touch_panel));
            singleTestClass.add(TouchPanel.class);
        }
        if (key) {
            singleTestTag.add(KeyTestActivity.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.key));
            singleTestClass.add(KeyTestActivity.class);
        }
        if (vibrator) {
            singleTestTag.add(Vibration.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Vibrator));
            singleTestClass.add(Vibration.class);
        }
        if (gravitySensor) {
            singleTestTag.add(GravitySensor.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Gravity_Sensor));
            singleTestClass.add(GravitySensor.class);
        }
        if (calibration) {
            singleTestTag.add(SensorCalibration.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Calibration));
            singleTestClass.add(SensorCalibration.class);
        }
        if (distanceSensor) {
            singleTestTag.add(DistanceSensor.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Distance_Sensor));
            singleTestClass.add(DistanceSensor.class);
        }
        if (lightSensor) {
            singleTestTag.add(LightSensor.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Light_Sensor));
            singleTestClass.add(LightSensor.class);
        }
        if (magnetic) {
            singleTestTag.add(MSensor.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.magnetic));
            singleTestClass.add(MSensor.class);
        }
        if (gyroscope) {
            singleTestTag.add(Gyroscope.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.gyroscope));
            singleTestClass.add(Gyroscope.class);
        }
        if (hall) {
            singleTestTag.add(Hall.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.hall));
            singleTestClass.add(Hall.class);
        }
        if (loundSpeaker) {
            singleTestTag.add(LoundSpeaker.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.LoundSpeaker));
            singleTestClass.add(LoundSpeaker.class);
        }
        if (mainCamera) {
            singleTestTag.add(MainCamera.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Main_Camera));
            singleTestClass.add(MainCamera.class);
        }
        if (flash) {
            singleTestTag.add(FlashlightManager.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.key_Flash));
            singleTestClass.add(FlashlightManager.class);
        }
        if (headset) {
            singleTestTag.add(HeadSet.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Headset));
            singleTestClass.add(HeadSet.class);
        }
        if (earphone) {
            singleTestTag.add(EarPhone.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Earphone));
            singleTestClass.add(EarPhone.class);
        }
        if (battery) {
            singleTestTag.add(Battery.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.battery));
            singleTestClass.add(Battery.class);
        }
        if (telephony) {
            singleTestTag.add(Telephony.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Telephony));
            singleTestClass.add(Telephony.class);
        }
        if (notificationLight) {
            singleTestTag.add(NotificationLight.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.notification_light));
            singleTestClass.add(NotificationLight.class);
        }
        if (otg) {
            singleTestTag.add(Otg.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.otg));
            singleTestClass.add(Otg.class);
        }
        if (memory) {
            singleTestTag.add(Memory.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Memory));
            singleTestClass.add(Memory.class);
        }
        if (sdCard) {
            singleTestTag.add(SdCard.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.SDcard));
            singleTestClass.add(SdCard.class);
        }
        if (gps) {
            singleTestTag.add(GPS.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.gps));
            singleTestClass.add(GPS.class);
        }
        if (bluetooth) {
            singleTestTag.add(Bluetooth.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Bluetooth));
            singleTestClass.add(Bluetooth.class);
        }
        if (wiFi) {
            singleTestTag.add(WiFi.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.WiFi));
            singleTestClass.add(WiFi.class);
        }
        if (microphone) {
            singleTestTag.add(MicrPhone.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.Microphone));
            singleTestClass.add(MicrPhone.class);
        }
        if (tdCode) {
            singleTestTag.add(CaptureActivity.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.TD_code));
            singleTestClass.add(CaptureActivity.class);
        }
        if (lcdBlur) {
            singleTestTag.add(LcdBlur.TAG);
            singleTestTitle.add(context.getResources().getString(R.string.lcd_blur));
            singleTestClass.add(LcdBlur.class);
        }
    }

    private void setAutoTestItems() {
        autoTestList.add(VersionInfo.TAG);
        autoTestClass.add(VersionInfo.class);
        autoTestList.add(MotherboardInfo.TAG);
        autoTestClass.add(MotherboardInfo.class);

        if (backLed) {
            autoTestList.add(BackLED.TAG);
            autoTestClass.add(BackLED.class);
        }
        if (lcd) {
            autoTestList.add(LCD.TAG);
            autoTestClass.add(LCD.class);
        }
        if (touchPanel) {
            autoTestList.add(TouchPanel.TAG);
            autoTestClass.add(TouchPanel.class);
        }
        if (key) {
            autoTestList.add(KeyTestActivity.TAG);
            autoTestClass.add(KeyTestActivity.class);
        }
        if (vibrator) {
            autoTestList.add(Vibration.TAG);
            autoTestClass.add(Vibration.class);
        }
        if (gravitySensor) {
            autoTestList.add(GravitySensor.TAG);
            autoTestClass.add(GravitySensor.class);
        }
        if (calibration) {
            autoTestList.add(SensorCalibration.TAG);
            autoTestClass.add(SensorCalibration.class);
        }
        if (distanceSensor) {
            autoTestList.add(DistanceSensor.TAG);
            autoTestClass.add(DistanceSensor.class);
        }
        if (lightSensor) {
            autoTestList.add(LightSensor.TAG);
            autoTestClass.add(LightSensor.class);
        }
        if (magnetic) {
            autoTestList.add(MSensor.TAG);
            autoTestClass.add(MSensor.class);
        }
        if (gyroscope) {
            autoTestList.add(Gyroscope.TAG);
            autoTestClass.add(Gyroscope.class);
        }
        if (hall) {
            autoTestList.add(Hall.TAG);
            autoTestClass.add(Hall.class);
        }
        if (loundSpeaker) {
            autoTestList.add(LoundSpeaker.TAG);
            autoTestClass.add(LoundSpeaker.class);
        }
        if (mainCamera) {
            autoTestList.add(MainCamera.TAG);
            autoTestClass.add(MainCamera.class);
        }
        if (flash) {
            autoTestList.add(FlashlightManager.TAG);
            autoTestClass.add(FlashlightManager.class);
        }
        if (earphone) {
            autoTestList.add(EarPhone.TAG);
            autoTestClass.add(EarPhone.class);
        }
        if (battery) {
            autoTestList.add(Battery.TAG);
            autoTestClass.add(Battery.class);
        }
        if (telephony) {
            autoTestList.add(Telephony.TAG);
            autoTestClass.add(Telephony.class);
        }
        if (notificationLight) {
            autoTestList.add(NotificationLight.TAG);
            autoTestClass.add(NotificationLight.class);
        }
        if (otg) {
            autoTestList.add(Otg.TAG);
            autoTestClass.add(Otg.class);
        }
        if (memory) {
            autoTestList.add(Memory.TAG);
            autoTestClass.add(Memory.class);
        }
        if (sdCard) {
            autoTestList.add(SdCard.TAG);
            autoTestClass.add(SdCard.class);
        }
        if (gps) {
            autoTestList.add(GPS.TAG);
            autoTestClass.add(GPS.class);
        }
        if (bluetooth) {
            autoTestList.add(Bluetooth.TAG);
            autoTestClass.add(Bluetooth.class);
        }
        if (wiFi) {
            autoTestList.add(WiFi.TAG);
            autoTestClass.add(WiFi.class);
        }
        if (microphone) {
            autoTestList.add(MicrPhone.TAG);
            autoTestClass.add(MicrPhone.class);
        }
        if (tdCode) {
            autoTestList.add(CaptureActivity.TAG);
            autoTestClass.add(CaptureActivity.class);
        }
        //This test does not require the automatic test
        //And you need to remove the judge in ResultHandle.java
        /**
         if(headset) {
         autoTestList.add(HeadSet.TAG);
         autoTestClass.add(HeadSet.class);
         }
         if(lcdBlur) {
         autoTestList.add(LcdBlur.TAG);
         autoTestClass.add(LcdBlur.class);
         }**/

        TestItems = (String[]) autoTestList.toArray(new String[autoTestList.size()]);
        TestItemClass = (Object[]) autoTestClass.toArray();
    }

    public void readResources(Context ctx) {
        Resources res = ctx.getResources();
        backLed = res.getBoolean(R.bool.backLed);
        lcd = res.getBoolean(R.bool.lcd);
        touchPanel = res.getBoolean(R.bool.touchPanel);
        key = res.getBoolean(R.bool.key);
        vibrator = res.getBoolean(R.bool.vibrator);
        gravitySensor = res.getBoolean(R.bool.gravitySensor);
        calibration = res.getBoolean(R.bool.calibration);
        distanceSensor = res.getBoolean(R.bool.distanceSensor);
        lightSensor = res.getBoolean(R.bool.lightSensor);
        loundSpeaker = res.getBoolean(R.bool.loundSpeaker);
        mainCamera = res.getBoolean(R.bool.mainCamera);
        flash = res.getBoolean(R.bool.flash);
        headset = res.getBoolean(R.bool.headset);
        earphone = res.getBoolean(R.bool.earphone);
        battery = res.getBoolean(R.bool.battery);
        telephony = res.getBoolean(R.bool.telephony);
        memory = res.getBoolean(R.bool.memory);
        sdCard = res.getBoolean(R.bool.sdCard);
        gps = res.getBoolean(R.bool.gps);
        bluetooth = res.getBoolean(R.bool.bluetooth);
        wiFi = res.getBoolean(R.bool.wiFi);
        microphone = res.getBoolean(R.bool.microphone);
        tdCode = res.getBoolean(R.bool.tdCode);
        hall = res.getBoolean(R.bool.hall);
        otg = res.getBoolean(R.bool.otg);
        gyroscope = res.getBoolean(R.bool.gyroscope);
        magnetic = res.getBoolean(R.bool.magnetic);
        notificationLight = res.getBoolean(R.bool.notificationLight);
        lcdBlur = res.getBoolean(R.bool.lcdBlur);

        gravitySensorNoZAxis = res.getBoolean(R.bool.gravitySensorNoZAxis);
        AP_CFG_REEB_PRODUCT_INFO_LID = res.getInteger(R.integer.ap_cfg_reeb_product_info_lid);
        AP_CFG_RDEB_FILE_WIFI_LID = res.getInteger(R.integer.ap_cfg_rdeb_file_wifi_lid);
        AP_CFG_CUSTOM_FILE_GPS_LID = res.getInteger(R.integer.ap_cfg_custom_file_gps_lid);
        defaultEnglish = res.getBoolean(R.bool.defaultEnglish);
    }

}
