/*
 * Copyright (c) 2016. Naivor.All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ds05.launcher.common.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.ds05.launcher.LauncherApplication;
import com.ds05.launcher.common.ConnectUtils;
import com.ds05.launcher.common.Constants;
import com.ds05.launcher.common.manager.PrefDataManager;
import com.ds05.launcher.net.SessionManager;
import com.ds05.launcher.service.HWSink;
import com.ds05.launcher.ui.monitor.MonitorFragment;

import org.apache.mina.core.buffer.IoBuffer;
import org.jsoup.helper.StringUtil;

import static org.weixvn.wae.webpage.net.proxy.UpdataUntil.TAG;

/**
 * AppUtils是一个android工具类，主要包含一些常用的有关android调用的功能，比如拨打电话，判断网络，获取屏幕宽高等等
 */
public class AppUtil {

//    /**
//     * 拨打电话
//     *
//     * @param context     当前上下文对象
//     * @param phoneNumber 电话号码
//     */
//    public static void call(Context context, String phoneNumber) {
//
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
//    }

    public static String BATTERY_LEVEL = "50";

    /**
     * 跳转至拨号界面
     *
     * @param context     当前上下文对象
     * @param phoneNumber 电话号码
     */
    public static void callDial(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    /**
     * 发送短信
     *
     * @param context     当前上下文对象
     * @param phoneNumber 电话号码
     * @param content     短信内容
     */
    public static void sendSms(Context context, String phoneNumber,
                               String content) {
        Uri uri = Uri.parse("smsto:"
                + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        context.startActivity(intent);
    }

    /**
     * 唤醒屏幕并解锁
     *
     * @param context 当前上下文对象
     */
    public static void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁  
        kl.disableKeyguard();
        //获取电源管理器对象  
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag  
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕  
        wl.acquire();
        //释放  
        wl.release();
    }

    /**
     * 判断当前App处于前台还是后台状态
     *
     * @param context 当前上下文对象
     * @return boolean
     */
    public static boolean isApplicationBackground(final Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前手机是否处于锁屏(睡眠)状态
     *
     * @param context 当前上下文对象
     * @return boolean
     */
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    /**
     * 判断当前是否有网络连接
     *
     * @param context 当前上下文对象
     * @return boolean
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否是WIFI连接状态
     *
     * @param context 当前上下文对象
     * @return boolean
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 安装APK
     *
     * @param context 当前上下文对象
     * @param file apk文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断当前设备是否为手机
     *
     * @param context 当前上下文对象
     * @return boolean
     */
    public static boolean isPhone(Context context) {
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取当前设备宽，单位px
     *
     * @param context 当前上下文对象
     * @return 屏幕宽度
     */
    @SuppressWarnings("deprecation")
    public static int getDeviceWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getWidth();
    }

    /**
     * 获取当前设备高，单位px
     *
     * @param context 当前上下文对象
     * @return 屏幕高度
     */
    @SuppressWarnings("deprecation")
    public static int getDeviceHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getHeight();
    }

    /**
     * 获取当前设备的IMEI，需要与上面的isPhone()一起使用
     *
     * @param context 当前上下文对象
     * @return String IMEi信息
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static String getDeviceIMEI(Context context) {
        String deviceId;
        if (isPhone(context)) {
            TelephonyManager telephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephony.getDeviceId();
        } else {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

        }
        return deviceId;
    }

    /**
     * 获取当前设备的MAC地址
     *
     * @param context 当前上下文对象
     * @return MAC的地址
     */
    public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        macAddress = info.getMacAddress();
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }

    /**
     * 获取当前程序的版本
     *
     * @param context 当前上下文对象
     * @return String 当前版本号
     */
    public static String getAppVersionName(Context context) {
        String version = "0";
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取当前设备的WiFi名称
     *
     * @param context 当前上下文对象
     *
     */
    public static String getWifiSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    /**
     * 获取当前设备的WiFi的信号强度
     *
     * @param context 当前上下文对象
     *
     */
    public static String getWifiLevel(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int strength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
        return String.valueOf(strength);
    }


    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd_HHmmss");
        return dateFormat.format(date)  + "_" + AppUtil.getZYLicense() + ".png";
    }

