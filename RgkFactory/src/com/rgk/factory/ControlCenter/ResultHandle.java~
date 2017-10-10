//file create by liunianliang 20130718~20130724

package com.rgk.factory.ControlCenter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import com.rgk.factory.Config;
import com.rgk.factory.NvRAMAgentHelper;
import com.rgk.factory.R;
import com.rgk.factory.Util;
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

import android.content.IntentSender.SendIntentException;
import android.util.Log;

public class ResultHandle {

	private static String TAG = "ResultHandle";	
	private static final int TESTRESULT_PASS = 0x01;
	private static final int TESTRESULT_FAIL = 0x02;
	private static final int TESTRESULT_INVALID = 0x00;
	private static int testResult = TESTRESULT_PASS;
	public static void SaveResultToSystem(String result, String Tag) {

		File file = new File(Config.ResaultUri + Tag + ".txt");
		Log.e(Tag, "Uri=" + Config.ResaultUri + Tag + ".txt");

		try {
			Log.e(Tag, "---start to save result---");
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.exists() && file.canWrite()) {
				FileWriter fileWrite = new FileWriter(file);
				fileWrite.write(result);
				fileWrite.close();
			}
		} catch (Exception e) {
			Log.e(Tag, e.toString());
		}
		NvRAMAgentHelper.writeTestResultToNV(64*4+40,readTestResult());
	}

	public static String ReadResultFromSystem(String defresult, String Tag) {

		File file = new File(Config.ResaultUri + Tag + ".txt");
		String Result = null;
		Log.e(Tag, "Uri=" + Config.ResaultUri + Tag + ".txt");

		try {
			Log.e(Tag, "---start to read result---");

			if (file.exists() && file.canRead()) {
				FileReader filereader = new FileReader(file);
				StringBuffer sb = new StringBuffer();
				char result[] = new char[6];
				int fd = filereader.read(result);
				if (fd != -1) {
					Result = new String(result, 0, fd);
					sb.append(Result);
				}
				filereader.close();
				Log.e(Tag, "Result=" + sb.toString());
				return sb.toString();
			}
		} catch (Exception e) {
			Log.e(Tag, e.toString());
			return defresult;
		}
		return defresult;
	}

	public static int GetColorFromSystem(String Tag) {
		String Result = ReadResultFromSystem(Config.NOTHING, Tag);
		if (Result.equals(Config.PASS)) {
			return Config.GREEN;
		} else if (Result.equals(Config.FAIL)) {
			return Config.RED;
		} else {
			return Config.BLUE;
		}
	}

	public static boolean DeleteResultFromSystem() {
		boolean result = true;
		File file;
		file = new File(Config.ResaultUri);
		if (file.listFiles(new ResultTextFilter()).length > 0) {
			for (File mfile : file.listFiles(new ResultTextFilter())) {
				File shoulDelete = new File(mfile.getAbsolutePath());
				Log.e(TAG, "path=" + file.getAbsolutePath());
				if (!shoulDelete.delete()) {
					result = false;
				}
			}
		}
		NvRAMAgentHelper.writeTestResultToNV(64*4+40,TESTRESULT_INVALID);
		return result;
	}

	public static class ResultTextFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String filename) {
			return (filename.endsWith(".txt"));
		}
	}

	public static String getTextFromSystem(String Tag) {
		String Result = ReadResultFromSystem(Config.NOTHING, Tag);
		if (Result.equals(Config.PASS)) {
			return Config.PASS;
		} else if (Result.equals(Config.FAIL)) {
			if(testResult != TESTRESULT_INVALID) {
				testResult = TESTRESULT_FAIL;
			}
			return Config.FAIL;
		} else {
			testResult = TESTRESULT_INVALID;
			return "  ";
		}
	}
	
	public static int readTestResult() {
		testResult = TESTRESULT_PASS;
		getTextFromSystem(VersionInfo.TAG);
		getTextFromSystem(MotherboardInfo.TAG);
		if(Util.backLed) {
			getTextFromSystem(BackLED.TAG);
		}
		if(Util.lcd) {
			getTextFromSystem(LCD.TAG);
		}	
		if(Util.touchPanel) {
			getTextFromSystem(TouchPanel.TAG);		
		}
		if(Util.key) {
			getTextFromSystem(KeyTestActivity.TAG);
		}
		if(Util.vibrator) {
			getTextFromSystem(Vibration.TAG);
		}
		if(Util.gravitySensor) {
			getTextFromSystem(GravitySensor.TAG);
		}
		if(Util.calibration) {
			getTextFromSystem(SensorCalibration.TAG);
		}
		if(Util.distanceSensor) {
			getTextFromSystem(DistanceSensor.TAG);
		}
		if(Util.lightSensor) {
			getTextFromSystem(LightSensor.TAG);
		}
		if(Util.loundSpeaker) {
			getTextFromSystem(LoundSpeaker.TAG);
		}
		if(Util.mainCamera) {
			getTextFromSystem(MainCamera.TAG);
		}
		if(Util.flash) {
			getTextFromSystem(FlashlightManager.TAG);
		}
		if(Util.earphone) {
			getTextFromSystem(EarPhone.TAG);
		}
		if(Util.battery) {
			getTextFromSystem(Battery.TAG);
		}
		if(Util.telephony) {
			getTextFromSystem(Telephony.TAG);
		}
		if(Util.memory) {
			getTextFromSystem(Memory.TAG);
		}
		if(Util.sdCard) {
			getTextFromSystem(SdCard.TAG);
		}
		if(Util.gps) {
			getTextFromSystem(GPS.TAG);
		}
		if(Util.bluetooth) {
			getTextFromSystem(Bluetooth.TAG);
		}
		if(Util.wiFi) {
			getTextFromSystem(WiFi.TAG);
		}
		if(Util.microphone) {
			getTextFromSystem(MicrPhone.TAG);
		}
		if(Util.tdCode) {
			getTextFromSystem(CaptureActivity.TAG);
		}
		if(Util.hall) {
			getTextFromSystem(Hall.TAG);
		}
		if(Util.otg) {
			getTextFromSystem(Otg.TAG);
		}
		if(Util.gyroscope) {
			getTextFromSystem(Gyroscope.TAG);
		}
		if(Util.magnetic) {
			getTextFromSystem(MSensor.TAG);
		}
		if(Util.notificationLight) {
			getTextFromSystem(NotificationLight.TAG);
		}
		//This test does not require the automatic test
		/**
		if(Util.headset) {
			getTextFromSystem(HeadSet.TAG);
		}
		if(Util.lcdBlur) {
			getTextFromSystem(LcdBlur.TAG);
		}**/
		return testResult;
	}

}