    // 使用系统当前日期加以调整作为录像的名称
    public static String getVideoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd_HHmmss");
        return dateFormat.format(date)  + "_" + AppUtil.getZYLicense() + ".mp4";
    }

    public static void uploadHumanMonitorMsgToServerAndSound(Context context, String fileName, String type) {
//        Intent broadcast = new Intent(HWSink.ACTION_HUMAN_MONITOR_NOTIFY);
//        broadcast.putExtra(HWSink.EXTRA_STATUS, HWSink.STATUS_HUMAN_IN);
//        context.sendBroadcast(broadcast,null);

        String msg = "[" + System.currentTimeMillis() + ",T5," + Constants.SOFT_VERSION + "," + AppUtil.getZYLicense() + "," + fileName + "," + type + "]";
        IoBuffer buffer = IoBuffer.allocate(msg.length());
        buffer.put(msg.getBytes());
        SessionManager.getInstance().writeToServer(buffer);
    }

    public static void uploadDoorbellMsgToServer(Context context, String fileName) {
        AppUtil.wakeUpAndUnlock(context);
        String msg = "[" + System.currentTimeMillis() + ",T4," + Constants.SOFT_VERSION + "," + AppUtil.getZYLicense() + "," + fileName + "," + Constants.IMAGE_FILE_TYPE + "]";
        IoBuffer buffer = IoBuffer.allocate(msg.length());
        buffer.put(msg.getBytes());
        SessionManager.getInstance().writeToServer(buffer);
    }

    public static boolean uploadConfigMsgToServer(Context context) {
        if(!ConnectUtils.NETWORK_IS_OK || !ConnectUtils.CONNECT_SERVER_STATUS){
            return false;
        }
        AppUtil.wakeUpAndUnlock(context);
        int alarmSensi = 2;
        if(PrefDataManager.MonitorSensitivity.High.equals(PrefDataManager.getHumanMonitorSensi())){
            alarmSensi = 1;
        }else {
            alarmSensi = 2;
        }
        int alarmMode = 0;
        if(PrefDataManager.AlarmMode.Capture.equals(PrefDataManager.getAlarmMode())){
            alarmMode = 0;
        }else if(PrefDataManager.AlarmMode.Recorder.equals(PrefDataManager.getAlarmMode())){
            alarmMode = 1;
        }
        int alarmsound = 0;
        Log.d("PPPP","PrefDataManager.getAlarmSoundVolume()="+PrefDataManager.getAlarmSound());
        if(PrefDataManager.AutoAlarmSound.Silence.equals(PrefDataManager.getAlarmSound())){
            alarmsound = 1;

        }else if(PrefDataManager.AutoAlarmSound.Alarm.equals(PrefDataManager.getAlarmSound())){
            alarmsound = 2;
        }else if(PrefDataManager.AutoAlarmSound.Scream.equals(PrefDataManager.getAlarmSound())){
            alarmsound = 3;
        }
        int length = (int)PrefDataManager.getAlarmSoundVolume()*10;

        String msg = "[" + System.currentTimeMillis() + ",T3," + Constants.SOFT_VERSION + "," + AppUtil.getZYLicense() + "," + PrefDataManager.getHumanMonitorState() + "," + PrefDataManager.getAutoAlarmTime()+","+alarmSensi+","+ alarmMode +","+ 1 +","+ alarmsound +","+length+","+PrefDataManager.getDoorbellLight()+","+PrefDataManager.getDoorbellSoundIndex()+","+PrefDataManager.getAlarmIntervalTime()+"]";
        Log.d("PPP"," msg = " + msg);

        IoBuffer buffer = IoBuffer.allocate(msg.length());
        buffer.put(msg.getBytes());
        SessionManager.getInstance().writeToServer(buffer);
        return true;
    }

    public static boolean respondReceiveConfigFromServer(Context context, boolean status) {
        if(!ConnectUtils.NETWORK_IS_OK || !ConnectUtils.CONNECT_SERVER_STATUS){
            return false;
        }
        String flag;
        if(status){
            flag = "True";
        }else{
            flag = "False";
        }
        AppUtil.wakeUpAndUnlock(context);
        String msg = "[" + System.currentTimeMillis() + ",S3," + flag + "]";
        IoBuffer buffer = IoBuffer.allocate(msg.length());
        buffer.put(msg.getBytes());
        SessionManager.getInstance().writeToServer(buffer);
        return true;
    }

    private static String getMacAddress(){
        String result = "";
        String str = "";
        WifiManager wifiManager = (WifiManager) LauncherApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        str = wifiInfo.getMacAddress();
        if(str != null){
            result = str.replaceAll(":","");
        }
        Log.i(TAG, "macAdd:" + result);
        return result;
    }

    private static String zy_license = null;
    public static String getZYLicense(){
        if(!StringUtil.isBlank(zy_license)){
            return zy_license;
        }
        String mac = getMacAddress();
        if(!StringUtil.isBlank(mac)){
            zy_license = mac;
            return zy_license;
        }
        return "";
    }


    public static boolean isForeground(Context context, String className) {
        if (context == null) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
